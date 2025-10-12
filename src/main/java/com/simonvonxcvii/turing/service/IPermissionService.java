package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.PermissionDTO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:49
 */
public interface IPermissionService {

    /**
     * 获取权限码
     * 这个接口用于获取用户的权限码，权限码用于控制用户的权限
     *
     * @return 用户的权限码
     * @author Simon Von
     * @since 10/12/2025 7:56 AM
     */
    Set<String> codes();

    /**
     * 单个新增或修改
     *
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    void insertOrUpdate(PermissionDTO dto);

    /**
     * 查询所有父级子级权限集合
     *
     * @return 所有父级子级权限集合
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    List<PermissionDTO> selectList(PermissionDTO dto);

    /**
     * 根据主键 id 逻辑删除
     *
     * @param id 用户 id
     * @author Simon Von
     * @since 3/4/2023 9:28 PM
     */
    void deleteById(Integer id);

}
