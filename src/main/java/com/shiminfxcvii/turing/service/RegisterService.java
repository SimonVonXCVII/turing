package com.shiminfxcvii.turing.service;

import com.shiminfxcvii.turing.model.dto.RegisterDTO;

/**
 * 注册 服务类
 *
 * @author ShiminFXCVII
 * @since 2023/4/12 22:19
 */
public interface RegisterService {

    /**
     * 注册
     *
     * @author ShiminFXCVII
     * @since 2023/4/12 22:20
     */
    void register(RegisterDTO dto);

}
