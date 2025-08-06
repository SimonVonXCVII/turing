package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.model.dto.OrganizationBusinessDTO;
import com.simonvonxcvii.turing.service.IOrganizationBusinessService;
import com.simonvonxcvii.turing.utils.Insert;
import com.simonvonxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 * 单位业务表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@Tag(name = "OrganizationBusinessController", description = "单位业务表 前端控制器")
@RestController
@RequestMapping("/api/organizationBusiness")
public class OrganizationBusinessController {

    private final IOrganizationBusinessService service;

    public OrganizationBusinessController(IOrganizationBusinessService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('TECHNICAL_DEFAULT', 'ADMIN_PROVINCE_GOV', 'STAFF_PROVINCE_GOV', 'ADMIN_CITY_GOV', 'STAFF_CITY_GOV', 'ADMIN_DISTRICT_GOV', 'STAFF_DISTRICT_GOV')")
    @Operation(summary = "单位管理员查询本单位已申请业务或者审核人员查询")
    @PostMapping("/selectPage")
    public Result<Page<OrganizationBusinessDTO>> selectPage(@RequestBody OrganizationBusinessDTO dto) throws IOException {
        return Result.ok(service.selectPage(dto));
    }

    @Operation(summary = "单位管理员在点击编辑前查询单条数据")
    @Parameter(name = "id", description = "主键 id")
    @GetMapping("/getOneById")
    public Result<OrganizationBusinessDTO> getOneById(@NotNull(message = "id 不能为空") String id) throws IOException {
        return Result.ok(service.getOneById(id));
    }

    @PreAuthorize("hasRole('TECHNICAL_DEFAULT')")
    @Operation(summary = "申请业务")
    @PostMapping("/insert")
    public Result<Object> insert(@RequestBody @Validated(Insert.class) OrganizationBusinessDTO dto) throws IOException {
        service.insert(dto);
        return Result.ok();
    }

    @PreAuthorize("hasRole('TECHNICAL_DEFAULT')")
    @Operation(summary = "申请页面更新业务")
    @PutMapping("/applyUpdate")
    public Result<Object> applyUpdate(@RequestBody @Validated(Update.class) OrganizationBusinessDTO dto) throws IOException {
        service.applyUpdate(dto);
        return Result.ok();
    }

    @PreAuthorize("hasAnyRole('ADMIN_PROVINCE_GOV', 'STAFF_PROVINCE_GOV', 'ADMIN_CITY_GOV', 'STAFF_CITY_GOV', 'ADMIN_DISTRICT_GOV', 'STAFF_DISTRICT_GOV')")
    @Operation(summary = "审核页面更新业务")
    @PutMapping("/checkUpdate")
    public Result<Object> checkUpdate(@RequestBody @Validated(Update.class) OrganizationBusinessDTO dto) throws IOException {
        service.checkUpdate(dto);
        return Result.ok();
    }

}
