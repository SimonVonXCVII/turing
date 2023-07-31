package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.OrganizationBusiness;
import com.shiminfxcvii.turing.model.cmd.OrganizationBusinessCmd;
import com.shiminfxcvii.turing.model.dto.OrganizationBusinessDTO;
import com.shiminfxcvii.turing.model.query.OrganizationBusinessQuery;

import java.io.IOException;

/**
 * <p>
 * 单位业务表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
public interface IOrganizationBusinessService extends IService<OrganizationBusiness> {

    /**
     * 单位管理员查询本单位已申请业务或者审核人员查询
     *
     * @param query 接收查询参数
     * @return 已申请业务
     * @author ShiminFXCVII
     * @since 1/5/2023 10:15 AM
     */
    IPage<OrganizationBusinessDTO> selectPage(OrganizationBusinessQuery query) throws IOException;

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
     * @param cmd 接收新增业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    void insert(OrganizationBusinessCmd cmd) throws IOException;

    /**
     * 申请页面更新业务
     *
     * @param cmd 接收修改业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    void applyUpdate(OrganizationBusinessCmd cmd) throws IOException;

    /**
     * 审核页面更新业务
     *
     * @param cmd 接收修改业务参数
     * @author ShiminFXCVII
     * @since 2023/3/25 14:32
     */
    void checkUpdate(OrganizationBusinessCmd cmd) throws IOException;

}