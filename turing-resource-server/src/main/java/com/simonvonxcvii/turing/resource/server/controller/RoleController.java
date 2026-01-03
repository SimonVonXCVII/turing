package com.simonvonxcvii.turing.resource.server.controller;

import com.simonvonxcvii.turing.resource.server.common.result.Result;
import com.simonvonxcvii.turing.resource.server.model.dto.RoleDTO;
import com.simonvonxcvii.turing.resource.server.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@RestController
@RequestMapping("/api/system/role")
@Tag(name = "RoleController", description = "角色表 前端控制器")
public class RoleController {

    private final IRoleService service;

    public RoleController(IRoleService service) {
        this.service = service;
    }

    @Operation(summary = "新增数据")
    @PostMapping
    public ResponseEntity<Result<Object>> insert(@RequestBody @Validated RoleDTO dto) {
        service.insert(dto);
        return ResponseEntity.ok(Result.ok());
    }

    @Operation(summary = "条件查询")
    @PostMapping("/list")
    public ResponseEntity<Result<Page<RoleDTO>>> selectBy(@RequestBody RoleDTO dto) {
        return ResponseEntity.ok(Result.ok(service.selectBy(dto)));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Object>> updateById(@PathVariable @NotNull(message = "主键 id 不能为 null") Integer id,
                                                     @RequestBody RoleDTO dto) {
        service.updateById(id, dto);
        return ResponseEntity.ok(Result.ok());
    }

    @Operation(summary = "逻辑删除")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Object>> deleteById(@PathVariable @NotNull(message = "主键 id 不能为 null") Integer id) {
        service.deleteById(id);
        return ResponseEntity.ok(Result.ok());
    }

}
