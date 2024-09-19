package com.simonvonxcvii.turing.repository;

import com.simonvonxcvii.turing.entity.Role;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 行政区划 Repository 接口
 * </p>
 *
 * @author SimonVonXCVII
 * @since 2023-08-21 18:08:08
 */
@Repository
public interface RoleRepository extends JpaRepositoryImplementation<Role, String> {
}
