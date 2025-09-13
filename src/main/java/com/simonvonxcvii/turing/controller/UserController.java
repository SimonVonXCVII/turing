package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.UserDTO;
import com.simonvonxcvii.turing.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
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

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated UserDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public Result<Page<UserDTO>> selectPage(@RequestBody UserDTO dto) {
        return Result.ok(service.selectPage(dto));
    }

    @Operation(summary = "根据用户 id 逻辑删除用户")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return Result.ok();
    }

}
