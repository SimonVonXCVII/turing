package com.shiminfxcvii.turing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.model.cmd.RoleCmd;
import com.shiminfxcvii.turing.model.dto.RoleDTO;
import com.shiminfxcvii.turing.model.query.RoleQuery;
import com.shiminfxcvii.turing.service.IRoleService;
import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author ShiminFXCVII
 * @author JiangEEZzz
 * @since 2022-12-22 16:22:50
 */
@Tag(name = "RoleController", description = "角色表 前端控制器")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final IRoleService service;

    public RoleController(IRoleService service) {
        this.service = service;
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public Result<IPage<RoleDTO>> selectPage(@RequestBody RoleQuery query) {
        return Result.ok(service.selectPage(query));
    }

    @Operation(summary = "列表查询")
    @PostMapping("/selectList")
    public Result<List<RoleDTO>> selectList(@RequestBody RoleQuery query) {
        return Result.ok(service.selectList(query));
    }

    @Operation(summary = "根据角色 id 获取单个角色")
    @GetMapping("/selectOneById")
    public Result<RoleDTO> selectOneById(String id) {
        return Result.ok(service.selectOneById(id));
    }

    @Operation(summary = "新增")
    @PostMapping("/insert")
    public Result<Object> insert(@RequestBody @Validated RoleCmd cmd) {
        service.insert(cmd);
        return Result.ok();
    }

    @Operation(summary = "更新")
    @PutMapping("/update")
    public Result<Object> update(@RequestBody @Validated(Update.class) RoleCmd cmd) {
        service.update(cmd);
        return Result.ok();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable String id) {
        service.deleteById(id);
        return Result.ok();
    }

    @Operation(summary = "查询业务单位管理员的能分配给本单位其他用户的角色")
    @GetMapping("/selectListForBusinessOrg")
    public Result<List<RoleDTO>> selectListForBusinessOrg() {
        return Result.ok(service.selectListForBusinessOrg());
    }

    @Operation(summary = "根据当前登录用户查询行政单位工作人员角色")
    @GetMapping("/selectListForAdministrativeOrg")
    public Result<List<RoleDTO>> selectListForAdministrativeOrg() {
        return Result.ok(service.selectListForAdministrativeOrg());
    }

}