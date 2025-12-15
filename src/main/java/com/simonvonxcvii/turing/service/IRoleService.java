package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.RoleDTO;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
public interface IRoleService {

    /**
     * 新增数据
     */
    void insert(RoleDTO dto);

    /**
     * 条件查询
     */
    Page<RoleDTO> selectBy(RoleDTO dto);

    /**
     * 修改数据
     *
     * @param id  主键 id
     * @param dto 其他数据
     */
    void updateById(Integer id, RoleDTO dto);

    /**
     * 逻辑删除
     *
     * @param id 主键 id
     */
    void deleteById(Integer id);

}
