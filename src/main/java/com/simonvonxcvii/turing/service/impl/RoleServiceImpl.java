package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.OrganizationBusiness;
import com.simonvonxcvii.turing.entity.Role;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.repository.jpa.OrganizationBusinessJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.RoleJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.RolePermissionJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.IRoleService;
import com.simonvonxcvii.turing.utils.UserUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
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
            role = roleJpaRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, role);
        roleJpaRepository.save(role);

        // 更新角色权限表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        Specification<RolePermission> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), dto.getId());
            return query.where(predicate).getRestriction();
        };
        rolePermissionJpaRepository.delete(spec);
        List<RolePermission> rolePermissionList = new LinkedList<>();
        dto.getPermissionIdList()
                .forEach(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(role.getId());
                    rolePermission.setPermissionId(permissionId);
                    rolePermissionList.add(rolePermission);
                });
        rolePermissionJpaRepository.saveAll(rolePermissionList);
    }

    @Override
    public Page<RoleDTO> selectPage(RoleDTO dto) {
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
            if (StringUtils.hasText(dto.getDescription())) {
                Predicate description = builder.like(root.get(Role.DESCRIPTION),
                        "%" + dto.getDescription() + "%", '/');
                predicateList.add(description);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).getRestriction();
        };
        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
        PageRequest pageRequest = PageRequest.of(dto.getNumber() - 1, dto.getSize());
        return roleJpaRepository.findAll(spec, pageRequest)
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    return roleDTO;
                });
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
            if (StringUtils.hasText(dto.getDescription())) {
                Predicate description = builder.like(root.get(Role.DESCRIPTION),
                        "%" + dto.getDescription() + "%", '/');
                predicateList.add(description);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).getRestriction();
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
        Role role = roleJpaRepository.findById(id).orElseThrow(() -> BizRuntimeException.from("没有查询到该角色"));
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        // 查询该角色具有的权限
        Specification<RolePermission> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), role.getId());
            return query.where(predicate).getRestriction();
        };
        List<Integer> permissionIdList = rolePermissionJpaRepository.findAll(spec)
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
        roleDTO.setPermissionIdList(permissionIdList);
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
            throw BizRuntimeException.from("该角色已关联用户");
        }
        // 删除角色-权限关联数据
        Specification<RolePermission> spec2 = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), id);
            return query.where(predicate).getRestriction();
        };
        rolePermissionJpaRepository.delete(spec2);
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
            return query.where(predicate).getRestriction();
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
                return query.where(predicate).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_CITY_GOV"))) {
            Specification<Role> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(Role.AUTHORITY), "STAFF_CITY_GOV");
                return query.where(predicate).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_DISTRICT_GOV"))) {
            Specification<Role> spec = (root, query, builder) -> {
                Predicate predicate = builder.equal(root.get(Role.AUTHORITY), "STAFF_DISTRICT_GOV");
                return query.where(predicate).getRestriction();
            };
            return roleJpaRepository.findAll(spec)
                    .stream()
                    .map(role -> new RoleDTO().setId(role.getId()).setName(role.getName()))
                    .toList();
        }
        return List.of();
    }

}
