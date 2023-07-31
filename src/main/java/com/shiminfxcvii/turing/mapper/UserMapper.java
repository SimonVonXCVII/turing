package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.model.dto.UserDTO;
import com.shiminfxcvii.turing.model.query.UserPageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-19 15:58:28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<UserDTO> getUserPage(Page<UserDTO> userDTOPage,
                               @Param("query") UserPageQuery query,
                               @Param("flag") boolean flag);

    void deleteManageOrgUser(@Param("userId") String userId);

    void deleteUsersByBusinessManageOrgId(@Param("orgId") String orgId);

    void deleteTechOrgUser(@Param("userId") String userId);
}