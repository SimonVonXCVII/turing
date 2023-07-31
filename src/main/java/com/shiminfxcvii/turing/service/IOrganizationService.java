package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.model.dto.OrgDetailDTO;
import com.shiminfxcvii.turing.model.dto.OrganizationDTO;
import com.shiminfxcvii.turing.model.dto.TechOrgSelectDTO;
import com.shiminfxcvii.turing.model.dto.TechOrgUserDTO;
import com.shiminfxcvii.turing.model.query.OrganizationPageQuery;

import java.util.List;

/**
 * <p>
 * 单位表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
public interface IOrganizationService extends IService<Organization> {

    IPage<OrganizationDTO> getOrganizationPage(OrganizationPageQuery query);

    OrgDetailDTO getOrgDetailById(String id);

    void updateOrganization(Organization organization);

    void deleteById(String id);

    /**
     * @param level 0表示省级，1表示市级，2表示县级
     * @param code  省/市/县编码
     * @return
     */
    List<TechOrgSelectDTO> getOrgListWithLead(Integer level, String code);

    List<TechOrgSelectDTO> getOrgListWithQc(Integer level, String code, String type);

    void addTechOrgUser(TechOrgUserDTO dto);

    void updateTechOrgUser(TechOrgUserDTO dto);

    void deleteTechOrgUser(String userId);

    List<TechOrgSelectDTO> getOrgListWithTech(Integer level, String code, String type);

    /**
     * 【测试项目分类管理】菜单【添加测试项目分类】弹窗中的检测实验室和检测质控实验室接口
     *
     * @param name 实验室名称
     * @return 符合条件的实验室数据
     * @author ShiminFXCVII
     * @since 2023/4/4 14:52
     */
    List<TechOrgSelectDTO> selectList(String name);

}