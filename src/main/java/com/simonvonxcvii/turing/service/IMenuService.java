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
     * 条件查询
     */
    List<MenuDTO> selectBy();

    /**
     * 根据主键 id 逻辑删除
     */
    void deleteById(Integer id);

}
