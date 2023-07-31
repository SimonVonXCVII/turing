package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiminfxcvii.turing.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    List<Dict> getDictTypeList();
}