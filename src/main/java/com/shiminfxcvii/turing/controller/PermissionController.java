package com.shiminfxcvii.turing.controller;

import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.model.cmd.PermissionCmd;
import com.shiminfxcvii.turing.model.query.PermissionQuery;
import com.shiminfxcvii.turing.service.IPermissionService;
import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author ShiminFXCVII
 * @author JiangEEZzz
 * @since 2023-01-04 17:22:49
 */
@Tag(name = "PermissionController", description = "权限表 前端控制器")
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    private final IPermissionService service;

    public PermissionController(IPermissionService service) {
        this.service = service;
    }

    @Operation(summary = "获取所有的权限列表")
    @PostMapping("/selectList")
    public Result<Object> selectList(@RequestBody PermissionQuery query) {
        return Result.ok(service.selectList(query));
    }

    @Operation(summary = "插入")
    @PostMapping("/insert")
    public Result<Object> insert(@RequestBody @Validated PermissionCmd cmd) {
        service.insert(cmd);
        return Result.ok();
    }

    @Operation(summary = "更新")
    @PutMapping("/update")
    public Result<Object> update(@RequestBody @Validated(Update.class) PermissionCmd cmd) {
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