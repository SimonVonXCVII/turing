package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.Menu;
import com.simonvonxcvii.turing.entity.Permission;
import com.simonvonxcvii.turing.entity.RolePermission;
import com.simonvonxcvii.turing.model.dto.PermissionDTO;
import com.simonvonxcvii.turing.repository.MenuRepository;
import com.simonvonxcvii.turing.repository.PermissionRepository;
import com.simonvonxcvii.turing.repository.RolePermissionRepository;
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

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final MenuRepository menuRepository;

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
            throw BizRuntimeException.from("父级权限的排序编号必须是一百的整数倍");
        }
        Permission permission;
        // 新增
        if (dto.getId() == null) {
            permission = new Permission();
        }
        // 修改
        else {
            permission = permissionRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, permission);
        permissionRepository.save(permission);
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
        List<Permission> permissionList = permissionRepository.findAll(Sort.by(Permission.SORT));
        // 按条件过滤
        List<Permission> permissionList1 = permissionList.stream()// TODO use anyMatch
                .filter(permission -> !StringUtils.hasText(dto.getName()) ||
                        (permission.getName().contains(dto.getName()) || dto.getName().contains(permission.getName())))
                .filter(permission -> !StringUtils.hasText(dto.getCode()) ||
                        (permission.getCode().contains(dto.getCode()) || dto.getCode().contains(permission.getCode())))
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
     * 根据主键 id 逻辑删除
     *
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        boolean exists = rolePermissionRepository.exists((root, _, _) ->
                root.get(RolePermission.PERMISSION_ID).in(id));
        if (exists) {
            throw BizRuntimeException.from("该权限已关联角色");
        }
        exists = menuRepository.exists((root, _, _) -> root.get(Menu.PERMISSION_ID).in(id));
        if (exists) {
            throw BizRuntimeException.from("该权限已关联菜单");
        }
        permissionRepository.deleteById(id);
    }
}
