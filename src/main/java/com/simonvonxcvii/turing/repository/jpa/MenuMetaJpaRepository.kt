package com.simonvonxcvii.turing.repository.jpa

import com.simonvonxcvii.turing.entity.MenuMeta
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository
import java.util.*

/**
 * 菜单元数据 JpaRepository 接口
 *
 * @author Simon Von
 * @since 12/16/25 1:37 AM
 */
@Repository
interface MenuMetaJpaRepository : JpaRepositoryImplementation<MenuMeta, Int> {

    fun findOneByMenuId(menuId: Int): Optional<MenuMeta>

    fun deleteByMenuId(menuId: Int)

}
