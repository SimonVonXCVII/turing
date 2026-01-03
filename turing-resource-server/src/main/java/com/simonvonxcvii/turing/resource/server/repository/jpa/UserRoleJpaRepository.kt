package com.simonvonxcvii.turing.resource.server.repository.jpa

import com.simonvonxcvii.turing.resource.server.entity.UserRole
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 行政区划 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface UserRoleJpaRepository : JpaRepositoryImplementation<UserRole, Int> {

    fun existsByRoleId(roleId: Int): Boolean

    fun findAllByUserId(userId: Int): MutableList<UserRole>

    fun deleteByUserIdIn(userIds: MutableCollection<Int>)

    fun findAllByRoleIdIn(roleIds: MutableCollection<Int>): MutableList<UserRole>

    fun deleteByUserId(userId: Int)

}
