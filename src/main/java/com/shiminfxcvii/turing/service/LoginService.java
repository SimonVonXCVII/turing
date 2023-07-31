package com.shiminfxcvii.turing.service;

import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.dto.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 登录 服务类
 *
 * @author ShiminFXCVII
 * @since 12/16/2022 4:07 PM
 */
public interface LoginService {

    /**
     * 获取登录验证码
     *
     * @author ShiminFXCVII
     * @since 12/16/2022 4:09 PM
     */
    void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取用户登录成功后所需要的信息
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    UserInfoDTO getUserInfo();

    /**
     * 获取用户登录成功后的菜单集合
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    List<MenuDTO> getMenuList();

}