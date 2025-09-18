package com.simonvonxcvii.turing.entity;

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
 * 权限表
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:49
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_permission")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_permission SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
public class Permission extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_permission";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String PID = "pid";
    public static final String NAME = "name";
    public static final String CODE = "code";
    public static final String SORT = "sort";

    /**
     * 上级权限 id
     */
    private Integer pid;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限编码
     */
    private String code;
    /**
     * 权限排序
     */
    private int sort;

}
