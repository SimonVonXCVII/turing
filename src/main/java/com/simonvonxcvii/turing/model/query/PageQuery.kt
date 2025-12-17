package com.simonvonxcvii.turing.model.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQuery {

    /**
     * 当前页数
     */
    private int page;
    /**
     * 当前页数大小
     */
    private int pageSize;

}
