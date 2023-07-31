package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.benmanes.caffeine.cache.Cache;
import com.shiminfxcvii.turing.entity.Role;
import com.shiminfxcvii.turing.model.cmd.RoleCmd;
import com.shiminfxcvii.turing.model.dto.RoleDTO;
import com.shiminfxcvii.turing.model.query.RoleQuery;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
public interface IRoleService extends IService<Role> {

    /**
     * 分页查询
     */
    IPage<RoleDTO> selectPage(RoleQuery query);

    /**
     * 列表查询
     */
    List<RoleDTO> selectList(RoleQuery query);

    /**
     * 根据角色 id 获取单个角色
     */
    RoleDTO selectOneById(String id);

    /**
     * 添加
     */
    void insert(RoleCmd cmd);

    /**
     * 更新
     */
    void update(RoleCmd cmd);

    /**
     * 删除
     */
    void deleteById(String roleId);

    void createCache(Cache<String, String> roleCache);

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