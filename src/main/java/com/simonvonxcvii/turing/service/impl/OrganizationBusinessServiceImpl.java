package com.simonvonxcvii.turing.service.impl;

//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.CreateRequest;
//import co.elastic.clients.elasticsearch.core.GetRequest;
//import co.elastic.clients.elasticsearch.core.GetResponse;
//import co.elastic.clients.elasticsearch.core.SearchResponse;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.Dict;
import com.simonvonxcvii.turing.entity.Organization;
import com.simonvonxcvii.turing.entity.OrganizationBusiness;
import com.simonvonxcvii.turing.entity.User;
import com.simonvonxcvii.turing.enums.OrganizationBusinessLevelEnum;
import com.simonvonxcvii.turing.model.dto.OrganizationBusinessDTO;
import com.simonvonxcvii.turing.repository.jpa.*;
import com.simonvonxcvii.turing.service.IOrganizationBusinessService;
import com.simonvonxcvii.turing.utils.UserUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 单位业务表 服务实现类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@RequiredArgsConstructor
@Service
public class OrganizationBusinessServiceImpl implements IOrganizationBusinessService {

    private final OrganizationBusinessJpaRepository organizationBusinessJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final StringRedisTemplate stringRedisTemplate;
//    private final ElasticsearchClient elasticsearchClient;

    /**
     * 单位管理员查询本单位已申请业务或者审核人员查询
     *
     * @param dto 接收查询参数
     * @return 已申请业务
     * @author Simon Von
     * @since 1/5/2023 10:15 AM
     */
    @Override
    public Page<OrganizationBusinessDTO> selectPage(OrganizationBusinessDTO dto) throws IOException {
//        SearchResponse<OrganizationBusiness> searchResponse = elasticsearchClient.search(searchRequest -> {
//                    searchRequest.index(OrganizationBusiness.INDEX)
//                            // 首页默认从 0 开始
//                            .from(dto.getNumber() - 1)
//                            .size(dto.getSize());
//                    if (StringUtils.hasText(dto.getOrgName())) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("orgName").query(dto.getOrgName())))
//                                // 高亮
//                                .highlight(highlight -> highlight.fields("orgName", function1 -> function1)
//                                        .preTags("<span style='color:red'>")
//                                        .postTags("</span>"));
//                    }
//                    if (dto.getProvinceCode() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("provinceCode").query(dto.getProvinceCode())));
//                    }
//                    if (dto.getCityCode() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("cityCode").query(dto.getCityCode())));
//                    }
//                    if (dto.getDistrictCode() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("districtCode").query(dto.getDistrictCode())));
//                    }
//                    if (dto.getLink() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("link").query(dto.getLink()[0])));
//                    }
//                    if (dto.getType() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("type").query(dto.getType()[0])));
//                    }
//                    if (dto.getState() != null) {
//                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("state").query(dto.getState())));
//                    }
//                    return searchRequest;
//                },
//                OrganizationBusiness.class);
//
//        if (searchResponse.hits().total() == null) {
//            throw BizRuntimeException.from("数据异常");
//        }
//
//        return new PageImpl<>(searchResponse.hits()
//                .hits()
//                .stream()
//                .map(hit -> {
//                    OrganizationBusiness organizationBusiness = hit.source();
//                    if (organizationBusiness == null) {
//                        throw BizRuntimeException.from("数据异常");
//                    }
//                    Map<String, List<String>> highlight = hit.highlight();
//                    OrganizationBusinessDTO dto1 = new OrganizationBusinessDTO();
//                    BeanUtils.copyProperties(organizationBusiness, dto1);
//                    if (!highlight.isEmpty()) {
//                        dto1.setOrgName(highlight.get("orgName").getFirst());
//                    }
//                    // 业务环节
//                    dto1.setLink(StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink()));
//                    // 质控类型
//                    dto1.setType(StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType()));
//                    return dto1;
//                })
//                .toList(),
//                PageRequest.of(dto.getNumber(), dto.getSize()),
//                searchResponse.hits().total().value());
        return null;
    }

    /**
     * 单位管理员在点击编辑前查询单条数据
     *
     * @param id 主键 id
     * @return 需要查询的数据
     * @author Simon Von
     * @since 1/5/2023 10:15 AM
     */
    @Override
    public OrganizationBusinessDTO getOneById(String id) throws IOException {
//        GetResponse<OrganizationBusiness> organizationBusinessGetResponse = elasticsearchClient.get(GetRequest.of(
//                builder -> builder.index(OrganizationBusiness.INDEX)
//                        .id(id)), OrganizationBusiness.class);
//        OrganizationBusiness organizationBusiness = organizationBusinessGetResponse.source();
//        if (organizationBusiness == null) {
//            throw BizRuntimeException.from("没有找到该业务记录");
//        }
//        OrganizationBusinessDTO dto = new OrganizationBusinessDTO();
//        BeanUtils.copyProperties(organizationBusiness, dto);
//        return dto;
        return null;
    }

    /**
     * 申请业务
     *
     * @param dto 接收新增业务参数
     * @author Simon Von
     * @since 1/4/2023 4:57 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(OrganizationBusinessDTO dto) throws IOException {
        boolean exists = organizationBusinessJpaRepository.exists((root, query, _) -> {
            List<Predicate> predicateList = new LinkedList<>();
            // 本单位
            predicateList.add(root.get(OrganizationBusiness.ORG_ID).in(UserUtils.getOrgId()));
            // 省级
            predicateList.add(root.get(OrganizationBusiness.PROVINCE_CODE).in(dto.getProvinceCode()));
            // 市级
            if (dto.getCityCode() == null) {
                predicateList.add(root.get(OrganizationBusiness.CITY_CODE).isNull());
            } else {
                predicateList.add(root.get(OrganizationBusiness.CITY_CODE).in(dto.getCityCode()));
            }
            // 县级
            if (dto.getCityCode() == null) {
                predicateList.add(root.get(OrganizationBusiness.DISTRICT_CODE).isNull());
            } else {
                predicateList.add(root.get(OrganizationBusiness.DISTRICT_CODE).in(dto.getDistrictCode()));
            }
            assert query != null;
            return query.where(predicateList.toArray(Predicate[]::new)).getRestriction();
        });
        if (exists) {
            throw BizRuntimeException.from("已申请该地区业务，请重新选择");
        }
        OrganizationBusiness organizationBusiness = new OrganizationBusiness();
        // 因为新增时没有主键 id，所以在 copy 时不需要排除 id 字段
        BeanUtils.copyProperties(dto, organizationBusiness);
        // 省市县
        String province = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getProvinceCode());
        organizationBusiness.setProvinceName(province);
        if (dto.getCityCode() != null) {
            String city = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getCityCode());
            organizationBusiness.setProvinceName(city);
        }
        if (dto.getDistrictCode() != null) {
            String district = stringRedisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getDistrictCode());
            organizationBusiness.setProvinceName(district);
        }
        // TODO 晚些时候修改！
//        organizationBusiness.setLink(dto.getLink());
//        organizationBusiness.setType(dto.getType());
        Organization organization = organizationJpaRepository.getReferenceById(UserUtils.getOrgId());
        organizationBusiness.setOrgId(organization.getId());
        organizationBusiness.setOrgName(organization.getName());
        // 业务申请状态
        organizationBusiness.setState("待审核");
        // 业务级别
        OrganizationBusinessLevelEnum businessLevel = organizationBusiness.getDistrictCode() != null
                ? OrganizationBusinessLevelEnum.DISTRICT : organizationBusiness.getCityCode() != null
                ? OrganizationBusinessLevelEnum.CITY : OrganizationBusinessLevelEnum.PROVINCE;
        organizationBusiness.setBusinessLevel(businessLevel);
        organizationBusinessJpaRepository.save(organizationBusiness);

        // 同步到 ES
//        elasticsearchClient.create(CreateRequest.of(builder -> builder.index(OrganizationBusiness.INDEX)
//                .id(organizationBusiness.getId())
//                .document(organizationBusiness)));
    }

    /**
     * 申请页面更新业务
     *
     * @param dto 接收修改业务参数
     * @author Simon Von
     * @since 1/4/2023 4:57 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyUpdate(OrganizationBusinessDTO dto) throws IOException {
        OrganizationBusiness organizationBusiness = organizationBusinessJpaRepository.findById(dto.getId())
                .orElseThrow(() -> BizRuntimeException.from("没有找到该业务记录"));
        // TODO 晚些时候修改！
//        organizationBusiness.setLink(dto.getLink());
//        organizationBusiness.setType(dto.getType());
        organizationBusiness.setState("待审核");
        organizationBusinessJpaRepository.save(organizationBusiness);

        // 同步到 ES
//        elasticsearchClient.update(builder -> builder.index(OrganizationBusiness.INDEX)
//                        .id(organizationBusiness.getId())
//                        .doc(organizationBusiness),
//                OrganizationBusiness.class);

        // TODO 晚些时候修改！
//        for (OrganizationBusinessBusinessLinksEnum link : organizationBusiness.getLink()) {
//            OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum -> {
//                Role role = roleJpaRepository.findOne((root, _, _) ->
//                                root.get(Role.AUTHORITY).in("ADMIN_" + anEnum.name()))
//                        .orElseThrow(() -> BizRuntimeException.from("没有找到对应角色"));
//
//                // 如果该单位对应的通过的业务类型为空才进行删除操作
//                List<OrganizationBusiness> organizationBusinessList = organizationBusinessJpaRepository.findAll(
//                        (root, query, criteriaBuilder) -> {
//                            assert query != null;
//                            return query.where(root.get(OrganizationBusiness.ORG_ID).in(UserUtils.getOrgId()),
//                                    criteriaBuilder.like(root.get(OrganizationBusiness.LINK), "%" + link + "%", '/'),
//                                    root.get(OrganizationBusiness.STATE).in("已通过")).getRestriction();
//                        });
//                if (organizationBusinessList.isEmpty()) {
//                    userRoleJpaRepository.delete((root, _, criteriaBuilder) ->
//                            criteriaBuilder.and(root.get(UserRole.USER_ID).in(UserUtils.getId()),
//                                    root.get(UserRole.ROLE_ID).in(role.getId())));
//                }
//            });
//        }
//        for (String type : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType())) {
//            OrganizationBusinessQualityControlTypeEnum.getEnumByDesc(type).ifPresent(anEnum -> {
//                Role role = roleJpaRepository.findOne((root, _, _) ->
//                                root.get(Role.AUTHORITY).in("ADMIN_" + anEnum.name()))
//                        .orElseThrow(() -> BizRuntimeException.from("没有找到对应角色"));
//
//                // 如果该单位对应的通过的业务类型为空才进行删除操作
//                List<OrganizationBusiness> organizationBusinessList = organizationBusinessJpaRepository.findAll(
//                        (root, query, criteriaBuilder) -> {
//                            assert query != null;
//                            return query.where(root.get(OrganizationBusiness.ORG_ID).in(UserUtils.getOrgId()),
//                                    criteriaBuilder.like(root.get(OrganizationBusiness.TYPE), "%" + type + "%", '/'),
//                                    root.get(OrganizationBusiness.STATE).in("已通过")).getRestriction();
//                        });
//                if (organizationBusinessList.isEmpty()) {
//                    userRoleJpaRepository.delete((root, _, criteriaBuilder) ->
//                            criteriaBuilder.and(root.get(UserRole.USER_ID).in(UserUtils.getId()),
//                                    root.get(UserRole.ROLE_ID).in(role.getId())));
//                }
//            });
//        }
    }

    /**
     * 审核页面更新业务
     *
     * @param dto 接收修改业务参数
     * @author Simon Von
     * @since 2023/3/25 14:32
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkUpdate(OrganizationBusinessDTO dto) throws IOException {
        OrganizationBusiness organizationBusiness = organizationBusinessJpaRepository.findById(dto.getId())
                .orElseThrow(() -> BizRuntimeException.from("没有找到该业务记录"));
        organizationBusiness.setState(dto.getState());
        organizationBusinessJpaRepository.save(organizationBusiness);

        // 同步到 ES
//        elasticsearchClient.update(builder -> builder.index(OrganizationBusiness.INDEX)
//                        .id(organizationBusiness.getId())
//                        .doc(organizationBusiness),
//                OrganizationBusiness.class);

        User user = userJpaRepository.findOne((root, _, criteriaBuilder) ->
                        criteriaBuilder.and(root.get(User.ORG_ID).in(organizationBusiness.getOrgId()),
                                root.get(User.MANAGER).in(Boolean.TRUE)))
                .orElseThrow(() -> BizRuntimeException.from("没有找到该业务的单位管理员"));

        // TODO 晚些时候修改！
//        for (String link : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink())) {
//            OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum -> {
//                Role role = roleJpaRepository.findOne((root, _, _) ->
//                                root.get(Role.AUTHORITY).in("ADMIN_" + anEnum.name()))
//                        .orElseThrow(() -> BizRuntimeException.from("没有找到对应角色"));
//                if ("已通过".equals(dto.getState())) {
//                    // 如果当前用户没有当前角色才进行添加操作
//                    boolean exists = userRoleJpaRepository.exists((root, _, criteriaBuilder) ->
//                            criteriaBuilder.and(root.get(UserRole.USER_ID).in(user.getId()),
//                                    root.get(UserRole.ROLE_ID).in(role.getId())));
//                    if (!exists) {
//                        UserRole userRole = new UserRole();
//                        userRole.setUserId(user.getId());
//                        userRole.setRoleId(role.getId());
//                        userRoleJpaRepository.save(userRole);
//                    }
//                } else {
//                    // 如果该单位对应的通过的业务类型为空才进行删除操作
//                    List<OrganizationBusiness> organizationBusinessList = organizationBusinessJpaRepository.findAll(
//                            (root, query, criteriaBuilder) -> {
//                                assert query != null;
//                                return query.where(root.get(OrganizationBusiness.ORG_ID).in(user.getOrgId()),
//                                        criteriaBuilder.like(root.get(OrganizationBusiness.LINK), "%" + link + "%", '/'),
//                                        root.get(OrganizationBusiness.STATE).in("已通过")).getRestriction();
//                            });
//                    if (organizationBusinessList.isEmpty()) {
//                        userRoleJpaRepository.delete((root, _, criteriaBuilder) ->
//                                criteriaBuilder.and(root.get(UserRole.USER_ID).in(user.getId()),
//                                        root.get(UserRole.ROLE_ID).in(role.getId())));
//                    }
//                }
//            });
//        }
//        for (String type : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType())) {
//            OrganizationBusinessQualityControlTypeEnum.getEnumByDesc(type).ifPresent(anEnum -> {
//                Role role = roleJpaRepository.findOne((root, _, _) ->
//                                root.get(Role.AUTHORITY).in("ADMIN_" + anEnum.name()))
//                        .orElseThrow(() -> BizRuntimeException.from("没有找到对应角色"));
//                if ("已通过".equals(dto.getState())) {
//                    // 如果当前用户没有当前角色才进行添加操作
//                    boolean exists = userRoleJpaRepository.exists((root, _, criteriaBuilder) ->
//                            criteriaBuilder.and(root.get(UserRole.USER_ID).in(user.getId()),
//                                    root.get(UserRole.ROLE_ID).in(role.getId())));
//                    if (!exists) {
//                        UserRole userRole = new UserRole();
//                        userRole.setUserId(user.getId());
//                        userRole.setRoleId(role.getId());
//                        userRoleJpaRepository.save(userRole);
//                    }
//                } else {
//                    // 如果该单位对应的通过的业务类型为空才进行删除操作
//                    List<OrganizationBusiness> organizationBusinessList = organizationBusinessJpaRepository.findAll(
//                            (root, query, criteriaBuilder) -> {
//                                assert query != null;
//                                return query.where(root.get(OrganizationBusiness.ORG_ID).in(user.getOrgId()),
//                                        criteriaBuilder.like(root.get(OrganizationBusiness.TYPE), "%" + type + "%", '/'),
//                                        root.get(OrganizationBusiness.STATE).in("已通过")).getRestriction();
//                            });
//                    if (organizationBusinessList.isEmpty()) {
//                        userRoleJpaRepository.delete((root, _, criteriaBuilder) ->
//                                criteriaBuilder.and(root.get(UserRole.USER_ID).in(user.getId()),
//                                        root.get(UserRole.ROLE_ID).in(role.getId())));
//                    }
//                }
//            });
//        }
    }

}
