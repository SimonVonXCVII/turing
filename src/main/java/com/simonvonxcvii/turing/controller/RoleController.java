package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.RoleDTO;
import com.simonvonxcvii.turing.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @author JiangEEZzz
 * @since 2022-12-22 16:22:50
 */
@RestController
@RequestMapping("/api/role")
@Tag(name = "RoleController", description = "角色表 前端控制器")
public class RoleController {

    private final IRoleService service;

    public RoleController(IRoleService service) {
        this.service = service;
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated RoleDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public Result<Page<RoleDTO>> selectPage(@RequestBody RoleDTO dto) {
        return Result.ok(service.selectPage(dto));
    }

    @Operation(summary = "列表查询")
    @PostMapping("/selectList")
    public Result<List<RoleDTO>> selectList(@RequestBody RoleDTO dto) {
        return Result.ok(service.selectList(dto));
    }

    @Operation(summary = "根据角色 id 获取单个角色")
    @GetMapping("/selectById")
    public Result<RoleDTO> selectById(Integer id) {
        return Result.ok(service.selectById(id));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable Integer id) {
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
