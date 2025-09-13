package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.OrganizationDTO;
import com.simonvonxcvii.turing.service.IOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 单位表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@RestController
@RequestMapping("/api/organization")
@Tag(name = "OrganizationController", description = "单位表 前端控制器")
public class OrganizationController {

    private final IOrganizationService service;

    public OrganizationController(IOrganizationService service) {
        this.service = service;
    }

    @Operation(summary = "单个新增或修改")
    @PostMapping("/insertOrUpdate")
    public Result<Object> insertOrUpdate(@RequestBody @Validated OrganizationDTO dto) {
        service.insertOrUpdate(dto);
        return Result.ok();
    }

    @Operation(summary = "分页查询")
    @PostMapping("/selectPage")
    public Result<Page<OrganizationDTO>> selectPage(@RequestBody OrganizationDTO dto) {
        return Result.ok(service.selectPage(dto));
    }

    @Operation(summary = "查询单位 id 和单位名称列表")
    @GetMapping("/selectIdAndNameList")
    public Result<List<OrganizationDTO>> selectIdAndNameList() {
        return Result.ok(service.selectIdAndNameList());
    }

    @Operation(summary = "【测试项目分类管理】菜单【添加测试项目分类】弹窗中的检测实验室和检测质控实验室接口")
    @GetMapping("/selectList")
    public Result<List<OrganizationDTO>> selectList(String name) {
        return Result.ok(service.selectList(name));
    }

    @Operation(summary = "根据 id 逻辑删除单位及其关联的用户和用户的角色")
    @DeleteMapping("/deleteById/{id}")
    public Result<String> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return Result.ok();
    }

}
