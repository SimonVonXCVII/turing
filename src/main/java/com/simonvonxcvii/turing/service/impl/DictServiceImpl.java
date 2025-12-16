package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.Dict;
import com.simonvonxcvii.turing.enums.DictTypeEnum;
import com.simonvonxcvii.turing.model.dto.DictDTO;
import com.simonvonxcvii.turing.repository.jpa.DictJpaRepository;
import com.simonvonxcvii.turing.service.IDictService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
 * @author Simon Von
 * @since 2022-12-30 12:49:40
 */
@RequiredArgsConstructor
@Service
public class DictServiceImpl implements IDictService {

    private final DictJpaRepository dictJpaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(DictDTO dto) {
        Dict dict;
        // 新增
        if (dto.getId() == null) {
            dict = new Dict();
        }
        // 修改
        else {
            dict = dictJpaRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("无法查找到该数据"));
        }
        if (dto.getSort() == null) {
            BeanUtils.copyProperties(dto, dict, Dict.SORT);
        } else {
            BeanUtils.copyProperties(dto, dict);
        }
        dictJpaRepository.save(dict);
    }

    @Override
    public Page<DictDTO> selectPage(DictDTO dto) {
        Specification<Dict> spec = (root, query, builder) -> {
            List<Predicate> predicateList = new LinkedList<>();
            if (StringUtils.hasText(dto.getType())) {
                Predicate type = builder.like(root.get(Dict.TYPE), "%" + dto.getType() + "%");
                predicateList.add(type);
            }
            if (StringUtils.hasText(dto.getName())) {
                Predicate name = builder.like(root.get(Dict.NAME), "%" + dto.getName() + "%");
                predicateList.add(name);
            }
            if (StringUtils.hasText(dto.getValue())) {
                Predicate value = builder.like(root.get(Dict.VALUE), "%" + dto.getValue() + "%");
                predicateList.add(value);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).getRestriction();
        };
        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        return dictJpaRepository.findAll(spec, pageRequest)
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
     * @author Simon Von
     * @since 12/30/2022 2:42 PM
     */
    @Override
    public DictDTO getAreaByCode(Integer code) {
        if (code == null) {
            DictDTO dictDTO = new DictDTO();
            Specification<Dict> spec = (root, query, builder) -> {
                Predicate pid = builder.isNull(root.get(Dict.PID));
                Predicate type = builder.equal(root.get(Dict.TYPE), DictTypeEnum.AREA);
                Predicate predicate = builder.and(pid, type);
                return query.where(predicate).getRestriction();
            };
            List<Dict> children = dictJpaRepository.findAll(spec, Sort.by(Dict.SORT));
            if (!children.isEmpty()) {
                dictDTO.setChildren(children.stream().map(this::convertToDictDTO).toList());
            }
            return dictDTO;
        }
        Specification<Dict> spec = (root, query, builder) -> {
            Predicate value = builder.equal(root.get(Dict.VALUE), code.toString());
            Predicate type = builder.equal(root.get(Dict.TYPE), DictTypeEnum.AREA);
            Predicate predicate = builder.and(value, type);
            return query.where(predicate).getRestriction();
        };
        Dict dict = dictJpaRepository.findOne(spec)
                .orElseThrow(() -> new RuntimeException("没有找到区域编码：" + code));
        DictDTO dictDTO = convertToDictDTO(dict);
        Specification<Dict> spec2 = (root, query, builder) -> {
            Predicate pid = builder.equal(root.get(Dict.PID), dict.getId());
            Predicate type = builder.equal(root.get(Dict.TYPE), DictTypeEnum.AREA);
            Predicate predicate = builder.and(pid, type);
            return query.where(predicate).getRestriction();
        };
        List<Dict> children = dictJpaRepository.findAll(spec2, Sort.by(Dict.SORT));
        if (!children.isEmpty()) {
            dictDTO.setChildren(children.stream().map(this::convertToDictDTO).toList());
        }
        return dictDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        dictJpaRepository.deleteById(id);
    }

    public DictDTO convertToDictDTO(Dict dict) {
        DictDTO dictDTO = new DictDTO();
        BeanUtils.copyProperties(dict, dictDTO);
        dictDTO.setChildren(new ArrayList<>());
        return dictDTO;
    }

}
