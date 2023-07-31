package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.entity.OrganizationBusiness;
import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.shiminfxcvii.turing.mapper.OrganizationBusinessMapper;
import com.shiminfxcvii.turing.mapper.OrganizationMapper;
import com.shiminfxcvii.turing.model.dto.*;
import com.shiminfxcvii.turing.model.query.OrganizationPageQuery;
import com.shiminfxcvii.turing.service.IDictService;
import com.shiminfxcvii.turing.service.IOrganizationService;
import com.shiminfxcvii.turing.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * <p>
 * 单位表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements IOrganizationService {

    private final OrganizationBusinessMapper organizationBusinessMapper;
    private final IUserService userService;
    private final IDictService dictService;

    @Override
    public IPage<OrganizationDTO> getOrganizationPage(OrganizationPageQuery query) {

        boolean flag = StringUtils.hasText(query.getDistrictCode()) ||
                StringUtils.hasText(query.getCityCode()) ||
                StringUtils.hasText(query.getProvinceCode()) ||
                StringUtils.hasText(query.getOrgName()) ||
                query.getOrgType() != null;
        if (query.getOrgType() != null) {
            query.setType(query.getOrgType().getValue());
        }
        IPage<OrganizationDTO> page =
                this.baseMapper.getOrganizationPage(
                        new Page<>(query.getPageIndex(), query.getPageSize()), query, flag);
        for (OrganizationDTO record : page.getRecords()) {
            if (record.getManagerId() != null) record.setNeedSetManager(false);
            //TODO 待优化：使用caffeine优化字典查询
            if (record.getProvinceCode() != null) {
                String province = dictService.getAreaNameByValue(record.getProvinceCode());
                String city = dictService.getAreaNameByValue(record.getCityCode());
                String district = dictService.getAreaNameByValue(record.getDistrictCode());
                record.setProvince(province);
                record.setCity(city);
                record.setDistrict(district);
            }
        }
        return page;
    }

    @Override
    public OrgDetailDTO getOrgDetailById(String id) {
        OrgDetailDTO dto = new OrgDetailDTO();
        Organization organization = getById(id);
        OrganizationDTO organizationDTO = new OrganizationDTO();
        BeanUtils.copyProperties(organization, organizationDTO);
        //TODO 待优化：使用caffeine优化字典查询
        String province = dictService.getAreaNameByValue(organization.getProvinceCode());
        String city = dictService.getAreaNameByValue(organization.getCityCode());
        String district = dictService.getAreaNameByValue(organization.getDistrictCode());
        organizationDTO.setProvince(province);
        organizationDTO.setCity(city);
        organizationDTO.setDistrict(district);
        dto.setOrganizationDTO(organizationDTO);
        OrgManagerDTO orgManagerDTO = userService.getOrgManager(id);
        dto.setOrgManagerDTO(orgManagerDTO);
        if (organization.getPid() != null) {
            Organization up = getById(organization.getPid());
            dto.setUpOrganization(up);
        }
        return dto;
    }

    @Override
    public void updateOrganization(Organization organization) {
        //校验code
        Organization old = getById(organization.getId());
        if (old == null) {
            throw new BizRuntimeException("您所要修改的单位不存在");
        }
        if (StringUtils.hasText(organization.getCode())) {
            //比较code是否相等
            if (!organization.getCode().equals(old.getCode())) {
                throw new BizRuntimeException("本次信用代码与原信用代码不一致，请联系管理员修改");
            }
            //防止BaseEntity中的字段没传
            organization.setCreateBy(old.getCreateBy());
            organization.setCreateTime(old.getCreateTime());
            organization.setVersion(old.getVersion());
            organization.setDeleted(old.getDeleted());
            updateById(organization);
        } else {
            throw new BizRuntimeException("信用代码不能为空");
        }
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        Organization organization = getById(id);
        removeById(id);
    }

    /**
     * @param level 0表示省级，1表示市级，2表示县级
     * @param code  省/市/县编码
     * @return
     */
    @Override
    public List<TechOrgSelectDTO> getOrgListWithLead(Integer level, String code) {
        return this.baseMapper.getOrgListWithLead(level, code, 0);
    }

    @Override
    public List<TechOrgSelectDTO> getOrgListWithQc(Integer level, String code, String type) {
        return this.baseMapper.getOrgListWithQc(level, code, type);
    }

    @Override
    public void addTechOrgUser(TechOrgUserDTO dto) {
        userService.addTechOrgUser(dto);
    }

    @Override
    public void updateTechOrgUser(TechOrgUserDTO dto) {
        userService.updateTechOrgUser(dto);
    }

    @Override
    public void deleteTechOrgUser(String userId) {
        userService.deleteTechOrgUser(userId);
    }

    @Override
    public List<TechOrgSelectDTO> getOrgListWithTech(Integer level, String code, String type) {
        return this.baseMapper.getOrgListWithTech(level, code, type);
    }

    /**
     * 【测试项目分类管理】菜单【添加测试项目分类】弹窗中的检测实验室和检测质控实验室接口
     *
     * @param name 实验室名称
     * @return 符合条件的实验室数据
     * @author ShiminFXCVII
     * @since 2023/4/4 14:52
     */
    @Override
    public List<TechOrgSelectDTO> selectList(String name) {
        return organizationBusinessMapper
                .selectList(Wrappers.<OrganizationBusiness>lambdaQuery()
                        .like(OrganizationBusiness::getLink, String.valueOf(OrganizationBusinessBusinessLinksEnum.SAMPLE_TESTING.getDesc()))
                        .eq(OrganizationBusiness::getState, OrganizationBusinessStateEnum.PASSES)
                        .like(OrganizationBusiness::getOrgName, name))
                .stream()
                .map(organizationBusiness -> {
                    TechOrgSelectDTO dto = new TechOrgSelectDTO();
                    dto.setOrgId(organizationBusiness.getOrgId());
                    dto.setOrgName(organizationBusiness.getOrgName());
                    return dto;
                })
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(TechOrgSelectDTO::getOrgId))), ArrayList::new));
    }

}