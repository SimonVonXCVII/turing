package com.shiminfxcvii.turing.controller;

import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.service.IMenuService;
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
 * @author ShiminFXCVII
 * @since 2022-12-26 19:56:04
 */
@Tag(name = "MenuController", description = "菜单表 前端控制器")
@RestController
@RequestMapping("/api/menu")
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

    @Operation(summary = "获取菜单集合")
    @PostMapping("/selectList")
    public Result<List<MenuDTO>> selectList(@RequestBody MenuDTO dto) {
        return Result.ok(service.selectList(dto));
    }

    @Operation(summary = "根据主键 id 逻辑删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable String id) {
        service.deleteById(id);
        return Result.ok();
    }

}
