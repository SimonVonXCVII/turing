package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Accessors(chain = true)
@Getter
@Setter
public class RoleDTO extends PageQuery {
    /**
     * 角色 id
     */
    private Integer id;
    /**
     * 角色名称
     */
//    @NotBlank(message = "角色名称不能为空")
    private String name;
    /**
     * 角色编码
     */
//    @NotBlank(message = "角色编码不能为空")
    private String authority;
    /**
     * 状态
     */
    @NotNull(message = "状态")
    private Byte status;
    /**
     * 备注
     */
//    @NotBlank(message = "备注不能为空")
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createdDate;
    /**
     * 权限 id 集合
     */
//    @NotEmpty(message = "权限 id 集合不能为空")
    private Set<Integer> permissions;
    /**
     * 起始创建时间
     */
    private LocalDate startTime;
    /**
     * 截止创建时间
     */
    private LocalDate endTime;
}
