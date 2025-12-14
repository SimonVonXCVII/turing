package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.OrganizationBusiness;
import com.simonvonxcvii.turing.entity.Role;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.repository.jpa.*;
import com.simonvonxcvii.turing.service.IRoleService;
import com.simonvonxcvii.turing.utils.UserUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.UpdateSpecification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final OrganizationBusinessJpaRepository organizationBusinessJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

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
            role = roleJpaRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, role);
        roleJpaRepository.save(role);

        // 更新角色权限表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        DeleteSpecification<RolePermission> spec =
                (root, delete, builder) -> {
                    Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), dto.getId());
                    return delete.where(predicate).getRestriction();
                };
        rolePermissionJpaRepository.delete(spec);
        List<RolePermission> rolePermissionList = new LinkedList<>();
        dto.getPermissions()
                .forEach(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(role.getId());
                    rolePermission.setPermissionId(permissionId);
                    rolePermissionList.add(rolePermission);
                });
        rolePermissionJpaRepository.saveAll(rolePermissionList);
    }

    @Override
    public Page<RoleDTO> list(RoleDTO dto) {
        Specification<Role> spec = (root, query, builder) -> {
            List<Predicate> predicateList = new LinkedList<>();
            if (StringUtils.hasText(dto.getName())) {
                Predicate name = builder.like(root.get(Role.NAME), "%" + dto.getName() + "%", '/');
                predicateList.add(name);
            }
            if (StringUtils.hasText(dto.getAuthority())) {
                Predicate code = builder.like(builder.lower(root.get(Role.AUTHORITY)),
                        "%" + dto.getAuthority().toLowerCase() + "%", '/');
                predicateList.add(code);
            }
            if (StringUtils.hasText(dto.getRemark())) {
                Predicate remark = builder.like(root.get(Role.REMARK),
                        "%" + dto.getRemark() + "%", '/');
                predicateList.add(remark);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).orderBy(builder.asc(root.get(Role.ID))).getRestriction();
        };
        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        return roleJpaRepository.findAll(spec, pageRequest)
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    PredicateSpecification<RolePermission> ps = (from, criteriaBuilder) ->
                            criteriaBuilder.equal(from.get(RolePermission.ROLE_ID), role.getId());
                    Set<Integer> rolePermissionIdSet = rolePermissionJpaRepository.findAll(ps)
                            .stream()
                            .map(RolePermission::getPermissionId)
                            .collect(Collectors.toSet());
                    roleDTO.setPermissions(rolePermissionIdSet);

                    // todo 如何只查询/只返回 RolePermission.PERMISSION_ID 这一列的值？在 RolePermissionJpaRepository 中写个接口？
//                    Specification<RolePermission> spec2 = (root, query, builder) -> {
//                        Predicate id = builder.equal(root.get(RolePermission.ROLE_ID), role.getId());
//                        return query.where(id).select(root.get(RolePermission.PERMISSION_ID)).getRestriction();
//                    };
//                    List<RolePermission> all = rolePermissionJpaRepository.findAll(spec2);
//                    Specification<Permission> spec22 = (root, query, builder) -> {
//                        Predicate id = builder.equal(root.get(Permission.ID), 1);
// //                        return query.where(id).select(root.get(Permission.ID)).getRestriction();
// //                        return query.where(id).select(query.from(Permission.class).get(Permission.ID)).getRestriction();
//                        return query.select(root.get(Permission.ID)).where(id).getRestriction();
//                    };
//                    List<Permission> all2 = permissionJpaRepository.findAll(spec22);

                    return roleDTO;
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Integer id, RoleDTO dto) {
        // 保存 Role
        UpdateSpecification<Role> us = (root, update, builder) -> {
            Predicate idPredicate = builder.equal(root.get(Role.ID), id);
            // 状态修改
            if (!StringUtils.hasText(dto.getName())) {
                update.set(root.get(Role.STATUS), dto.getStatus());
            }
            // 单个修改
            else {
                update.set(root.get(Role.NAME), dto.getName())
                        .set(root.get(Role.AUTHORITY), dto.getName())
                        .set(root.get(Role.STATUS), dto.getStatus())
                        .set(root.get(Role.REMARK), dto.getRemark());
            }
            return update.where(idPredicate).getRestriction();
        };
        long updated = roleJpaRepository.update(us);
        if (updated == 0) {
            throw new RuntimeException("数据修改失败，无法查找到该数据");
        }

        // 保存 RolePermission
        if (!CollectionUtils.isEmpty(dto.getPermissions())) {
            // TODO 可以优化成只添加需要添加的，只删除需要删除的
            DeleteSpecification<RolePermission> deleteSpecification =
                    (root, query, builder) -> {
                        Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), id);
                        return query.where(predicate).getRestriction();
                    };
            rolePermissionJpaRepository.delete(deleteSpecification);
            List<RolePermission> rolePermissionList = new LinkedList<>();
            dto.getPermissions().forEach(permissionId -> {
                RolePermission rp = new RolePermission();
                rp.setRoleId(id);
                rp.setPermissionId(permissionId);
                rolePermissionList.add(rp);
            });
            rolePermissionJpaRepository.saveAll(rolePermissionList);
        }
    }

    @Override
    public List<RoleDTO> selectList(RoleDTO dto) {
        Specification<Role> spec = (root, query, builder) -> {
            List<Predicate> predicateList = new LinkedList<>();
            if (StringUtils.hasText(dto.getName())) {
                Predicate name = builder.like(root.get(Role.NAME), "%" + dto.getName() + "%", '/');
                predicateList.add(name);
            }
            if (StringUtils.hasText(dto.getAuthority())) {
                Predicate authority = builder.like(builder.lower(root.get(Role.AUTHORITY)),
                        "%" + dto.getAuthority().toLowerCase() + "%", '/');
                predicateList.add(authority);
            }
            if (StringUtils.hasText(dto.getRemark())) {
                Predicate description = builder.like(root.get(Role.REMARK),
                        "%" + dto.getRemark() + "%", '/');
                predicateList.add(description);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).orderBy(builder.asc(root.get(Role.ID))).getRestriction();
        };
        return roleJpaRepository.findAll(spec)
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
        Role role = roleJpaRepository.findById(id).orElseThrow(() -> new RuntimeException("没有查询到该角色"));
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        // 查询该角色具有的权限
        Specification<RolePermission> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), role.getId());
            return query.where(predicate).orderBy(builder.asc(root.get(RolePermission.ID))).getRestriction();
        };
        Set<Integer> permissionIdList = rolePermissionJpaRepository.findAll(spec)
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());
        roleDTO.setPermissions(permissionIdList);
        return roleDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        Specification<UserRole> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(UserRole.ROLE_ID), id);
            return query.where(predicate).getRestriction();
        };
        boolean exists = userRoleJpaRepository.exists(spec);
        if (exists) {
            throw new RuntimeException("该角色已关联用户");
        }
        // 删除角色-权限关联数据
        DeleteSpecification<RolePermission> deleteSpecification =
                (root, delete, builder) -> {
                    Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), id);
                    return delete.where(predicate).getRestriction();
                };
        rolePermissionJpaRepository.delete(deleteSpecification);
        roleJpaRepository.deleteById(id);
    }

    /**
     * 查询单个单位的申请业务审核已通过的对应的角色
     *
     * @author Simon Von
     */
    public List<RoleDTO> selectListForBusinessOrg() {
        // 获取所有已通过的业务申请
        Specification<OrganizationBusiness> spec = (root, query, builder) -> {
            Predicate orgId = builder.equal(root.get(OrganizationBusiness.ORG_ID), UserUtils.getOrgId());
            Predicate state = builder.equal(root.get(OrganizationBusiness.STATE), OrganizationBusinessStateEnum.PASSES);
            Predicate predicate = builder.and(orgId, state);
            return query.where(predicate).orderBy(builder.asc(root.get(OrganizationBusiness.ID))).getRestriction();
        };
        List<OrganizationBusiness> organizationBusinessList = organizationBusinessJpaRepository.findAll(spec);
        if (organizationBusinessList.isEmpty()) {
            return List.of();
        }

        List<RoleDTO> roleDTOList = new LinkedList<>();
        organizationBusinessList.forEach(organizationBusiness -> {
            if (organizationBusiness.getLink() != null) {
                // TODO 晚些时候修改！
//                String[] links = StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink());
//                for (String link : links) {
//                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum ->
//                            roleJpaRepository.findAll((root, _, _) ->
//                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
//                                    .forEach(role -> {
//                                        RoleDTO roleDTO = new RoleDTO();
//                                        roleDTO.setId(role.getId());
//                                        roleDTO.setName(role.getName());
//                                        roleDTOList.add(roleDTO);
//                                    }));
//                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum ->
//                            roleJpaRepository.findAll((root, _, _) ->
//                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
//                                    .forEach(role -> {
//                                        RoleDTO roleDTO = new RoleDTO();
//                                        roleDTO.setId(role.getId());
//                                        roleDTO.setName(role.getName());
//                                        roleDTOList.add(roleDTO);
//                                    }));
//                }
            }
            if (organizationBusiness.getType() != null) {
                // TODO 晚些时候修改！
//                String[] types = StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType());
//                for (String type : types) {
//                    OrganizationBusinessBusinessLinksEnum.getEnumByDesc(type).ifPresent(anEnum ->
//                            roleJpaRepository.findAll((root, _, _) ->
//                                            root.get(Role.AUTHORITY).in("STAFF_" + anEnum.name()))
//                                    .forEach(role -> {
//                                        RoleDTO roleDTO = new RoleDTO();
//                                        roleDTO.setId(role.getId());
//                                        roleDTO.setName(role.getName());
//                                        roleDTOList.add(roleDTO);
//                                    }));
//                }
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
            Specification<Role> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(Role.AUTHORITY), "STAFF_PROVINCE_GOV");
                return query.where(predicate).orderBy(builder.asc(root.get(Role.ID))).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_CITY_GOV"))) {
            Specification<Role> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(Role.AUTHORITY), "STAFF_CITY_GOV");
                return query.where(predicate).orderBy(builder.asc(root.get(Role.ID))).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_DISTRICT_GOV"))) {
            Specification<Role> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(Role.AUTHORITY), "STAFF_DISTRICT_GOV");
                return query.where(predicate).orderBy(builder.asc(root.get(Role.ID))).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        }
        return List.of();
    }

}
