package com.simonvonxcvii.turing.resource.server.repository.jpa

import com.simonvonxcvii.turing.resource.server.entity.AppFile
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 文件 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface AppFileJpaRepository : JpaRepositoryImplementation<AppFile, Int> {

    fun findOneByMd5(md5: String): Optional<AppFile>

}
