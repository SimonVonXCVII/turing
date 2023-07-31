package com.shiminfxcvii.turing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.model.dto.OrgDetailDTO;
import com.shiminfxcvii.turing.model.dto.OrganizationDTO;
import com.shiminfxcvii.turing.model.dto.TechOrgSelectDTO;
import com.shiminfxcvii.turing.model.dto.TechOrgUserDTO;
import com.shiminfxcvii.turing.model.query.OrganizationPageQuery;
import com.shiminfxcvii.turing.service.IOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 单位表 前端控制器
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    private final IOrganizationService organizationService;

    public OrganizationController(IOrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Operation(summary = "获取单位分页，目前支持单位名称模糊查询、单位类型查询、单位所在省市县查询")
    @PostMapping("/getOrganizationPage")
    public Result<IPage<OrganizationDTO>> getOrganizationPage(@RequestBody OrganizationPageQuery query) {
        IPage<OrganizationDTO> page = organizationService.getOrganizationPage(query);
        return Result.ok(page);
    }

    @Operation(summary = "根据单位id获取单位详细信息（包括本单位基本信息、单位管理员基本信息、上级单位基本信息）")
    @GetMapping("/getOrgDetailById")
    public Result<OrgDetailDTO> getOrgDetailById(@RequestParam("id") String id) {
        OrgDetailDTO dto = organizationService.getOrgDetailById(id);
        return Result.ok(dto);
    }

    @Operation(summary = "修改单位",
            description = "此处无需再发送请求获取单位的信息，因为分页请求返回的就是完整的单位信息，如果还是需要单独获取的话，发送getOrgById这个请求")
    @PostMapping("/updateOrganization")
    public Result<String> updateOrganization(@RequestBody Organization organization) {
        organizationService.updateOrganization(organization);
        return Result.ok("修改成功");
    }

    @Operation(summary = "根据id获取该单位基本信息")
    @GetMapping("/getOrgById")
    public Result<Organization> getOrgById(@RequestParam("id") String id) {
        return Result.ok(organizationService.getById(id));
    }

    @Operation(summary = "删除单位")
    @GetMapping("/deleteById")
    public Result<String> deleteById(@RequestParam("id") String id) {
        organizationService.deleteById(id);
        return Result.ok("删除单位");
    }

    /**
     * @param level 0表示省级，1表示市级，2表示县级
     * @param code  省/市/县编码
     * @return
     */
    @Operation(summary = "查询通指定行政区划级别的技术牵头单位列表")
    @GetMapping("/getOrgListWithLead")
    public Result<List<TechOrgSelectDTO>> getOrgListWithLead(Integer level, String code) {
        List<TechOrgSelectDTO> list = organizationService.getOrgListWithLead(level, code);
        return Result.ok(list);
    }


    /**
     * @param level 0表示省级，1表示市级，2表示县级
     * @param code  省/市/县编码
     * @param type  1表示布点质控、2表示采样质控、3表示检测质控
     * @return
     */
    @Operation(summary = "查询通过审核了的布点质控(1)、采样质控(2)、检测质控(3)的单位的列表(省级、市级、县级)")
    @GetMapping("/getOrgListWithQc")
    public Result<List<TechOrgSelectDTO>> getOrgListWithQc(Integer level, String code, String type) {
        List<TechOrgSelectDTO> list = organizationService.getOrgListWithQc(level, code, type);
        return Result.ok(list);
    }

    /**
     * @param level 0表示省级，1表示市级，2表示县级
     * @param code  省/市/县编码
     * @param type  1表示布点、2表示采样、6表示检测
     * @return
     */
    @Operation(summary = "查询通过审核了的布点(1)、采样(2)、检测(3)的单位的列表(省级、市级、县级)")
    @GetMapping("/getOrgListWithTech")
    public Result<List<TechOrgSelectDTO>> getOrgListWithTech(Integer level, String code, String type) {
        List<TechOrgSelectDTO> list = organizationService.getOrgListWithTech(level, code, type);
        return Result.ok(list);
    }


    @Operation(summary = "暂时用于获取所有界面的下拉的单位列表")
    @GetMapping("/getOrgListForTemp")
    public Result<List<Organization>> getOrgListForTemp() {
        return Result.ok(organizationService.list());
    }

    @Operation(summary = "技术单位用户维护-新增")
    @PostMapping("/addTechOrgUser")
    public Result<String> addTechOrgUser(@RequestBody TechOrgUserDTO dto) {
        organizationService.addTechOrgUser(dto);
        return Result.ok();
    }

    @Operation(summary = "技术单位用户维护-维护，只能修改手机号、姓名、性别、身份证")
    @PostMapping("/updateTechOrgUser")
    public Result<String> updateTechOrgUser(@RequestBody TechOrgUserDTO dto) {
        organizationService.updateTechOrgUser(dto);
        return Result.ok();
    }

    @Operation(summary = "技术单位用户维护-删除")
    @PostMapping("/deleteTechOrgUser")
    public Result<String> deleteTechOrgUser(String userId) {
        organizationService.deleteTechOrgUser(userId);
        return Result.ok();
    }

    @Operation(summary = "【测试项目分类管理】菜单【添加测试项目分类】弹窗中的检测实验室和检测质控实验室接口")
    @GetMapping("/selectList")
    public Result<List<TechOrgSelectDTO>> selectList(String name) {
        return Result.ok(organizationService.selectList(name));
    }

}