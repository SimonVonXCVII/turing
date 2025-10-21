package com.simonvonxcvii.turing.service;

import com.simonvonxcvii.turing.model.dto.UserDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
public interface IUserService {

    /**
     * 获取用户信息
     *
     * @author Simon Von
     * @since 12/17/2022 8:19 PM
     */
    UserDTO info();

    /**
     * 单个新增或修改
     */
    void insertOrUpdate(UserDTO dto);

    /**
     * 分页查询
     */
    Page<@NonNull UserDTO> selectPage(UserDTO dto);

    /**
     * 根据用户 id 逻辑删除用户
     *
     * @param id 用户 id
     * @author Simon Von
     * @since 2023/9/6 18:12
     */
    void deleteById(Integer id);

}
