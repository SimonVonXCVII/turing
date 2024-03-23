package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.*;
import com.shiminfxcvii.turing.mapper.MenuMapper;
import com.shiminfxcvii.turing.mapper.OrganizationMapper;
import com.shiminfxcvii.turing.mapper.RolePermissionMapper;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.dto.MenuMetaDTO;
import com.shiminfxcvii.turing.model.dto.UserInfoDTO;
import com.shiminfxcvii.turing.service.LoginService;
import com.shiminfxcvii.turing.utils.Constants;
import com.shiminfxcvii.turing.utils.RandomCode;
import com.shiminfxcvii.turing.utils.UserUtils;
import com.shiting.soil.entity.BaseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.IPv6Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 登录 服务实现类
 *
 * @author ShiminFXCVII
 * @since 12/16/2022 4:08 PM
 */
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final RandomCode randomCode;
    private final OrganizationMapper organizationMapper;
    private final MenuMapper menuMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取登录验证码
     *
     * @author ShiminFXCVII
     * @since 12/16/2022 4:09 PM
     */
    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 生成验证码并写入到响应中
        String captcha = randomCode.getCaptcha(response);
        // 使用 md5 这种方式作为 key 的原因是 session id 总是会改变，同一个客户端的浏览器发送的请求的 session id 无法保持一致
        String ipAddr = IPv6Utils.canonize(request.getRemoteAddr());
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex((ipAddr + userAgent).getBytes(StandardCharsets.UTF_8));
        // 缓存当前用户标识生成的 md5 和验证码
        // 验证码一分钟有效期
        redisTemplate.opsForValue().set(Constants.REDIS_CAPTCHA + md5DigestAsHex, captcha, 1, TimeUnit.MINUTES);
    }

    /**
     * 获取用户登录成功后所需要的信息
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    @Override
    public UserInfoDTO getUserInfo() {
        String username = UserUtils.getUserOrElseThrow().getUsername();
        User user = (User) redisTemplate.opsForValue().get(User.REDIS_KEY_PREFIX + username);
        if (user == null) {
            throw new BizRuntimeException("该用户账号不存在：" + username);
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        List<Role> roleList = user.getRoleList();
        if (CollectionUtils.isEmpty(roleList)) {
            throw new BizRuntimeException("非法账号，该账号没有角色：" + username);
        }
        userInfoDTO.setRoles(roleList.stream().map(Role::getCode).toList());
        // TODO: 2023/3/14 token 是否能够省略
        userInfoDTO.setToken(user.getToken());
        userInfoDTO.setOrgId(user.getOrgId());
        Organization organization = organizationMapper.selectById(user.getOrgId());
        if (organization != null) {
            userInfoDTO.setOrgName(organization.getName());
        } else {
            userInfoDTO.setOrgName(user.getOrgName());
        }
        return userInfoDTO;
    }

    /**
     * 获取用户登录成功后的菜单集合
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    @Override
    public List<MenuDTO> getMenuList() {
        var ref = new Object() {
            List<MenuDTO> menuDTOList = new LinkedList<>();
        };
        // 预先查询所有菜单
        List<Menu> menuList = menuMapper.selectList(Wrappers.<Menu>lambdaQuery().orderByAsc(Menu::getSort));
        // 超级管理员可查看所有菜单
        User user = UserUtils.getUserOrElseThrow();
        if (user.getAdmin()) {
            menuList.stream()
                    .map(this::convertToDTO)
                    // 区分父级和子级菜单
                    .forEach(menuDTO -> {
                        if (menuDTO.getPid() == null) {
                            ref.menuDTOList.add(menuDTO);
                        } else {
                            ref.menuDTOList.stream()
                                    .filter(parentMenuDTO -> Objects.equals(parentMenuDTO.getId(), menuDTO.getPid()))
                                    .forEach(parentMenuDTO -> parentMenuDTO.getChildren().add(menuDTO));
                        }
                    });
            return ref.menuDTOList;
        }

        List<Role> roleList = user.getRoleList();
        if (CollectionUtils.isEmpty(roleList)) {
            throw new BizRuntimeException("无法获取菜单，因为当前用户没有任何角色");
        }

        // 获取所有角色的所有角色与权限中间表数据
        List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getRoleId, roleList.stream().map(BaseEntity::getId).toList()));
        if (rolePermissionList.isEmpty()) {
            throw new BizRuntimeException("无法获取菜单，因为当前用户的角色没有任何权限");
        }

        // 该用户权限对应的所有子级菜单
        ref.menuDTOList = menuList.stream()
                .filter(menu -> rolePermissionList.stream().map(RolePermission::getPermissionId).anyMatch(Predicate.isEqual(menu.getPermissionId())))
                // 只需要子级
                .filter(menu -> menu.getPid() != null)
                .map(this::convertToDTO)
                .toList();
        if (ref.menuDTOList.isEmpty()) {
            throw new BizRuntimeException("当前用户没有任何菜单");
        }

        // 匹配父级和子级菜单
        return menuList.stream()
                .filter(menu -> ref.menuDTOList.stream().map(MenuDTO::getPid).anyMatch(Predicate.isEqual(menu.getId())))
                .map(this::convertToDTO)
                .peek(menuDTO -> ref.menuDTOList.stream()
                        .filter(childMenuDTO -> Objects.equals(menuDTO.getId(), childMenuDTO.getPid()))
                        .forEach(childMenuDTO -> menuDTO.getChildren().add(childMenuDTO)))
                .toList();
    }

    /**
     * Menu convert to MenuDTO
     */
    public MenuDTO convertToDTO(Menu menu) {
        MenuDTO menuDTO = new MenuDTO();
        BeanUtils.copyProperties(menu, menuDTO);
        MenuMetaDTO menuMetaDTO = new MenuMetaDTO();
        menuMetaDTO.setTitle(menu.getTitle());
//        menuMetaDTO.setIcon(menu.getIcon());
        menuMetaDTO.setHideMenu(!menu.getShow());
        menuDTO.setMeta(menuMetaDTO);
        return menuDTO;
    }

}