package com.simonvonxcvii.turing.repository.jpa

import com.simonvonxcvii.turing.entity.Menu
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 菜单 JpaRepository 接口
 *
 * @author Simon Von
 * @since 2023-08-19 18:08:08
 */
@Repository
interface MenuJpaRepository : JpaRepositoryImplementation<Menu, Int> {

    fun existsByName(name: String): Boolean

    fun existsByNameAndIdNot(name: String, id: Int): Boolean

    fun existsByPath(path: String): Boolean

    fun existsByPathAndIdNot(path: String, id: Int): Boolean

    fun findByPid(pid: Int): MutableList<Menu>

    fun findByPidIn(pids: MutableCollection<Int>): MutableList<Menu>

}
