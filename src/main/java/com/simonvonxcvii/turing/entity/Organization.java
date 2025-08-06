package com.simonvonxcvii.turing.entity;

import com.simonvonxcvii.turing.enums.OrganizationTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * <p>
 * 单位表
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_organization")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_organization SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
public class Organization extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_organization";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String PID = "pid";
    public static final String NAME = "name";
    public static final String CODE = "code";
    public static final String TYPE = "type";
    public static final String PROVINCE_CODE = "provinceCode";
    public static final String CITY_CODE = "cityCode";
    public static final String DISTRICT_CODE = "districtCode";
    public static final String ADDRESS = "address";
    public static final String LEGAL_PERSON = "legalPerson";
    public static final String PHONE = "phone";
    /**
     * 单位所在省编号
     */
    public int provinceCode;
    /**
     * 单位所在市编号
     */
    public int cityCode;
    /**
     * 单位所在县编号
     */
    public int districtCode;
    /**
     * 单位所在省名称
     */
    public String provinceName;
    /**
     * 单位所在市名称
     */
    public String cityName;
    /**
     * 单位所在县名称
     */
    public String districtName;
    /**
     * 单位性质
     */
    public transient String property;
    public transient String orgManagerId;
    public transient String orgManagerName;
    public transient String orgManagerMobile;
    public transient Integer status;
    public transient Integer remainingTime;
    public transient String openPersonName;
    public transient String orgLevel;
    /**
     * 上级单位 id
     */
    private String pid;
    /**
     * 单位名称
     */
    private String name;
    /**
     * 信用代码
     */
    private String code;
    /**
     * 单位类型
     *
     * @see OrganizationTypeEnum
     */
    private String type;
    /**
     * 单位地址详情
     */
    private String address;
    /**
     * 单位法人
     */
    private String legalPerson;
    /**
     * 联系电话
     */
    private String phone;

}
