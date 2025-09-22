package com.simonvonxcvii.turing.repository.jpa

import com.simonvonxcvii.turing.entity.AppFile
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 文件 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface AppFileJpaRepository : JpaRepositoryImplementation<AppFile, Int>/*, KeyValueRepository<AppFile, Int>*/
