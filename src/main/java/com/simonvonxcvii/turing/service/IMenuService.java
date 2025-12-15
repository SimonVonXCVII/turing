package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.MenuDTO;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-26 18:25:51
 */
public interface IMenuService {

    /**
     * 单个新增或修改
     */
    void insertOrUpdate(MenuDTO dto);

    /**
     * 查询名称是否存在
     *
     * @param name 菜单名称
     * @return true - 存在，false - 不存在
     * @author Simon Von
     * @since 12/15/25 9:29 AM
     */
    Boolean nameExists(String name);

    /**
     * 查询路径是否存在
     *
     * @param path 路由地址
     * @return true - 存在，false - 不存在
     * @author Simon Von
     * @since 12/15/25 9:29 AM
     */
    Boolean pathExists(String path);

    /**
     * 条件查询
     */
    List<MenuDTO> selectBy();

    /**
     * 根据主键 id 逻辑删除
     */
    void deleteById(Integer id);

}
