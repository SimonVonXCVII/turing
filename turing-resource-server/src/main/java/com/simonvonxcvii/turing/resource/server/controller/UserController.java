package com.simonvonxcvii.turing.resource.server.controller;

import com.simonvonxcvii.turing.resource.server.common.result.Result;
import com.simonvonxcvii.turing.resource.server.model.dto.UserDTO;
import com.simonvonxcvii.turing.resource.server.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService service;

    public UserController(IUserService service) {
        this.service = service;
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public ResponseEntity<Result<UserDTO>> info() {
        return ResponseEntity.ok(Result.ok(service.info()));
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public ResponseEntity<Result<Object>> insertOrUpdate(@RequestBody @Validated UserDTO dto) {
        service.insertOrUpdate(dto);
        return ResponseEntity.ok(Result.ok());
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public ResponseEntity<Result<Page<UserDTO>>> selectPage(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(Result.ok(service.selectPage(dto)));
    }

    @Operation(summary = "根据用户 id 逻辑删除用户")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Result<Object>> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.ok(Result.ok());
    }

}
