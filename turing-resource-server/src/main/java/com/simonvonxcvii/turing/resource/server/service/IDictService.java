package com.simonvonxcvii.turing.resource.server.service;

import com.simonvonxcvii.turing.resource.server.model.dto.DictDTO;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-30 12:49:40
 */
public interface IDictService {

    /**
     * 单个新增或修改
     *
     * @author Simon Von
     * @since 2023/9/7 16:25
     */
    void insertOrUpdate(DictDTO dto);

    /**
     * 分页查询
     *
     * @author Simon Von
     * @since 2023/9/7 15:48
     */
    Page<DictDTO> selectPage(DictDTO dto);

    /**
     * 根据区域行政编码获取区域数据
     *
     * @param code 区域行政编码
     * @return 区域数据
     * @author Simon Von
     * @since 12/30/2022 2:42 PM
     */
    DictDTO getAreaByCode(Integer code);

    /**
     * 根据主键 id 逻辑删除
     *
     * @param id 字典主键 id
     * @author Simon Von
     * @since 2023/9/7 16:49
     */
    void deleteById(Integer id);

}
