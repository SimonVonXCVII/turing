package com.simonvonxcvii.turing.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuMetaDTO {

    /**
     * 菜单名称
     */
    public String title;

    /**
     * 菜单图标
     */
    public String icon;

    /**
     * 是否隐藏菜单
     */
    public boolean hideMenu;

}
