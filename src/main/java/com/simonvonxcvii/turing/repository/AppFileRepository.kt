package com.simonvonxcvii.turing.repository;

import com.simonvonxcvii.turing.entity.AppFile;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 菜单 Repository 接口
 * </p>
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
public interface AppFileRepository extends JpaRepositoryImplementation<AppFile, Integer> {
}
