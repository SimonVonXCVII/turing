package com.shiminfxcvii.turing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shiminfxcvii.turing.common.result.Result;
import com.shiminfxcvii.turing.entity.Dict;
import com.shiminfxcvii.turing.model.dto.DictDTO;
import com.shiminfxcvii.turing.model.query.DictPageQuery;
import com.shiminfxcvii.turing.service.IDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
@Tag(name = "DictController", description = "字典表 前端控制器")
@RestController
@RequestMapping("/api/dict")
public class DictController {

    private final IDictService dictService;

    public DictController(IDictService dictService) {
        this.dictService = dictService;
    }

    @GetMapping("/getAreaByCode")
    @Operation(summary = "地区及下级地区查询")
    public Result<DictDTO> getAreaByCode(Integer code) {
        return Result.ok(dictService.getAreaByCode(code));
    }

    @Operation(summary = "获取字典分页")
    @PostMapping("/getDictPage")
    public Result<IPage<Dict>> getDictPage(@RequestBody DictPageQuery query) {
        IPage<Dict> page = dictService.getDictPage(query);
        return Result.ok(page);
    }

    @Operation(summary = "根据pid获取子列表")
    @GetMapping("/getChildDictList")
    public Result<List<Dict>> getChildDictList(@RequestParam("pid") String pid) {
        List<Dict> childList = dictService.list(Wrappers.<Dict>lambdaQuery().eq(Dict::getPid, pid));
        return Result.ok(childList);
    }

    @Operation(summary = "添加/修改字典时,根据type获取一级字典")
    @GetMapping("/getFirstDictListByType")
    public Result<List<Dict>> getFirstDictListByType(@RequestParam("type") String type) {
        List<Dict> firstDictList =
                dictService.list(Wrappers.<Dict>lambdaQuery()
                        .eq(Dict::getType, type)
                        .isNull(Dict::getPid));
        return Result.ok(firstDictList);
    }

    @Operation(summary = "添加新字典",
            description = "添加新字典时，需要先选择分类，然后根据分类去查分类下的一级字典，也就是调用getFirstDictListByType()方法" +
                    "选完一级字典之后，调用getChildDictList()方法获取二级字典，以此类推（也就是多级选择器），最后选择的字典的id作为pid传输到后端即可，" +
                    "如果当前新加字典就是一级，则无需传pid")
    @PostMapping("/addDict")
    public Result<String> addDict(@RequestBody Dict dict) {
        dictService.addDict(dict);
        return Result.ok("添加成功");
    }

    @Operation(summary = "修改字典")
    @PostMapping("/updateDict")
    public Result<String> updateDict(@RequestBody Dict dict) {
        dictService.updateDict(dict);
        return Result.ok("修改成功");
    }

    @Operation(summary = "删除字典")
    @PostMapping("/deleteDictByIds")
    public Result<String> deleteDictByIds(@RequestBody List<String> ids) {
        dictService.removeBatchByIds(ids);
        return Result.ok("删除成功");
    }

    @Operation(summary = "获取字典type列表")
    @GetMapping("/getDictTypeList")
    public Result<List<Dict>> getDictTypeList() {
        List<Dict> dictTypeList = dictService.getDictTypeList();
        return Result.ok(dictTypeList);
    }

    @Operation(summary = "根据id获取字典详情")
    @GetMapping("/getDetailById")
    public Result<Dict> getDetailById(@RequestParam("id") String id) {
        return Result.ok(dictService.getById(id));
    }


    @Operation(summary = "添加字典分类，只需要传type和name，name为分类的中文，type为分类的英文")
    @PostMapping("/addDictType")
    public Result<String> addDictType(@RequestBody Dict dict) {
        dictService.addDictType(dict);
        return Result.ok("添加成功");
    }


    @Operation(summary = "修改字典状态，enabled<->unabled")
    @GetMapping("/changeDictStatus")
    public Result<String> changeDictStatus(@RequestParam("id") String id) {
        dictService.changeDictStatus(id);
        return Result.ok("修改成功");
    }

}