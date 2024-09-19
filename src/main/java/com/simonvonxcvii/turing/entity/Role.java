package com.simonvonxcvii.turing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-22 16:22:50
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_role")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_role SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@Where(clause = "deleted = FALSE")
public class Role extends AbstractAuditable implements GrantedAuthority {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_role";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String NAME = "name";
    public static final String AUTHORITY = "authority";
    public static final String DESCRIPTION = "description";
    /**
     * 角色编码
     */
    public String authority;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色说明
     */
    private String description;
}
