package com.shiminfxcvii.turing.service.impl;

import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.*;
import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.shiminfxcvii.turing.model.dto.OrganizationDTO;
import com.shiminfxcvii.turing.repository.OrganizationBusinessRepository;
import com.shiminfxcvii.turing.repository.OrganizationRepository;
import com.shiminfxcvii.turing.repository.UserRepository;
import com.shiminfxcvii.turing.repository.UserRoleRepository;
import com.shiminfxcvii.turing.service.IOrganizationService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
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
public class OrganizationServiceImpl implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBusinessRepository organizationBusinessRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(OrganizationDTO dto) {
        Organization organization;
        // 新增
        if (!StringUtils.hasText(dto.getId())) {
            organization = new Organization();
        }
        // 修改
        else {
            organization = organizationRepository.findById(dto.getId()).orElseThrow(() -> BizRuntimeException.from("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, organization, AbstractAuditable.CREATED_DATE);
        // 获取省市县名称
        String provinceName = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getProvinceCode().toString());
        organization.setProvinceName(provinceName);
        String cityName = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getCityCode().toString());
        organization.setCityName(cityName);
        String districtName = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getDistrictCode().toString());
        organization.setDistrictName(districtName);
        organizationRepository.save(organization);
    }

    @Override
    public org.springframework.data.domain.Page<OrganizationDTO> selectPage(OrganizationDTO dto) {
        return organizationRepository.findAll((root, query, criteriaBuilder) -> {
                            List<Predicate> predicateList = new LinkedList<>();
                            if (StringUtils.hasText(dto.getName())) {
                                Predicate name = criteriaBuilder.like(root.get(Organization.NAME),
                                        "%" + dto.getName() + "%", '/');
                                predicateList.add(name);
                            }
                            if (StringUtils.hasText(dto.getCode())) {
                                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get(Organization.CODE)),
                                        "%" + dto.getCode().toLowerCase() + "%", '/');
                                predicateList.add(code);
                            }
                            if (StringUtils.hasText(dto.getType())) {
                                Predicate type = criteriaBuilder.equal(root.get(Organization.TYPE), dto.getType());
                                predicateList.add(type);
                            }
                            if (dto.getProvinceCode() != null) {
                                Predicate provinceCode = criteriaBuilder.equal(root.get(Organization.PROVINCE_CODE), dto.getProvinceCode());
                                predicateList.add(provinceCode);
                            }
                            if (dto.getCityCode() != null) {
                                Predicate cityCode = criteriaBuilder.equal(root.get(Organization.CITY_CODE), dto.getCityCode());
                                predicateList.add(cityCode);
                            }
                            if (dto.getDistrictCode() != null) {
                                Predicate districtCode = criteriaBuilder.equal(root.get(Organization.DISTRICT_CODE), dto.getDistrictCode());
                                predicateList.add(districtCode);
                            }
                            if (StringUtils.hasText(dto.getLegalPerson())) {
                                Predicate legalPerson = criteriaBuilder.like(root.get(Organization.LEGAL_PERSON),
                                        "%" + dto.getLegalPerson() + "%", '/');
                                predicateList.add(legalPerson);
                            }
                            return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                        },
                        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
                        PageRequest.of(dto.getNumber() - 1, dto.getSize()))
                .map(organization -> {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    BeanUtils.copyProperties(organization, organizationDTO);
                    return organizationDTO;
                });
    }

    @Override
    public List<OrganizationDTO> selectIdAndNameList() {
        return organizationRepository.findAll()
                .stream()
                .map(organization -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId(organization.getId());
                    dto.setName(organization.getName());
                    return dto;
                })
                .toList();
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
    public List<OrganizationDTO> selectList(String name) {
        return organizationBusinessRepository.findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicateList = new LinkedList<>();
                    Predicate linkPredicate = criteriaBuilder.like(root.get(OrganizationBusiness.LINK),
                            "%" + OrganizationBusinessBusinessLinksEnum.SAMPLE_TESTING.getDesc() + "%", '/');
                    predicateList.add(linkPredicate);
                    Predicate statePredicate = criteriaBuilder.like(root.get(OrganizationBusiness.STATE),
                            "%" + OrganizationBusinessStateEnum.PASSES + "%", '/');
                    predicateList.add(statePredicate);
                    Predicate namePredicate = criteriaBuilder.like(root.get(OrganizationBusiness.ORG_NAME),
                            "%" + name + "%", '/');
                    predicateList.add(namePredicate);
                    return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
                })
                .stream()
                .map(organizationBusiness -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId(organizationBusiness.getOrgId());
                    dto.setName(organizationBusiness.getOrgName());
                    return dto;
                })
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(OrganizationDTO::getId))), ArrayList::new));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        // 逻辑删除用户-角色关联数据
        // TODO: 2023/9/7 是否能实现查询指定列
        List<User> userList = userRepository.findAll((root, query, criteriaBuilder) -> root.get(User.ORG_ID).in(id));
        List<String> userIdList = userList.stream().map(AbstractAuditable::getId).toList();
        userRoleRepository.delete((root, query, criteriaBuilder) -> root.get(UserRole.USER_ID).in(userIdList));
        // 删除单位下的所有用户
        userRepository.delete((root, query, criteriaBuilder) -> root.get(User.ORG_ID).in(id));
        // 删除单位
        organizationRepository.deleteById(id);
    }

}
