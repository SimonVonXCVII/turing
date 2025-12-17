package com.simonvonxcvii.turing.repository.jpa

import com.simonvonxcvii.turing.entity.MenuMeta
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 菜单元数据 JpaRepository 接口
 *
 * @author Simon Von
 * @since 12/16/25 1:37 AM
 */
@Repository
interface MenuMetaJpaRepository : JpaRepositoryImplementation<MenuMeta, Int>
