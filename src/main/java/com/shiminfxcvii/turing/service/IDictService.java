package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.Dict;
import com.shiminfxcvii.turing.model.dto.DictDTO;
import com.shiminfxcvii.turing.model.query.DictPageQuery;

import java.util.List;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
public interface IDictService extends IService<Dict> {

    /**
     * 根据区域行政编码获取区域数据
     *
     * @param code 区域行政编码
     * @return 区域数据
     * @author ShiminFXCVII
     * @since 12/30/2022 2:42 PM
     */
    DictDTO getAreaByCode(Integer code);

    IPage<Dict> getDictPage(DictPageQuery query);

    List<Dict> getDictTypeList();

    void addDict(Dict dict);

    void updateDict(Dict dict);

    void addDictType(Dict dict);

    void changeDictStatus(String id);

    String getAreaNameByValue(String value);

    DictDTO getDictByValue(String type, String value);
}