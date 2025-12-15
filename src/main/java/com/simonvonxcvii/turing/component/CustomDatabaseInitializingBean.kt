package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.*
import com.simonvonxcvii.turing.enums.DictTypeEnum
import com.simonvonxcvii.turing.enums.MenuTypeEnum
import com.simonvonxcvii.turing.enums.OrganizationTypeEnum
import com.simonvonxcvii.turing.repository.jpa.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.domain.Specification
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
    private val userRoleJpaRepository: UserRoleJpaRepository,
    private val permissionJpaRepository: PermissionJpaRepository,
    private val menuJpaRepository: MenuJpaRepository,
    private val dictJpaRepository: DictJpaRepository
) : InitializingBean {
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        // 判断是否需要初始化，如果表数据存在说明不需要
        val spec = Specification<Dict> { root, query, builder ->
            val type = builder.equal(root.get<String>(Dict.TYPE), DictTypeEnum.AREA)
            query.where(type)?.restriction
        }
        val exists = dictJpaRepository.exists(spec)
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
            password = passwordEncoder.encode("123456")!!,
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
            manager = true,
            needResetPassword = false
        )
        userJpaRepository.save(user)


        val role = Role(authority = "SUPER_ADMIN", name = "SUPER_ADMIN", remark = "超级管理员")
        val roleList = listOfNotNull(
            role,
            Role(authority = "GOV_COUNTRY_ADMIN", name = "GOV_COUNTRY_ADMIN", remark = "国家级行政单位管理员"),
            Role(authority = "GOV_COUNTRY_STAFF", name = "GOV_COUNTRY_STAFF", remark = "国家级行政单位工作人员"),
            Role(authority = "GOV_PROVINCE_ADMIN", name = "GOV_PROVINCE_ADMIN", remark = "省级行政单位管理员"),
            Role(authority = "GOV_PROVINCE_STAFF", name = "GOV_PROVINCE_STAFF", remark = "省级行政单位工作人员"),
            Role(authority = "GOV_CITY_ADMIN", name = "GOV_CITY_ADMIN", remark = "市级行政单位管理员"),
            Role(authority = "GOV_CITY_STAFF", name = "GOV_CITY_STAFF", remark = "市级行政单位工作人员"),
            Role(authority = "GOV_DISTRICT_ADMIN", name = "GOV_DISTRICT_ADMIN", remark = "县级行政单位管理员"),
            Role(authority = "GOV_DISTRICT_STAFF", name = "GOV_DISTRICT_STAFF", remark = "县级行政单位工作人员"),
            Role(
                authority = "BUSINESS_MINE_INFORMATION_COLLECTION_ADMIN",
                name = "BUSINESS_MINE_INFORMATION_COLLECTION_ADMIN",
                remark = "矿山信息采集单位管理员"
            ),
            Role(
                authority = "BUSINESS_MINE_INFORMATION_COLLECTION_STAFF",
                name = "BUSINESS_MINE_INFORMATION_COLLECTION_STAFF",
                remark = "矿山信息采集单位工作人员"
            ),
            Role(
                authority = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_ADMIN",
                name = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_ADMIN",
                remark = "区域调查布点单位管理员"
            ),
            Role(
                authority = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_STAFF",
                name = "BUSINESS_REGIONAL_SURVEY_LOCATIONS_STAFF",
                remark = "区域调查布点单位工作人员"
            ),
            Role(
                authority = "BUSINESS_AREA_SAMPLING_SURVEYS_ADMIN",
                name = "BUSINESS_AREA_SAMPLING_SURVEYS_ADMIN",
                remark = "区域采样调查单位管理员"
            ),
            Role(
                authority = "BUSINESS_AREA_SAMPLING_SURVEYS_STAFF",
                name = "BUSINESS_AREA_SAMPLING_SURVEYS_STAFF",
                remark = "区域采样调查单位工作人员"
            ),
            Role(
                authority = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_ADMIN",
                name = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_ADMIN",
                remark = "地块信息采集单位管理员"
            ),
            Role(
                authority = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_STAFF",
                name = "BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_STAFF",
                remark = "地块信息采集单位工作人员"
            ),
            Role(
                authority = "BUSINESS_PLOT_SURVEY_LAYOUT_ADMIN",
                name = "BUSINESS_PLOT_SURVEY_LAYOUT_ADMIN",
                remark = "地块调查布点单位管理员"
            ),
            Role(
                authority = "BUSINESS_PLOT_SURVEY_LAYOUT_STAFF",
                name = "BUSINESS_PLOT_SURVEY_LAYOUT_STAFF",
                remark = "地块调查布点单位工作人员"
            ),
            Role(
                authority = "BUSINESS_PLOT_SAMPLING_SURVEY_ADMIN",
                name = "BUSINESS_PLOT_SAMPLING_SURVEY_ADMIN",
                remark = "地块采样调查单位管理员"
            ),
            Role(
                authority = "BUSINESS_PLOT_SAMPLING_SURVEY_STAFF",
                name = "BUSINESS_PLOT_SAMPLING_SURVEY_STAFF",
                remark = "地块采样调查单位工作人员"
            ),
            Role(
                authority = "BUSINESS_SAMPLE_TESTING_ADMIN",
                name = "BUSINESS_SAMPLE_TESTING_ADMIN",
                remark = "样品检测单位管理员"
            ),
            Role(
                authority = "BUSINESS_SAMPLE_TESTING_STAFF",
                name = "BUSINESS_SAMPLE_TESTING_STAFF",
                remark = "样品检测单位工作人员"
            ),
            Role(
                authority = "BUSINESS_DATA_ANALYSIS_EVALUATION_ADMIN",
                name = "BUSINESS_DATA_ANALYSIS_EVALUATION_ADMIN",
                remark = "数据分析评价单位管理员"
            ),
            Role(
                authority = "BUSINESS_DATA_ANALYSIS_EVALUATION_STAFF",
                name = "BUSINESS_DATA_ANALYSIS_EVALUATION_STAFF",
                remark = "数据分析评价单位工作人员"
            ),
            Role(
                authority = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_ADMIN",
                name = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_ADMIN",
                remark = "污染风险评估单位管理员"
            ),
            Role(
                authority = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_STAFF",
                name = "BUSINESS_CONTAMINATION_RISK_ASSESSMENT_STAFF",
                remark = "污染风险评估单位工作人员"
            ),
            Role(
                authority = "QC_INFORMATION_COLLECTION_ADMIN",
                name = "QC_INFORMATION_COLLECTION_ADMIN",
                remark = "信息采集质控单位管理员"
            ),
            Role(
                authority = "QC_INFORMATION_COLLECTION_STAFF",
                name = "QC_INFORMATION_COLLECTION_STAFF",
                remark = "信息采集质控单位工作人员"
            ),
            Role(authority = "QC_DISTRIBUTE_ADMIN", name = "QC_DISTRIBUTE_ADMIN", remark = "布点质控单位管理员"),
            Role(authority = "QC_DISTRIBUTE_STAFF", name = "QC_DISTRIBUTE_STAFF", remark = "布点质控单位工作人员"),
            Role(authority = "QC_SAMPLING_ADMIN", name = "QC_SAMPLING_ADMIN", remark = "采样质控单位管理员"),
            Role(authority = "QC_SAMPLING_STAFF", name = "QC_SAMPLING_STAFF", remark = "采样质控单位工作人员"),
            Role(
                authority = "QC_SAMPLE_TESTING_ADMIN",
                name = "QC_SAMPLE_TESTING_ADMIN",
                remark = "样品检测质控单位管理员"
            ),
            Role(
                authority = "QC_SAMPLE_TESTING_STAFF",
                name = "QC_SAMPLE_TESTING_STAFF",
                remark = "样品检测质控单位工作人员"
            ),
            Role(authority = "LEADER", name = "LEADER", remark = "信息采集和取样调查小组组长"),
            Role(authority = "MEMBER", name = "MEMBER", remark = "信息采集和取样调查小组组员"),
            Role(
                authority = "INFORMATION_COLLECTION_INTERNAL_AUDITOR",
                name = "INFORMATION_COLLECTION_INTERNAL_AUDITOR",
                remark = "信息采集单位内审人员"
            ),
            Role(
                authority = "PLOT_SAMPLING_SURVEY_INTERNAL_AUDITOR",
                name = "PLOT_SAMPLING_SURVEY_INTERNAL_AUDITOR",
                remark = "取样调查单位内审人员"
            ),
            Role(
                authority = "DEFAULT_TECHNICAL",
                name = "DEFAULT_TECHNICAL",
                remark = "技术单位管理员的默认角色，注册技术单位时赋予管理员的默认角色，仅有【技术单位业务申请】权限"
            )
        )
        roleJpaRepository.saveAll(roleList)


        val userRole = UserRole(userId = user.id, roleId = role.id)
        userRoleJpaRepository.save(userRole)


        val permission10000 = Permission(name = "基础数据管理", sort = 10000)
        permissionJpaRepository.save(permission10000)
        val permission10100 = Permission(pid = permission10000.id, name = "业务管理账号开通", sort = 10100)
        val permission10200 = Permission(pid = permission10000.id, name = "业务管理单位授权", sort = 10200)
        val permission10300 = Permission(pid = permission10000.id, name = "技术单位信息管理", sort = 10300)
        val permission10400 = Permission(pid = permission10000.id, name = "技术专家信息维护", sort = 10400)
        val permission10500 = Permission(pid = permission10000.id, name = "数据字典管理", sort = 10500)
        val permission10600 = Permission(pid = permission10000.id, name = "设备终端授权管理", sort = 10600)
        val permission10700 = Permission(pid = permission10000.id, name = "平台用户新增维护", sort = 10700)
        val permission10000List = listOfNotNull(
            permission10100,
            permission10200,
            permission10300,
            permission10400,
            permission10500,
            permission10600,
            permission10700
        )
        permissionJpaRepository.saveAll(permission10000List)
        val permission20000 = Permission(name = "用户单位管理", sort = 20000)
        permissionJpaRepository.save(permission20000)
        val permission20100 = Permission(pid = permission20000.id, name = "管理单位用户维护", sort = 20100)
        val permission20200 = Permission(pid = permission20000.id, name = "技术单位业务申请", sort = 20200)
        val permission20300 = Permission(pid = permission20000.id, name = "技术单位业务审核", sort = 20300)
        val permission20400 = Permission(pid = permission20000.id, name = "技术单位用户维护", sort = 20400)
        val permission20500 = Permission(pid = permission20000.id, name = "技术专家业务申请", sort = 20500)
        val permission20600 = Permission(pid = permission20000.id, name = "技术专家业务审核", sort = 20600)
        val permission20700 = Permission(pid = permission20000.id, name = "调查小组新建维护", sort = 20700)
        val permission20800 = Permission(pid = permission20000.id, name = "调查单位信息查询", sort = 20800)
        val permission20900 = Permission(pid = permission20000.id, name = "调查用户信息查询", sort = 20900)
        val permission20000List = listOfNotNull(
            permission20100,
            permission20200,
            permission20300,
            permission20400,
            permission20500,
            permission20600,
            permission20700,
            permission20800,
            permission20900
        )
        permissionJpaRepository.saveAll(permission20000List)
        val permission30000 = Permission(name = "项目任务管理", sort = 30000)
        permissionJpaRepository.save(permission30000)
        val permission30100 = Permission(pid = permission30000.id, name = "项目新增维护", sort = 30100)
        val permission30200 = Permission(pid = permission30000.id, name = "监管对象维护", sort = 30200)
        val permission30300 = Permission(pid = permission30000.id, name = "任务下发管理", sort = 30300)
        val permission30400 = Permission(pid = permission30000.id, name = "牵头单位实施", sort = 30400)
        val permission30500 = Permission(pid = permission30000.id, name = "监管对象信息查询", sort = 30500)
        val permission30600 = Permission(pid = permission30000.id, name = "技术单位任务执行", sort = 30600)
        val permission30000List = listOfNotNull(
            permission30100, permission30200, permission30300, permission30400, permission30500, permission30600
        )
        permissionJpaRepository.saveAll(permission30000List)
        val permission40000 = Permission(name = "点位布设管理", sort = 40000)
        permissionJpaRepository.save(permission40000)
        val permission40100 = Permission(pid = permission40000.id, name = "测试项目新增维护", sort = 40100)
        val permission40200 = Permission(pid = permission40000.id, name = "测试项目分类管理", sort = 40200)
        val permission40300 = Permission(pid = permission40000.id, name = "布点人员任务分配", sort = 40300)
        val permission40400 = Permission(pid = permission40000.id, name = "布点数据成果录入", sort = 40400)
        val permission40500 = Permission(pid = permission40000.id, name = "布点方案数据退回", sort = 40500)
        val permission40600 = Permission(pid = permission40000.id, name = "布点方案数据查询", sort = 40600)
        val permission40700 = Permission(pid = permission40000.id, name = "新增测试项目审核", sort = 40700)
        val permission40000List = listOfNotNull(
            permission40100,
            permission40200,
            permission40300,
            permission40400,
            permission40500,
            permission40600,
            permission40700
        )
        permissionJpaRepository.saveAll(permission40000List)
        val permission50000 = Permission(name = "点位布设", sort = 50000)
        permissionJpaRepository.save(permission50000)
        val permission50100 = Permission(pid = permission50000.id, name = "布点方案问题整改", sort = 50100)
        val permission50200 = Permission(pid = permission50000.id, name = "布点方案数据维护", sort = 50200)
        val permission50300 = Permission(pid = permission50000.id, name = "布点方案信息查询", sort = 50300)
        val permission50000List = listOfNotNull(
            permission50100, permission50200, permission50300
        )
        permissionJpaRepository.saveAll(permission50000List)
        val permission60000 = Permission(name = "布点质控管理", sort = 60000)
        permissionJpaRepository.save(permission60000)
        val permission60100 = Permission(pid = permission60000.id, name = "布点质控专家组维护", sort = 60100)
        val permission60200 = Permission(pid = permission60000.id, name = "一级质控(县)任务分配", sort = 60200)
        val permission60300 = Permission(pid = permission60000.id, name = "二级质控(市)任务分配", sort = 60300)
        val permission60400 = Permission(pid = permission60000.id, name = "三级质控(省)任务分配", sort = 60400)
        val permission60500 = Permission(pid = permission60000.id, name = "布点质控专家组任务", sort = 60500)
        val permission60600 = Permission(pid = permission60000.id, name = "布点质控意见反馈", sort = 60600)
        val permission60700 = Permission(pid = permission60000.id, name = "布点质控专家查询", sort = 60700)
        val permission60800 = Permission(pid = permission60000.id, name = "布点质控意见退回", sort = 60800)
        val permission60000List = listOfNotNull(
            permission60100,
            permission60200,
            permission60300,
            permission60400,
            permission60500,
            permission60600,
            permission60700,
            permission60800
        )
        permissionJpaRepository.saveAll(permission60000List)
        val permission70000 = Permission(name = "采样调查管理", sort = 70000)
        permissionJpaRepository.save(permission70000)
        val permission70100 = Permission(pid = permission70000.id, name = "牵头单位组织实施", sort = 70100)
        val permission70200 = Permission(pid = permission70000.id, name = "采样小组任务分配", sort = 70200)
        val permission70300 = Permission(pid = permission70000.id, name = "采样调查信息查询", sort = 70300)
        val permission70400 = Permission(pid = permission70000.id, name = "检测子样进度查询", sort = 70400)
        val permission70500 = Permission(pid = permission70000.id, name = "取样调查表单下载", sort = 70500)
        val permission70600 = Permission(pid = permission70000.id, name = "采样资料单位内审", sort = 70600)
        val permission70700 = Permission(pid = permission70000.id, name = "质控退回样点查询", sort = 70700)
        val permission70800 = Permission(pid = permission70000.id, name = "批次样品运送表单", sort = 70800)
        val permission70900 = Permission(pid = permission70000.id, name = "单位取样进展统计", sort = 70900)
        val permission71000 = Permission(pid = permission70000.id, name = "资料内审状态查询", sort = 71000)
        val permission71100 = Permission(pid = permission70000.id, name = "资料内审进展统计", sort = 71100)
        val permission71200 = Permission(pid = permission70000.id, name = "重采样品信息查询", sort = 71200)
        val permission71300 = Permission(pid = permission70000.id, name = "严重质量问题申诉", sort = 71300)
        val permission700List = listOfNotNull(
            permission70100,
            permission70200,
            permission70300,
            permission70400,
            permission70500,
            permission70600,
            permission70700,
            permission70800,
            permission70900,
            permission71000,
            permission71100,
            permission71200,
            permission71300
        )
        permissionJpaRepository.saveAll(permission700List)
        val permission80000 = Permission(name = "取样调查", sort = 80000)
        permissionJpaRepository.save(permission80000)
        val permission80100 = Permission(pid = permission80000.id, name = "取样调查表单明细", sort = 80100)
        val permission80200 = Permission(pid = permission80000.id, name = "质控退回样点明细", sort = 80200)
        val permission80300 = Permission(pid = permission80000.id, name = "严重问题申诉记录", sort = 80300)
        val permission80400 = Permission(pid = permission80000.id, name = "重采样品信息明细", sort = 80400)
        val permission80000List = listOfNotNull(
            permission80100, permission80200, permission80300, permission80400
        )
        permissionJpaRepository.saveAll(permission80000List)
        val permission90000 = Permission(name = "采样质控管理", sort = 90000)
        permissionJpaRepository.save(permission90000)
        val permission90100 = Permission(pid = permission90000.id, name = "采样一级质控(县)任务", sort = 90100)
        val permission90200 = Permission(pid = permission90000.id, name = "采样二级质控(市)任务", sort = 90200)
        val permission90300 = Permission(pid = permission90000.id, name = "采样三级质控(省)任务", sort = 90300)
        val permission90400 = Permission(pid = permission90000.id, name = "采样质控专家任务", sort = 90400)
        val permission90500 = Permission(pid = permission90000.id, name = "采样质控意见反馈", sort = 90500)
        val permission90600 = Permission(pid = permission90000.id, name = "取样资料质控进度", sort = 90600)
        val permission90700 = Permission(pid = permission90000.id, name = "采样质控专家查询", sort = 90700)
        val permission90800 = Permission(pid = permission90000.id, name = "采样质控意见退回", sort = 90800)
        val permission90000List = listOfNotNull(
            permission90100,
            permission90200,
            permission90300,
            permission90400,
            permission90500,
            permission90600,
            permission90700,
            permission90800
        )
        permissionJpaRepository.saveAll(permission90000List)
        val permission100000 = Permission(name = "样品检测管理", sort = 100000)
        permissionJpaRepository.save(permission100000)
        val permission100100 = Permission(pid = permission100000.id, name = "批次送检样交接单", sort = 100100)
        val permission100200 = Permission(pid = permission100000.id, name = "检测子样信息查询", sort = 100200)
        val permission100300 = Permission(pid = permission100000.id, name = "检测资质文件报送", sort = 100300)
        val permission100400 = Permission(pid = permission100000.id, name = "检测资质能力审核", sort = 100400)
        val permission100500 = Permission(pid = permission100000.id, name = "基本检测方法标准", sort = 100500)
        val permission100600 = Permission(pid = permission100000.id, name = "地方新增检测方法", sort = 100600)
        val permission100700 = Permission(pid = permission100000.id, name = "方法验证材料上传", sort = 100700)
        val permission100800 = Permission(pid = permission100000.id, name = "方法验证材料审核", sort = 100800)
        val permission100900 = Permission(pid = permission100000.id, name = "统一监控样品管理", sort = 100900)
        val permission101000 = Permission(pid = permission100000.id, name = "统一监控样品查询", sort = 101000)
        val permission101100 = Permission(pid = permission100000.id, name = "严重问题样品查询", sort = 101100)
        val permission101200 = Permission(pid = permission100000.id, name = "批次检测数据报送", sort = 101200)
        val permission101300 = Permission(pid = permission100000.id, name = "批次检测数据整改", sort = 101300)
        val permission101400 = Permission(pid = permission100000.id, name = "样品检测数据查询", sort = 101400)
        val permission100000List = listOfNotNull(
            permission100100,
            permission100200,
            permission100300,
            permission100400,
            permission100500,
            permission100600,
            permission100700,
            permission100800,
            permission100900,
            permission101000,
            permission101100,
            permission101200,
            permission101300,
            permission101400
        )
        permissionJpaRepository.saveAll(permission100000List)
        val permission110000 = Permission(name = "数据质量审核", sort = 110000)
        permissionJpaRepository.save(permission110000)
        val permission110100 = Permission(pid = permission110000.id, name = "检测一级质控(县)任务", sort = 110100)
        val permission110200 = Permission(pid = permission110000.id, name = "检测二级质控(市)任务", sort = 110200)
        val permission110300 = Permission(pid = permission110000.id, name = "检测三级质控(省)任务", sort = 110300)
        val permission110400 = Permission(pid = permission110000.id, name = "检测质控专家任务", sort = 110400)
        val permission110500 = Permission(pid = permission110000.id, name = "检测质控意见反馈", sort = 110500)
        val permission110600 = Permission(pid = permission110000.id, name = "质控退改批次查询", sort = 110600)
        val permission110000List = listOfNotNull(
            permission110100, permission110200, permission110300, permission110400, permission110500, permission110600
        )
        permissionJpaRepository.saveAll(permission110000List)
        val permission120000 = Permission(name = "数据统计分析", sort = 120000)
        permissionJpaRepository.save(permission120000)
        val permission130000 = Permission(name = "数据对标评价", sort = 130000)
        permissionJpaRepository.save(permission130000)
        val permission140000 = Permission(name = "工作文件管理", sort = 140000)
        permissionJpaRepository.save(permission140000)
        val permission140100 = Permission(pid = permission140000.id, name = "工作文件下载", sort = 140100)
        permissionJpaRepository.save(permission140100)
        val permission150000 = Permission(name = "后台数据管理", sort = 150000)
        permissionJpaRepository.save(permission150000)
        val permission150100 = Permission(pid = permission150000.id, name = "单位管理", sort = 150100)
        val permission150200 = Permission(pid = permission150000.id, name = "用户管理", sort = 150200)
        val permission150300 = Permission(pid = permission150000.id, name = "角色管理", sort = 150300)
        val permission150400 = Permission(pid = permission150000.id, name = "权限管理", sort = 150400)
        val permission150500 = Permission(pid = permission150000.id, name = "菜单管理", sort = 150500)
        val permission150600 = Permission(pid = permission150000.id, name = "字典管理", sort = 150600)
        val permission150000List = listOfNotNull(
            permission150100, permission150200, permission150300, permission150400, permission150500, permission150600
        )
        permissionJpaRepository.saveAll(permission150000List)
        val permission150501 = Permission(pid = permission150500.id, name = "新增", sort = 150501)
        val permission150502 = Permission(pid = permission150500.id, name = "修改", sort = 150502)
        val permission150503 = Permission(pid = permission150500.id, name = "删除", sort = 150503)
        val permission150500List = listOfNotNull(permission150501, permission150502, permission150503)
        permissionJpaRepository.saveAll(permission150500List)


        val menu10000 = Menu(
            permissionId = permission10000.id,
            name = "基础数据管理",
            title = "基础数据管理",
            type = MenuTypeEnum.CATALOG,
            path = "/basic",
            component = "LAYOUT",
            sort = 10000
        )
        menuJpaRepository.save(menu10000)
        val menu10100 = Menu(
            pid = menu10000.id,
            permissionId = permission10100.id,
            name = "业务管理账号开通",
            title = "业务管理账号开通",
            type = MenuTypeEnum.MENU,
            path = "/basic/accountOpen",
            component = "/basic/accountOpen/list",
            sort = 10100
        )
        val menu10200 = Menu(
            pid = menu10000.id,
            permissionId = permission10200.id,
            name = "业务管理单位授权",
            title = "业务管理单位授权",
            type = MenuTypeEnum.MENU,
            path = "/basic/empower",
            component = "/basic/empower/list",
            sort = 10200
        )
        val menu10300 = Menu(
            pid = menu10000.id,
            permissionId = permission10300.id,
            name = "技术单位信息管理",
            title = "技术单位信息管理",
            type = MenuTypeEnum.MENU,
            path = "/basic/orgInfoManage",
            component = "/basic/orgInfoManage/list",
            sort = 10300
        )
        val menu10400 = Menu(
            pid = menu10000.id,
            permissionId = permission10400.id,
            name = "技术专家信息维护",
            title = "技术专家信息维护",
            type = MenuTypeEnum.MENU,
            path = "/basic/d",
            component = "/basic/d/list",
            sort = 10400
        )
        val menu10500 = Menu(
            pid = menu10000.id,
            permissionId = permission10500.id,
            name = "平台新增用户维护",
            title = "平台新增用户维护",
            type = MenuTypeEnum.MENU,
            path = "/basic/newUsersManage",
            component = "/basic/newUsersManage/list",
            sort = 10500
        )
        val menu10600 = Menu(
            pid = menu10000.id,
            permissionId = permission10600.id,
            name = "设备终端授权管理",
            title = "设备终端授权管理",
            type = MenuTypeEnum.MENU,
            path = "/basic/f",
            component = "/basic/f/list",
            sort = 10600
        )
        val menu10000List = listOfNotNull(
            menu10100, menu10200, menu10300, menu10400, menu10500, menu10600
        )
        menuJpaRepository.saveAll(menu10000List)
        val menu20000 = Menu(
            permissionId = permission20000.id,
            name = "用户单位管理",
            title = "用户单位管理",
            type = MenuTypeEnum.CATALOG,
            path = "/organization",
            component = "LAYOUT",
            sort = 20000
        )
        menuJpaRepository.save(menu20000)
        val menu20100 = Menu(
            pid = menu20000.id,
            permissionId = permission20100.id,
            name = "管理单位用户维护",
            title = "管理单位用户维护",
            type = MenuTypeEnum.MENU,
            path = "/organization/manageUnitUser",
            component = "/organization/manageUnitUser/list",
            sort = 20100
        )
        val menu20200 = Menu(
            pid = menu20000.id,
            permissionId = permission20200.id,
            name = "技术单位业务申请",
            title = "技术单位业务申请",
            type = MenuTypeEnum.MENU,
            path = "/organization/organizationBusinessApply",
            component = "/organization/organizationBusinessApply/list",
            sort = 20200
        )
        val menu20300 = Menu(
            pid = menu20000.id,
            permissionId = permission20300.id,
            name = "技术单位业务审核",
            title = "技术单位业务审核",
            type = MenuTypeEnum.MENU,
            path = "/organization/organizationBusinessCheck",
            component = "/organization/organizationBusinessCheck/list",
            sort = 20300
        )
        val menu20400 = Menu(
            pid = menu20000.id,
            permissionId = permission20400.id,
            name = "技术单位用户维护",
            title = "技术单位用户维护",
            type = MenuTypeEnum.MENU,
            path = "/organization/technicalUnitUser",
            component = "/organization/technicalUnitUser/list",
            sort = 20400
        )
        val menu20500 = Menu(
            pid = menu20000.id,
            permissionId = permission20500.id,
            name = "技术专家业务申请",
            title = "技术专家业务申请",
            type = MenuTypeEnum.MENU,
            path = "/organization/e",
            component = "/organization/e/list",
            sort = 20500
        )
        val menu20600 = Menu(
            pid = menu20000.id,
            permissionId = permission20600.id,
            name = "技术专家业务审核",
            title = "技术专家业务审核",
            type = MenuTypeEnum.MENU,
            path = "/organization/f",
            component = "/organization/f/list",
            sort = 20600
        )
        val menu20700 = Menu(
            pid = menu20000.id,
            permissionId = permission20700.id,
            name = "调查小组新建维护",
            title = "调查小组新建维护",
            type = MenuTypeEnum.MENU,
            path = "/organization/groupAddOrUpt",
            component = "/organization/groupAddOrUpt/list",
            sort = 20700
        )
        val menu20800 = Menu(
            pid = menu20000.id,
            permissionId = permission20800.id,
            name = "调查单位信息查询",
            title = "调查单位信息查询",
            type = MenuTypeEnum.MENU,
            path = "/organization/unitInformationQuery",
            component = "/organization/unitInformationQuery/list",
            sort = 20800
        )
        val menu20900 = Menu(
            pid = menu20000.id,
            permissionId = permission20900.id,
            name = "调查用户信息查询",
            title = "调查用户信息查询",
            type = MenuTypeEnum.MENU,
            path = "/organization/userInformationQuery",
            component = "/organization/userInformationQuery/list",
            sort = 20900
        )
        val menu20000List = listOfNotNull(
            menu20100, menu20200, menu20300, menu20400, menu20500, menu20600, menu20700, menu20800, menu20900
        )
        menuJpaRepository.saveAll(menu20000List)
        val menu30000 = Menu(
            permissionId = permission30000.id,
            name = "项目任务管理",
            title = "项目任务管理",
            type = MenuTypeEnum.CATALOG,
            path = "/projectTask",
            component = "LAYOUT",
            sort = 30000
        )
        menuJpaRepository.save(menu30000)
        val menu30100 = Menu(
            pid = menu30000.id,
            permissionId = permission30100.id,
            name = "调查项目新增维护",
            title = "调查项目新增维护",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/a",
            component = "/projectTask/a/list",
            sort = 30100
        )
        val menu30200 = Menu(
            pid = menu30000.id,
            permissionId = permission30200.id,
            name = "监管对象新增维护",
            title = "监管对象新增维护",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/superviseObjectMaintain",
            component = "/projectTask/superviseObjectMaintain/list",
            sort = 30200
        )
        val menu30300 = Menu(
            pid = menu30000.id,
            permissionId = permission30300.id,
            name = "工作任务下发管理",
            title = "工作任务下发管理",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/taskDistribution",
            component = "/projectTask/taskDistribution/list",
            sort = 30300
        )
        val menu30400 = Menu(
            pid = menu30000.id,
            permissionId = permission30400.id,
            name = "牵头单位组织实施",
            title = "牵头单位组织实施",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/leadOrgImplement",
            component = "/projectTask/leadOrgImplement/list",
            sort = 30400
        )
        val menu30500 = Menu(
            pid = menu30000.id,
            permissionId = permission30500.id,
            name = "技术单位任务执行",
            title = "技术单位任务执行",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/taskExecution",
            component = "/projectTask/taskExecution/list",
            sort = 30500
        )
        val menu30600 = Menu(
            pid = menu30000.id,
            permissionId = permission30600.id,
            name = "监管对象信息查询",
            title = "监管对象信息查询",
            type = MenuTypeEnum.MENU,
            path = "/projectTask/supervisionInformationQuery",
            component = "/projectTask/supervisionInformationQuery/list",
            sort = 30600
        )
        val menu30000List = listOfNotNull(
            menu30100, menu30200, menu30300, menu30400, menu30500, menu30600
        )
        menuJpaRepository.saveAll(menu30000List)
        val menu40000 = Menu(
            permissionId = permission40000.id,
            name = "点位布设管理",
            title = "点位布设管理",
            type = MenuTypeEnum.CATALOG,
            path = "/pointManage",
            component = "LAYOUT",
            sort = 40000
        )
        menuJpaRepository.save(menu40000)
        val menu40100 = Menu(
            pid = menu40000.id,
            permissionId = permission40100.id,
            name = "测试项目新增维护",
            title = "测试项目新增维护",
            type = MenuTypeEnum.MENU,
            path = "/pointManage/testMaintenance",
            component = "/pointManage/testMaintenance/list",
            sort = 40100
        )
        val menu40200 = Menu(
            pid = menu40000.id,
            permissionId = permission40200.id,
            name = "测试项目分类管理",
            title = "测试项目分类管理",
            type = MenuTypeEnum.MENU,
            path = "/pointManage/testClassification",
            component = "/pointManage/testClassification/list",
            sort = 40200
        )
        val menu40300 = Menu(
            pid = menu40000.id,
            permissionId = permission40300.id,
            name = "新增测试项目审核",
            title = "新增测试项目审核",
            type = MenuTypeEnum.MENU,
            path = "/pointManage/addTestItemAudit",
            component = "/pointManage/addTestItemAudit/list",
            sort = 40300
        )
        val menu40400 = Menu(
            pid = menu40000.id,
            permissionId = permission40400.id,
            name = "布点人员任务分配",
            title = "布点人员任务分配",
            type = MenuTypeEnum.MENU,
            path = "/pointManage/assignPersonnelTask",
            component = "/pointManage/assignPersonnelTask/list",
            sort = 40400
        )
        val menu40500 = Menu(
            pid = menu40000.id,
            permissionId = permission40500.id,
            name = "布点数据成果录入",
            title = "布点数据成果录入",
            type = MenuTypeEnum.MENU,
            // TODO 应该是 pointManage 吧？
            path = "/pointUserTasks/LayOutMethodMaintain",
            component = "/pointUserTasks/LayOutMethodMaintain/list",
            sort = 40500
        )
        val menu40600 = Menu(
            pid = menu40000.id,
            permissionId = permission40600.id,
            name = "布点方案数据退回",
            title = "布点方案数据退回",
            type = MenuTypeEnum.MENU,
            path = "/pointManage/dotsDataReturned",
            component = "/pointManage/dotsDataReturned/list",
            sort = 40600
        )
        val menu40700 = Menu(
            pid = menu40000.id,
            permissionId = permission40700.id,
            name = "布点方案数据查询",
            title = "布点方案数据查询",
            type = MenuTypeEnum.MENU,
            // TODO 应该是 pointManage 吧？
            path = "/point/f",
            component = "/point/f/list",
            sort = 40700
        )
        val menu40000List = listOfNotNull(
            menu40100, menu40200, menu40300, menu40400, menu40500, menu40600, menu40700
        )
        menuJpaRepository.saveAll(menu40000List)
        val menu50000 = Menu(
            permissionId = permission50000.id,
            name = "点位布设",
            title = "点位布设",
            type = MenuTypeEnum.CATALOG,
            path = "/pointLayout",
            component = "LAYOUT",
            sort = 50000
        )
        menuJpaRepository.save(menu50000)
        val menu50100 = Menu(
            pid = menu50000.id,
            permissionId = permission50100.id,
            name = "布点方案数据维护",
            title = "布点方案数据维护",
            type = MenuTypeEnum.MENU,
            path = "pointLayout/planMaintain",
            component = "pointLayout/planMaintain/list",
            sort = 50100
        )
        val menu50200 = Menu(
            pid = menu50000.id,
            permissionId = permission50200.id,
            name = "布点方案问题整改",
            title = "布点方案问题整改",
            type = MenuTypeEnum.MENU,
            path = "pointLayout/planUpdate",
            component = "pointLayout/planUpdate/list",
            sort = 50200
        )
        val menu50300 = Menu(
            pid = menu50000.id,
            permissionId = permission50300.id,
            name = "布点方案信息查询",
            title = "布点方案信息查询",
            type = MenuTypeEnum.MENU,
            path = "pointLayout/planQuery",
            component = "pointLayout/planQuery/list",
            sort = 50300
        )
        val menu50000List = listOfNotNull(
            menu50100, menu50200, menu50300
        )
        menuJpaRepository.saveAll(menu50000List)
        val menu60000 = Menu(
            permissionId = permission60000.id,
            name = "布点质控管理",
            title = "布点质控管理",
            type = MenuTypeEnum.CATALOG,
            path = "/layout",
            component = "LAYOUT",
            sort = 60000
        )
        menuJpaRepository.save(menu60000)
        val menu60100 = Menu(
            pid = menu60000.id,
            permissionId = permission60100.id,
            name = "布点质控专家组维护",
            title = "布点质控专家组维护",
            type = MenuTypeEnum.MENU,
            path = "/layout/a",
            component = "/layout/a/list",
            sort = 60100
        )
        val menu60200 = Menu(
            pid = menu60000.id,
            permissionId = permission60200.id,
            name = "一级质控(县)任务分配",
            title = "一级质控(县)任务分配",
            type = MenuTypeEnum.MENU,
            path = "/layout/b",
            component = "/layout/b/list",
            sort = 60200
        )
        val menu60300 = Menu(
            pid = menu60000.id,
            permissionId = permission60300.id,
            name = "二级质控(市)任务分配",
            title = "二级质控(市)任务分配",
            type = MenuTypeEnum.MENU,
            path = "/layout/c",
            component = "/layout/c/list",
            sort = 60300
        )
        val menu60400 = Menu(
            pid = menu60000.id,
            permissionId = permission60400.id,
            name = "三级质控(省)任务分配",
            title = "三级质控(省)任务分配",
            type = MenuTypeEnum.MENU,
            path = "/layout/d",
            component = "/layout/d/list",
            sort = 60400
        )
        val menu60500 = Menu(
            pid = menu60000.id,
            permissionId = permission60500.id,
            name = "布点质控专家组任务",
            title = "布点质控专家组任务",
            type = MenuTypeEnum.MENU,
            path = "/layout/e",
            component = "/layout/e/list",
            sort = 60500
        )
        val menu60600 = Menu(
            pid = menu60000.id,
            permissionId = permission60600.id,
            name = "布点质控意见反馈",
            title = "布点质控意见反馈",
            type = MenuTypeEnum.MENU,
            path = "/layout/f",
            component = "/layout/f/list",
            sort = 60600
        )
        val menu60700 = Menu(
            pid = menu60000.id,
            permissionId = permission60700.id,
            name = "布点质控专家查询",
            title = "布点质控专家查询",
            type = MenuTypeEnum.MENU,
            path = "/layout/g",
            component = "/layout/g/list",
            sort = 60700
        )
        val menu60800 = Menu(
            pid = menu60000.id,
            permissionId = permission60800.id,
            name = "布点质控意见退回",
            title = "布点质控意见退回",
            type = MenuTypeEnum.MENU,
            path = "/layout/h",
            component = "/layout/h/list",
            sort = 60800
        )
        val menu60000List = listOfNotNull(
            menu60100, menu60200, menu60300, menu60400, menu60500, menu60600, menu60700, menu60800
        )
        menuJpaRepository.saveAll(menu60000List)
        val menu70000 = Menu(
            permissionId = permission70000.id,
            name = "采样调查管理",
            title = "采样调查管理",
            type = MenuTypeEnum.CATALOG,
            path = "/sampleManage",
            component = "LAYOUT",
            sort = 70000
        )
        menuJpaRepository.save(menu70000)
        val menu70100 = Menu(
            pid = menu70000.id,
            permissionId = permission70100.id,
            name = "取样小组任务分配",
            title = "取样小组任务分配",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/sampleGroupTask",
            component = "/sampleManage/sampleGroupTask/list",
            sort = 70100
        )
        val menu70200 = Menu(
            pid = menu70000.id,
            permissionId = permission70200.id,
            name = "批次样品运送表单",
            title = "批次样品运送表单",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/sampleShippingForm",
            component = "/sampleManage/sampleShippingForm/list",
            sort = 70200
        )
        val menu70300 = Menu(
            pid = menu70000.id,
            permissionId = permission70300.id,
            name = "取样调查表单下载",
            title = "取样调查表单下载",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/formDownload",
            component = "/sampleManage/formDownload/list",
            sort = 70300
        )
        val menu70400 = Menu(
            pid = menu70000.id,
            permissionId = permission70400.id,
            name = "子样流转进度查询",
            title = "子样流转进度查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/sampleProgressQuery",
            component = "/sampleManage/sampleProgressQuery/list",
            sort = 70400
        )
        val menu70500 = Menu(
            pid = menu70000.id,
            permissionId = permission70500.id,
            name = "取样资料单位内审",
            title = "取样资料单位内审",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/CYInformationCheck",
            component = "/sampleManage/CYInformationCheck/list",
            sort = 70500
        )
        val menu70600 = Menu(
            pid = menu70000.id,
            permissionId = permission70600.id,
            name = "质控退回样点查询",
            title = "质控退回样点查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/QCPointQuery",
            component = "/sampleManage/QCPointQuery/list",
            sort = 70600
        )
        val menu70700 = Menu(
            pid = menu70000.id,
            permissionId = permission70700.id,
            name = "严重质量问题申诉",
            title = "严重质量问题申诉",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/seriousIssueAppeal",
            component = "/sampleManage/seriousIssueAppeal/list",
            sort = 70700
        )
        val menu70800 = Menu(
            pid = menu70000.id,
            permissionId = permission70800.id,
            name = "重采样品信息查询",
            title = "重采样品信息查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/resampleSampleQuery",
            component = "/sampleManage/resampleSampleQuery/list",
            sort = 70800
        )
        val menu70900 = Menu(
            pid = menu70000.id,
            permissionId = permission70900.id,
            name = "资料内审状态查询",
            title = "资料内审状态查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleManage/internalStatusInquiry",
            component = "/sampleManage/internalStatusInquiry/list",
            sort = 70900
        )
        val menu71000 = Menu(
            pid = menu70000.id,
            permissionId = permission71000.id,
            name = "资料内审进展统计",
            title = "资料内审进展统计",
            type = MenuTypeEnum.MENU,
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/ky",
            component = "/sampleSurveys/ky/list",
            sort = 71000
        )
        val menu71100 = Menu(
            pid = menu70000.id,
            permissionId = permission71100.id,
            name = "采样调查信息查询",
            title = "采样调查信息查询",
            type = MenuTypeEnum.MENU,
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/l",
            component = "/sampleSurveys/l/list",
            sort = 71100
        )
        val menu71200 = Menu(
            pid = menu70000.id,
            permissionId = permission71200.id,
            name = "单位取样进展统计",
            title = "单位取样进展统计",
            type = MenuTypeEnum.MENU,
            // TODO 应该是 sampleManage 吧？
            path = "/sampleSurveys/i",
            component = "/sampleSurveys/i/list",
            sort = 71200
        )
        val menu70000List = listOfNotNull(
            menu70100,
            menu70200,
            menu70300,
            menu70400,
            menu70500,
            menu70600,
            menu70700,
            menu70800,
            menu70900,
            menu71000,
            menu71100,
            menu71200
        )
        menuJpaRepository.saveAll(menu70000List)
        val menu80000 = Menu(
            permissionId = permission80000.id,
            name = "取样调查",
            title = "取样调查",
            type = MenuTypeEnum.CATALOG,
            path = "/sampleSurvey",
            component = "LAYOUT",
            sort = 80000
        )
        menuJpaRepository.save(menu80000)
        val menu80100 = Menu(
            pid = menu80000.id,
            permissionId = permission80100.id,
            name = "取样调查表单明细",
            title = "取样调查表单明细",
            type = MenuTypeEnum.MENU,
            path = "/sampleSurvey/formDetails",
            component = "/sampleSurvey/formDetails/list",
            sort = 80100
        )
        val menu80200 = Menu(
            pid = menu80000.id,
            permissionId = permission80200.id,
            name = "质控退回样点明细",
            title = "质控退回样点明细",
            type = MenuTypeEnum.MENU,
            path = "/sampleSurvey/QCReturnDetails",
            component = "/sampleSurvey/QCReturnDetails/list",
            sort = 80200
        )
        val menu80300 = Menu(
            pid = menu80000.id,
            permissionId = permission80300.id,
            name = "严重问题申诉记录",
            title = "严重问题申诉记录",
            type = MenuTypeEnum.MENU,
            path = "/sampleSurvey/seriousProblemRecord",
            component = "/sampleSurvey/seriousProblemRecord/list",
            sort = 80300
        )
        val menu80400 = Menu(
            pid = menu80000.id,
            permissionId = permission80400.id,
            name = "重采样品信息明细",
            title = "重采样品信息明细",
            type = MenuTypeEnum.MENU,
            path = "/sampleSurvey/sampleInformationDetails",
            component = "/sampleSurvey/sampleInformationDetails/list",
            sort = 80400
        )
        val menu80000List = listOfNotNull(
            menu80100, menu80200, menu80300, menu80400
        )
        menuJpaRepository.saveAll(menu80000List)
        val menu90000 = Menu(
            permissionId = permission90000.id,
            name = "采样质控管理",
            title = "采样质控管理",
            type = MenuTypeEnum.CATALOG,
            path = "/samplingQualityControl",
            component = "LAYOUT",
            sort = 90000
        )
        menuJpaRepository.save(menu90000)
        val menu90100 = Menu(
            pid = menu90000.id,
            permissionId = permission90100.id,
            name = "采样一级质控(县)任务",
            title = "采样一级质控(县)任务",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/a",
            component = "/samplingQualityControl/a/list",
            sort = 90100
        )
        val menu90200 = Menu(
            pid = menu90000.id,
            permissionId = permission90200.id,
            name = "采样二级质控(市)任务",
            title = "采样二级质控(市)任务",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/b",
            component = "/samplingQualityControl/b/list",
            sort = 90200
        )
        val menu90300 = Menu(
            pid = menu90000.id,
            permissionId = permission90300.id,
            name = "采样三级质控(省)任务",
            title = "采样三级质控(省)任务",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/c",
            component = "/samplingQualityControl/c/list",
            sort = 90300
        )
        val menu90400 = Menu(
            pid = menu90000.id,
            permissionId = permission90400.id,
            name = "采样质控专家任务",
            title = "采样质控专家任务",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/d",
            component = "/samplingQualityControl/d/list",
            sort = 90400
        )
        val menu90500 = Menu(
            pid = menu90000.id,
            permissionId = permission90500.id,
            name = "采样质控意见反馈",
            title = "采样质控意见反馈",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/e",
            component = "/samplingQualityControl/e/list",
            sort = 90500
        )
        val menu90600 = Menu(
            pid = menu90000.id,
            permissionId = permission90600.id,
            name = "取样资料质控进度",
            title = "取样资料质控进度",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/f",
            component = "/samplingQualityControl/f/list",
            sort = 90600
        )
        val menu90700 = Menu(
            pid = menu90000.id,
            permissionId = permission90700.id,
            name = "采样质控专家查询",
            title = "采样质控专家查询",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/g",
            component = "/samplingQualityControl/g/list",
            sort = 90700
        )
        val menu90800 = Menu(
            pid = menu90000.id,
            permissionId = permission90800.id,
            name = "采样质控意见退回",
            title = "采样质控意见退回",
            type = MenuTypeEnum.MENU,
            path = "/samplingQualityControl/h",
            component = "/samplingQualityControl/h/list",
            sort = 90800
        )
        val menu90000List = listOfNotNull(
            menu90100, menu90200, menu90300, menu90400, menu90500, menu90600, menu90700, menu90800
        )
        menuJpaRepository.saveAll(menu90000List)
        val menu100000 = Menu(
            permissionId = permission100000.id,
            name = "样品检测管理",
            title = "样品检测管理",
            type = MenuTypeEnum.CATALOG,
            path = "/sampleTesting",
            component = "LAYOUT",
            sort = 100000
        )
        menuJpaRepository.save(menu100000)
        val menu100100 = Menu(
            pid = menu100000.id,
            permissionId = permission100100.id,
            name = "批次送检样交接单",
            title = "批次送检样交接单",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/a",
            component = "/sampleTesting/a/list",
            sort = 100100
        )
        val menu100200 = Menu(
            pid = menu100000.id,
            permissionId = permission100200.id,
            name = "检测子样信息查询",
            title = "检测子样信息查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/b",
            component = "/sampleTesting/b/list",
            sort = 100200
        )
        val menu100300 = Menu(
            pid = menu100000.id,
            permissionId = permission100300.id,
            name = "检测资质文件报送",
            title = "检测资质文件报送",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/TestQualificationDocuments",
            component = "/sampleTesting/TestQualificationDocuments/list",
            sort = 100300
        )
        val menu100400 = Menu(
            pid = menu100000.id,
            permissionId = permission100400.id,
            name = "检测资质能力审核",
            title = "检测资质能力审核",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/QualificationCompetencyAudit",
            component = "/sampleTesting/QualificationCompetencyAudit/list",
            sort = 100400
        )
        val menu100500 = Menu(
            pid = menu100000.id,
            permissionId = permission100500.id,
            name = "基本检测方法标准",
            title = "基本检测方法标准",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/e",
            component = "/sampleTesting/e/list",
            sort = 100500
        )
        val menu100600 = Menu(
            pid = menu100000.id,
            permissionId = permission100600.id,
            name = "地方新增检测方法",
            title = "地方新增检测方法",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/f",
            component = "/sampleTesting/f/list",
            sort = 100600
        )
        val menu100700 = Menu(
            pid = menu100000.id,
            permissionId = permission100700.id,
            name = "方法验证材料上传",
            title = "方法验证材料上传",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/g",
            component = "/sampleTesting/g/list",
            sort = 100700
        )
        val menu100800 = Menu(
            pid = menu100000.id,
            permissionId = permission100800.id,
            name = "方法验证材料审核",
            title = "方法验证材料审核",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/h",
            component = "/sampleTesting/h/list",
            sort = 100800
        )
        val menu100900 = Menu(
            pid = menu100000.id,
            permissionId = permission100900.id,
            name = "统一监控样品管理",
            title = "统一监控样品管理",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/i",
            component = "/sampleTesting/i/list",
            sort = 100900
        )
        val menu101000 = Menu(
            pid = menu100000.id,
            permissionId = permission101000.id,
            name = "统一监控样品查询",
            title = "统一监控样品查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/j",
            component = "/sampleTesting/j/list",
            sort = 101000
        )
        val menu101100 = Menu(
            pid = menu100000.id,
            permissionId = permission101100.id,
            name = "严重问题样品查询",
            title = "严重问题样品查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/k",
            component = "/sampleTesting/k/list",
            sort = 101100
        )
        val menu101200 = Menu(
            pid = menu100000.id,
            permissionId = permission101200.id,
            name = "批次检测数据报送",
            title = "批次检测数据报送",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/l",
            component = "/sampleTesting/l/list",
            sort = 101200
        )
        val menu101300 = Menu(
            pid = menu100000.id,
            permissionId = permission101300.id,
            name = "批次检测数据整改",
            title = "批次检测数据整改",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/m",
            component = "/sampleTesting/m/list",
            sort = 101300
        )
        val menu101400 = Menu(
            pid = menu100000.id,
            permissionId = permission101400.id,
            name = "样品检测数据查询",
            title = "样品检测数据查询",
            type = MenuTypeEnum.MENU,
            path = "/sampleTesting/n",
            component = "/sampleTesting/n/list",
            sort = 101400
        )
        val menu100000List = listOfNotNull(
            menu100100,
            menu100200,
            menu100300,
            menu100400,
            menu100500,
            menu100600,
            menu100700,
            menu100800,
            menu100900,
            menu101000,
            menu101100,
            menu101200,
            menu101300,
            menu101400
        )
        menuJpaRepository.saveAll(menu100000List)
        val menu110000 = Menu(
            permissionId = permission110000.id,
            name = "数据质量审核",
            title = "数据质量审核",
            type = MenuTypeEnum.CATALOG,
            path = "/dataQualityAudits",
            component = "LAYOUT",
            sort = 110000
        )
        menuJpaRepository.save(menu110000)
        val menu110100 = Menu(
            pid = menu110000.id,
            permissionId = permission110100.id,
            name = "检测一级质控(县)任务",
            title = "检测一级质控(县)任务",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/a",
            component = "/dataQualityAudits/a/list",
            sort = 110100
        )
        val menu110200 = Menu(
            pid = menu110000.id,
            permissionId = permission110200.id,
            name = "检测二级质控(市)任务",
            title = "检测二级质控(市)任务",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/b",
            component = "/dataQualityAudits/b/list",
            sort = 110200
        )
        val menu110300 = Menu(
            pid = menu110000.id,
            permissionId = permission110300.id,
            name = "检测三级质控(省)任务",
            title = "检测三级质控(省)任务",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/c",
            component = "/dataQualityAudits/c/list",
            sort = 110300
        )
        val menu110400 = Menu(
            pid = menu110000.id,
            permissionId = permission110400.id,
            name = "检测质控专家任务",
            title = "检测质控专家任务",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/d",
            component = "/dataQualityAudits/d/list",
            sort = 110400
        )
        val menu110500 = Menu(
            pid = menu110000.id,
            permissionId = permission110500.id,
            name = "检测质控意见反馈",
            title = "检测质控意见反馈",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/e",
            component = "/dataQualityAudits/e/list",
            sort = 110500
        )
        val menu110600 = Menu(
            pid = menu110000.id,
            permissionId = permission110600.id,
            name = "质控退改批次查询",
            title = "质控退改批次查询",
            type = MenuTypeEnum.MENU,
            path = "/dataQualityAudits/f",
            component = "/dataQualityAudits/f/list",
            sort = 110600
        )
        val menu110000List = listOfNotNull(
            menu110100, menu110200, menu110300, menu110400, menu110500, menu110600
        )
        menuJpaRepository.saveAll(menu110000List)
        val menu120000 = Menu(
            permissionId = permission120000.id,
            name = "数据统计分析",
            title = "数据统计分析",
            type = MenuTypeEnum.CATALOG,
            path = "/statisticalAnalysisOfData",
            component = "LAYOUT",
            sort = 120000
        )
        menuJpaRepository.save(menu120000)
        val menu130000 = Menu(
            permissionId = permission130000.id,
            name = "数据对标评价",
            title = "数据对标评价",
            type = MenuTypeEnum.CATALOG,
            path = "/dataBenchmarkingEvaluation",
            component = "LAYOUT",
            sort = 130000
        )
        menuJpaRepository.save(menu130000)
        val menu140000 = Menu(
            permissionId = permission140000.id,
            name = "工作文件管理",
            title = "工作文件管理",
            type = MenuTypeEnum.CATALOG,
            path = "/workFileManagement",
            component = "LAYOUT",
            sort = 140000
        )
        menuJpaRepository.save(menu140000)
        val menu140100 = Menu(
            pid = menu140000.id,
            permissionId = permission140100.id,
            name = "工作文件下载",
            title = "工作文件下载",
            type = MenuTypeEnum.MENU,
            path = "/workFileManagement/a",
            component = "/workFileManagement/a/list",
            sort = 140100
        )
        menuJpaRepository.save(menu140100)
        val menu150000 = Menu(
            permissionId = permission150000.id,
            name = "后台数据管理",
            title = "后台数据管理",
            type = MenuTypeEnum.CATALOG,
            path = "/system",
            component = "LAYOUT",
            sort = 150000
        )
        menuJpaRepository.save(menu150000)
        val menu150100 = Menu(
            pid = menu150000.id,
            permissionId = permission150100.id,
            name = "单位管理",
            title = "单位管理",
            type = MenuTypeEnum.MENU,
            path = "/system/organization",
            component = "/system/organization/list",
            sort = 150100
        )
        val menu150200 = Menu(
            pid = menu150000.id,
            permissionId = permission150200.id,
            name = "用户管理",
            title = "用户管理",
            type = MenuTypeEnum.MENU,
            path = "/system/user",
            component = "/system/user/list",
            sort = 150200
        )
        val menu150300 = Menu(
            pid = menu150000.id,
            permissionId = permission150300.id,
            name = "角色管理",
            title = "角色管理",
            type = MenuTypeEnum.MENU,
            path = "/system/role",
            component = "/system/role/list",
            sort = 150300
        )
        val menu150400 = Menu(
            pid = menu150000.id,
            permissionId = permission150400.id,
            name = "权限管理",
            title = "权限管理",
            type = MenuTypeEnum.MENU,
            path = "/system/permission",
            component = "/system/permission/list",
            sort = 150400
        )
        val menu150500 = Menu(
            pid = menu150000.id,
            permissionId = permission150500.id,
            name = "菜单管理",
            title = "菜单管理",
            type = MenuTypeEnum.MENU,
            path = "/system/menu",
            component = "/system/menu/list",
            sort = 150500
        )
        val menu150600 = Menu(
            pid = menu150000.id,
            permissionId = permission150600.id,
            name = "字典管理",
            title = "字典管理",
            type = MenuTypeEnum.MENU,
            path = "/system/dict",
            component = "/system/dict/list",
            sort = 150600
        )
        val menu150000List = listOfNotNull(
            menu150100, menu150200, menu150300, menu150400, menu150500, menu150600
        )
        menuJpaRepository.saveAll(menu150000List)
        val menu150501 = Menu(
            pid = menu150500.id,
            permissionId = permission150501.id,
            name = "新增",
            title = "新增",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Create",
            sort = 150501
        )
        val menu150502 = Menu(
            pid = menu150500.id,
            permissionId = permission150502.id,
            name = "修改",
            title = "修改",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Edit",
            sort = 150502
        )
        val menu150503 = Menu(
            pid = menu150500.id,
            permissionId = permission150503.id,
            name = "删除",
            title = "删除",
            type = MenuTypeEnum.BUTTON,
            authCode = "System:Menu:Delete",
            sort = 150503
        )
        val menu150500List = listOfNotNull(menu150501, menu150502, menu150503)
        menuJpaRepository.saveAll(menu150500List)
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
