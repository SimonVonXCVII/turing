package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.PermissionDTO;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-22 16:22:49
 */
public interface IPermissionService {

    /**
     * 单个新增或修改
     *
     * @author SimonVonXCVII
     * @since 3/4/2023 9:28 PM
     */
    void insertOrUpdate(PermissionDTO dto);

    /**
     * 查询所有父级子级权限集合
     *
     * @return 所有父级子级权限集合
     * @author SimonVonXCVII
     * @since 3/4/2023 9:28 PM
     */
    List<PermissionDTO> selectList(PermissionDTO dto);

    /**
     * 根据主键 id 逻辑删除
     *
     * @param id 用户 id
     * @author SimonVonXCVII
     * @since 3/4/2023 9:28 PM
     */
    void deleteById(String id);

}
