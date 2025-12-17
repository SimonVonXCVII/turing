package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Permission;
import com.simonvonxcvii.turing.model.dto.PermissionDTO;
import com.simonvonxcvii.turing.repository.jpa.PermissionJpaRepository;
import com.simonvonxcvii.turing.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:49
 */
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionJpaRepository permissionJpaRepository;
//    private final RolePermissionJpaRepository rolePermissionJpaRepository;
//    private final MenuJpaRepository menuJpaRepository;

    /**
     * 获取权限码
     * 这个接口用于获取用户的权限码，权限码用于控制用户的权限
     *
     * @return 用户的权限码
     * @author Simon Von
     * @since 10/12/2025 7:56 AM
     */
    @Override
    public Set<String> codes() {
        List<Permission> permissionList = permissionJpaRepository.findAll(Sort.by(Permission.ID));
        return permissionList.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * 单个新增或修改
     *
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(PermissionDTO dto) {
        if (dto.getPid() == null && dto.getSort() % 100 != 0) {
            throw new RuntimeException("父级权限的排序编号必须是一百的整数倍");
        }
        Permission permission;
        // 新增
        if (dto.getId() == null) {
            permission = new Permission();
        }
        // 修改
        else {
            permission = permissionJpaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, permission);
        permissionJpaRepository.save(permission);
    }

    /**
     * 查询所有父级子级权限集合
     *
     * @return 所有父级子级权限集合
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    @Override
    public List<PermissionDTO> selectList(PermissionDTO dto) {
        // 将两次查询改为提前查询所有数据，减少查询次数，减轻数据库压力
        List<Permission> permissionList = permissionJpaRepository.findAll(Sort.by(Permission.SORT));
        // 按条件过滤
        List<Permission> permissionList1 = permissionList.stream()// TODO use anyMatch
                .filter(permission -> !StringUtils.hasText(dto.getName()) ||
                        (permission.getName().contains(dto.getName()) || dto.getName().contains(permission.getName())))
                .filter(permission -> !StringUtils.hasText(dto.getCode()) ||
                        (StringUtils.hasText(permission.getCode()) &&
                                (permission.getCode().contains(dto.getCode()) || dto.getCode().contains(permission.getCode()))))
                .filter(permission -> dto.getSort() == null || Objects.equals(permission.getSort(), dto.getSort()))
                .toList();
        if (permissionList1.isEmpty()) {
            return new ArrayList<>();
        }
        // 收集 id
        List<Integer> parentIdList = permissionList1.stream().filter(permission -> permission.getPid() == null)
                .map(Permission::getId).toList();
        List<Integer> childIdList = permissionList1.stream().filter(permission -> permission.getPid() != null)
                .map(Permission::getId).toList();
        List<Integer> childPidList = permissionList1.stream().map(Permission::getPid).filter(Objects::nonNull).toList();
        permissionList1 = permissionList.stream()
                .filter(permission -> {
                    if (!parentIdList.isEmpty() && parentIdList.contains(permission.getId())) {
                        return true;
                    } else if (!parentIdList.isEmpty() && parentIdList.contains(permission.getPid())) {
                        return true;
                    } else if (!childIdList.isEmpty() && childIdList.contains(permission.getId())) {
                        return true;
                    } else return !childPidList.isEmpty() && childPidList.contains(permission.getId());
                })
                .toList();
        List<Permission> parentList = permissionList1.stream().filter(permission -> permission.getPid() == null).toList();
        List<Permission> childList = permissionList1.stream().filter(permission -> permission.getPid() != null).toList();
        return parentList.stream()
                .map(parent -> {
                    PermissionDTO parentDTO = new PermissionDTO();
                    BeanUtils.copyProperties(parent, parentDTO);
                    childList.stream()
                            .filter(child -> Objects.equals(parent.getId(), child.getPid()))
                            .forEach(child -> {
                                PermissionDTO childDTO = new PermissionDTO();
                                BeanUtils.copyProperties(child, childDTO);
                                parentDTO.getChildren().add(childDTO);
                            });
                    return parentDTO;
                })
                .toList();
    }

    /**
     * 根据主键 id 逻辑删除 todo
     *
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
//        Specification<RolePermission> spec = (root, query, builder) -> {
//            Predicate predicate = builder.equal(root.get(RolePermission.PERMISSION_ID), id);
//            return query.where(predicate).getRestriction();
//        };
//        boolean exists = rolePermissionJpaRepository.exists(spec);
//        if (exists) {
//            throw new RuntimeException("该权限已关联角色");
//        }
//        Specification<Menu> menuSpec = (root, query, builder) -> {
//            Predicate predicate = builder.equal(root.get(Menu.PERMISSION_ID), id);
//            return query.where(predicate).getRestriction();
//        };
//        exists = menuJpaRepository.exists(menuSpec);
//        if (exists) {
//            throw new RuntimeException("该权限已关联菜单");
//        }
//        permissionJpaRepository.deleteById(id);
    }
}
