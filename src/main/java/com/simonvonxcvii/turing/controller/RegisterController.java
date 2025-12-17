package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.RegisterDTO;
import com.simonvonxcvii.turing.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册 前端控制器
 *
 * @author Simon Von
 * @since 2023/4/12 22:22
 */
@Tag(name = "RegisterController", description = "注册 前端控制器")
@RequestMapping("/api/register")
@RestController
public class RegisterController {

    private final RegisterService service;

    public RegisterController(RegisterService service) {
        this.service = service;
    }

    @Operation(summary = "注册")
    @PostMapping
    public ResponseEntity<Result<Object>> register(@RequestBody @Valid RegisterDTO dto) {
        service.register(dto);
        return ResponseEntity.ok(Result.ok());
    }

}
