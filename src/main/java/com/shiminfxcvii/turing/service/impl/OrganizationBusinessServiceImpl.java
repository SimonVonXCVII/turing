package com.shiminfxcvii.turing.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.*;
import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessLevelEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessQualityControlTypeEnum;
import com.shiminfxcvii.turing.mapper.*;
import com.shiminfxcvii.turing.model.cmd.OrganizationBusinessCmd;
import com.shiminfxcvii.turing.model.dto.OrganizationBusinessDTO;
import com.shiminfxcvii.turing.model.query.OrganizationBusinessQuery;
import com.shiminfxcvii.turing.service.IOrganizationBusinessService;
import com.shiminfxcvii.turing.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 单位业务表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@RequiredArgsConstructor
@Service
public class OrganizationBusinessServiceImpl extends ServiceImpl<OrganizationBusinessMapper, OrganizationBusiness>
        implements IOrganizationBusinessService {

    private final OrganizationMapper organizationMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 单位管理员查询本单位已申请业务或者审核人员查询
     *
     * @param query 接收查询参数
     * @return 已申请业务
     * @author ShiminFXCVII
     * @since 1/5/2023 10:15 AM
     */
    @Override
    public IPage<OrganizationBusinessDTO> selectPage(OrganizationBusinessQuery query) throws IOException {
        SearchResponse<OrganizationBusiness> searchResponse = elasticsearchClient.search(searchRequest -> {
                    searchRequest.index(OrganizationBusiness.INDEX)
                            // 首页默认从 0 开始
                            .from(query.getPageIndex() - 1)
                            .size(query.getPageSize());
                    if (StringUtils.hasText(query.getOrgName())) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("orgName").query(query.getOrgName())))
                                // 高亮
                                .highlight(highlight -> highlight.fields("orgName", function1 -> function1)
                                        .preTags("<span style='color:red'>")
                                        .postTags("</span>"));
                    }
                    if (query.getProvinceCode() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("provinceCode").query(query.getProvinceCode())));
                    }
                    if (query.getCityCode() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("cityCode").query(query.getCityCode())));
                    }
                    if (query.getDistrictCode() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("districtCode").query(query.getDistrictCode())));
                    }
                    if (query.getLink() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("link").query(query.getLink())));
                    }
                    if (query.getType() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("type").query(query.getType())));
                    }
                    if (query.getState() != null) {
                        searchRequest.query(query1 -> query1.match(matchQuery -> matchQuery.field("state").query(query.getState())));
                    }
                    return searchRequest;
                },
                OrganizationBusiness.class);

        if (searchResponse.hits().total() == null) {
            throw BizRuntimeException.from("数据异常");
        }

        return new Page<OrganizationBusinessDTO>(query.getPageIndex(), query.getPageSize(), searchResponse.hits().total().value())
                .setRecords(searchResponse.hits()
                        .hits()
                        .stream()
                        .map(hit -> {
                            OrganizationBusiness organizationBusiness = hit.source();
                            if (organizationBusiness == null) {
                                throw BizRuntimeException.from("数据异常");
                            }
                            Map<String, List<String>> highlight = hit.highlight();
                            OrganizationBusinessDTO dto = new OrganizationBusinessDTO();
                            BeanUtils.copyProperties(organizationBusiness, dto);
                            if (!highlight.isEmpty()) {
                                dto.setOrgName(highlight.get("orgName").get(0));
                            }
                            // 业务环节
                            dto.setLink(StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink()));
                            // 质控类型
                            dto.setType(StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType()));
                            return dto;
                        })
                        .toList());
    }

    /**
     * 单位管理员在点击编辑前查询单条数据
     *
     * @param id 主键 id
     * @return 需要查询的数据
     * @author ShiminFXCVII
     * @since 1/5/2023 10:15 AM
     */
    @Override
    public OrganizationBusinessDTO getOneById(String id) throws IOException {
        GetResponse<OrganizationBusiness> organizationBusinessGetResponse = elasticsearchClient.get(GetRequest.of(
                builder -> builder.index(OrganizationBusiness.INDEX)
                        .id(id)), OrganizationBusiness.class);
        OrganizationBusiness organizationBusiness = organizationBusinessGetResponse.source();
        if (organizationBusiness == null) {
            throw BizRuntimeException.from("没有找到该业务记录");
        }
        OrganizationBusinessDTO dto = new OrganizationBusinessDTO();
        BeanUtils.copyProperties(organizationBusiness, dto);
        return dto;
    }

    /**
     * 申请业务
     *
     * @param cmd 接收新增业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(OrganizationBusinessCmd cmd) throws IOException {
        OrganizationBusiness organizationBusiness = lambdaQuery()
                .eq(OrganizationBusiness::getProvinceCode, cmd.getProvinceCode())
                .func(consumer -> {
                    if (cmd.getCityCode() == null) {
                        consumer.isNull(OrganizationBusiness::getCityCode);
                    } else {
                        consumer.eq(OrganizationBusiness::getCityCode, cmd.getCityCode());
                    }
                })
                .func(consumer -> {
                    if (cmd.getDistrictCode() == null) {
                        consumer.isNull(OrganizationBusiness::getDistrictCode);
                    } else {
                        consumer.eq(OrganizationBusiness::getDistrictCode, cmd.getDistrictCode());
                    }
                })
                .eq(OrganizationBusiness::getOrgId, UserUtils.getOrgId())
                .one();
        if (organizationBusiness != null) {
            throw BizRuntimeException.from("已申请该地区业务，请重新选择");
        }
        organizationBusiness = new OrganizationBusiness();
        // 因为新增时没有主键 id，所以在 copy 时不需要排除 id 字段
        BeanUtils.copyProperties(cmd, organizationBusiness);
        // 省市县
        String province = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getProvinceCode());
        organizationBusiness.setProvinceName(province);
        if (cmd.getCityCode() != null) {
            String city = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getCityCode());
            organizationBusiness.setProvinceName(city);
        }
        if (cmd.getDistrictCode() != null) {
            String district = (String) redisTemplate.opsForValue().get(Dict.REDIS_KEY_PREFIX + organizationBusiness.getDistrictCode());
            organizationBusiness.setProvinceName(district);
        }
        organizationBusiness.setLink(StringUtils.arrayToCommaDelimitedString(cmd.getLink()));
        organizationBusiness.setType(StringUtils.arrayToCommaDelimitedString(cmd.getType()));
        Organization organization = organizationMapper.selectById(UserUtils.getOrgId());
        if (organization != null) {
            organizationBusiness.setOrgId(organization.getId());
            organizationBusiness.setOrgName(organization.getName());
        }
        // 业务申请状态
        organizationBusiness.setState("待审核");
        // 业务级别
        organizationBusiness.setBusinessLevel(organizationBusiness.getDistrictCode() != null
                ? OrganizationBusinessLevelEnum.DISTRICT
                : organizationBusiness.getCityCode() != null ? OrganizationBusinessLevelEnum.CITY : OrganizationBusinessLevelEnum.PROVINCE);
        save(organizationBusiness);

        // 同步到 ES
        OrganizationBusiness finalOrganizationBusiness = organizationBusiness;
        elasticsearchClient.create(CreateRequest.of(builder -> builder.index(OrganizationBusiness.INDEX)
                .id(finalOrganizationBusiness.getId())
                .document(finalOrganizationBusiness)));
    }

    /**
     * 申请页面更新业务
     *
     * @param cmd 接收修改业务参数
     * @author ShiminFXCVII
     * @since 1/4/2023 4:57 PM
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyUpdate(OrganizationBusinessCmd cmd) throws IOException {
        OrganizationBusiness organizationBusiness = getById(cmd.getId());
        if (organizationBusiness == null) {
            throw BizRuntimeException.from("没有找到该业务记录");
        }
        organizationBusiness.setLink(StringUtils.arrayToCommaDelimitedString(cmd.getLink()));
        organizationBusiness.setType(StringUtils.arrayToCommaDelimitedString(cmd.getType()));
        organizationBusiness.setState("待审核");
        updateById(organizationBusiness);

        // 同步到 ES
        elasticsearchClient.update(builder -> builder.index(OrganizationBusiness.INDEX)
                        .id(organizationBusiness.getId())
                        .doc(organizationBusiness),
                OrganizationBusiness.class);

        for (String link : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink())) {
            OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum -> {
                Role role = roleMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getCode, "ADMIN_" + anEnum.name()));
                if (role == null) {
                    throw BizRuntimeException.from("没有找到对应角色");
                }

                // 如果该单位对应的通过的业务类型为空才进行删除操作
                List<OrganizationBusiness> organizationBusinessList = lambdaQuery()
                        .eq(OrganizationBusiness::getOrgId, UserUtils.getOrgId())
                        .like(OrganizationBusiness::getLink, link)
                        .eq(OrganizationBusiness::getState, "已通过")
                        .list();
                if (organizationBusinessList.isEmpty()) {
                    userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                            .eq(UserRole::getUserId, UserUtils.getUserId())
                            .eq(UserRole::getRoleId, role.getId()));
                }
            });
        }
        for (String type : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType())) {
            OrganizationBusinessQualityControlTypeEnum.getEnumByDesc(type).ifPresent(anEnum -> {
                Role role = roleMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getCode, "ADMIN_" + anEnum.name()));
                if (role == null) {
                    throw BizRuntimeException.from("没有找到对应角色");
                }

                // 如果该单位对应的通过的业务类型为空才进行删除操作
                List<OrganizationBusiness> organizationBusinessList = lambdaQuery()
                        .eq(OrganizationBusiness::getOrgId, UserUtils.getOrgId())
                        .like(OrganizationBusiness::getLink, type)
                        .eq(OrganizationBusiness::getState, "已通过")
                        .list();
                if (organizationBusinessList.isEmpty()) {
                    userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                            .eq(UserRole::getUserId, UserUtils.getUserId())
                            .eq(UserRole::getRoleId, role.getId()));
                }
            });
        }
    }

    /**
     * 审核页面更新业务
     *
     * @param cmd 接收修改业务参数
     * @author ShiminFXCVII
     * @since 2023/3/25 14:32
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkUpdate(OrganizationBusinessCmd cmd) throws IOException {
        OrganizationBusiness organizationBusiness = getById(cmd.getId());
        if (organizationBusiness == null) {
            throw BizRuntimeException.from("没有找到该业务记录");
        }
        organizationBusiness.setState(cmd.getState());
        updateById(organizationBusiness);

        // 同步到 ES
        elasticsearchClient.update(builder -> builder.index(OrganizationBusiness.INDEX)
                        .id(organizationBusiness.getId())
                        .doc(organizationBusiness),
                OrganizationBusiness.class);

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOrgId, organizationBusiness.getOrgId())
                .eq(User::getManager, Boolean.TRUE));
        if (user == null) {
            throw BizRuntimeException.from("没有找到该业务的单位管理员");
        }

        for (String link : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getLink())) {
            OrganizationBusinessBusinessLinksEnum.getEnumByDesc(link).ifPresent(anEnum -> {
                Role role = roleMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getCode, "ADMIN_" + anEnum.name()));
                if (role == null) {
                    throw BizRuntimeException.from("没有找到对应角色");
                }
                if ("已通过".equals(cmd.getState())) {
                    // 如果当前用户没有当前角色才进行添加操作
                    boolean exists = userRoleMapper.exists(Wrappers.<UserRole>lambdaQuery()
                            .eq(UserRole::getUserId, user.getId())
                            .eq(UserRole::getRoleId, role.getId()));
                    if (!exists) {
                        userRoleMapper.insert(new UserRole().setUserId(user.getId()).setRoleId(role.getId()));
                    }
                } else {
                    // 如果该单位对应的通过的业务类型为空才进行删除操作
                    List<OrganizationBusiness> organizationBusinessList = lambdaQuery()
                            .eq(OrganizationBusiness::getOrgId, user.getOrgId())
                            .like(OrganizationBusiness::getLink, link)
                            .eq(OrganizationBusiness::getState, "已通过")
                            .list();
                    if (organizationBusinessList.isEmpty()) {
                        userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                                .eq(UserRole::getUserId, user.getId())
                                .eq(UserRole::getRoleId, role.getId()));
                    }
                }
            });
        }
        for (String type : StringUtils.commaDelimitedListToStringArray(organizationBusiness.getType())) {
            OrganizationBusinessQualityControlTypeEnum.getEnumByDesc(type).ifPresent(anEnum -> {
                Role role = roleMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getCode, "ADMIN_" + anEnum.name()));
                if (role == null) {
                    throw BizRuntimeException.from("没有找到对应角色");
                }
                if ("已通过".equals(cmd.getState())) {
                    // 如果当前用户没有当前角色才进行添加操作
                    boolean exists = userRoleMapper.exists(Wrappers.<UserRole>lambdaQuery()
                            .eq(UserRole::getUserId, user.getId())
                            .eq(UserRole::getRoleId, role.getId()));
                    if (!exists) {
                        userRoleMapper.insert(new UserRole().setUserId(user.getId()).setRoleId(role.getId()));
                    }
                } else {
                    // 如果该单位对应的通过的业务类型为空才进行删除操作
                    List<OrganizationBusiness> organizationBusinessList = lambdaQuery()
                            .eq(OrganizationBusiness::getOrgId, user.getOrgId())
                            .like(OrganizationBusiness::getLink, type)
                            .eq(OrganizationBusiness::getState, "已通过")
                            .list();
                    if (organizationBusinessList.isEmpty()) {
                        userRoleMapper.delete(Wrappers.<UserRole>lambdaQuery()
                                .eq(UserRole::getUserId, user.getId())
                                .eq(UserRole::getRoleId, role.getId()));
                    }
                }
            });
        }
    }

}