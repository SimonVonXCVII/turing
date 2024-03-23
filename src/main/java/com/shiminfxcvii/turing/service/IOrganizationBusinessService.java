package com.shiminfxcvii.turing.service;

import com.shiminfxcvii.turing.model.dto.OrganizationBusinessDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * <p>
 * 单位业务表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
public interface IOrganizationBusinessService {

    /**
     * 单位管理员查询本单位已申请业务或者审核人员查询
     *
     * @param dto 接收查询参数
     * @return 已申请业务
     * @author ShiminFXCVII
     * @since 1/5/2023 10:15 AM
     */
    Page<OrganizationBusinessDTO> selectPage(OrganizationBusinessDTO dto) throws IOException;

    /**
     * 单位管理员在点击编辑前查询单条数据
     *
     * @param id 主键 id
     * @return 需要查询的数据
     * @author ShiminFXCVII
     * @since 1/5/2023 10:15 AM
     */
    OrganizationBusinessDTO getOneById(String id) throws IOException;

    /**
     * 申请业务
     *
     * @param dto 接收新增业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    void insert(OrganizationBusinessDTO dto) throws IOException;

    /**
     * 申请页面更新业务
     *
     * @param dto 接收修改业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    void applyUpdate(OrganizationBusinessDTO dto) throws IOException;

    /**
     * 审核页面更新业务
     *
     * @param dto 接收修改业务参数
     * @author ShiminFXCVII
     * @since 2023/3/25 14:32
     */
    void checkUpdate(OrganizationBusinessDTO dto) throws IOException;

}
