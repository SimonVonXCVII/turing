package com.shiminfxcvii.turing.entity;

import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessLevelEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessQualityControlTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * <p>
 * 单位业务表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_organization_business")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_organization_business SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@Where(clause = "deleted = FALSE")
public class OrganizationBusiness extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "turing_organization_business";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    public static final String ORG_ID = "orgId";
    public static final String ORG_NAME = "orgName";
    public static final String LINK = "link";
    public static final String TYPE = "type";
    public static final String PROVINCE_CODE = "provinceCode";
    public static final String CITY_CODE = "cityCode";
    public static final String DISTRICT_CODE = "districtCode";
    public static final String PROVINCE_NAME = "provinceName";
    public static final String CITY_NAME = "cityName";
    public static final String DISTRICT_NAME = "districtName";
    public static final String STATE = "state";
    public static final String BUSINESS_LEVEL = "businessLevel";

    /**
     * 单位 id
     */
    private String orgId;

    /**
     * 单位名称
     */
    private String orgName;

    /**
     * 业务环节
     *
     * @see OrganizationBusinessBusinessLinksEnum
     */
    private String link;

    /**
     * 质控类型
     *
     * @see OrganizationBusinessQualityControlTypeEnum
     */
    private String type;

    /**
     * 业务申请所在省区号
     */
    private Integer provinceCode;

    /**
     * 业务申请所在市区号
     */
    private Integer cityCode;

    /**
     * 业务申请所在区县区号
     */
    private Integer districtCode;

    /**
     * 业务申请所在省名称
     */
    private String provinceName;

    /**
     * 业务申请所在市名称
     */
    private String cityName;

    /**
     * 业务申请所在区县名称
     */
    private String districtName;

    /**
     * 业务申请状态
     */
    private String state;

    /**
     * 申请业务级别
     */
    private OrganizationBusinessLevelEnum businessLevel;

}
