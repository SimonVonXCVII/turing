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
     * 新增数据
     */
    void insert(MenuDTO dto);

    /**
     * 查询名称是否存在
     *
     * @param name 菜单名称
     * @param id   菜单主键 id
     * @return true - 存在，false - 不存在
     * @author Simon Von
     * @since 12/15/25 9:29 AM
     */
    Boolean nameExists(String name, Integer id);

    /**
     * 查询路径是否存在
     *
     * @param path 路由地址
     * @param id   菜单主键 id
     * @return true - 存在，false - 不存在
     * @author Simon Von
     * @since 12/15/25 9:29 AM
     */
    Boolean pathExists(String path, Integer id);

    /**
     * 条件查询
     */
    List<MenuDTO> selectBy();

    /**
     * 修改数据
     *
     * @param id  主键 id
     * @param dto 其他数据
     * @author Simon Von
     * @since 12/16/25 5:28 AM
     */
    void updateById(Integer id, MenuDTO dto);

    /**
     * 逻辑删除
     *
     * @param id 主键 id
     */
    void deleteById(Integer id);

}
