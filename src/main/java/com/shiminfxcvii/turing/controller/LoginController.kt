package com.shiminfxcvii.turing.controller;

import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.dto.UserInfoDTO;
import com.shiminfxcvii.turing.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 登录 前端控制器
 *
 * @author ShiminFXCVII
 * @since 12/16/2022 4:07 PM
 */
@Tag(name = "LoginController", description = "登录 前端控制器")
@RequestMapping("/api/login")
@RestController
public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }

    @Operation(summary = "获取登录验证码")
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.getCaptcha(request, response);
    }

    @Operation(summary = "获取用户登录成功后所需要的信息")
    @GetMapping("/getUserInfo")
    public Result<UserInfoDTO> getUserInfo() {
        return Result.ok(service.getUserInfo());
    }

    @Operation(summary = "获取用户登录成功后的菜单集合")
    @GetMapping("/getMenuList")
    public Result<List<MenuDTO>> getMenuList() {
        return Result.ok(service.getMenuList());
    }

}