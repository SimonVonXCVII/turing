package com.simonvonxcvii.turing.controller

import com.simonvonxcvii.turing.common.result.Result
import com.simonvonxcvii.turing.model.dto.MenuDTO
import com.simonvonxcvii.turing.model.dto.UserDTO
import com.simonvonxcvii.turing.service.LoginService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

/**
 * 登录 前端控制器
 *
 * @author Simon Von
 * @since 12/16/2022 4:07 PM
 */
@RequestMapping("/api/login")
@RestController
@Tag(name = "LoginController", description = "登录 前端控制器")
class LoginController(private val service: LoginService) {
    @Operation(summary = "获取登录验证码")
    @GetMapping("/getCaptcha")
    @Throws(IOException::class)
    fun getCaptcha(request: HttpServletRequest, response: HttpServletResponse) {
        service.getCaptcha(request, response)
    }

    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户登录成功后所需要的信息")
    fun userInfo(): Result<UserDTO> {
        return Result.ok(service.getUserInfo())
    }

    @GetMapping("/getMenuList")
    @Operation(summary = "获取用户登录成功后的菜单集合")
    fun menuList(): Result<List<MenuDTO>> {
        return Result.ok(service.getMenuList())
    }
}
