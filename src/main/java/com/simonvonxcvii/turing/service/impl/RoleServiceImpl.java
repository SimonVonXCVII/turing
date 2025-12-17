package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Role;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.repository.jpa.RoleJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.RolePermissionJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private final RoleJpaRepository roleJpaRepository;
    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(RoleDTO dto) {
        // 保存 Role
        Role role = new Role();
        role.setName(dto.getName());
        role.setAuthority(dto.getName());
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
        Specification<Role> ps = Specification.<Role>where((from, builder) -> {
            if (!StringUtils.hasText(dto.getName())) {
                return null;
            }
            return builder.like(from.get(Role.NAME), "%" + dto.getName() + "%");
        }).and((from, criteriaBuilder) -> {
            if (dto.getId() == null) {
                return null;
            }
            return criteriaBuilder.equal(from.get(Role.ID), dto.getId());
        }).and((from, criteriaBuilder) -> {
            if (dto.getStatus() == null) {
                return null;
            }
            return criteriaBuilder.equal(from.get(Role.STATUS), dto.getStatus());
        }).and((from, criteriaBuilder) -> {
            if (!StringUtils.hasText(dto.getRemark())) {
                return null;
            }
            return criteriaBuilder.like(from.get(Role.REMARK), "%" + dto.getRemark() + "%");
        }).and((from, criteriaBuilder) -> {
            if (dto.getStartTime() == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(from.get(Role.CREATED_DATE), dto.getStartTime());
        }).and((from, criteriaBuilder) -> {
            if (dto.getEndTime() == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(from.get(Role.CREATED_DATE), dto.getEndTime());
        });
        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPageSize(), Sort.by(Role.ID));
        // 映射 RoleId 分组为 PermissionIdSet Map
        Map<Integer, Set<Integer>> roleIdToPermissionIdSetMap = rolePermissionJpaRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        RolePermission::getRoleId,
                        Collectors.mapping(RolePermission::getPermissionId, Collectors.toSet())
                ));
        return roleJpaRepository.findAll(ps, pageRequest)
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    BeanUtils.copyProperties(role, roleDTO);
                    Set<Integer> rolePermissionIdSet = roleIdToPermissionIdSetMap.get(role.getId());
                    roleDTO.setPermissions(rolePermissionIdSet);
                    return roleDTO;
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Integer id, RoleDTO dto) {
        // 修改 Role
        roleJpaRepository.findById(id)
                .ifPresent(role -> {
                    // 状态修改与单个修改
                    role.setStatus(dto.getStatus());
                    // 单个修改
                    if (StringUtils.hasText(dto.getName())) {
                        role.setName(dto.getName());
                        role.setAuthority(dto.getName());
                        role.setRemark(dto.getRemark());
                    }
                });

        // 修改 RolePermission
        if (!CollectionUtils.isEmpty(dto.getPermissions())) {
            // TODO 可以优化成只添加需要添加的，只删除需要删除的
            rolePermissionJpaRepository.deleteByRoleId(id);
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
        boolean exists = userRoleJpaRepository.existsByRoleId(id);
        if (exists) {
            throw new RuntimeException("该角色已关联用户");
        }
        // 删除角色-权限关联数据
        rolePermissionJpaRepository.deleteByRoleId(id);
        roleJpaRepository.deleteById(id);
    }

}
