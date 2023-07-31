package com.shiminfxcvii.turing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.model.dto.OrgManagerDTO;
import com.shiminfxcvii.turing.model.dto.PlatFormUserDTO;
import com.shiminfxcvii.turing.model.dto.UserDTO;
import com.shiminfxcvii.turing.model.dto.UserDetailDTO;
import com.shiminfxcvii.turing.model.query.UserPageQuery;
import com.shiminfxcvii.turing.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-19 15:58:28
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取用户分页，支持用户真实姓名模糊查询、登录账号模糊查询、用户所在单位名称模糊查询、用户手机号模糊查询")
    @PostMapping("/getUserPage")
    public Result<IPage<UserDTO>> getUserPage(@RequestBody UserPageQuery query) {
        IPage<UserDTO> page = userService.getUserPage(query);
        return Result.ok(page);
    }

    @Operation(summary = "根据用户id获取用户详细信息（包括用户基本信息，用户所在单位信息）")
    @GetMapping("/getUserDetail")
    public Result<UserDetailDTO> getUserDetail(@RequestParam("id") String id) {
        UserDetailDTO dto = userService.getUserDetail(id);
        return Result.ok(dto);
    }

    @Operation(summary = "修改用户，支持修改性别、用户名、姓名、单位、出生日期、身份证号")
    @PostMapping("/updateUser")
    public Result<String> updateUser(@RequestBody UserDTO dto) {
        userService.updateUser(dto);
        return Result.ok("修改成功");
    }

    @Operation(summary = "删除单位")
    @GetMapping("/deleteById")
    public Result<String> deleteById(@RequestParam("id") String id) {
        userService.removeById(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "/新增用户，用户名、真实姓名、手机号、性别、单位id、身份证号码、出生日期")
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.ok("添加成功");
    }

    @Operation(summary = "设置单位管理员，只需要传orgId和userId")
    @PostMapping("/setOrgManager")
    public Result<String> setOrgManager(@RequestBody OrgManagerDTO dto) {
        userService.setOrgManager(dto);
        return Result.ok("设置成功");
    }

    @Operation(summary = "根据单位id获取该单位下的所有管理员")
    @GetMapping("/getUserListByOrgId")
    public Result<List<UserDTO>> getUserListByOrgId(@RequestParam("orgId") String orgId) {
        List<UserDTO> list = userService.getUserListByOrgId(orgId);
        return Result.ok(list);
    }

    @Operation(summary = "平台用户-新增")
    @PostMapping("/addPlatformUser")
    public Result<String> addPlatformUser(@RequestBody PlatFormUserDTO dto) {
        userService.addPlatformUser(dto);
        return Result.ok();
    }

    @Operation(summary = "平台用户-维护（只允许修改身份证、性别、手机号码、姓名）")
    @PostMapping("/maintainPlatformUser")
    public Result<String> maintainPlatformUser(@RequestBody PlatFormUserDTO dto) {
        userService.maintainPlatformUser(dto);
        return Result.ok();
    }


    @Operation(summary = "根据单位id获取单位下所有用户")
    @GetMapping("/getAllUsersByOrgId")
    public Result<List<User>> getAllUsersByOrgId(@RequestParam("orgId") String orgId) {
        List<User> list = userService.list(Wrappers.<User>lambdaQuery().eq(User::getOrgId, orgId));
        return Result.ok(list);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(String userId) {
        userService.resetPassword(userId);
        return Result.ok();
    }


    @Operation(summary = "平台用户-删除")
    @PostMapping("/deletePlatformUser")
    public Result<String> deletePlatformUser(String userId) {
        userService.deletePlatformUser(userId);
        return Result.ok();
    }

}