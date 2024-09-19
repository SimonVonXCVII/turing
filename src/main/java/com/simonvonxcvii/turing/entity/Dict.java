package com.simonvonxcvii.turing.entity;

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
 * 字典表
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2022-12-30 12:49:40
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_dict")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_dict SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@Where(clause = "deleted = FALSE")
public class Dict extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "turing_dict";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    public static final String TYPE = "type";
    public static final String PID = "pid";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String SORT = "sort";

    /**
     * 字典类型
     */
    public String type;
    /**
     * 上级 id
     */
    public String pid;
    /**
     * 字典名称
     */
    public String name;
    /**
     * 字典值
     */
    public String value;
    /**
     * 说明
     */
    public String description;
    /**
     * 排序
     */
    public int sort;
}
