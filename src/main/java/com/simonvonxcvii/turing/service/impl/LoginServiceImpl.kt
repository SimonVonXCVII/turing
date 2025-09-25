package com.simonvonxcvii.turing.service.impl

import com.simonvonxcvii.turing.common.exception.BizRuntimeException
import com.simonvonxcvii.turing.entity.AbstractAuditable
import com.simonvonxcvii.turing.entity.Menu
import com.simonvonxcvii.turing.entity.RolePermission
import com.simonvonxcvii.turing.model.dto.MenuDTO
import com.simonvonxcvii.turing.model.dto.MenuMetaDTO
import com.simonvonxcvii.turing.model.dto.UserDTO
import com.simonvonxcvii.turing.repository.jpa.MenuJpaRepository
import com.simonvonxcvii.turing.repository.jpa.RolePermissionJpaRepository
import com.simonvonxcvii.turing.service.LoginService
import com.simonvonxcvii.turing.utils.Constants
import com.simonvonxcvii.turing.utils.RandomUtils
import com.simonvonxcvii.turing.utils.UserUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import java.io.IOException
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.time.Duration
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
    private val menuJpaRepository: MenuJpaRepository,
    private val rolePermissionJpaRepository: RolePermissionJpaRepository,
    private val stringRedisTemplate: StringRedisTemplate
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
        val ipAddrUserAgentByte = (ipAddr + userAgent).toByteArray(StandardCharsets.UTF_8)
        val md5DigestAsHex = DigestUtils.md5DigestAsHex(ipAddrUserAgentByte)
        // 缓存当前用户标识生成的 md5 和验证码
        // 验证码一分钟有效期
        stringRedisTemplate.opsForValue()
            .set(Constants.REDIS_CAPTCHA + md5DigestAsHex, captcha, Duration.ofMinutes(1))
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
        val menuList = menuJpaRepository.findAll(Sort.by(Menu.SORT)).filterNotNull()
        // 超级管理员可查看所有菜单
        val user = UserUtils.getUser()
        if (user.admin) {
            menuList.stream()
                .map { menu -> menuConvertToDTO(menu) }
                // 区分父级和子级菜单
                .forEach { menuDTO ->
                    if (menuDTO.pid == null) {
                        menuDTOList.add(menuDTO)
                    } else {
                        menuDTOList.stream()
                            .filter { parentMenuDTO -> parentMenuDTO.id == menuDTO.pid }
                            .forEach { parentMenuDTO -> parentMenuDTO.children.add(menuDTO) }
                    }
                }
            return menuDTOList
        }

        // 获取所有角色的所有角色与权限中间表数据
        val roleIdList = user.authorities.stream().map(AbstractAuditable::id).toList()
        val rolePermissionList = rolePermissionJpaRepository.findAll { root, query, builder ->
            val roleId = builder.`in`(root.get<String>(RolePermission.ROLE_ID)).`in`(roleIdList)
            query?.where(roleId)?.restriction
        }.filterNotNull()
        if (rolePermissionList.isEmpty()) {
            throw BizRuntimeException("无法获取菜单，因为当前用户的角色没有任何权限")
        }

        // 该用户权限对应的所有子级菜单
        menuDTOList = menuList.stream()
            .filter { menu ->
                rolePermissionList.stream()
                    .map { obj -> obj.permissionId }
                    .anyMatch(Predicate.isEqual(menu.permissionId))
            }
            // 只需要子级
            .filter { menu -> menu.pid != null }
            .map { menu -> menuConvertToDTO(menu) }
            .toList()
        if (menuDTOList.isEmpty()) {
            throw BizRuntimeException("当前用户没有任何菜单")
        }

        // 匹配父级和子级菜单
        return menuList.stream()
            .filter { menu ->
                menuDTOList.stream()
                    .map { obj -> obj.pid }
                    .anyMatch(Predicate.isEqual(menu.id))
            }
            .map { menu -> menuConvertToDTO(menu) }
            .peek { menuDTO: MenuDTO ->
                menuDTOList.stream()
                    .filter { childMenuDTO -> menuDTO.id == childMenuDTO.pid }
                    .forEach { childMenuDTO -> menuDTO.children.add(childMenuDTO) }
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
