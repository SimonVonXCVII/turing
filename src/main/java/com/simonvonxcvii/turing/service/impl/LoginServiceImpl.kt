package com.simonvonxcvii.turing.service.impl

import com.simonvonxcvii.turing.common.exception.BizRuntimeException
import com.simonvonxcvii.turing.entity.AbstractAuditable
import com.simonvonxcvii.turing.entity.Menu
import com.simonvonxcvii.turing.entity.RolePermission
import com.simonvonxcvii.turing.model.dto.MenuDTO
import com.simonvonxcvii.turing.model.dto.MenuMetaDTO
import com.simonvonxcvii.turing.model.dto.UserDTO
import com.simonvonxcvii.turing.repository.MenuRepository
import com.simonvonxcvii.turing.repository.RolePermissionRepository
import com.simonvonxcvii.turing.service.LoginService
import com.simonvonxcvii.turing.utils.Constants
import com.simonvonxcvii.turing.utils.RandomUtils
import com.simonvonxcvii.turing.utils.UserUtils
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import java.io.IOException
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

/**
 * 登录 服务实现类
 *
 * @author Simon Von
 * @since 12/16/2022 4:08 PM
 */
@Service
class LoginServiceImpl(
    private val randomUtils: RandomUtils,
    private val menuRepository: MenuRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) : LoginService {
    /**
     * 获取登录验证码
     *
     * @author Simon Von
     * @since 12/16/2022 4:09 PM
     */
    @Throws(IOException::class)
    override fun getCaptcha(request: HttpServletRequest, response: HttpServletResponse) {
        // 生成验证码并写入到响应中
        val captcha = randomUtils.getCaptcha(response)
        // 使用 md5 这种方式作为 key 的原因是 session id 总是会改变，同一个客户端的浏览器发送的请求的 session id 无法保持一致
        val ipAddr = InetAddress.getByName(request.remoteAddr).hostAddress
        val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
        val md5DigestAsHex = DigestUtils.md5DigestAsHex((ipAddr + userAgent).toByteArray(StandardCharsets.UTF_8))
        // 缓存当前用户标识生成的 md5 和验证码
        // 验证码一分钟有效期
        redisTemplate.opsForValue()[Constants.REDIS_CAPTCHA + md5DigestAsHex, captcha, 1] = TimeUnit.MINUTES
    }

    /**
     * 获取用户登录成功后所需要的信息
     *
     * @author Simon Von
     * @since 12/17/2022 8:19 PM
     */
    override fun getUserInfo(): UserDTO {
        val user = UserUtils.getUser()
        val userDTO = UserDTO()
        BeanUtils.copyProperties(user, userDTO)
        return userDTO
    }

    /**
     * 获取用户登录成功后的菜单集合
     *
     * @author Simon Von
     * @since 12/17/2022 8:19 PM
     */
    override fun getMenuList(): List<MenuDTO> {
        var menuDTOList = mutableListOf<MenuDTO>()
        // 预先查询所有菜单
        val menuList = menuRepository.findAll(Sort.by(Menu.SORT))
        // 超级管理员可查看所有菜单
        val user = UserUtils.getUser()
        if (user.admin) {
            menuList.stream()
                .map { menu: Menu -> menuConvertToDTO(menu) }
                // 区分父级和子级菜单
                .forEach { menuDTO: MenuDTO ->
                    if (menuDTO.pid == null) {
                        menuDTOList.add(menuDTO)
                    } else {
                        menuDTOList.stream()
                            .filter { parentMenuDTO: MenuDTO -> parentMenuDTO.id == menuDTO.pid }
                            .forEach { parentMenuDTO: MenuDTO ->
                                parentMenuDTO.children.add(menuDTO)
                            }
                    }
                }
            return menuDTOList
        }

        // 获取所有角色的所有角色与权限中间表数据
        val rolePermissionList =
            rolePermissionRepository.findAll { root: Root<RolePermission?>, _: CriteriaQuery<*>?, _: CriteriaBuilder? ->
                root.get<Any>(RolePermission.ROLE_ID).`in`(
                    user.authorities.stream().map(
                        AbstractAuditable::id
                    ).toList()
                )
            }
        if (rolePermissionList.isEmpty()) {
            throw BizRuntimeException("无法获取菜单，因为当前用户的角色没有任何权限")
        }

        // 该用户权限对应的所有子级菜单
        menuDTOList = menuList.stream()
            .filter { menu: Menu ->
                rolePermissionList.stream().map { obj: RolePermission -> obj.permissionId }
                    .anyMatch(Predicate.isEqual(menu.permissionId))
            }
            // 只需要子级
            .filter { menu: Menu -> menu.pid != null }
            .map { menu: Menu -> menuConvertToDTO(menu) }
            .toList()
        if (menuDTOList.isEmpty()) {
            throw BizRuntimeException("当前用户没有任何菜单")
        }

        // 匹配父级和子级菜单
        return menuList.stream()
            .filter { menu: Menu ->
                menuDTOList.stream().map { obj: MenuDTO -> obj.pid }
                    .anyMatch(Predicate.isEqual(menu.id))
            }
            .map { menu: Menu -> menuConvertToDTO(menu) }
            .peek { menuDTO: MenuDTO ->
                menuDTOList.stream()
                    .filter { childMenuDTO: MenuDTO -> menuDTO.id == childMenuDTO.pid }
                    .forEach { childMenuDTO: MenuDTO? -> menuDTO.children.add(childMenuDTO) }
            }
            .toList()
    }

    /**
     * // TODO: 2023/8/31 优化
     * Menu convert to MenuDTO
     */
    fun menuConvertToDTO(menu: Menu): MenuDTO {
        val menuDTO = MenuDTO()
        BeanUtils.copyProperties(menu, menuDTO)
        val menuMetaDTO = MenuMetaDTO()
        menuMetaDTO.title = menu.title
        menuMetaDTO.icon = menu.icon
        menuMetaDTO.hideMenu = !menu.showed
        menuDTO.meta = menuMetaDTO
        return menuDTO
    }
}
