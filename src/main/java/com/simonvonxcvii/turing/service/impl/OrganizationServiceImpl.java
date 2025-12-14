package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.*;
import com.simonvonxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.simonvonxcvii.turing.enums.OrganizationBusinessStateEnum;
import com.simonvonxcvii.turing.model.dto.OrganizationDTO;
import com.simonvonxcvii.turing.repository.jpa.OrganizationBusinessJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.OrganizationJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.IOrganizationService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
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
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements IOrganizationService {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationBusinessJpaRepository organizationBusinessJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(OrganizationDTO dto) {
        Organization organization;
        // 新增
        if (dto.getId() == null) {
            organization = new Organization();
        }
        // 修改
        else {
            organization = organizationJpaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("无法查找到该数据"));
        }
        BeanUtils.copyProperties(dto, organization, AbstractAuditable.CREATED_DATE);
        // 获取省市县名称
        String provinceName = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getProvinceCode().toString());
        organization.setProvinceName(provinceName);
        String cityName = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getCityCode().toString());
        organization.setCityName(cityName);
        String districtName = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + dto.getDistrictCode().toString());
        organization.setDistrictName(districtName);
        organizationJpaRepository.save(organization);
    }

    @Override
    public Page<OrganizationDTO> selectPage(OrganizationDTO dto) {
        Specification<Organization> spec = (root, query, builder) -> {
            List<Predicate> predicateList = new LinkedList<>();
            if (StringUtils.hasText(dto.getName())) {
                Predicate name = builder.like(root.get(Organization.NAME), "%" + dto.getName() + "%");
                predicateList.add(name);
            }
            if (StringUtils.hasText(dto.getCode())) {
                Predicate code = builder.like(builder.lower(root.get(Organization.CODE)),
                        "%" + dto.getCode().toLowerCase() + "%");
                predicateList.add(code);
            }
            if (StringUtils.hasText(dto.getType())) {
                Predicate type = builder.equal(root.get(Organization.TYPE), dto.getType());
                predicateList.add(type);
            }
            if (dto.getProvinceCode() != null) {
                Predicate provinceCode = builder.equal(root.get(Organization.PROVINCE_CODE), dto.getProvinceCode());
                predicateList.add(provinceCode);
            }
            if (dto.getCityCode() != null) {
                Predicate cityCode = builder.equal(root.get(Organization.CITY_CODE), dto.getCityCode());
                predicateList.add(cityCode);
            }
            if (dto.getDistrictCode() != null) {
                Predicate districtCode = builder.equal(root.get(Organization.DISTRICT_CODE), dto.getDistrictCode());
                predicateList.add(districtCode);
            }
            if (StringUtils.hasText(dto.getLegalPerson())) {
                Predicate legalPerson = builder.like(root.get(Organization.LEGAL_PERSON),
                        "%" + dto.getLegalPerson() + "%");
                predicateList.add(legalPerson);
            }
            Predicate predicate = builder.and(predicateList.toArray(Predicate[]::new));
            return query.where(predicate).getRestriction();
        };
        // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        return organizationJpaRepository.findAll(spec, pageRequest)
                .map(organization -> {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    BeanUtils.copyProperties(organization, organizationDTO);
                    return organizationDTO;
                });
    }

    @Override
    public List<OrganizationDTO> selectIdAndNameList() {
        return organizationJpaRepository.findAll()
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
     * @author Simon Von
     * @since 2023/4/4 14:52
     */
    @Override
    public List<OrganizationDTO> selectList(String name) {
        Specification<OrganizationBusiness> spec =
                (root, query, builder) -> {
                    Predicate linkPredicate = builder.like(root.get(OrganizationBusiness.LINK),
                            "%" + OrganizationBusinessBusinessLinksEnum.SAMPLE_TESTING.getDesc() + "%");
                    Predicate statePredicate = builder.like(root.get(OrganizationBusiness.STATE),
                            "%" + OrganizationBusinessStateEnum.PASSES + "%");
                    Predicate namePredicate = builder.like(root.get(OrganizationBusiness.ORG_NAME),
                            "%" + name + "%");
                    Predicate predicate = builder.and(linkPredicate, statePredicate, namePredicate);
                    return query.where(predicate).getRestriction();
                };
        return organizationBusinessJpaRepository.findAll(spec)
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
    public void deleteById(Integer id) {
        // 逻辑删除用户-角色关联数据
        // TODO: 2023/9/7 是否能实现查询指定列
        PredicateSpecification<User> spec =
                (root, builder) -> builder.equal(root.get(User.ORG_ID), id);
        List<User> userList = userJpaRepository.findAll(spec);
        List<Integer> userIdList = userList.stream().map(AbstractAuditable::getId).toList();
        DeleteSpecification<UserRole> userRoleSpec =
                (root, query, builder) -> {
                    Predicate predicate = builder.in(root.get(UserRole.USER_ID)).in(userIdList);
                    return query.where(predicate).getRestriction();
                };
        userRoleJpaRepository.delete(userRoleSpec);
        // 删除单位下的所有用户
        userJpaRepository.delete(spec);
        // 删除单位
        organizationJpaRepository.deleteById(id);
    }

}
