package com.shiminfxcvii.turing.repository;

import com.shiminfxcvii.turing.entity.OrganizationBusiness;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 行政区划 Repository 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2023-08-21 18:08:08
 */
@Repository
public interface OrganizationBusinessRepository extends JpaRepositoryImplementation<OrganizationBusiness, String> {
}
