package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.MenuDTO;
import com.simonvonxcvii.turing.service.IMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-26 19:56:04
 */
@Tag(name = "MenuController", description = "菜单表 前端控制器")
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    private final IMenuService service;

    public MenuController(IMenuService service) {
        this.service = service;
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated MenuDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "查询菜单名称是否存在")
    @GetMapping("/name-exists")
    public Result<Boolean> nameExists(String name) {
        return Result.ok(service.nameExists(name));
    }

    @Operation(summary = "查询路由地址是否存在")
    @GetMapping("/path-exists")
    public Result<Object> pathExists(String path) {
        return Result.ok(service.pathExists(path));
    }

    @Operation(summary = "条件查询")
    @GetMapping("/list")
    public Result<List<MenuDTO>> selectBy() {
        return Result.ok(service.selectBy());
    }

    @Operation(summary = "根据主键 id 逻辑删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return Result.ok();
    }

}
