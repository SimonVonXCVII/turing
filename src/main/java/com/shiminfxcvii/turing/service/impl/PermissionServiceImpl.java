package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Permission;
import com.shiminfxcvii.turing.mapper.PermissionMapper;
import com.shiminfxcvii.turing.model.cmd.PermissionCmd;
import com.shiminfxcvii.turing.model.dto.PermissionDTO;
import com.shiminfxcvii.turing.model.query.PermissionQuery;
import com.shiminfxcvii.turing.service.IPermissionService;
import org.springframework.beans.BeanUtils;
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
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:49
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    /**
     * 查询所有父级子级权限集合
     *
     * @return 所有父级子级权限集合
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    @Override
    public List<PermissionDTO> selectList(PermissionQuery query) {
        // 将两次查询改为提前查询所有数据，减少查询次数，减轻数据库压力
        List<Permission> permissionList = lambdaQuery().orderByAsc(Permission::getSort).list();
        List<Permission> permissionList1 = permissionList.stream()// TODO use anyMatch
                .filter(permission -> !StringUtils.hasText(query.getName()) ||
                        (permission.getName().contains(query.getName()) || query.getName().contains(permission.getName())))
                .filter(permission -> !StringUtils.hasText(query.getCode()) || Objects.equals(permission.getCode(), query.getCode()))
                .filter(permission -> query.getSort() == null || Objects.equals(permission.getSort(), query.getSort()))
                .toList();
        if (permissionList1.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> parentIdList = permissionList1.stream().filter(permission -> permission.getPid() == null).map(Permission::getId).toList();
        List<String> childIdList = permissionList1.stream().filter(permission -> permission.getPid() != null).map(Permission::getId).toList();
        List<String> childPidList = permissionList1.stream().map(Permission::getPid).filter(Objects::nonNull).toList();
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
     * 插入
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional
    public void insert(PermissionCmd cmd) {
        Permission permission = lambdaQuery().eq(Permission::getName, cmd.getName()).one();
        if (permission != null) {
            throw new BizRuntimeException("权限名称重复，请输入其他权限名称");
        }
        permission = lambdaQuery().eq(Permission::getSort, cmd.getSort()).one();
        if (permission != null) {
            Integer sort = 0;
            if (cmd.getPid() != null) {
                permission = getById(cmd.getPid());
                if (permission != null) {
                    sort = permission.getSort();
                    do {
                        permission = lambdaQuery().eq(Permission::getSort, ++sort).one();
                    } while (permission != null);
                }
            } else {
                permission = lambdaQuery().isNull(Permission::getPid).orderByDesc(Permission::getSort).last("LIMIT 1").one();
                if (permission != null) {
                    sort = permission.getSort() + 100;
                }
            }
            throw new BizRuntimeException("排序编号重复，请输入其他排序编号。建议排序编号：" + sort);
        }
        permission = new Permission();
        BeanUtils.copyProperties(cmd, permission);
        save(permission);
    }

    /**
     * 更新
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional
    public void update(PermissionCmd cmd) {
        Permission permission = getById(cmd.getId());
        if (permission == null) {
            throw new BizRuntimeException("没有查询到该权限");
        }
        BeanUtils.copyProperties(cmd, permission);
        updateById(permission);
    }

    /**
     * 删除
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    @Override
    @Transactional
    public void deleteById(String id) {
        Permission permission = getById(id);
        if (permission == null) {
            throw new BizRuntimeException("没有查询到该权限");
        }
        removeById(id);
    }
}