package com.simonvonxcvii.turing.repository

import com.simonvonxcvii.turing.entity.Menu
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.stereotype.Repository

/**
 * 菜单 Repository 接口
 *
 * @author Simon Von
 * @since 2023-08-19 18:08:08
 */
@Repository
interface MenuRepository : JpaRepositoryImplementation<Menu, Int>
