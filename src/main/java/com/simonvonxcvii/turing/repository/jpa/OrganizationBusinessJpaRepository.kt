package com.simonvonxcvii.turing.repository.jpa

import com.simonvonxcvii.turing.entity.OrganizationBusiness
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 行政区划 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface OrganizationBusinessJpaRepository : JpaRepositoryImplementation<OrganizationBusiness, Int>
