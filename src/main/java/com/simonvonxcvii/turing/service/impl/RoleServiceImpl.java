package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Role;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.repository.jpa.RoleJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.RolePermissionJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.IRoleService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.UpdateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private final RoleJpaRepository roleJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(RoleDTO dto) {
        // 保存 Role
        Role role = new Role();
        role.setAuthority(dto.getName());
        role.setName(dto.getName());
        role.setRemark(dto.getRemark());
        role.setStatus(dto.getStatus());
        roleJpaRepository.save(role);

        // 保存 RolePermission
        if (!CollectionUtils.isEmpty(dto.getPermissions())) {
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
    }

    @Override
    public Page<RoleDTO> selectBy(RoleDTO dto) {
        Specification<Role> spec = (root, query, builder) -> {
            List<Predicate> predicateList = new LinkedList<>();
            if (StringUtils.hasText(dto.getName())) {
                Predicate name = builder.like(root.get(Role.NAME), "%" + dto.getName() + "%");
                predicateList.add(name);
            }
            if (dto.getId() != null) {
                Predicate id = builder.equal(root.get(Role.ID), dto.getId());
                predicateList.add(id);
            }
            if (dto.getStatus() != null) {
                Predicate status = builder.equal(root.get(Role.STATUS), dto.getStatus());
                predicateList.add(status);
            }
            if (StringUtils.hasText(dto.getRemark())) {
                Predicate remark = builder.like(root.get(Role.REMARK), "%" + dto.getRemark() + "%");
                predicateList.add(remark);
            }
            if (dto.getStartTime() != null) {
                Predicate createdDate = builder.greaterThanOrEqualTo(root.get(Role.CREATED_DATE), dto.getStartTime());
                predicateList.add(createdDate);
            }
            if (dto.getEndTime() != null) {
                Predicate createdDate = builder.lessThanOrEqualTo(root.get(Role.CREATED_DATE), dto.getEndTime());
                predicateList.add(createdDate);
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
    public void updateById(Integer id, RoleDTO dto) {
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
                    (root, delete, builder) -> {
                        Predicate predicate = builder.equal(root.get(RolePermission.ROLE_ID), id);
                        return delete.where(predicate).getRestriction();
                    };
            rolePermissionJpaRepository.delete(deleteSpecification);
            List<RolePermission> rolePermissionList = new LinkedList<>();
            dto.getPermissions()
                    .forEach(permissionId -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(id);
                        rolePermission.setPermissionId(permissionId);
                        rolePermissionList.add(rolePermission);
                    });
            rolePermissionJpaRepository.saveAll(rolePermissionList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        PredicateSpecification<UserRole> spec = (from, criteriaBuilder) ->
                criteriaBuilder.equal(from.get(UserRole.ROLE_ID), id);
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

}
