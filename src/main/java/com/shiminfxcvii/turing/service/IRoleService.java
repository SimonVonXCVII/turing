package com.shiminfxcvii.turing.service;

import com.shiminfxcvii.turing.model.dto.RoleDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
public interface IRoleService {

    /**
     * 单个新增或修改
     */
    void insertOrUpdate(RoleDTO dto);

    /**
     * 分页查询
     */
    Page<RoleDTO> selectPage(RoleDTO dto);

    /**
     * 列表查询
     */
    List<RoleDTO> selectList(RoleDTO dto);

    /**
     * 根据角色 id 获取单个角色
     */
    RoleDTO selectById(String id);

    /**
     * 删除
     */
    void deleteById(String roleId);

    /**
     * 查询业务单位管理员的能分配给本单位其他用户的角色
     *
     * @author ShiminFXCVII
     */
    List<RoleDTO> selectListForBusinessOrg();

    /**
     * 根据当前登录用户查询行政单位工作人员角色
     *
     * @author ShiminFXCVII
     */
    List<RoleDTO> selectListForAdministrativeOrg();

}
