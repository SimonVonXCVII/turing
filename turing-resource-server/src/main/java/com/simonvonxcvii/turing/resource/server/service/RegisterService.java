package com.simonvonxcvii.turing.resource.server.service;

import com.simonvonxcvii.turing.resource.server.model.dto.RegisterDto;

/**
 * 注册 服务类
 *
 * @author Simon Von
 * @since 2023/4/12 22:19
 */
public interface RegisterService {

    /**
     * 注册
     *
     * @author Simon Von
     * @since 2023/4/12 22:20
     */
    void register(RegisterDto dto);

}
