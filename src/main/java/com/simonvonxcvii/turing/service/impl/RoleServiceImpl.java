package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.OrganizationBusiness;
import com.simonvonxcvii.turing.entity.Role;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.simonvonxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.repository.OrganizationBusinessRepository;
import com.simonvonxcvii.turing.repository.RolePermissionRepository;
import com.simonvonxcvii.turing.repository.RoleRepository;
import com.simonvonxcvii.turing.repository.UserRoleRepository;
import com.simonvonxcvii.turing.service.IRoleService;
import com.simonvonxcvii.turing.utils.UserUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements IRoleService {

    private final RolePermissionRepository rolePermissionRepository;
    private final OrganizationBusinessRepository organizationBusinessRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(RoleDTO dto) {
        Role role;
        // 新增
        if (dto.getId() == null) {
            role = new Role();
        }
        // 修改
        else {
            role = roleRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, role);
        roleRepository.save(role);

        // 更新角色权限表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        rolePermissionRepository.delete((root, _, _) ->
                root.get(RolePermission.ROLE_ID).in(dto.getId()));
        List<RolePermission> rolePermissionList = new LinkedList<>();
        dto.getPermissionIdList()
                .forEach(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(role.getId());
                    rolePermission.setPermissionId(permissionId);
                    rolePermissionList.add(rolePermission);
                });
        rolePermissionRepository.saveAll(rolePermissionList);
    }

    @Override
    public Page<RoleDTO> selectPage(RoleDTO dto) {
        return roleRepository.findAll((root, query, criteriaBuilder) -> {
                            List<Predicate> predicateList = new LinkedList<>();
                            if (StringUtils.hasText(dto.getName())) {
                                Predicate name = criteriaBuilder.like(root.get(Role.NAME),
                                        "%" + dto.getName() + "%", '/');
                                predicateList.add(name);
                            }
                            if (StringUtils.hasText(dto.getAuthority())) {
                                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get(Role.AUTHORITY)),
                                        "%" + dto.getAuthority().toLowerCase() + "%", '/');
                                predicateList.add(code);
                            }
                            if (StringUtils.hasText(dto.getDescription())) {
                                Predicate description = criteriaBuilder.like(root.get(Role.DESCRIPTION),
                                        "%" + dto.getDescription() + "%", '/');
                                predicateList.add(description);
                            }
                            assert query != null;
                            return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                        },
                        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
                        PageRequest.of(dto.getNumber() - 1, dto.getSize()))
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                });
    }

    @Override
    public List<RoleDTO> selectList(RoleDTO dto) {
        return roleRepository.findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicateList = new LinkedList<>();
                    if (StringUtils.hasText(dto.getName())) {
                        Predicate name = criteriaBuilder.like(root.get(Role.NAME),
                                "%" + dto.getName() + "%", '/');
                        predicateList.add(name);
                    }
                    if (StringUtils.hasText(dto.getAuthority())) {
                        Predicate authority = criteriaBuilder.like(criteriaBuilder.lower(root.get(Role.AUTHORITY)),
                                "%" + dto.getAuthority().toLowerCase() + "%", '/');
                        predicateList.add(authority);
                    }
                    if (StringUtils.hasText(dto.getDescription())) {
                        Predicate description = criteriaBuilder.like(root.get(Role.DESCRIPTION),
                                "%" + dto.getDescription() + "%", '/');
                        predicateList.add(description);
                    }
                    assert query != null;
                    return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                })
                .stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                })
                .toList();
    }

    @Override
    public RoleDTO selectById(Integer id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> BizRuntimeException.from("没有查询到该角色"));
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        // 查询该角色具有的权限
        List<Integer> permissionIdList = rolePermissionRepository
                .findAll((root, _, _) ->
                        root.get(RolePermission.ROLE_ID).in(role.getId()))
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
        roleDTO.setPermissionIdList(permissionIdList);
        return roleDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        boolean exists = userRoleRepository.exists((root, _, _) ->
                root.get(UserRole.ROLE_ID).in(id));
        if (exists) {
            throw BizRuntimeException.from("该角色已关联用户");
        }
        // 删除角色-权限关联数据
        rolePermissionRepository.delete((root, _, _) ->
                root.get(RolePermission.ROLE_ID).in(id));
        roleRepository.deleteById(id);
    }

    /**
     * 查询单个单位的申请业务审核已通过的对应的角色
     *
     * @author Simon Von
     */
    public List<RoleDTO> selectListForBusinessOrg() {
        // 获取所有已通过的业务申请
        List<OrganizationBusiness> organizationBusinessList = organizationBusinessRepository.findAll(
                (root, _, criteriaBuilder) ->
                        criteriaBuilder.and(root.get(OrganizationBusiness.ORG_ID).in(UserUtils.getOrgId()),
                                root.get(OrganizationBusiness.STATE).in(OrganizationBusinessStateEnum.PASSES.getDesc())));
        if (organizationBusinessList.isEmpty()) {
            return List.of();
        }

        List<RoleDTO> roleDTOList = new LinkedList<>();
        organizationBusinessList.forEach(organizationBusiness -> {
            if (organizationBusiness.getLink() != null) {
                String[] links = StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink());
                for (String link : links) {
                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum ->
                            roleRepository.findAll((root, _, _) ->
                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
                                    .forEach(role -> {
                                        RoleDTO roleDTO = new RoleDTO();
                                        roleDTO.setId(role.getId());
                                        roleDTO.setName(role.getName());
                                        roleDTOList.add(roleDTO);
                                    }));
                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum ->
                            roleRepository.findAll((root, _, _) ->
                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
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
                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(type).ifPresent(anEnum ->
                            roleRepository.findAll((root, _, _) ->
                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
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
     * @author Simon Von
     */
    @Override
    public List<RoleDTO> selectListForAdministrativeOrg() {
        Collection<? extends GrantedAuthority> authorities = UserUtils.getAuthorities();
        assert authorities != null;
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_PROVINCE_GOV"))) {
            return roleRepository.findAll((root, _, _) ->
                            root.get(Role.AUTHORITY).in("STAFF_PROVINCE_GOV"))
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_CITY_GOV"))) {
            return roleRepository.findAll((root, _, _) ->
                            root.get(Role.AUTHORITY).in("STAFF_CITY_GOV"))
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_DISTRICT_GOV"))) {
            return roleRepository.findAll((root, _, _) ->
                            root.get(Role.AUTHORITY).in("STAFF_DISTRICT_GOV"))
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        }
        return List.of();
    }

}
