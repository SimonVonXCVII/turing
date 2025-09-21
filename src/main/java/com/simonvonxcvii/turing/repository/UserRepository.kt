package com.simonvonxcvii.turing.repository

import com.simonvonxcvii.turing.entity.User
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository

/**
 * 行政区划 Repository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface UserRepository : JpaRepositoryImplementation<User, Int>,
    QueryByExampleExecutor<User> /*, JpaCriteriaQuery<User>*/

