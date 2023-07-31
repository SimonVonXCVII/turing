package com.shiminfxcvii.turing.controller;

import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.model.cmd.MenuCmd;
import com.shiminfxcvii.turing.model.dto.MenuDTO;
import com.shiminfxcvii.turing.model.query.MenuQuery;
import com.shiminfxcvii.turing.service.IMenuService;
import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Operation(summary = "获取菜单集合")
    @PostMapping("/selectList")
    public Result<List<MenuDTO>> selectList(@RequestBody MenuQuery query) {
        return Result.ok(service.selectList(query));
    }

    @Operation(summary = "添加")
    @PostMapping("/insert")
    public Result<Object> insert(@RequestBody @Validated MenuCmd cmd) {
        service.insert(cmd);
        return Result.ok();
    }

    @Operation(summary = "更新")
    @PutMapping("/update")
    public Result<Object> update(@RequestBody @Validated(Update.class) MenuCmd cmd) {
        service.update(cmd);
        return Result.ok();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/deleteById")
    public Result<Object> deleteById(@RequestBody Map<String, String> map) {
        service.deleteById(map.get("id"));
        return Result.ok();
    }

}