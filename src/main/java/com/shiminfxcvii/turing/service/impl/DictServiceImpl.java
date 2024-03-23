package com.shiminfxcvii.turing.service.impl;

import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Dict;
import com.shiminfxcvii.turing.model.dto.DictDTO;
import com.shiminfxcvii.turing.repository.DictRepository;
import com.shiminfxcvii.turing.service.IDictService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
@RequiredArgsConstructor
@Service
public class DictServiceImpl  implements IDictService {

    private final DictRepository dictRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(DictDTO dto) {
        Dict dict;
        // 新增
        if (!StringUtils.hasText(dto.getId())) {
            dict = new Dict();
        }
        // 修改
        else {
            dict = dictRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        if (dto.getSort() == null) {
            BeanUtils.copyProperties(dto, dict, Dict.SORT);
        } else {
            BeanUtils.copyProperties(dto, dict);
        }
        dictRepository.save(dict);
    }

    @Override
    public Page<DictDTO> selectPage(DictDTO dto) {
        return dictRepository.findAll((root, query, criteriaBuilder) -> {
                            List<Predicate> predicateList = new LinkedList<>();
                            if (StringUtils.hasText(dto.getType())) {
                                Predicate name = criteriaBuilder.like(root.get(Dict.TYPE),
                                        "%" + dto.getType() + "%", '/');
                                predicateList.add(name);
                            }
                            if (StringUtils.hasText(dto.getName())) {
                                Predicate name = criteriaBuilder.like(root.get(Dict.NAME),
                                        "%" + dto.getName() + "%", '/');
                                predicateList.add(name);
                            }
                            if (StringUtils.hasText(dto.getValue())) {
                                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get(Dict.VALUE)),
                                        "%" + dto.getValue().toLowerCase() + "%", '/');
                                predicateList.add(code);
                            }
                            return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                        },
                        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
                        PageRequest.of(dto.getNumber() - 1, dto.getSize()))
                .map(dict -> {
                    DictDTO dictDTO = new DictDTO();
                    BeanUtils.copyProperties(dict, dictDTO);
                    return dictDTO;
                });
    }

    /**
     * 根据区域行政编码获取区域数据
     * // TODO: 2023/9/7 改为从 redis 查询
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
            List<Dict> children = dictRepository.findAll((root, query, criteriaBuilder) ->
                    query.where(root.get(Dict.PID).isNull(), root.get(Dict.TYPE).in("area"))
                            .orderBy(criteriaBuilder.asc(root.get(Dict.SORT)))
                            .getRestriction()
            );
            if (!children.isEmpty()) {
                dictDTO.setChildren(children.stream().map(this::convertToDictDTO).toList());
            }
            return dictDTO;
        }
        Dict dict = dictRepository.findOne((root, query, criteriaBuilder) ->
                        query.where(root.get(Dict.VALUE).in(code.toString()), root.get(Dict.TYPE).in("area")).getRestriction())
                .orElseThrow(() -> BizRuntimeException.from("没有找到区域编号：" + code));
        DictDTO dictDTO = convertToDictDTO(dict);
        List<Dict> children = dictRepository.findAll((root, query, criteriaBuilder) ->
                query.where(root.get(Dict.PID).in(code), root.get(Dict.TYPE).in("area"))
                        .orderBy(criteriaBuilder.asc(root.get(Dict.SORT)))
                        .getRestriction()
        );
        if (!children.isEmpty()) {
            dictDTO.setChildren(children.stream().map(this::convertToDictDTO).toList());
        }
        return dictDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        dictRepository.deleteById(id);
    }

    public DictDTO convertToDictDTO(Dict dict) {
        DictDTO dictDTO = new DictDTO();
        BeanUtils.copyProperties(dict, dictDTO);
        dictDTO.setChildren(new ArrayList<>());
        return dictDTO;
    }

}
