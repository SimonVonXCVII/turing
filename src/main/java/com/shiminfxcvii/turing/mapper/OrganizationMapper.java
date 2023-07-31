package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.model.dto.OrganizationDTO;
import com.shiminfxcvii.turing.model.dto.TechOrgSelectDTO;
import com.shiminfxcvii.turing.model.query.OrganizationPageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 单位表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    IPage<OrganizationDTO> getOrganizationPage(Page<OrganizationDTO> organizationDTOPage,
                                               @Param("query") OrganizationPageQuery query,
                                               @Param("flag") boolean flag);

    /**
     * 根据业务类型、级别以及地区code，查询通过了某地区、某业务的单位列表
     *
     * @param businessLevel
     * @param code
     * @param businessType
     * @return
     */
    List<TechOrgSelectDTO> getOrgListWithQc(@Param("businessLevel") Integer businessLevel,
                                            @Param("code") String code,
                                            @Param("businessType") String businessType);

    /**
     * 查询技术牵头单位列表，只要申请的级别、编码是对的就可以，无需对应具体的业务、质控
     *
     * @param businessLevel
     * @param code
     * @param businessType
     * @return
     */
    List<TechOrgSelectDTO> getOrgListWithLead(@Param("businessLevel") Integer businessLevel,
                                              @Param("code") String code,
                                              @Param("businessType") Integer businessType);

    List<TechOrgSelectDTO> getOrgListWithTech(@Param("businessLevel") Integer businessLevel,
                                              @Param("code") String code,
                                              @Param("businessType") String businessType);
}