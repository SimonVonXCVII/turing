package com.simonvonxcvii.turing.repository;

import com.simonvonxcvii.turing.entity.Organization;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 行政区划 Repository 接口
 * </p>
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
public interface OrganizationRepository extends JpaRepositoryImplementation<Organization, String> {
}
