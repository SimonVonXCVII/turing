package com.simonvonxcvii.turing.resource.server.controller;

import com.simonvonxcvii.turing.resource.server.common.result.Result;
import com.simonvonxcvii.turing.resource.server.model.dto.MenuDTO;
import com.simonvonxcvii.turing.resource.server.service.IMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "新增数据")
    @PostMapping
    public ResponseEntity<Result<Object>> insert(@RequestBody @Validated MenuDTO dto) {
        service.insert(dto);
        return ResponseEntity.ok(Result.ok());
    }

    @Operation(summary = "查询菜单名称是否存在")
    @GetMapping("/name-exists")
    public ResponseEntity<Result<Boolean>> nameExists(String name, Integer id) {
        return ResponseEntity.ok(Result.ok(service.nameExists(name, id)));
    }

    @Operation(summary = "查询路由地址是否存在")
    @GetMapping("/path-exists")
    public ResponseEntity<Result<Boolean>> pathExists(String path, Integer id) {
        return ResponseEntity.ok(Result.ok(service.pathExists(path, id)));
    }

    @Operation(summary = "条件查询")
    @GetMapping("/list")
    public ResponseEntity<Result<List<MenuDTO>>> selectBy() {
        return ResponseEntity.ok(Result.ok(service.selectBy()));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Object>> updateById(@PathVariable @NotNull(message = "主键 id 不能为 null") Integer id,
                                                     @RequestBody MenuDTO dto) {
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
