package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiminfxcvii.turing.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-26 19:56:04
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 更新菜单
     * 防止涉及关键字报错 @InterceptorIgnore(blockAttack = "1")
     *
     * @author ShiminFXCVII
     * @since 3/8/2023 3:26 PM
     */
    @InterceptorIgnore(blockAttack = "1")
    void updateOneById(@Param("menu") Menu menu);

}