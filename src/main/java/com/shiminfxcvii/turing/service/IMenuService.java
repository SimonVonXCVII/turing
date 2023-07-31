package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.Menu;
import com.shiminfxcvii.turing.model.cmd.MenuCmd;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.query.MenuQuery;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-26 18:25:51
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 获取菜单集合
     */
    List<MenuDTO> selectList(MenuQuery query);

    /**
     * 添加
     */
    void insert(MenuCmd cmd);

    /**
     * 更新
     */
    void update(MenuCmd cmd);

    /**
     * 删除
     */
    void deleteById(String id);

}