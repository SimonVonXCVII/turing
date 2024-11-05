package com.simonvonxcvii.turing.entity;

import com.simonvonxcvii.turing.enums.FileTypeEnum;
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
 * 文件表
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2023-04-01 23:08:08
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_app_file")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_app_file SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
public class AppFile extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "turing_app_file";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    public static final String OWNER_ID = "ownerId";
    public static final String FILENAME = "filename";
    public static final String ORIGIN_FILENAME = "originFilename";
    public static final String SUFFIX = "suffix";
    public static final String CONTENT_TYPE = "contentType";
    public static final String CONTENT_LENGTH = "contentLength";
    public static final String MD5 = "md5";
    public static final String PATH = "path";
    public static final String BIZ_TYPE = "bizType";
    public static final String REMARK = "remark";

    /**
     * 所有者 id
     */
    private String ownerId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originFilename;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 内容长度
     */
    private Long contentLength;

    /**
     * md5
     */
    private String md5;

    /**
     * 存放路径
     */
    private String path;

    /**
     * 业务类型
     */
    private FileTypeEnum bizType;

    /**
     * 备注
     */
    private String remark;

}
