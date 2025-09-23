package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.*
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
class DatabaseInitializingBeanImpl(
    private val organizationJpaRepository: OrganizationJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userJpaRepository: UserJpaRepository,
    private val roleJpaRepository: RoleJpaRepository,
    private val userRoleJpaRepository: UserRoleJpaRepository,
    private val permissionJpaRepository: PermissionJpaRepository,
    private val menuJpaRepository: MenuJpaRepository,
    private val dictJpaRepository: DictJpaRepository
) : InitializingBean {
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        // 判断是否需要初始化，如果表数据存在说明不需要
        val exists = dictJpaRepository.exists { root, _, _ -> root.get<String>(Dict.TYPE).`in`("area") }
        if (exists) {
            return
        }
//        val connection = dataSource.connection
//        connection.metaData.getTables(null, null, "turing_dict", null)
//            .next()
//            .run { if (this) return }

        // 创建数据库表
        // 已实现在服务启动时自动检测是否存在实体类对应的 table，不存在则根据实体类相关注解自动生成对应的 table
//        val classPathResourceTableSql = ClassPathResource("/db/table.sql")
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
        classPathResourceAreaCsv.exists()
            .run {
                if (!this) {
                    log.warn("区域文件不存在，无法初始化")
                    return
                }
            }

        val areaList = ArrayList<Area>()
        val provinceMap = TreeMap<String, Area>()
        classPathResourceAreaCsv.getInputStream().use { inputStream ->
            InputStreamReader(inputStream).use { inputStreamReader ->
                BufferedReader(inputStreamReader).use { bufferedReader ->
                    var line: String?
                    // 解析区域文件
                    val aresCodeSet = HashSet<String>()
                    while ((bufferedReader.readLine().also { line = it }) != null) {
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
            name = "admin",
            mobile = 18888888888,
            gender = "男",
            orgId = organization.id,
            orgName = organization.name,
            username = "admin",
            password = passwordEncoder.encode("123456"),
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
            manager = true,
            needResetPassword = false
        )
        userJpaRepository.save(user)


        val role = Role(authority = "SUPER_ADMIN", name = "超级管理员")
        val roleList = listOfNotNull(
            role,
            Role(authority = "GOV_COUNTRY_ADMIN", name = "国家级行政单位管理员"),
            Role(authority = "GOV_COUNTRY_STAFF", name = "国家级行政单位工作人员"),
            Role(authority = "GOV_PROVINCE_ADMIN", name = "省级行政单位管理员"),
            Role(authority = "GOV_PROVINCE_STAFF", name = "省级行政单位工作人员"),
            Role(authority = "GOV_CITY_ADMIN", name = "市级行政单位管理员"),
            Role(authority = "GOV_CITY_STAFF", name = "市级行政单位工作人员"),
            Role(authority = "GOV_DISTRICT_ADMIN", name = "县级行政单位管理员"),
            Role(authority = "GOV_DISTRICT_STAFF", name = "县级行政单位工作人员"),
            Role(authority = "BUSINESS_MINE_INFORMATION_COLLECTION_ADMIN", name = "矿山信息采集单位管理员"),
            Role(authority = "BUSINESS_MINE_INFORMATION_COLLECTION_STAFF", name = "矿山信息采集单位工作人员"),
            Role(authority = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_ADMIN", name = "区域调查布点单位管理员"),
            Role(authority = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_STAFF", name = "区域调查布点单位工作人员"),
            Role(authority = "BUSINESS_AREA_SAMPLING_SURVEYS_ADMIN", name = "区域采样调查单位管理员"),
            Role(authority = "BUSINESS_AREA_SAMPLING_SURVEYS_STAFF", name = "区域采样调查单位工作人员"),
            Role(authority = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_ADMIN", name = "地块信息采集单位管理员"),
            Role(authority = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_STAFF", name = "地块信息采集单位工作人员"),
            Role(authority = "BUSINESS_PLOT_SURVEY_LAYOUT_ADMIN", name = "地块调查布点单位管理员"),
            Role(authority = "BUSINESS_PLOT_SURVEY_LAYOUT_STAFF", name = "地块调查布点单位工作人员"),
            Role(authority = "BUSINESS_PLOT_SAMPLING_SURVEY_ADMIN", name = "地块采样调查单位管理员"),
            Role(authority = "BUSINESS_PLOT_SAMPLING_SURVEY_STAFF", name = "地块采样调查单位工作人员"),
            Role(authority = "BUSINESS_SAMPLE_TESTING_ADMIN", name = "样品检测单位管理员"),
            Role(authority = "BUSINESS_SAMPLE_TESTING_STAFF", name = "样品检测单位工作人员"),
            Role(authority = "BUSINESS_DATA_ANALYSIS_EVALUATION_ADMIN", name = "数据分析评价单位管理员"),
            Role(authority = "BUSINESS_DATA_ANALYSIS_EVALUATION_STAFF", name = "数据分析评价单位工作人员"),
            Role(authority = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_ADMIN", name = "污染风险评估单位管理员"),
            Role(authority = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_STAFF", name = "污染风险评估单位工作人员"),
            Role(authority = "QC_INFORMATION_COLLECTION_ADMIN", name = "信息采集质控单位管理员"),
            Role(authority = "QC_INFORMATION_COLLECTION_STAFF", name = "信息采集质控单位工作人员"),
            Role(authority = "QC_DISTRIBUTE_ADMIN", name = "布点质控单位管理员"),
            Role(authority = "QC_DISTRIBUTE_STAFF", name = "布点质控单位工作人员"),
            Role(authority = "QC_SAMPLING_ADMIN", name = "采样质控单位管理员"),
            Role(authority = "QC_SAMPLING_STAFF", name = "采样质控单位工作人员"),
            Role(authority = "QC_SAMPLE_TESTING_ADMIN", name = "样品检测质控单位管理员"),
            Role(authority = "QC_SAMPLE_TESTING_STAFF", name = "样品检测质控单位工作人员"),
            Role(authority = "LEADER", name = "信息采集和取样调查小组组长"),
            Role(authority = "MEMBER", name = "信息采集和取样调查小组组员"),
            Role(authority = "INFORMATION_COLLECTION_INTERNAL_AUDITOR", name = "信息采集单位内审人员"),
            Role(authority = "PLOT_SAMPLING_SURVEY_INTERNAL_AUDITOR", name = "取样调查单位内审人员"),
            Role(
                authority = "DEFAULT_TECHNICAL",
                name = "技术单位管理员的默认角色",
                description = "注册技术单位时赋予管理员的默认角色，仅有【技术单位业务申请】权限"
            )
        )
        roleJpaRepository.saveAll(roleList)


        val userRole = UserRole(userId = user.id, roleId = role.id)
        userRoleJpaRepository.save(userRole)


        val permission100 = Permission(name = "基础数据管理", sort = 100)
        permissionJpaRepository.save(permission100)
        val permission101 = Permission(pid = permission100.id, name = "业务管理账号开通", sort = 101)
        val permission102 = Permission(pid = permission100.id, name = "业务管理单位授权", sort = 102)
        val permission103 = Permission(pid = permission100.id, name = "技术单位信息管理", sort = 103)
        val permission104 = Permission(pid = permission100.id, name = "技术专家信息维护", sort = 104)
        val permission105 = Permission(pid = permission100.id, name = "数据字典管理", sort = 105)
        val permission106 = Permission(pid = permission100.id, name = "设备终端授权管理", sort = 106)
        val permission107 = Permission(pid = permission100.id, name = "平台用户新增维护", sort = 107)
        val permission100List = listOfNotNull(
            permission101,
            permission102,
            permission103,
            permission104,
            permission105,
            permission106,
            permission107
        )
        permissionJpaRepository.saveAll(permission100List)
        val permission200 = Permission(name = "用户单位管理", sort = 200)
        permissionJpaRepository.save(permission200)
        val permission201 = Permission(pid = permission200.id, name = "管理单位用户维护", sort = 201)
        val permission202 = Permission(pid = permission200.id, name = "技术单位业务申请", sort = 202)
        val permission203 = Permission(pid = permission200.id, name = "技术单位业务审核", sort = 203)
        val permission204 = Permission(pid = permission200.id, name = "技术单位用户维护", sort = 204)
        val permission205 = Permission(pid = permission200.id, name = "技术专家业务申请", sort = 205)
        val permission206 = Permission(pid = permission200.id, name = "技术专家业务审核", sort = 206)
        val permission207 = Permission(pid = permission200.id, name = "调查小组新建维护", sort = 207)
        val permission208 = Permission(pid = permission200.id, name = "调查单位信息查询", sort = 208)
        val permission209 = Permission(pid = permission200.id, name = "调查用户信息查询", sort = 209)
        val permission200List = listOfNotNull(
            permission201,
            permission202,
            permission203,
            permission204,
            permission205,
            permission206,
            permission207,
            permission208,
            permission209
        )
        permissionJpaRepository.saveAll(permission200List)
        val permission300 = Permission(name = "项目任务管理", sort = 300)
        permissionJpaRepository.save(permission300)
        val permission301 = Permission(pid = permission300.id, name = "项目新增维护", sort = 301)
        val permission302 = Permission(pid = permission300.id, name = "监管对象维护", sort = 302)
        val permission303 = Permission(pid = permission300.id, name = "任务下发管理", sort = 303)
        val permission304 = Permission(pid = permission300.id, name = "牵头单位实施", sort = 304)
        val permission305 = Permission(pid = permission300.id, name = "监管对象信息查询", sort = 305)
        val permission306 = Permission(pid = permission300.id, name = "技术单位任务执行", sort = 306)
        val permission300List = listOfNotNull(
            permission301,
            permission302,
            permission303,
            permission304,
            permission305,
            permission306
        )
        permissionJpaRepository.saveAll(permission300List)
        val permission400 = Permission(name = "点位布设管理", sort = 400)
        permissionJpaRepository.save(permission400)
        val permission401 = Permission(pid = permission400.id, name = "测试项目新增维护", sort = 401)
        val permission402 = Permission(pid = permission400.id, name = "测试项目分类管理", sort = 402)
        val permission403 = Permission(pid = permission400.id, name = "布点人员任务分配", sort = 403)
        val permission404 = Permission(pid = permission400.id, name = "布点数据成果录入", sort = 404)
        val permission405 = Permission(pid = permission400.id, name = "布点方案数据退回", sort = 405)
        val permission406 = Permission(pid = permission400.id, name = "布点方案数据查询", sort = 406)
        val permission407 = Permission(pid = permission400.id, name = "新增测试项目审核", sort = 407)
        val permission400List = listOfNotNull(
            permission401,
            permission402,
            permission403,
            permission404,
            permission405,
            permission406,
            permission407
        )
        permissionJpaRepository.saveAll(permission400List)
        val permission500 = Permission(name = "点位布设", sort = 500)
        permissionJpaRepository.save(permission500)
        val permission501 = Permission(pid = permission500.id, name = "布点方案问题整改", sort = 501)
        val permission502 = Permission(pid = permission500.id, name = "布点方案数据维护", sort = 502)
        val permission503 = Permission(pid = permission500.id, name = "布点方案信息查询", sort = 503)
        val permission500List = listOfNotNull(
            permission501,
            permission502,
            permission503
        )
        permissionJpaRepository.saveAll(permission500List)
        val permission600 = Permission(name = "布点质控管理", sort = 600)
        permissionJpaRepository.save(permission600)
        val permission601 = Permission(pid = permission600.id, name = "布点质控专家组维护", sort = 601)
        val permission602 = Permission(pid = permission600.id, name = "一级质控(县)任务分配", sort = 602)
        val permission603 = Permission(pid = permission600.id, name = "二级质控(市)任务分配", sort = 603)
        val permission604 = Permission(pid = permission600.id, name = "三级质控(省)任务分配", sort = 604)
        val permission605 = Permission(pid = permission600.id, name = "布点质控专家组任务", sort = 605)
        val permission606 = Permission(pid = permission600.id, name = "布点质控意见反馈", sort = 606)
        val permission607 = Permission(pid = permission600.id, name = "布点质控专家查询", sort = 607)
        val permission608 = Permission(pid = permission600.id, name = "布点质控意见退回", sort = 608)
        val permission600List = listOfNotNull(
            permission601,
            permission602,
            permission603,
            permission604,
            permission605,
            permission606,
            permission607,
            permission608
        )
        permissionJpaRepository.saveAll(permission600List)
        val permission700 = Permission(name = "采样调查管理", sort = 700)
        permissionJpaRepository.save(permission700)
        val permission701 = Permission(pid = permission700.id, name = "牵头单位组织实施", sort = 701)
        val permission702 = Permission(pid = permission700.id, name = "采样小组任务分配", sort = 702)
        val permission703 = Permission(pid = permission700.id, name = "采样调查信息查询", sort = 703)
        val permission704 = Permission(pid = permission700.id, name = "检测子样进度查询", sort = 704)
        val permission705 = Permission(pid = permission700.id, name = "取样调查表单下载", sort = 705)
        val permission706 = Permission(pid = permission700.id, name = "采样资料单位内审", sort = 706)
        val permission707 = Permission(pid = permission700.id, name = "质控退回样点查询", sort = 707)
        val permission708 = Permission(pid = permission700.id, name = "批次样品运送表单", sort = 708)
        val permission709 = Permission(pid = permission700.id, name = "单位取样进展统计", sort = 709)
        val permission710 = Permission(pid = permission700.id, name = "资料内审状态查询", sort = 710)
        val permission711 = Permission(pid = permission700.id, name = "资料内审进展统计", sort = 711)
        val permission712 = Permission(pid = permission700.id, name = "重采样品信息查询", sort = 712)
        val permission713 = Permission(pid = permission700.id, name = "严重质量问题申诉", sort = 713)
        val permission700List = listOfNotNull(
            permission701,
            permission702,
            permission703,
            permission704,
            permission705,
            permission706,
            permission707,
            permission708,
            permission709,
            permission710,
            permission711,
            permission712,
            permission713
        )
        permissionJpaRepository.saveAll(permission700List)
        val permission800 = Permission(name = "取样调查", sort = 800)
        permissionJpaRepository.save(permission800)
        val permission801 = Permission(pid = permission800.id, name = "取样调查表单明细", sort = 801)
        val permission802 = Permission(pid = permission800.id, name = "质控退回样点明细", sort = 802)
        val permission803 = Permission(pid = permission800.id, name = "严重问题申诉记录", sort = 803)
        val permission804 = Permission(pid = permission800.id, name = "重采样品信息明细", sort = 804)
        val permission800List = listOfNotNull(
            permission801,
            permission802,
            permission803,
            permission804
        )
        permissionJpaRepository.saveAll(permission800List)
        val permission900 = Permission(name = "采样质控管理", sort = 900)
        permissionJpaRepository.save(permission900)
        val permission901 = Permission(pid = permission900.id, name = "采样一级质控(县)任务", sort = 901)
        val permission902 = Permission(pid = permission900.id, name = "采样二级质控(市)任务", sort = 902)
        val permission903 = Permission(pid = permission900.id, name = "采样三级质控(省)任务", sort = 903)
        val permission904 = Permission(pid = permission900.id, name = "采样质控专家任务", sort = 904)
        val permission905 = Permission(pid = permission900.id, name = "采样质控意见反馈", sort = 905)
        val permission906 = Permission(pid = permission900.id, name = "取样资料质控进度", sort = 906)
        val permission907 = Permission(pid = permission900.id, name = "采样质控专家查询", sort = 907)
        val permission908 = Permission(pid = permission900.id, name = "采样质控意见退回", sort = 908)
        val permission900List = listOfNotNull(
            permission901,
            permission902,
            permission903,
            permission904,
            permission905,
            permission906,
            permission907,
            permission908
        )
        permissionJpaRepository.saveAll(permission900List)
        val permission1000 = Permission(name = "样品检测管理", sort = 1000)
        permissionJpaRepository.save(permission1000)
        val permission1001 = Permission(pid = permission1000.id, name = "批次送检样交接单", sort = 1001)
        val permission1002 = Permission(pid = permission1000.id, name = "检测子样信息查询", sort = 1002)
        val permission1003 = Permission(pid = permission1000.id, name = "检测资质文件报送", sort = 1003)
        val permission1004 = Permission(pid = permission1000.id, name = "检测资质能力审核", sort = 1004)
        val permission1005 = Permission(pid = permission1000.id, name = "基本检测方法标准", sort = 1005)
        val permission1006 = Permission(pid = permission1000.id, name = "地方新增检测方法", sort = 1006)
        val permission1007 = Permission(pid = permission1000.id, name = "方法验证材料上传", sort = 1007)
        val permission1008 = Permission(pid = permission1000.id, name = "方法验证材料审核", sort = 1008)
        val permission1009 = Permission(pid = permission1000.id, name = "统一监控样品管理", sort = 1009)
        val permission1010 = Permission(pid = permission1000.id, name = "统一监控样品查询", sort = 1010)
        val permission1011 = Permission(pid = permission1000.id, name = "严重问题样品查询", sort = 1011)
        val permission1012 = Permission(pid = permission1000.id, name = "批次检测数据报送", sort = 1012)
        val permission1013 = Permission(pid = permission1000.id, name = "批次检测数据整改", sort = 1013)
        val permission1014 = Permission(pid = permission1000.id, name = "样品检测数据查询", sort = 1014)
        val permission1000List = listOfNotNull(
            permission1001,
            permission1002,
            permission1003,
            permission1004,
            permission1005,
            permission1006,
            permission1007,
            permission1008,
            permission1009,
            permission1010,
            permission1011,
            permission1012,
            permission1013,
            permission1014
        )
        permissionJpaRepository.saveAll(permission1000List)
        val permission1100 = Permission(name = "数据质量审核", sort = 1100)
        permissionJpaRepository.save(permission1100)
        val permission1101 = Permission(pid = permission1100.id, name = "检测一级质控(县)任务", sort = 1101)
        val permission1102 = Permission(pid = permission1100.id, name = "检测二级质控(市)任务", sort = 1102)
        val permission1103 = Permission(pid = permission1100.id, name = "检测三级质控(省)任务", sort = 1103)
        val permission1104 = Permission(pid = permission1100.id, name = "检测质控专家任务", sort = 1104)
        val permission1105 = Permission(pid = permission1100.id, name = "检测质控意见反馈", sort = 1105)
        val permission1106 = Permission(pid = permission1100.id, name = "质控退改批次查询", sort = 1106)
        val permission1100List = listOfNotNull(
            permission1101,
            permission1102,
            permission1103,
            permission1104,
            permission1105,
            permission1106
        )
        permissionJpaRepository.saveAll(permission1100List)
        val permission1200 = Permission(name = "数据统计分析", sort = 1200)
        permissionJpaRepository.save(permission1200)
        val permission1300 = Permission(name = "数据对标评价", sort = 1300)
        permissionJpaRepository.save(permission1300)
        val permission1400 = Permission(name = "工作文件管理", sort = 1400)
        permissionJpaRepository.save(permission1400)
        val permission1401 = Permission(pid = permission1400.id, name = "工作文件下载", sort = 1401)
        permissionJpaRepository.save(permission1401)
        val permission1500 = Permission(name = "后台数据管理", sort = 1500)
        permissionJpaRepository.save(permission1500)
        val permission1501 = Permission(pid = permission1500.id, name = "单位管理", sort = 1501)
        val permission1502 = Permission(pid = permission1500.id, name = "用户管理", sort = 1502)
        val permission1503 = Permission(pid = permission1500.id, name = "角色管理", sort = 1503)
        val permission1504 = Permission(pid = permission1500.id, name = "权限管理", sort = 1504)
        val permission1505 = Permission(pid = permission1500.id, name = "菜单管理", sort = 1505)
        val permission1506 = Permission(pid = permission1500.id, name = "字典管理", sort = 1506)
        val permission1500List = listOfNotNull(
            permission1501,
            permission1502,
            permission1503,
            permission1504,
            permission1505,
            permission1506
        )
        permissionJpaRepository.saveAll(permission1500List)


        val menu100 = Menu(
            permissionId = permission100.id,
            name = "基础数据管理",
            title = "基础数据管理",
            type = "ROUTE",
            path = "/basic",
            component = "LAYOUT",
            sort = 100,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu100)
        val menu101 = Menu(
            pid = menu100.id,
            permissionId = permission101.id,
            name = "业务管理账号开通",
            title = "业务管理账号开通",
            type = "ROUTE",
            path = "/basic/accountOpen",
            component = "/basic/accountOpen/index",
            sort = 101,
            showed = true,
            cached = true,
            external = false
        )
        val menu102 = Menu(
            pid = menu100.id,
            permissionId = permission102.id,
            name = "业务管理单位授权",
            title = "业务管理单位授权",
            type = "ROUTE",
            path = "/basic/empower",
            component = "/basic/empower/index",
            sort = 102,
            showed = true,
            cached = true,
            external = false
        )
        val menu103 = Menu(
            pid = menu100.id,
            permissionId = permission103.id,
            name = "技术单位信息管理",
            title = "技术单位信息管理",
            type = "ROUTE",
            path = "/basic/orgInfoManage",
            component = "/basic/orgInfoManage/index",
            sort = 103,
            showed = true,
            cached = true,
            external = false
        )
        val menu104 = Menu(
            pid = menu100.id,
            permissionId = permission104.id,
            name = "技术专家信息维护",
            title = "技术专家信息维护",
            type = "ROUTE",
            path = "/basic/d",
            component = "/basic/d/index",
            sort = 104,
            showed = true,
            cached = true,
            external = false
        )
        val menu105 = Menu(
            pid = menu100.id,
            permissionId = permission105.id,
            name = "平台新增用户维护",
            title = "平台新增用户维护",
            type = "ROUTE",
            path = "/basic/newUsersManage",
            component = "/basic/newUsersManage/index",
            sort = 105,
            showed = true,
            cached = true,
            external = false
        )
        val menu106 = Menu(
            pid = menu100.id,
            permissionId = permission106.id,
            name = "设备终端授权管理",
            title = "设备终端授权管理",
            type = "ROUTE",
            path = "/basic/f",
            component = "/basic/f/index",
            sort = 106,
            showed = true,
            cached = true,
            external = false
        )
        val menu100List = listOfNotNull(
            menu101,
            menu102,
            menu103,
            menu104,
            menu105,
            menu106
        )
        menuJpaRepository.saveAll(menu100List)
        val menu200 = Menu(
            permissionId = permission200.id,
            name = "用户单位管理",
            title = "用户单位管理",
            type = "ROUTE",
            path = "/organization",
            component = "LAYOUT",
            sort = 200,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu200)
        val menu201 = Menu(
            pid = menu200.id,
            permissionId = permission201.id,
            name = "管理单位用户维护",
            title = "管理单位用户维护",
            type = "ROUTE",
            path = "/organization/manageUnitUser",
            component = "/organization/manageUnitUser/index",
            sort = 201,
            showed = true,
            cached = true,
            external = false
        )
        val menu202 = Menu(
            pid = menu200.id,
            permissionId = permission202.id,
            name = "技术单位业务申请",
            title = "技术单位业务申请",
            type = "ROUTE",
            path = "/organization/organizationBusinessApply",
            component = "/organization/organizationBusinessApply/index",
            sort = 202,
            showed = true,
            cached = true,
            external = false
        )
        val menu203 = Menu(
            pid = menu200.id,
            permissionId = permission203.id,
            name = "技术单位业务审核",
            title = "技术单位业务审核",
            type = "ROUTE",
            path = "/organization/organizationBusinessCheck",
            component = "/organization/organizationBusinessCheck/index",
            sort = 203,
            showed = true,
            cached = true,
            external = false
        )
        val menu204 = Menu(
            pid = menu200.id,
            permissionId = permission204.id,
            name = "技术单位用户维护",
            title = "技术单位用户维护",
            type = "ROUTE",
            path = "/organization/technicalUnitUser",
            component = "/organization/technicalUnitUser/index",
            sort = 204,
            showed = true,
            cached = true,
            external = false
        )
        val menu205 = Menu(
            pid = menu200.id,
            permissionId = permission205.id,
            name = "技术专家业务申请",
            title = "技术专家业务申请",
            type = "ROUTE",
            path = "/organization/e",
            component = "/organization/e/index",
            sort = 205,
            showed = true,
            cached = true,
            external = false
        )
        val menu206 = Menu(
            pid = menu200.id,
            permissionId = permission206.id,
            name = "技术专家业务审核",
            title = "技术专家业务审核",
            type = "ROUTE",
            path = "/organization/f",
            component = "/organization/f/index",
            sort = 206,
            showed = true,
            cached = true,
            external = false
        )
        val menu207 = Menu(
            pid = menu200.id,
            permissionId = permission207.id,
            name = "调查小组新建维护",
            title = "调查小组新建维护",
            type = "ROUTE",
            path = "/organization/groupAddOrUpt",
            component = "/organization/groupAddOrUpt/index",
            sort = 207,
            showed = true,
            cached = true,
            external = false
        )
        val menu208 = Menu(
            pid = menu200.id,
            permissionId = permission208.id,
            name = "调查单位信息查询",
            title = "调查单位信息查询",
            type = "ROUTE",
            path = "/organization/unitInformationQuery",
            component = "/organization/unitInformationQuery/index",
            sort = 208,
            showed = true,
            cached = true,
            external = false
        )
        val menu209 = Menu(
            pid = menu200.id,
            permissionId = permission209.id,
            name = "调查用户信息查询",
            title = "调查用户信息查询",
            type = "ROUTE",
            path = "/organization/userInformationQuery",
            component = "/organization/userInformationQuery/index",
            sort = 209,
            showed = true,
            cached = true,
            external = false
        )
        val menu200List = listOfNotNull(
            menu201,
            menu202,
            menu203,
            menu204,
            menu205,
            menu206,
            menu207,
            menu208,
            menu209
        )
        menuJpaRepository.saveAll(menu200List)
        val menu300 = Menu(
            permissionId = permission300.id,
            name = "项目任务管理",
            title = "项目任务管理",
            type = "ROUTE",
            path = "/projectTask",
            component = "LAYOUT",
            sort = 300,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu300)
        val menu301 = Menu(
            pid = menu300.id,
            permissionId = permission301.id,
            name = "调查项目新增维护",
            title = "调查项目新增维护",
            type = "ROUTE",
            path = "/projectTask/a",
            component = "/projectTask/a/index",
            sort = 301,
            showed = true,
            cached = true,
            external = false
        )
        val menu302 = Menu(
            pid = menu300.id,
            permissionId = permission302.id,
            name = "监管对象新增维护",
            title = "监管对象新增维护",
            type = "ROUTE",
            path = "/projectTask/superviseObjectMaintain",
            component = "/projectTask/superviseObjectMaintain/index",
            sort = 302,
            showed = true,
            cached = true,
            external = false
        )
        val menu303 = Menu(
            pid = menu300.id,
            permissionId = permission303.id,
            name = "工作任务下发管理",
            title = "工作任务下发管理",
            type = "ROUTE",
            path = "/projectTask/taskDistribution",
            component = "/projectTask/taskDistribution/index",
            sort = 303,
            showed = true,
            cached = true,
            external = false
        )
        val menu304 = Menu(
            pid = menu300.id,
            permissionId = permission304.id,
            name = "牵头单位组织实施",
            title = "牵头单位组织实施",
            type = "ROUTE",
            path = "/projectTask/leadOrgImplement",
            component = "/projectTask/leadOrgImplement/index",
            sort = 304,
            showed = true,
            cached = true,
            external = false
        )
        val menu305 = Menu(
            pid = menu300.id,
            permissionId = permission305.id,
            name = "技术单位任务执行",
            title = "技术单位任务执行",
            type = "ROUTE",
            path = "/projectTask/taskExecution",
            component = "/projectTask/taskExecution/index",
            sort = 305,
            showed = true,
            cached = true,
            external = false
        )
        val menu306 = Menu(
            pid = menu300.id,
            permissionId = permission306.id,
            name = "监管对象信息查询",
            title = "监管对象信息查询",
            type = "ROUTE",
            path = "/projectTask/supervisionInformationQuery",
            component = "/projectTask/supervisionInformationQuery/index",
            sort = 306,
            showed = true,
            cached = true,
            external = false
        )
        val menu300List = listOfNotNull(
            menu301,
            menu302,
            menu303,
            menu304,
            menu305,
            menu306
        )
        menuJpaRepository.saveAll(menu300List)
        val menu400 = Menu(
            permissionId = permission400.id,
            name = "点位布设管理",
            title = "点位布设管理",
            type = "ROUTE",
            path = "/pointManage",
            component = "LAYOUT",
            sort = 400,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu400)
        val menu401 = Menu(
            pid = menu400.id,
            permissionId = permission401.id,
            name = "测试项目新增维护",
            title = "测试项目新增维护",
            type = "ROUTE",
            path = "/pointManage/testMaintenance",
            component = "/pointManage/testMaintenance/index",
            sort = 401,
            showed = true,
            cached = true,
            external = false
        )
        val menu402 = Menu(
            pid = menu400.id,
            permissionId = permission402.id,
            name = "测试项目分类管理",
            title = "测试项目分类管理",
            type = "ROUTE",
            path = "/pointManage/testClassification",
            component = "/pointManage/testClassification/index",
            sort = 402,
            showed = true,
            cached = true,
            external = false
        )
        val menu403 = Menu(
            pid = menu400.id,
            permissionId = permission403.id,
            name = "新增测试项目审核",
            title = "新增测试项目审核",
            type = "ROUTE",
            path = "/pointManage/addTestItemAudit",
            component = "/pointManage/addTestItemAudit/index",
            sort = 403,
            showed = true,
            cached = true,
            external = false
        )
        val menu404 = Menu(
            pid = menu400.id,
            permissionId = permission404.id,
            name = "布点人员任务分配",
            title = "布点人员任务分配",
            type = "ROUTE",
            path = "/pointManage/assignPersonnelTask",
            component = "/pointManage/assignPersonnelTask/index",
            sort = 404,
            showed = true,
            cached = true,
            external = false
        )
        val menu405 = Menu(
            pid = menu400.id,
            permissionId = permission405.id,
            name = "布点数据成果录入",
            title = "布点数据成果录入",
            type = "ROUTE",
            // TODO 应该是 pointManage 吧？
            path = "/pointUserTasks/LayOutMethodMaintain",
            component = "/pointUserTasks/LayOutMethodMaintain/index",
            sort = 405,
            showed = true,
            cached = true,
            external = false
        )
        val menu406 = Menu(
            pid = menu400.id,
            permissionId = permission406.id,
            name = "布点方案数据退回",
            title = "布点方案数据退回",
            type = "ROUTE",
            path = "/pointManage/dotsDataReturned",
            component = "/pointManage/dotsDataReturned/index",
            sort = 406,
            showed = true,
            cached = true,
            external = false
        )
        val menu407 = Menu(
            pid = menu400.id,
            permissionId = permission407.id,
            name = "布点方案数据查询",
            title = "布点方案数据查询",
            type = "ROUTE",
            // TODO 应该是 pointManage 吧？
            path = "/point/f",
            component = "/point/f/index",
            sort = 407,
            showed = true,
            cached = true,
            external = false
        )
        val menu400List = listOfNotNull(
            menu401,
            menu402,
            menu403,
            menu404,
            menu405,
            menu406,
            menu407
        )
        menuJpaRepository.saveAll(menu400List)
        val menu500 = Menu(
            permissionId = permission500.id,
            name = "点位布设",
            title = "点位布设",
            type = "ROUTE",
            path = "/pointLayout",
            component = "LAYOUT",
            sort = 500,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu500)
        val menu501 = Menu(
            pid = menu500.id,
            permissionId = permission501.id,
            name = "布点方案数据维护",
            title = "布点方案数据维护",
            type = "ROUTE",
            path = "pointLayout/planMaintain",
            component = "pointLayout/planMaintain/index",
            sort = 501,
            showed = true,
            cached = true,
            external = false
        )
        val menu502 = Menu(
            pid = menu500.id,
            permissionId = permission502.id,
            name = "布点方案问题整改",
            title = "布点方案问题整改",
            type = "ROUTE",
            path = "pointLayout/planUpdate",
            component = "pointLayout/planUpdate/index",
            sort = 502,
            showed = true,
            cached = true,
            external = false
        )
        val menu503 = Menu(
            pid = menu500.id,
            permissionId = permission503.id,
            name = "布点方案信息查询",
            title = "布点方案信息查询",
            type = "ROUTE",
            path = "pointLayout/planQuery",
            component = "pointLayout/planQuery/index",
            sort = 503,
            showed = true,
            cached = true,
            external = false
        )
        val menu500List = listOfNotNull(
            menu501,
            menu502,
            menu503
        )
        menuJpaRepository.saveAll(menu500List)
        val menu600 = Menu(
            permissionId = permission600.id,
            name = "布点质控管理",
            title = "布点质控管理",
            type = "ROUTE",
            path = "/layout",
            component = "LAYOUT",
            sort = 600,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu600)
        val menu601 = Menu(
            pid = menu600.id,
            permissionId = permission601.id,
            name = "布点质控专家组维护",
            title = "布点质控专家组维护",
            type = "ROUTE",
            path = "/layout/a",
            component = "/layout/a/index",
            sort = 601,
            showed = true,
            cached = true,
            external = false
        )
        val menu602 = Menu(
            pid = menu600.id,
            permissionId = permission602.id,
            name = "一级质控(县)任务分配",
            title = "一级质控(县)任务分配",
            type = "ROUTE",
            path = "/layout/b",
            component = "/layout/b/index",
            sort = 602,
            showed = true,
            cached = true,
            external = false
        )
        val menu603 = Menu(
            pid = menu600.id,
            permissionId = permission603.id,
            name = "二级质控(市)任务分配",
            title = "二级质控(市)任务分配",
            type = "ROUTE",
            path = "/layout/c",
            component = "/layout/c/index",
            sort = 603,
            showed = true,
            cached = true,
            external = false
        )
        val menu604 = Menu(
            pid = menu600.id,
            permissionId = permission604.id,
            name = "三级质控(省)任务分配",
            title = "三级质控(省)任务分配",
            type = "ROUTE",
            path = "/layout/d",
            component = "/layout/d/index",
            sort = 604,
            showed = true,
            cached = true,
            external = false
        )
        val menu605 = Menu(
            pid = menu600.id,
            permissionId = permission605.id,
            name = "布点质控专家组任务",
            title = "布点质控专家组任务",
            type = "ROUTE",
            path = "/layout/e",
            component = "/layout/e/index",
            sort = 605,
            showed = true,
            cached = true,
            external = false
        )
        val menu606 = Menu(
            pid = menu600.id,
            permissionId = permission606.id,
            name = "布点质控意见反馈",
            title = "布点质控意见反馈",
            type = "ROUTE",
            path = "/layout/f",
            component = "/layout/f/index",
            sort = 606,
            showed = true,
            cached = true,
            external = false
        )
        val menu607 = Menu(
            pid = menu600.id,
            permissionId = permission607.id,
            name = "布点质控专家查询",
            title = "布点质控专家查询",
            type = "ROUTE",
            path = "/layout/g",
            component = "/layout/g/index",
            sort = 607,
            showed = true,
            cached = true,
            external = false
        )
        val menu608 = Menu(
            pid = menu600.id,
            permissionId = permission608.id,
            name = "布点质控意见退回",
            title = "布点质控意见退回",
            type = "ROUTE",
            path = "/layout/h",
            component = "/layout/h/index",
            sort = 608,
            showed = true,
            cached = true,
            external = false
        )
        val menu600List = listOfNotNull(
            menu601,
            menu602,
            menu603,
            menu604,
            menu605,
            menu606,
            menu607,
            menu608
        )
        menuJpaRepository.saveAll(menu600List)
        val menu700 = Menu(
            permissionId = permission700.id,
            name = "采样调查管理",
            title = "采样调查管理",
            type = "ROUTE",
            path = "/sampleManage",
            component = "LAYOUT",
            sort = 700,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu700)
        val menu701 = Menu(
            pid = menu700.id,
            permissionId = permission701.id,
            name = "取样小组任务分配",
            title = "取样小组任务分配",
            type = "ROUTE",
            path = "/sampleManage/sampleGroupTask",
            component = "/sampleManage/sampleGroupTask/index",
            sort = 701,
            showed = true,
            cached = true,
            external = false
        )
        val menu702 = Menu(
            pid = menu700.id,
            permissionId = permission702.id,
            name = "批次样品运送表单",
            title = "批次样品运送表单",
            type = "ROUTE",
            path = "/sampleManage/sampleShippingForm",
            component = "/sampleManage/sampleShippingForm/index",
            sort = 702,
            showed = true,
            cached = true,
            external = false
        )
        val menu703 = Menu(
            pid = menu700.id,
            permissionId = permission703.id,
            name = "取样调查表单下载",
            title = "取样调查表单下载",
            type = "ROUTE",
            path = "/sampleManage/formDownload",
            component = "/sampleManage/formDownload/index",
            sort = 703,
            showed = true,
            cached = true,
            external = false
        )
        val menu704 = Menu(
            pid = menu700.id,
            permissionId = permission704.id,
            name = "子样流转进度查询",
            title = "子样流转进度查询",
            type = "ROUTE",
            path = "/sampleManage/sampleProgressQuery",
            component = "/sampleManage/sampleProgressQuery/index",
            sort = 704,
            showed = true,
            cached = true,
            external = false
        )
        val menu705 = Menu(
            pid = menu700.id,
            permissionId = permission705.id,
            name = "取样资料单位内审",
            title = "取样资料单位内审",
            type = "ROUTE",
            path = "/sampleManage/CYInformationCheck",
            component = "/sampleManage/CYInformationCheck/index",
            sort = 705,
            showed = true,
            cached = true,
            external = false
        )
        val menu706 = Menu(
            pid = menu700.id,
            permissionId = permission706.id,
            name = "质控退回样点查询",
            title = "质控退回样点查询",
            type = "ROUTE",
            path = "/sampleManage/QCPointQuery",
            component = "/sampleManage/QCPointQuery/index",
            sort = 706,
            showed = true,
            cached = true,
            external = false
        )
        val menu707 = Menu(
            pid = menu700.id,
            permissionId = permission707.id,
            name = "严重质量问题申诉",
            title = "严重质量问题申诉",
            type = "ROUTE",
            path = "/sampleManage/seriousIssueAppeal",
            component = "/sampleManage/seriousIssueAppeal/index",
            sort = 707,
            showed = true,
            cached = true,
            external = false
        )
        val menu708 = Menu(
            pid = menu700.id,
            permissionId = permission708.id,
            name = "重采样品信息查询",
            title = "重采样品信息查询",
            type = "ROUTE",
            path = "/sampleManage/resampleSampleQuery",
            component = "/sampleManage/resampleSampleQuery/index",
            sort = 708,
            showed = true,
            cached = true,
            external = false
        )
        val menu709 = Menu(
            pid = menu700.id,
            permissionId = permission709.id,
            name = "资料内审状态查询",
            title = "资料内审状态查询",
            type = "ROUTE",
            path = "/sampleManage/internalStatusInquiry",
            component = "/sampleManage/internalStatusInquiry/index",
            sort = 709,
            showed = true,
            cached = true,
            external = false
        )
        val menu710 = Menu(
            pid = menu700.id,
            permissionId = permission710.id,
            name = "资料内审进展统计",
            title = "资料内审进展统计",
            type = "ROUTE",
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/ky",
            component = "/sampleSurveys/ky/index",
            sort = 710,
            showed = true,
            cached = true,
            external = false
        )
        val menu711 = Menu(
            pid = menu700.id,
            permissionId = permission711.id,
            name = "采样调查信息查询",
            title = "采样调查信息查询",
            type = "ROUTE",
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/l",
            component = "/sampleSurveys/l/index",
            sort = 711,
            showed = true,
            cached = true,
            external = false
        )
        val menu712 = Menu(
            pid = menu700.id,
            permissionId = permission712.id,
            name = "单位取样进展统计",
            title = "单位取样进展统计",
            type = "ROUTE",
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/i",
            component = "/sampleSurveys/i/index",
            sort = 712,
            showed = true,
            cached = true,
            external = false
        )
        val menu700List = listOfNotNull(
            menu701,
            menu702,
            menu703,
            menu704,
            menu705,
            menu706,
            menu707,
            menu708,
            menu709,
            menu710,
            menu711,
            menu712
        )
        menuJpaRepository.saveAll(menu700List)
        val menu800 = Menu(
            permissionId = permission800.id,
            name = "取样调查",
            title = "取样调查",
            type = "ROUTE",
            path = "/sampleSurvey",
            component = "LAYOUT",
            sort = 800,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu800)
        val menu801 = Menu(
            pid = menu800.id,
            permissionId = permission801.id,
            name = "取样调查表单明细",
            title = "取样调查表单明细",
            type = "ROUTE",
            path = "/sampleSurvey/formDetails",
            component = "/sampleSurvey/formDetails/index",
            sort = 801,
            showed = true,
            cached = true,
            external = false
        )
        val menu802 = Menu(
            pid = menu800.id,
            permissionId = permission802.id,
            name = "质控退回样点明细",
            title = "质控退回样点明细",
            type = "ROUTE",
            path = "/sampleSurvey/QCReturnDetails",
            component = "/sampleSurvey/QCReturnDetails/index",
            sort = 802,
            showed = true,
            cached = true,
            external = false
        )
        val menu803 = Menu(
            pid = menu800.id,
            permissionId = permission803.id,
            name = "严重问题申诉记录",
            title = "严重问题申诉记录",
            type = "ROUTE",
            path = "/sampleSurvey/seriousProblemRecord",
            component = "/sampleSurvey/seriousProblemRecord/index",
            sort = 803,
            showed = true,
            cached = true,
            external = false
        )
        val menu804 = Menu(
            pid = menu800.id,
            permissionId = permission804.id,
            name = "重采样品信息明细",
            title = "重采样品信息明细",
            type = "ROUTE",
            path = "/sampleSurvey/sampleInformationDetails",
            component = "/sampleSurvey/sampleInformationDetails/index",
            sort = 804,
            showed = true,
            cached = true,
            external = false
        )
        val menu800List = listOfNotNull(
            menu801,
            menu802,
            menu803,
            menu804
        )
        menuJpaRepository.saveAll(menu800List)
        val menu900 = Menu(
            permissionId = permission900.id,
            name = "采样质控管理",
            title = "采样质控管理",
            type = "ROUTE",
            path = "/samplingQualityControl",
            component = "LAYOUT",
            sort = 900,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu900)
        val menu901 = Menu(
            pid = menu900.id,
            permissionId = permission901.id,
            name = "采样一级质控(县)任务",
            title = "采样一级质控(县)任务",
            type = "ROUTE",
            path = "/samplingQualityControl/a",
            component = "/samplingQualityControl/a/index",
            sort = 901,
            showed = true,
            cached = true,
            external = false
        )
        val menu902 = Menu(
            pid = menu900.id,
            permissionId = permission902.id,
            name = "采样二级质控(市)任务",
            title = "采样二级质控(市)任务",
            type = "ROUTE",
            path = "/samplingQualityControl/b",
            component = "/samplingQualityControl/b/index",
            sort = 902,
            showed = true,
            cached = true,
            external = false
        )
        val menu903 = Menu(
            pid = menu900.id,
            permissionId = permission903.id,
            name = "采样三级质控(省)任务",
            title = "采样三级质控(省)任务",
            type = "ROUTE",
            path = "/samplingQualityControl/c",
            component = "/samplingQualityControl/c/index",
            sort = 903,
            showed = true,
            cached = true,
            external = false
        )
        val menu904 = Menu(
            pid = menu900.id,
            permissionId = permission904.id,
            name = "采样质控专家任务",
            title = "采样质控专家任务",
            type = "ROUTE",
            path = "/samplingQualityControl/d",
            component = "/samplingQualityControl/d/index",
            sort = 904,
            showed = true,
            cached = true,
            external = false
        )
        val menu905 = Menu(
            pid = menu900.id,
            permissionId = permission905.id,
            name = "采样质控意见反馈",
            title = "采样质控意见反馈",
            type = "ROUTE",
            path = "/samplingQualityControl/e",
            component = "/samplingQualityControl/e/index",
            sort = 905,
            showed = true,
            cached = true,
            external = false
        )
        val menu906 = Menu(
            pid = menu900.id,
            permissionId = permission906.id,
            name = "取样资料质控进度",
            title = "取样资料质控进度",
            type = "ROUTE",
            path = "/samplingQualityControl/f",
            component = "/samplingQualityControl/f/index",
            sort = 906,
            showed = true,
            cached = true,
            external = false
        )
        val menu907 = Menu(
            pid = menu900.id,
            permissionId = permission907.id,
            name = "采样质控专家查询",
            title = "采样质控专家查询",
            type = "ROUTE",
            path = "/samplingQualityControl/g",
            component = "/samplingQualityControl/g/index",
            sort = 907,
            showed = true,
            cached = true,
            external = false
        )
        val menu908 = Menu(
            pid = menu900.id,
            permissionId = permission908.id,
            name = "采样质控意见退回",
            title = "采样质控意见退回",
            type = "ROUTE",
            path = "/samplingQualityControl/h",
            component = "/samplingQualityControl/h/index",
            sort = 908,
            showed = true,
            cached = true,
            external = false
        )
        val menu900List = listOfNotNull(
            menu901,
            menu902,
            menu903,
            menu904,
            menu905,
            menu906,
            menu907,
            menu908
        )
        menuJpaRepository.saveAll(menu900List)
        val menu1000 = Menu(
            permissionId = permission1000.id,
            name = "样品检测管理",
            title = "样品检测管理",
            type = "ROUTE",
            path = "/sampleTesting",
            component = "LAYOUT",
            sort = 1000,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1000)
        val menu1001 = Menu(
            pid = menu1000.id,
            permissionId = permission1001.id,
            name = "批次送检样交接单",
            title = "批次送检样交接单",
            type = "ROUTE",
            path = "/sampleTesting/a",
            component = "/sampleTesting/a/index",
            sort = 1001,
            showed = true,
            cached = true,
            external = false
        )
        val menu1002 = Menu(
            pid = menu1000.id,
            permissionId = permission1002.id,
            name = "检测子样信息查询",
            title = "检测子样信息查询",
            type = "ROUTE",
            path = "/sampleTesting/b",
            component = "/sampleTesting/b/index",
            sort = 1002,
            showed = true,
            cached = true,
            external = false
        )
        val menu1003 = Menu(
            pid = menu1000.id,
            permissionId = permission1003.id,
            name = "检测资质文件报送",
            title = "检测资质文件报送",
            type = "ROUTE",
            path = "/sampleTesting/TestQualificationDocuments",
            component = "/sampleTesting/TestQualificationDocuments/index",
            sort = 1003,
            showed = true,
            cached = true,
            external = false
        )
        val menu1004 = Menu(
            pid = menu1000.id,
            permissionId = permission1004.id,
            name = "检测资质能力审核",
            title = "检测资质能力审核",
            type = "ROUTE",
            path = "/sampleTesting/QualificationCompetencyAudit",
            component = "/sampleTesting/QualificationCompetencyAudit/index",
            sort = 1004,
            showed = true,
            cached = true,
            external = false
        )
        val menu1005 = Menu(
            pid = menu1000.id,
            permissionId = permission1005.id,
            name = "基本检测方法标准",
            title = "基本检测方法标准",
            type = "ROUTE",
            path = "/sampleTesting/e",
            component = "/sampleTesting/e/index",
            sort = 1005,
            showed = true,
            cached = true,
            external = false
        )
        val menu1006 = Menu(
            pid = menu1000.id,
            permissionId = permission1006.id,
            name = "地方新增检测方法",
            title = "地方新增检测方法",
            type = "ROUTE",
            path = "/sampleTesting/f",
            component = "/sampleTesting/f/index",
            sort = 1006,
            showed = true,
            cached = true,
            external = false
        )
        val menu1007 = Menu(
            pid = menu1000.id,
            permissionId = permission1007.id,
            name = "方法验证材料上传",
            title = "方法验证材料上传",
            type = "ROUTE",
            path = "/sampleTesting/g",
            component = "/sampleTesting/g/index",
            sort = 1007,
            showed = true,
            cached = true,
            external = false
        )
        val menu1008 = Menu(
            pid = menu1000.id,
            permissionId = permission1008.id,
            name = "方法验证材料审核",
            title = "方法验证材料审核",
            type = "ROUTE",
            path = "/sampleTesting/h",
            component = "/sampleTesting/h/index",
            sort = 1008,
            showed = true,
            cached = true,
            external = false
        )
        val menu1009 = Menu(
            pid = menu1000.id,
            permissionId = permission1009.id,
            name = "统一监控样品管理",
            title = "统一监控样品管理",
            type = "ROUTE",
            path = "/sampleTesting/i",
            component = "/sampleTesting/i/index",
            sort = 1009,
            showed = true,
            cached = true,
            external = false
        )
        val menu1010 = Menu(
            pid = menu1000.id,
            permissionId = permission1010.id,
            name = "统一监控样品查询",
            title = "统一监控样品查询",
            type = "ROUTE",
            path = "/sampleTesting/j",
            component = "/sampleTesting/j/index",
            sort = 1010,
            showed = true,
            cached = true,
            external = false
        )
        val menu1011 = Menu(
            pid = menu1000.id,
            permissionId = permission1011.id,
            name = "严重问题样品查询",
            title = "严重问题样品查询",
            type = "ROUTE",
            path = "/sampleTesting/k",
            component = "/sampleTesting/k/index",
            sort = 1011,
            showed = true,
            cached = true,
            external = false
        )
        val menu1012 = Menu(
            pid = menu1000.id,
            permissionId = permission1012.id,
            name = "批次检测数据报送",
            title = "批次检测数据报送",
            type = "ROUTE",
            path = "/sampleTesting/l",
            component = "/sampleTesting/l/index",
            sort = 1012,
            showed = true,
            cached = true,
            external = false
        )
        val menu1013 = Menu(
            pid = menu1000.id,
            permissionId = permission1013.id,
            name = "批次检测数据整改",
            title = "批次检测数据整改",
            type = "ROUTE",
            path = "/sampleTesting/m",
            component = "/sampleTesting/m/index",
            sort = 1013,
            showed = true,
            cached = true,
            external = false
        )
        val menu1014 = Menu(
            pid = menu1000.id,
            permissionId = permission1014.id,
            name = "样品检测数据查询",
            title = "样品检测数据查询",
            type = "ROUTE",
            path = "/sampleTesting/n",
            component = "/sampleTesting/n/index",
            sort = 1014,
            showed = true,
            cached = true,
            external = false
        )
        val menu1000List = listOfNotNull(
            menu1001,
            menu1002,
            menu1003,
            menu1004,
            menu1005,
            menu1006,
            menu1007,
            menu1008,
            menu1009,
            menu1010,
            menu1011,
            menu1012,
            menu1013,
            menu1014
        )
        menuJpaRepository.saveAll(menu1000List)
        val menu1100 = Menu(
            permissionId = permission1100.id,
            name = "数据质量审核",
            title = "数据质量审核",
            type = "ROUTE",
            path = "/dataQualityAudits",
            component = "LAYOUT",
            sort = 1100,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1100)
        val menu1101 = Menu(
            pid = menu1100.id,
            permissionId = permission1101.id,
            name = "检测一级质控(县)任务",
            title = "检测一级质控(县)任务",
            type = "ROUTE",
            path = "/dataQualityAudits/a",
            component = "/dataQualityAudits/a/index",
            sort = 1101,
            showed = true,
            cached = true,
            external = false
        )
        val menu1102 = Menu(
            pid = menu1100.id,
            permissionId = permission1102.id,
            name = "检测二级质控(市)任务",
            title = "检测二级质控(市)任务",
            type = "ROUTE",
            path = "/dataQualityAudits/b",
            component = "/dataQualityAudits/b/index",
            sort = 1102,
            showed = true,
            cached = true,
            external = false
        )
        val menu1103 = Menu(
            pid = menu1100.id,
            permissionId = permission1103.id,
            name = "检测三级质控(省)任务",
            title = "检测三级质控(省)任务",
            type = "ROUTE",
            path = "/dataQualityAudits/c",
            component = "/dataQualityAudits/c/index",
            sort = 1103,
            showed = true,
            cached = true,
            external = false
        )
        val menu1104 = Menu(
            pid = menu1100.id,
            permissionId = permission1104.id,
            name = "检测质控专家任务",
            title = "检测质控专家任务",
            type = "ROUTE",
            path = "/dataQualityAudits/d",
            component = "/dataQualityAudits/d/index",
            sort = 1104,
            showed = true,
            cached = true,
            external = false
        )
        val menu1105 = Menu(
            pid = menu1100.id,
            permissionId = permission1105.id,
            name = "检测质控意见反馈",
            title = "检测质控意见反馈",
            type = "ROUTE",
            path = "/dataQualityAudits/e",
            component = "/dataQualityAudits/e/index",
            sort = 1105,
            showed = true,
            cached = true,
            external = false
        )
        val menu1106 = Menu(
            pid = menu1100.id,
            permissionId = permission1106.id,
            name = "质控退改批次查询",
            title = "质控退改批次查询",
            type = "ROUTE",
            path = "/dataQualityAudits/f",
            component = "/dataQualityAudits/f/index",
            sort = 1106,
            showed = true,
            cached = true,
            external = false
        )
        val menu1100List = listOfNotNull(
            menu1101,
            menu1102,
            menu1103,
            menu1104,
            menu1105,
            menu1106
        )
        menuJpaRepository.saveAll(menu1100List)
        val menu1200 = Menu(
            permissionId = permission1200.id,
            name = "数据统计分析",
            title = "数据统计分析",
            type = "ROUTE",
            path = "/statisticalAnalysisOfData",
            component = "LAYOUT",
            sort = 1200,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1200)
        val menu1300 = Menu(
            permissionId = permission1300.id,
            name = "数据对标评价",
            title = "数据对标评价",
            type = "ROUTE",
            path = "/dataBenchmarkingEvaluation",
            component = "LAYOUT",
            sort = 1300,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1300)
        val menu1400 = Menu(
            permissionId = permission1400.id,
            name = "工作文件管理",
            title = "工作文件管理",
            type = "ROUTE",
            path = "/workFileManagement",
            component = "LAYOUT",
            sort = 1400,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1400)
        val menu1401 = Menu(
            pid = menu1400.id,
            permissionId = permission1401.id,
            name = "工作文件下载",
            title = "工作文件下载",
            type = "ROUTE",
            path = "/workFileManagement/a",
            component = "/workFileManagement/a/index",
            sort = 1401,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1401)
        val menu1500 = Menu(
            permissionId = permission1500.id,
            name = "后台数据管理",
            title = "后台数据管理",
            type = "ROUTE",
            path = "/system",
            component = "LAYOUT",
            sort = 1500,
            showed = true,
            cached = true,
            external = false
        )
        menuJpaRepository.save(menu1500)
        val menu1501 = Menu(
            pid = menu1500.id,
            permissionId = permission1501.id,
            name = "单位管理",
            title = "单位管理",
            type = "ROUTE",
            path = "/system/organization",
            component = "/system/organization/index",
            sort = 1501,
            showed = true,
            cached = true,
            external = false
        )
        val menu1502 = Menu(
            pid = menu1500.id,
            permissionId = permission1502.id,
            name = "用户管理",
            title = "用户管理",
            type = "ROUTE",
            path = "/system/user",
            component = "/system/user/index",
            sort = 1502,
            showed = true,
            cached = true,
            external = false
        )
        val menu1503 = Menu(
            pid = menu1500.id,
            permissionId = permission1503.id,
            name = "角色管理",
            title = "角色管理",
            type = "ROUTE",
            path = "/system/role",
            component = "/system/role/index",
            sort = 1503,
            showed = true,
            cached = true,
            external = false
        )
        val menu1504 = Menu(
            pid = menu1500.id,
            permissionId = permission1504.id,
            name = "权限管理",
            title = "权限管理",
            type = "ROUTE",
            path = "/system/permission",
            component = "/system/permission/index",
            sort = 1504,
            showed = true,
            cached = true,
            external = false
        )
        val menu1505 = Menu(
            pid = menu1500.id,
            permissionId = permission1505.id,
            name = "菜单管理",
            title = "菜单管理",
            type = "ROUTE",
            path = "/system/menu",
            component = "/system/menu/index",
            sort = 1505,
            showed = true,
            cached = true,
            external = false
        )
        val menu1506 = Menu(
            pid = menu1500.id,
            permissionId = permission1506.id,
            name = "字典管理",
            title = "字典管理",
            type = "ROUTE",
            path = "/system/dict",
            component = "/system/dict/index",
            sort = 1506,
            showed = true,
            cached = true,
            external = false
        )
        val menu1500List = listOfNotNull(
            menu1501,
            menu1502,
            menu1503,
            menu1504,
            menu1505,
            menu1506
        )
        menuJpaRepository.saveAll(menu1500List)
    }

    /**
     * 保存区域数据
     */
    private fun saveArea(parent: Dict?, child: Area, dictList: MutableList<Dict>) {
        val dict = Dict(
            value = child.adCode.toInt(),
            name = child.name,
            type = "area",
            sort = areaSort++,
            pid = parent?.value
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
        private val log = LogFactory.getLog(DatabaseInitializingBeanImpl::class.java)

        /**
         * 字典排序
         */
        private var areaSort: Short = 0
    }
}
