package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.*
import com.simonvonxcvii.turing.enums.DictTypeEnum
import com.simonvonxcvii.turing.enums.MenuTypeEnum
import com.simonvonxcvii.turing.enums.OrganizationTypeEnum
import com.simonvonxcvii.turing.repository.jpa.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * 数据库初始化 runner
 * 需要保证在 RedisApplicationRunner 之前注册组件，所有改为了实现 InitializingBean
 * TODO 尝试在各个地方删除 : Any
 *
 * @author Simon Von
 * @since 2023/8/25 21:24
 */
@Component
class CustomDatabaseInitializingBean(
    private val organizationJpaRepository: OrganizationJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userJpaRepository: UserJpaRepository,
    private val roleJpaRepository: RoleJpaRepository,
    private val rolePermissionJpaRepository: RolePermissionJpaRepository,
    private val userRoleJpaRepository: UserRoleJpaRepository,
    private val permissionJpaRepository: PermissionJpaRepository,
    private val menuJpaRepository: MenuJpaRepository,
    private val dictJpaRepository: DictJpaRepository
) : InitializingBean {
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        // 判断是否需要初始化，如果表数据存在说明不需要
        val exists = dictJpaRepository.existsByType(DictTypeEnum.AREA)
        if (exists) {
            return
        }
//        val connection = dataSource.connection
//        connection.metaData.getTables(null, null, "turing_dict", null)
//            .next()
//            .run { if (this) return }

        // 创建数据库表
        // 已实现在服务启动时自动检测是否存在实体类对应的 table，不存在则根据实体类相关注解自动生成对应的 table
//        val classPathResourceTableSql = ClassPathResource("/db/schema.sql")
//        classPathResourceTableSql.exists()
//            .run {
//                if (!this) {
//                    log.warn("数据库表文件不存在，无法初始化")
//                    return
//                }
//            }
//        ScriptUtils.executeSqlScript(connection, classPathResourceTableSql)

        // 创建基础数据
        init()

        // 创建地区数据
        log.info("开始初始化区域字典")
        val classPathResourceAreaCsv = ClassPathResource("/dict/area.csv")
        classPathResourceAreaCsv.exists().run {
            if (!this) {
                log.warn("区域文件不存在，无法初始化")
                return
            }
        }

        val areaList = ArrayList<Area>()
        val provinceMap = TreeMap<String, Area>()
        classPathResourceAreaCsv.inputStream.use { inputStream ->
            InputStreamReader(inputStream).use { inputStreamReader ->
                BufferedReader(inputStreamReader).use { bufferedReader ->
                    var line: String?
                    // 解析区域文件
                    val aresCodeSet = HashSet<String>()
                    while (bufferedReader.readLine().also { line = it } != null) {
                        val oneArea = listOf(*StringUtils.commaDelimitedListToStringArray(line))
                        val area = Area()
                        area.name = oneArea[0]
                        area.adCode = oneArea[1]
                        area.cityCode = if (oneArea.size > 2) oneArea[2] else ""
                        aresCodeSet.add(area.adCode)
                        areaList.add(area)

                        if (area.adCode.endsWith("0000")) {
                            area.parentAdCode = null
                        } else if (area.adCode.endsWith("00")) {
                            area.parentAdCode = area.adCode.substring(0, 2) + "0000"
                        } else {
                            val cityCode = area.adCode.substring(0, 4) + "00"
                            if (aresCodeSet.contains(cityCode)) {
                                area.parentAdCode = cityCode
                            } else {
                                // 直辖县
                                area.parentAdCode = area.adCode.substring(0, 2) + "0000"
                            }
                        }
                    }
                }
            }
        }
        // 按层级
        val detailVoMap = areaList.stream()
            .collect(Collectors.toMap(Function { a: Area? -> a!!.adCode }, Function.identity<Area>()))
        val pid = Function { a: Area -> a.parentAdCode }
        val consumer = BiConsumer { parent: Area?, child: Area ->
            if (parent == null) {
                provinceMap[child.adCode] = child
            } else {
                parent.children.add(child)
            }
        }
        val pidWrapper = Function { t: Area ->
            val pid2 = pid.apply(t)
            if (!StringUtils.hasText(pid2)) {
                return@Function pid2
            }
            if (!pid2!!.contains(",")) {
                return@Function pid2
            }

            val ids = StringUtils.commaDelimitedListToStringArray(pid2)
            ids[ids.size - 1]
        }
        areaList.forEach(Consumer { node: Area ->
            val parentId = pidWrapper.apply(node)
            if (StringUtils.hasText(parentId)) {
                val parent = detailVoMap[parentId]
                consumer.accept(parent, node)
            } else {
                consumer.accept(null, node)
            }
        })
        val dictList: MutableList<Dict> = ArrayList<Dict>()
        provinceMap.forEach { (_: String, area: Area) -> saveArea(null, area, dictList) }
        dictJpaRepository.saveAll(dictList)
        log.info("初始化区域完成")
    }

    /**
     * 初始化后台数据管理、单位管理、用户管理、角色管理、权限管理、菜单管理、字典管理
     *
     * @author Simon Von
     * @since 2023/8/25 21:45
     */
    fun init() {
        val permission10000 = Permission(name = "工作台", code = "workspace")
        permissionJpaRepository.save(permission10000)
        val permission20000 = Permission(name = "系统管理", code = "system")
        permissionJpaRepository.save(permission20000)
        val permission20100 = Permission(pid = permission20000.id, name = "菜单管理", code = "menu")
        val permission20200 = Permission(pid = permission20000.id, name = "部门管理", code = "dept")
        val permission20300 = Permission(pid = permission20000.id, name = "单位管理", code = "organization")
        val permission20400 = Permission(pid = permission20000.id, name = "用户管理", code = "user")
        val permission20500 = Permission(pid = permission20000.id, name = "角色管理", code = "role")
        val permission20600 = Permission(pid = permission20000.id, name = "权限管理", code = "permission")
        val permission20700 = Permission(pid = permission20000.id, name = "字典管理", code = "dict")
        val permission20000List = listOfNotNull(
            permission20100,
            permission20200,
            permission20300,
            permission20400,
            permission20500,
            permission20600,
            permission20700
        )
        permissionJpaRepository.saveAll(permission20000List)
        val permission20101 = Permission(pid = permission20100.id, name = "新增", code = "System:Menu:Create")
        val permission20102 = Permission(pid = permission20100.id, name = "修改", code = "System:Menu:Edit")
        val permission20103 = Permission(pid = permission20100.id, name = "删除", code = "System:Menu:Delete")
        val permission20100List = listOfNotNull(
            permission20101,
            permission20102,
            permission20103
        )
        permissionJpaRepository.saveAll(permission20100List)


        val superRole = Role(name = "Super", authority = "Super", remark = "超级管理员")
        val adminRole = Role(name = "Admin", authority = "Admin", remark = "管理员")
        val userRole = Role(name = "User", authority = "User", remark = "用户")
        val roleList = listOfNotNull(
            superRole, adminRole, userRole
        )
        roleJpaRepository.saveAll(roleList)


        val rolePermissionList = listOfNotNull(
            RolePermission(adminRole, permission10000),
            RolePermission(adminRole, permission20000),
            RolePermission(adminRole, permission20100),
            RolePermission(adminRole, permission20200),
            RolePermission(adminRole, permission20300),
            RolePermission(adminRole, permission20400),
            RolePermission(adminRole, permission20500),
            RolePermission(adminRole, permission20600),
            RolePermission(adminRole, permission20700),
            RolePermission(adminRole, permission20101),
            RolePermission(adminRole, permission20102),
            RolePermission(adminRole, permission20103)
        )
        rolePermissionJpaRepository.saveAll(rolePermissionList)


        val menuMeta10000 = MenuMeta(title = "工作台")
        val menu10000 = Menu(
            name = "工作台",
            type = MenuTypeEnum.MENU,
            path = "/workspace",
            component = "/dashboard/workspace/index",
            meta = menuMeta10000
        )
        menuJpaRepository.save(menu10000)
        val menuMeta20000 = MenuMeta(title = "系统管理")
        val menu20000 = Menu(
            name = "系统管理",
            type = MenuTypeEnum.CATALOG,
            path = "/system",
            meta = menuMeta20000
        )
        menuJpaRepository.save(menu20000)
        val menuMeta20100 = MenuMeta(title = "菜单管理")
        val menuMeta20200 = MenuMeta(title = "部门管理")
        val menuMeta20300 = MenuMeta(title = "单位管理")
        val menuMeta20400 = MenuMeta(title = "用户管理")
        val menuMeta20500 = MenuMeta(title = "角色管理")
        val menuMeta20600 = MenuMeta(title = "权限管理")
        val menuMeta20700 = MenuMeta(title = "字典管理")
        val menu20100 = Menu(
            pid = menu20000.id,
            name = "菜单管理",
            type = MenuTypeEnum.MENU,
            path = "/system/menu",
            component = "/system/menu/list",
            meta = menuMeta20100
        )
        val menu20200 = Menu(
            pid = menu20000.id,
            name = "部门管理",
            type = MenuTypeEnum.MENU,
            path = "/system/menu",
            component = "/system/dept/list",
            meta = menuMeta20200
        )
        val menu20300 = Menu(
            pid = menu20000.id,
            name = "单位管理",
            type = MenuTypeEnum.MENU,
            path = "/system/organization",
            component = "/system/organization/list",
            meta = menuMeta20300
        )
        val menu20400 = Menu(
            pid = menu20000.id,
            name = "用户管理",
            type = MenuTypeEnum.MENU,
            path = "/system/user",
            component = "/system/user/list",
            meta = menuMeta20400
        )
        val menu20500 = Menu(
            pid = menu20000.id,
            name = "角色管理",
            type = MenuTypeEnum.MENU,
            path = "/system/role",
            component = "/system/role/list",
            meta = menuMeta20500
        )
        val menu20600 = Menu(
            pid = menu20000.id,
            name = "权限管理",
            type = MenuTypeEnum.MENU,
            path = "/system/permission",
            component = "/system/permission/list",
            meta = menuMeta20600
        )
        val menu20700 = Menu(
            pid = menu20000.id,
            name = "字典管理",
            type = MenuTypeEnum.MENU,
            path = "/system/dict",
            component = "/system/dict/list",
            meta = menuMeta20700
        )
        val menu20000List = listOfNotNull(
            menu20100, menu20200, menu20300, menu20400, menu20500, menu20600, menu20700
        )
        menuJpaRepository.saveAll(menu20000List)
        val menuMeta20101 = MenuMeta(title = "新增")
        val menuMeta20102 = MenuMeta(title = "修改")
        val menuMeta20103 = MenuMeta(title = "删除")
        val menu20101 = Menu(
            pid = menu20100.id,
            name = "新增",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Create",
            meta = menuMeta20101
        )
        val menu20102 = Menu(
            pid = menu20100.id,
            name = "修改",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Edit",
            meta = menuMeta20102
        )
        val menu20103 = Menu(
            pid = menu20100.id,
            name = "删除",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Delete",
            meta = menuMeta20103
        )
        val menu20100List = listOfNotNull(menu20101, menu20102, menu20103)
        menuJpaRepository.saveAll(menu20100List)


        val organization = Organization(
            name = "平台管理单位",
            code = "000000000000000000",
            legalPerson = "admin",
            phone = "021-88888888",
            type = OrganizationTypeEnum.PLATFORM,
            provinceCode = 310000,
            cityCode = 310100,
            districtCode = 310101,
            provinceName = "上海市",
            cityName = "上海市市辖区",
            districtName = "黄浦区",
            address = "太平路一号"
        )
        organizationJpaRepository.save(organization)
        // TODO 为什么保存后 created_by 和 last_modified_by 是 0，而不是 null？


        val user = User(
            realName = "admin",
            mobile = 18888888888,
            gender = "男",
            orgId = organization.id,
            orgName = organization.name,
            username = "admin",
            password = passwordEncoder.encode("123456")!!,
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
            manager = true,
            needResetPassword = false
        )
        userJpaRepository.save(user)


        val adminUserRole = UserRole(userId = user.id, roleId = adminRole.id)
        userRoleJpaRepository.save(adminUserRole)
    }

    /**
     * 保存区域数据
     */
    private fun saveArea(parent: Dict?, child: Area, dictList: MutableList<Dict>) {
        val dict = Dict(
            value = child.adCode, name = child.name, type = DictTypeEnum.AREA, sort = areaSort++, pid = parent?.id
        )
        dictList.add(dict)
        // 保存下级区域
        for (c in child.children) {
            saveArea(dict, c, dictList)
        }
    }

    private class Area {
        /**
         * 名称
         */
        lateinit var name: String

        /**
         * 编码
         */
        lateinit var adCode: String

        /**
         * zip编码
         */
        lateinit var cityCode: String

        /**
         * 上级编码
         */
        var parentAdCode: String? = null

        /**
         * 下级区域
         */
        var children: MutableList<Area> = ArrayList<Area>()
    }

    companion object {
        private val log = LogFactory.getLog(CustomDatabaseInitializingBean::class.java)

        /**
         * 字典排序
         */
        private var areaSort: Short = 0
    }
}
