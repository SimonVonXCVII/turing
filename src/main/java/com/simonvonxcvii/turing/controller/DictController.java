package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.DictDTO;
import com.simonvonxcvii.turing.service.IDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-30 12:49:40
 */
@Tag(name = "DictController", description = "字典表 前端控制器")
@RestController
@RequestMapping("/api/dict")
public class DictController {

    private final IDictService service;

    public DictController(IDictService service) {
        this.service = service;
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated DictDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public Result<Page<DictDTO>> selectPage(@RequestBody DictDTO dto) {
        return Result.ok(service.selectPage(dto));
    }

    @Operation(summary = "地区及下级地区查询")
    @GetMapping("/getAreaByCode")
    public Result<DictDTO> getAreaByCode(Integer code) {
        return Result.ok(service.getAreaByCode(code));
    }

    @Operation(summary = "根据主键 id 逻辑删除")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteDictByIds(@PathVariable String id) {
        service.deleteById(id);
        return Result.ok();
    }

}
