package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.OrganizationBusiness;
import com.shiminfxcvii.turing.entity.Role;
import com.shiminfxcvii.turing.entity.RolePermission;
import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.shiminfxcvii.turing.mapper.OrganizationBusinessMapper;
import com.shiminfxcvii.turing.mapper.RoleMapper;
import com.shiminfxcvii.turing.mapper.RolePermissionMapper;
import com.shiminfxcvii.turing.model.cmd.RoleCmd;
import com.shiminfxcvii.turing.model.dto.RoleDTO;
import com.shiminfxcvii.turing.model.query.RoleQuery;
import com.shiminfxcvii.turing.service.IRoleService;
import com.shiminfxcvii.turing.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@RequiredArgsConstructor
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final OrganizationBusinessMapper organizationBusinessMapper;

    @Override
    public IPage<RoleDTO> selectPage(RoleQuery query) {
        return lambdaQuery()
                .like(query.getName() != null, Role::getName, query.getName())
                .like(query.getCode() != null, Role::getCode, query.getCode())
                .like(query.getDescription() != null, Role::getDescription, query.getDescription())
                .page(new Page<>(query.getPageIndex(), query.getPageSize()))
                .convert(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                });
    }

    @Override
    public List<RoleDTO> selectList(RoleQuery query) {
        return lambdaQuery()
                .like(query.getName() != null, Role::getName, query.getName())
                .like(query.getCode() != null, Role::getCode, query.getCode())
                .like(query.getDescription() != null, Role::getDescription, query.getDescription())
                .list()
                .stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                })
                .toList();
    }

    @Override
    public RoleDTO selectOneById(String id) {
        Role role = getById(id);
        if (role == null) {
            throw new BizRuntimeException("没有查询到该角色");
        }
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        // 查询该角色具有的权限
        List<String> permissionIdList = rolePermissionMapper
                .selectList(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, role.getId()))
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
        roleDTO.setPermissionIdList(permissionIdList);
        return roleDTO;
    }

    @Override
    @Transactional
    public void insert(RoleCmd cmd) {
        boolean exists = lambdaQuery().eq(Role::getName, cmd.getName()).exists();
        if (exists) {
            throw new BizRuntimeException("该角色名称已经存在");
        }
        exists = lambdaQuery().eq(Role::getCode, cmd.getCode()).exists();
        if (exists) {
            throw new BizRuntimeException("该角色编号已经存在");
        }
        Role role = new Role();
        BeanUtils.copyProperties(cmd, role);
        save(role);

        // 角色权限表
        cmd.getPermissionIdList().forEach(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        });
    }

    @Override
    @Transactional
    public void update(RoleCmd cmd) {
        Role role = getById(cmd.getId());
        if (role == null) {
            throw new BizRuntimeException("没有查询到该角色");
        }
        boolean exists = lambdaQuery().ne(Role::getId, cmd.getId()).eq(Role::getName, cmd.getName()).exists();
        if (exists) {
            throw new BizRuntimeException("该角色名称已经存在");
        }
        exists = lambdaQuery().ne(Role::getId, cmd.getId()).eq(Role::getCode, cmd.getCode()).exists();
        if (exists) {
            throw new BizRuntimeException("该角色编号已经存在");
        }
        BeanUtils.copyProperties(cmd, role);
        updateById(role);

        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        rolePermissionMapper.delete(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, cmd.getId()));
        cmd.getPermissionIdList().forEach(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        });
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        Role role = getById(id);
        if (role == null) {
            throw new BizRuntimeException("没有查询到该角色");
        }
        removeById(id);
    }

    @Override
    public void createCache(Cache<String, String> roleCache) {
        List<Role> list = list();
        for (Role role : list) {
            roleCache.put(role.getId(), role.getName());
            roleCache.put(role.getName(), role.getId());
        }
    }

    /**
     * 查询单个单位的申请业务审核已通过的对应的角色
     *
     * @author ShiminFXCVII
     */
    public List<RoleDTO> selectListForBusinessOrg() {
        // 获取所有已通过的业务申请
        List<OrganizationBusiness> organizationBusinessList = organizationBusinessMapper.selectList(Wrappers
                .<OrganizationBusiness>lambdaQuery()
                .eq(OrganizationBusiness::getOrgId, UserUtils.getOrgId())
                .eq(OrganizationBusiness::getState, OrganizationBusinessStateEnum.PASSES));
        if (organizationBusinessList.isEmpty()) {
            return List.of();
        }

        List<RoleDTO> roleDTOList = new LinkedList<>();
        organizationBusinessList.forEach(organizationBusiness -> {
            if (organizationBusiness.getLink() != null) {
                String[] links = StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink());
                for (String link : links) {
                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum -> lambdaQuery()
                            .eq(Role::getCode, "STAFF_" + anEnum.name())
                            .list()
                            .forEach(role -> {
                                RoleDTO roleDTO = new RoleDTO();
                                roleDTO.setId(role.getId());
                                roleDTO.setName(role.getName());
                                roleDTOList.add(roleDTO);
                            }));
                }
            }
            if (organizationBusiness.getType() != null) {
                String[] types = StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType());
                for (String type : types) {
                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(type).ifPresent(anEnum -> lambdaQuery()
                            .eq(Role::getCode, "STAFF_" + anEnum.name())
                            .list()
                            .forEach(role -> {
                                RoleDTO roleDTO = new RoleDTO();
                                roleDTO.setId(role.getId());
                                roleDTO.setName(role.getName());
                                roleDTOList.add(roleDTO);
                            }));
                }
            }
        });
        return roleDTOList;
    }

    /**
     * 根据当前登录用户查询行政单位工作人员角色
     *
     * @author ShiminFXCVII
     */
    @Override
    public List<RoleDTO> selectListForAdministrativeOrg() {
        Collection<? extends GrantedAuthority> authorities = UserUtils.getUserDetailsOrElseThrow().getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_PROVINCE_GOV"))) {
            return lambdaQuery()
                    .eq(Role::getCode, "STAFF_PROVINCE_GOV")
                    .list()
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_CITY_GOV"))) {
            return lambdaQuery()
                    .eq(Role::getCode, "STAFF_CITY_GOV")
                    .list()
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_DISTRICT_GOV"))) {
            return lambdaQuery()
                    .eq(Role::getCode, "STAFF_DISTRICT_GOV")
                    .list()
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        }
        return List.of();
    }

}