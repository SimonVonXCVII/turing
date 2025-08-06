package com.simonvonxcvii.turing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色与权限关联记录表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@Tag(name = "RolePermissionController", description = "角色与权限关联记录表 前端控制器")
@RestController
@RequestMapping("/rolePermission")
public class RolePermissionController {
}
