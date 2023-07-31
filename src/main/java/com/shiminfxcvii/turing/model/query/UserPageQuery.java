package com.shiminfxcvii.turing.model.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPageQuery extends PageQuery {

    private String username;

    private String nickName;

    private String mobile;

    private String orgName;
}