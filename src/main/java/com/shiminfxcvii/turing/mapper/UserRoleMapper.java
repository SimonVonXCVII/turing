package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiminfxcvii.turing.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色与用户关联记录表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    void deleteRecordsByUserIdLogically(@Param("userId") String userId);
}