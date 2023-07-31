package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.Permission;
import com.shiminfxcvii.turing.model.cmd.PermissionCmd;
import com.shiminfxcvii.turing.model.dto.PermissionDTO;
import com.shiminfxcvii.turing.model.query.PermissionQuery;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:49
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 查询所有父级子级权限集合
     *
     * @return 所有父级子级权限集合
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    List<PermissionDTO> selectList(PermissionQuery query);

    /**
     * 插入
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    void insert(PermissionCmd cmd);

    /**
     * 更新
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    void update(PermissionCmd cmd);

    /**
     * 删除
     *
     * @author ShiminFXCVII
     * @since 3/4/2023 9:28 PM
     */
    void deleteById(String id);

}