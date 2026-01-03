package com.simonvonxcvii.turing.resource.server.repository.jpa

import com.simonvonxcvii.turing.resource.server.entity.Dict
import com.simonvonxcvii.turing.resource.server.enums.DictTypeEnum
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 行政区划 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-21 18:08:08
 */
@Repository
interface DictJpaRepository : JpaRepositoryImplementation<Dict, Int> {

    fun existsByType(type: DictTypeEnum): Boolean

    fun findAllByType(type: DictTypeEnum): MutableList<Dict>

    fun findAllByPidIsNullAndTypeEquals(type: DictTypeEnum, sort: Sort): MutableList<Dict>

    fun findOneByValueEqualsAndTypeEquals(value: String, type: DictTypeEnum): Optional<Dict>

    fun findAllByPidEqualsAndTypeEquals(pid: Int, type: DictTypeEnum, sort: Sort): MutableList<Dict>

}
