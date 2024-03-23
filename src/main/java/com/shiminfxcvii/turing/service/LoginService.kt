package com.shiminfxcvii.turing.service

import com.shiminfxcvii.turing.model.dto.MenuDTO
import com.shiminfxcvii.turing.model.dto.UserDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException

/**
 * 登录 服务类
 *
 * @author ShiminFXCVII
 * @since 12/16/2022 4:07 PM
 */
interface LoginService {
    /**
     * 获取登录验证码
     *
     * @author ShiminFXCVII
     * @since 12/16/2022 4:09 PM
     */
    @Throws(IOException::class)
    fun getCaptcha(request: HttpServletRequest, response: HttpServletResponse)

    /**
     * 获取用户登录成功后所需要的信息
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    fun getUserInfo(): UserDTO

    /**
     * 获取用户登录成功后的菜单集合
     *
     * @author ShiminFXCVII
     * @since 12/17/2022 8:19 PM
     */
    fun getMenuList(): List<MenuDTO>
}
