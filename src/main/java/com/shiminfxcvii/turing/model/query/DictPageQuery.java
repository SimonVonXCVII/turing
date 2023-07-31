package com.shiminfxcvii.turing.model.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictPageQuery extends PageQuery {

    /**
     * 字典类型
     */
    private String type;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典值
     */
    private String value;
}