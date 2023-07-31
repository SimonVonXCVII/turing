package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.common.result.ResultCode;
import com.shiminfxcvii.turing.entity.Dict;
import com.shiminfxcvii.turing.mapper.DictMapper;
import com.shiminfxcvii.turing.model.dto.DictDTO;
import com.shiminfxcvii.turing.model.query.DictPageQuery;
import com.shiminfxcvii.turing.service.IDictService;
import com.shiminfxcvii.turing.utils.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    private static DictDTO convertToDictDTO(Dict dict) {
        DictDTO dictDTO = new DictDTO();
        BeanUtils.copyProperties(dict, dictDTO);
        dictDTO.setChildren(new ArrayList<>());
        return dictDTO;
    }

    /**
     * 获取字典
     */
    @Override
    public DictDTO getDictByValue(String type, String value) {
        return convertToDictDTO(
                getOne(
                        Wrappers.<Dict>query()
                                .eq("type", type)
                                .eq("value", value)
                )
        );
    }

    /**
     * 根据区域行政编码获取区域数据
     *
     * @param code 区域行政编码
     * @return 区域数据
     * @author ShiminFXCVII
     * @since 12/30/2022 2:42 PM
     */
    @Override
    public DictDTO getAreaByCode(Integer code) {
        if (code == null) {
            DictDTO dictDTO = new DictDTO();
            List<Dict> children = lambdaQuery().isNull(Dict::getPid).eq(Dict::getType, Constants.AREA).orderByAsc(Dict::getSort).list();
            if (!children.isEmpty()) {
                dictDTO.setChildren(children.stream().map(DictServiceImpl::convertToDictDTO).toList());
            }
            return dictDTO;
        }
        Dict dict = lambdaQuery().eq(Dict::getValue, code.toString()).eq(Dict::getType, Constants.AREA).one();
        if (dict == null) {
            throw BizRuntimeException.from(ResultCode.ERROR, "没有找到区域编号：" + code);
        }
        DictDTO dictDTO = convertToDictDTO(dict);
        List<Dict> children = lambdaQuery().eq(Dict::getPid, code).eq(Dict::getType, Constants.AREA).orderByAsc(Dict::getSort).list();
        if (!children.isEmpty()) {
            dictDTO.setChildren(children.stream().map(DictServiceImpl::convertToDictDTO).toList());
        }
        return dictDTO;
    }

    @Override
    public IPage<Dict> getDictPage(DictPageQuery query) {
        return page(new Page<>(query.getPageIndex(), query.getPageSize()),
                Wrappers.<Dict>lambdaQuery()
                        .eq(StringUtils.hasText(query.getType()), Dict::getType, query.getType())
                        .like(StringUtils.hasText(query.getName()), Dict::getName, query.getName())
                        .like(StringUtils.hasText(query.getValue()), Dict::getValue, query.getValue())
                        .eq(Dict::getStatus, "ENABLED")
        );
    }

    @Override
    public List<Dict> getDictTypeList() {
        return this.baseMapper.getDictTypeList();
    }

    @Override
    public void addDict(Dict dict) {
        //TODO 需要做哪些校验？
        save(dict);
    }

    @Override
    public void updateDict(Dict dict) {
        //TODO 需要做哪些校验？
        updateById(dict);
    }

    /**
     * 添加分类之后，将分类的pid设置为20230104，这个没什么别的意义，只是一个魔数，用来表示该记录只是作为分类使用
     *
     * @param dict
     */
    @Override
    public void addDictType(Dict dict) {
        dict.setValue(dict.getName());
        //pid为魔数，无其他含义，起标识作用
        dict.setPid("20230104");
        save(dict);
    }

    @Override
    public void changeDictStatus(String id) {
        Dict dict = getById(id);
        if (dict != null) {
            if (dict.getStatus().equalsIgnoreCase("enabled")) {
                dict.setStatus("disabled".toUpperCase());
                updateById(dict);
            } else {
                dict.setStatus("enabled".toUpperCase());
                updateById(dict);
            }
        }
    }

    @Override
    public String getAreaNameByValue(String value) {
        Dict dict = getOne(Wrappers.<Dict>lambdaQuery().eq(Dict::getValue, value));
        return dict.getName();
    }
}