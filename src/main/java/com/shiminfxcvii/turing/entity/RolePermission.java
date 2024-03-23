package com.shiminfxcvii.turing.entity;

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
 * 角色与权限关联记录表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_role_permission")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_role_permission SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@Where(clause = "deleted = FALSE")
public class RolePermission extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_role_permission";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String ROLE_ID = "roleId";
    public static final String PERMISSION_ID = "permissionId";

    /**
     * 角色 id
     */
    public String roleId;

    /**
     * 权限 id
     */
    public String permissionId;
}
