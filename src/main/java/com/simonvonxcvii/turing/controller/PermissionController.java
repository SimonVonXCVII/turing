package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.PermissionDTO;
import com.simonvonxcvii.turing.service.IPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2023-01-04 17:22:49
 */
@Tag(name = "PermissionController", description = "权限表 前端控制器")
@RestController
@RequestMapping({"/api/permission", "/api/auth"})
public class PermissionController {

    private final IPermissionService service;

    public PermissionController(IPermissionService service) {
        this.service = service;
    }

    @Operation(summary = "获取权限码")
    @GetMapping("/codes")
    public Result<Set<String>> codes() {
        return Result.ok(service.codes());
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated PermissionDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "获取所有的权限列表")
    @PostMapping("/selectList")
    public Result<Object> selectList(@RequestBody PermissionDTO dto) {
        return Result.ok(service.selectList(dto));
    }

    @Operation(summary = "根据主键 id 逻辑删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return Result.ok();
    }

}
