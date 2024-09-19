package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.UserDTO;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-19 15:58:28
 */
public interface IUserService {

    /**
     * 单个新增或修改
     */
    void insertOrUpdate(UserDTO dto);

    /**
     * 分页查询
     */
    Page<UserDTO> selectPage(UserDTO dto);

    /**
     * 根据用户 id 逻辑删除用户
     *
     * @param id 用户 id
     * @author SimonVonXCVII
     * @since 2023/9/6 18:12
     */
    void deleteById(String id);

}
