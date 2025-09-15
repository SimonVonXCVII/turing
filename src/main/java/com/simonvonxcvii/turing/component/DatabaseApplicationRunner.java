package com.simonvonxcvii.turing.component;

import com.simonvonxcvii.turing.entity.*;
import com.simonvonxcvii.turing.enums.OrganizationTypeEnum;
import com.simonvonxcvii.turing.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据库初始化 runner
 * TODO Convert to Kotlin file
 *
 * @author Simon Von
 * @since 2023/8/25 21:24
 */
@Component
@RequiredArgsConstructor
public class DatabaseApplicationRunner implements ApplicationRunner {

    private static final Log log = LogFactory.getLog(DatabaseApplicationRunner.class);

    /**
     * 字典排序
     */
    private static int areaSort;

    private final DataSource dataSource;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final DictRepository dictRepository;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 判断是否需要初始化，如果表存在说明不需要
        Connection connection = dataSource.getConnection();
        boolean next = connection.getMetaData()
                .getTables(null, null, "turing_dict", null)
                .next();
        if (next) return;

        // 创建数据库表
        ClassPathResource classPathResourceTableSql = new ClassPathResource("/sql/table.sql");
        if (!classPathResourceTableSql.exists()) {
            log.warn("数据库表文件不存在，无法初始化");
            return;
        }
        ScriptUtils.executeSqlScript(connection, classPathResourceTableSql);

        // 创建基础数据
        init();

        // 创建地区数据
        log.info("开始初始化区域字典");
        ClassPathResource classPathResourceAreaCsv = new ClassPathResource("/dict/area.csv");
        if (!classPathResourceAreaCsv.exists()) {
            log.warn("区域文件不存在，无法初始化");
            return;
        }

        List<Area> areaList;
        Map<String, Area> provinceMap;
        try (InputStream inputStream = classPathResourceAreaCsv.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader in = new BufferedReader(inputStreamReader)) {
            String line;
            areaList = new ArrayList<>();
            provinceMap = new TreeMap<>();
            //解析区域文件
            Set<String> aresCodeSet = new HashSet<>();
            while ((line = in.readLine()) != null) {
                List<String> oneArea = Arrays.asList(StringUtils.commaDelimitedListToStringArray(line));
                Area area = new Area();
                area.name = oneArea.get(0);
                area.adCode = oneArea.get(1);
                area.cityCode = oneArea.size() > 2 ? oneArea.get(2) : "";
                aresCodeSet.add(area.adCode);
                areaList.add(area);

                if (area.adCode.endsWith("0000")) {
                    area.parentAdCode = null;
                } else if (area.adCode.endsWith("00")) {
                    area.parentAdCode = area.adCode.substring(0, 2) + "0000";
                } else {
                    String cityCode = area.adCode.substring(0, 4) + "00";
                    if (aresCodeSet.contains(cityCode)) {
                        area.parentAdCode = cityCode;
                    } else {
                        //直辖县
                        area.parentAdCode = area.adCode.substring(0, 2) + "0000";
                    }
                }
            }
        }
        // 按层级
        Map<String, Area> detailVoMap = areaList.stream().collect(Collectors.toMap(a -> a.adCode, Function.identity()));
        Function<Area, String> pid = a -> a.parentAdCode;
        BiConsumer<Area, Area> consumer = (parent, child) -> {
            if (parent == null) {
                provinceMap.put(child.adCode, child);
            } else {
                parent.children.add(child);
            }
        };
        Function<Area, String> pidWrapper = t -> {
            String pid2 = pid.apply(t);
            if (!StringUtils.hasText(pid2)) {
                return pid2;
            }
            if (!pid2.contains(",")) {
                return pid2;
            }

            String[] ids = StringUtils.commaDelimitedListToStringArray(pid2);
            return ids[ids.length - 1];
        };
        areaList.forEach(node -> {
            String parentId = pidWrapper.apply(node);
            if (StringUtils.hasText(parentId)) {
                Area parent = detailVoMap.get(parentId);
                consumer.accept(parent, node);
            } else {
                consumer.accept(null, node);
            }
        });
        List<Dict> dictList = new ArrayList<>();
        provinceMap.forEach((_, area) -> saveArea(null, area, dictList));
        dictRepository.saveAll(dictList);
        log.info("初始化区域完成");
    }

    /**
     * 初始化后台数据管理、单位管理、用户管理、角色管理、权限管理、菜单管理、字典管理
     *
     * @author Simon Von
     * @since 2023/8/25 21:45
     */
    void init() {
        Organization organization = new Organization();
        organization.setName("平台管理单位")
                .setCode("000000000000000000")
                .setType(OrganizationTypeEnum.PLATFORM.getDesc())
                .setProvinceCode(310000)
                .setCityCode(310100)
                .setDistrictCode(310101)
                .setProvinceName("上海市")
                .setCityName("上海市市辖区")
                .setDistrictName("黄浦区")
                .setAddress("太平路一号")
                .setLegalPerson("admin")
                .setPhone("021-88888888");
        organizationRepository.save(organization);


        User user = new User();
        user.setName("admin")
                .setMobile(18888888888L)
                .setGender("男")
                .setOrgId(organization.getId())
                .setOrgName(organization.getName())
                .setUsername("admin")
                .setPassword(passwordEncoder.encode("123456"))
                .setAccountNonExpired(Boolean.TRUE)
                .setAccountNonLocked(Boolean.TRUE)
                .setCredentialsNonExpired(Boolean.TRUE)
                .setEnabled(Boolean.TRUE)
                .setManager(Boolean.TRUE)
                .setNeedSetPassword(Boolean.FALSE);
        userRepository.save(user);


        List<Role> roleList = new ArrayList<>();
        Role role = new Role();
        role.setAuthority("SUPER_ADMIN");
        role.setName("超级管理员");
        role.setDescription("超级管理员");
        roleList.add(role);

        Role role1 = new Role();
        role1.setAuthority("GOV_COUNTRY_ADMIN");
        role1.setName("国家级行政单位管理员");
        role1.setDescription("国家级行政单位管理员");
        roleList.add(role1);

        Role role2 = new Role();
        role2.setAuthority("GOV_COUNTRY_STAFF");
        role2.setName("国家级行政单位工作人员");
        role2.setDescription("国家级行政单位工作人员");
        roleList.add(role2);

        Role role3 = new Role();
        role3.setAuthority("GOV_PROVINCE_ADMIN");
        role3.setName("省级行政单位管理员");
        role3.setDescription("省级行政单位管理员");
        roleList.add(role3);

        Role role4 = new Role();
        role4.setAuthority("GOV_PROVINCE_STAFF");
        role4.setName("省级行政单位工作人员");
        role4.setDescription("省级行政单位工作人员");
        roleList.add(role4);

        Role role5 = new Role();
        role5.setAuthority("GOV_CITY_ADMIN");
        role5.setName("市级行政单位管理员");
        role5.setDescription("市级行政单位管理员");
        roleList.add(role5);

        Role role6 = new Role();
        role6.setAuthority("GOV_CITY_STAFF");
        role6.setName("市级行政单位工作人员");
        role6.setDescription("市级行政单位工作人员");
        roleList.add(role6);

        Role role7 = new Role();
        role7.setAuthority("GOV_DISTRICT_ADMIN");
        role7.setName("县级行政单位管理员");
        role7.setDescription("县级行政单位管理员");
        roleList.add(role7);

        Role role8 = new Role();
        role8.setAuthority("GOV_DISTRICT_STAFF");
        role8.setName("县级行政单位工作人员");
        role8.setDescription("县级行政单位工作人员");
        roleList.add(role8);

        Role role9 = new Role();
        role9.setAuthority("BUSINESS_MINE_INFORMATION_COLLECTION_ADMIN");
        role9.setName("矿山信息采集单位管理员");
        role9.setDescription("矿山信息采集单位管理员");
        roleList.add(role9);

        Role role10 = new Role();
        role10.setAuthority("BUSINESS_MINE_INFORMATION_COLLECTION_STAFF");
        role10.setName("矿山信息采集单位工作人员");
        role10.setDescription("矿山信息采集单位工作人员");
        roleList.add(role10);

        Role role11 = new Role();
        role11.setAuthority("BUSINESS_REGIONAL_SURVEY_LOCATIONS_ADMIN");
        role11.setName("区域调查布点单位管理员");
        role11.setDescription("区域调查布点单位管理员");
        roleList.add(role11);

        Role role12 = new Role();
        role12.setAuthority("BUSINESS_REGIONAL_SURVEY_LOCATIONS_STAFF");
        role12.setName("区域调查布点单位工作人员");
        role12.setDescription("区域调查布点单位工作人员");
        roleList.add(role12);

        Role role13 = new Role();
        role13.setAuthority("BUSINESS_AREA_SAMPLING_SURVEYS_ADMIN");
        role13.setName("区域采样调查单位管理员");
        role13.setDescription("区域采样调查单位管理员");
        roleList.add(role13);

        Role role14 = new Role();
        role14.setAuthority("BUSINESS_AREA_SAMPLING_SURVEYS_STAFF");
        role14.setName("区域采样调查单位工作人员");
        role14.setDescription("区域采样调查单位工作人员");
        roleList.add(role14);

        Role role15 = new Role();
        role15.setAuthority("BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_ADMIN");
        role15.setName("地块信息采集单位管理员");
        role15.setDescription("地块信息采集单位管理员");
        roleList.add(role15);

        Role role16 = new Role();
        role16.setAuthority("BUSINESS_LAND_PARCEL_INFORMATION_COLLECTION_STAFF");
        role16.setName("地块信息采集单位工作人员");
        role16.setDescription("地块信息采集单位工作人员");
        roleList.add(role16);

        Role role17 = new Role();
        role17.setAuthority("BUSINESS_PLOT_SURVEY_LAYOUT_ADMIN");
        role17.setName("地块调查布点单位管理员");
        role17.setDescription("地块调查布点单位管理员");
        roleList.add(role17);

        Role role18 = new Role();
        role18.setAuthority("BUSINESS_PLOT_SURVEY_LAYOUT_STAFF");
        role18.setName("地块调查布点单位工作人员");
        role18.setDescription("地块调查布点单位工作人员");
        roleList.add(role18);

        Role role19 = new Role();
        role19.setAuthority("BUSINESS_PLOT_SAMPLING_SURVEY_ADMIN");
        role19.setName("地块采样调查单位管理员");
        role19.setDescription("地块采样调查单位管理员");
        roleList.add(role19);

        Role role20 = new Role();
        role20.setAuthority("BUSINESS_PLOT_SAMPLING_SURVEY_STAFF");
        role20.setName("地块采样调查单位工作人员");
        role20.setDescription("地块采样调查单位工作人员");
        roleList.add(role20);

        Role role21 = new Role();
        role21.setAuthority("BUSINESS_SAMPLE_TESTING_ADMIN");
        role21.setName("样品检测单位管理员");
        role21.setDescription("样品检测单位管理员");
        roleList.add(role21);

        Role role22 = new Role();
        role22.setAuthority("BUSINESS_SAMPLE_TESTING_STAFF");
        role22.setName("样品检测单位工作人员");
        role22.setDescription("样品检测单位工作人员");
        roleList.add(role22);

        Role role23 = new Role();
        role23.setAuthority("BUSINESS_DATA_ANALYSIS_EVALUATION_ADMIN");
        role23.setName("数据分析评价单位管理员");
        role23.setDescription("数据分析评价单位管理员");
        roleList.add(role23);

        Role role24 = new Role();
        role24.setAuthority("BUSINESS_DATA_ANALYSIS_EVALUATION_STAFF");
        role24.setName("数据分析评价单位工作人员");
        role24.setDescription("数据分析评价单位工作人员");
        roleList.add(role24);

        Role role25 = new Role();
        role25.setAuthority("BUSINESS_CONTAMINATION_RISK_ASSESSMENT_ADMIN");
        role25.setName("污染风险评估单位管理员");
        role25.setDescription("污染风险评估单位管理员");
        roleList.add(role25);

        Role role26 = new Role();
        role26.setAuthority("BUSINESS_CONTAMINATION_RISK_ASSESSMENT_STAFF");
        role26.setName("污染风险评估单位工作人员");
        role26.setDescription("污染风险评估单位工作人员");
        roleList.add(role26);

        Role role27 = new Role();
        role27.setAuthority("QC_INFORMATION_COLLECTION_ADMIN");
        role27.setName("信息采集质控单位管理员");
        role27.setDescription("信息采集质控单位管理员");
        roleList.add(role27);

        Role role28 = new Role();
        role28.setAuthority("QC_INFORMATION_COLLECTION_STAFF");
        role28.setName("信息采集质控单位工作人员");
        role28.setDescription("信息采集质控单位工作人员");
        roleList.add(role28);

        Role role29 = new Role();
        role29.setAuthority("QC_DISTRIBUTE_ADMIN");
        role29.setName("布点质控单位管理员");
        role29.setDescription("布点质控单位管理员");
        roleList.add(role29);

        Role role30 = new Role();
        role30.setAuthority("QC_DISTRIBUTE_STAFF");
        role30.setName("布点质控单位工作人员");
        role30.setDescription("布点质控单位工作人员");
        roleList.add(role30);

        Role role31 = new Role();
        role31.setAuthority("QC_SAMPLING_ADMIN");
        role31.setName("采样质控单位管理员");
        role31.setDescription("采样质控单位管理员");
        roleList.add(role31);

        Role role32 = new Role();
        role32.setAuthority("QC_SAMPLING_STAFF");
        role32.setName("采样质控单位工作人员");
        role32.setDescription("采样质控单位工作人员");
        roleList.add(role32);

        Role role33 = new Role();
        role33.setAuthority("QC_SAMPLE_TESTING_ADMIN");
        role33.setName("样品检测质控单位管理员");
        role33.setDescription("样品检测质控单位管理员");
        roleList.add(role33);

        Role role34 = new Role();
        role34.setAuthority("QC_SAMPLE_TESTING_STAFF");
        role34.setName("样品检测质控单位工作人员");
        role34.setDescription("样品检测质控单位工作人员");
        roleList.add(role34);

        Role role35 = new Role();
        role35.setAuthority("LEADER");
        role35.setName("信息采集和取样调查小组组长");
        role35.setDescription("信息采集和取样调查小组组长");
        roleList.add(role35);

        Role role36 = new Role();
        role36.setAuthority("MEMBER");
        role36.setName("信息采集和取样调查小组组员");
        role36.setDescription("信息采集和取样调查小组组员");
        roleList.add(role36);

        Role role37 = new Role();
        role37.setAuthority("INFORMATION_COLLECTION_INTERNAL_AUDITOR");
        role37.setName("信息采集单位内审人员");
        role37.setDescription("信息采集单位内审人员");
        roleList.add(role37);

        Role role38 = new Role();
        role38.setAuthority("PLOT_SAMPLING_SURVEY_INTERNAL_AUDITOR");
        role38.setName("取样调查单位内审人员");
        role38.setDescription("取样调查单位内审人员");
        roleList.add(role38);

        Role role39 = new Role();
        role39.setAuthority("DEFAULT_TECHNICAL");
        role39.setName("技术单位管理员的默认角色");
        role39.setDescription("注册技术单位时赋予管理员的默认角色，仅有【技术单位业务申请】权限");
        roleList.add(role39);
        roleRepository.saveAll(roleList);


        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleRepository.save(userRole);


        List<Permission> permissionList = new ArrayList<>();
        Permission permission100 = new Permission();
        permission100.setName("基础数据管理");
        permission100.setSort(100);
        permissionList.add(permission100);

        Permission permission101 = new Permission();
        permission101.setPid(permission100.getId());
        permission101.setName("业务管理账号开通");
        permission101.setSort(101);
        permissionList.add(permission101);

        Permission permission102 = new Permission();
        permission102.setPid(permission100.getId());
        permission102.setName("业务管理单位授权");
        permission102.setSort(102);
        permissionList.add(permission102);

        Permission permission103 = new Permission();
        permission103.setPid(permission100.getId());
        permission103.setName("技术单位信息管理");
        permission103.setSort(103);
        permissionList.add(permission103);

        Permission permission104 = new Permission();
        permission104.setPid(permission100.getId());
        permission104.setName("技术专家信息维护");
        permission104.setSort(104);
        permissionList.add(permission104);

        Permission permission105 = new Permission();
        permission105.setPid(permission100.getId());
        permission105.setName("数据字典管理");
        permission105.setSort(105);
        permissionList.add(permission105);

        Permission permission106 = new Permission();
        permission106.setPid(permission100.getId());
        permission106.setName("设备终端授权管理");
        permission106.setSort(106);
        permissionList.add(permission106);

        Permission permission107 = new Permission();
        permission107.setPid(permission100.getId());
        permission107.setName("平台用户新增维护");
        permission107.setSort(107);
        permissionList.add(permission107);

        Permission permission200 = new Permission();
        permission200.setName("用户单位管理");
        permission200.setSort(200);
        permissionList.add(permission200);

        Permission permission201 = new Permission();
        permission201.setPid(permission200.getId());
        permission201.setName("管理单位用户维护");
        permission201.setSort(201);
        permissionList.add(permission201);

        Permission permission202 = new Permission();
        permission202.setPid(permission200.getId());
        permission202.setName("技术单位业务申请");
        permission202.setSort(202);
        permissionList.add(permission202);

        Permission permission203 = new Permission();
        permission203.setPid(permission200.getId());
        permission203.setName("技术单位业务审核");
        permission203.setSort(203);
        permissionList.add(permission203);

        Permission permission204 = new Permission();
        permission204.setPid(permission200.getId());
        permission204.setName("技术单位用户维护");
        permission204.setSort(204);
        permissionList.add(permission204);

        Permission permission205 = new Permission();
        permission205.setPid(permission200.getId());
        permission205.setName("技术专家业务申请");
        permission205.setSort(205);
        permissionList.add(permission205);

        Permission permission206 = new Permission();
        permission206.setPid(permission200.getId());
        permission206.setName("技术专家业务审核");
        permission206.setSort(206);
        permissionList.add(permission206);

        Permission permission207 = new Permission();
        permission207.setPid(permission200.getId());
        permission207.setName("调查小组新建维护");
        permission207.setSort(207);
        permissionList.add(permission207);

        Permission permission208 = new Permission();
        permission208.setPid(permission200.getId());
        permission208.setName("调查单位信息查询");
        permission208.setSort(208);
        permissionList.add(permission208);

        Permission permission209 = new Permission();
        permission209.setPid(permission200.getId());
        permission209.setName("调查用户信息查询");
        permission209.setSort(209);
        permissionList.add(permission209);

        Permission permission300 = new Permission();
        permission300.setName("项目任务管理");
        permission300.setSort(300);
        permissionList.add(permission300);

        Permission permission301 = new Permission();
        permission301.setPid(permission300.getId());
        permission301.setName("项目新增维护");
        permission301.setSort(301);
        permissionList.add(permission301);

        Permission permission302 = new Permission();
        permission302.setPid(permission300.getId());
        permission302.setName("监管对象维护");
        permission302.setSort(302);
        permissionList.add(permission302);

        Permission permission303 = new Permission();
        permission303.setPid(permission300.getId());
        permission303.setName("任务下发管理");
        permission303.setSort(303);
        permissionList.add(permission303);

        Permission permission304 = new Permission();
        permission304.setPid(permission300.getId());
        permission304.setName("牵头单位实施");
        permission304.setSort(304);
        permissionList.add(permission304);

        Permission permission305 = new Permission();
        permission305.setPid(permission300.getId());
        permission305.setName("监管对象信息查询");
        permission305.setSort(305);
        permissionList.add(permission305);

        Permission permission306 = new Permission();
        permission306.setPid(permission300.getId());
        permission306.setName("技术单位任务执行");
        permission306.setSort(306);
        permissionList.add(permission306);

        Permission permission400 = new Permission();
        permission400.setName("点位布设管理");
        permission400.setSort(400);
        permissionList.add(permission400);

        Permission permission401 = new Permission();
        permission401.setPid(permission400.getId());
        permission401.setName("测试项目新增维护");
        permission401.setSort(401);
        permissionList.add(permission401);

        Permission permission402 = new Permission();
        permission402.setPid(permission400.getId());
        permission402.setName("测试项目分类管理");
        permission402.setSort(402);
        permissionList.add(permission402);

        Permission permission403 = new Permission();
        permission403.setPid(permission400.getId());
        permission403.setName("布点人员任务分配");
        permission403.setSort(403);
        permissionList.add(permission403);

        Permission permission404 = new Permission();
        permission404.setPid(permission400.getId());
        permission404.setName("布点数据成果录入");
        permission404.setSort(404);
        permissionList.add(permission404);

        Permission permission405 = new Permission();
        permission405.setPid(permission400.getId());
        permission405.setName("布点方案数据退回");
        permission405.setSort(405);
        permissionList.add(permission405);

        Permission permission406 = new Permission();
        permission406.setPid(permission400.getId());
        permission406.setName("布点方案数据查询");
        permission406.setSort(406);
        permissionList.add(permission406);

        Permission permission407 = new Permission();
        permission407.setPid(permission400.getId());
        permission407.setName("新增测试项目审核");
        permission407.setSort(407);
        permissionList.add(permission407);

        Permission permission500 = new Permission();
        permission500.setName("点位布设");
        permission500.setSort(500);
        permissionList.add(permission500);

        Permission permission501 = new Permission();
        permission501.setPid(permission500.getId());
        permission501.setName("布点方案问题整改");
        permission501.setSort(501);
        permissionList.add(permission501);

        Permission permission502 = new Permission();
        permission502.setPid(permission500.getId());
        permission502.setName("布点方案数据维护");
        permission502.setSort(502);
        permissionList.add(permission502);

        Permission permission503 = new Permission();
        permission503.setPid(permission500.getId());
        permission503.setName("布点方案信息查询");
        permission503.setSort(503);
        permissionList.add(permission503);

        Permission permission600 = new Permission();
        permission600.setName("布点质控管理");
        permission600.setSort(600);
        permissionList.add(permission600);

        Permission permission601 = new Permission();
        permission601.setPid(permission600.getId());
        permission601.setName("布点质控专家组维护");
        permission601.setSort(601);
        permissionList.add(permission601);

        Permission permission602 = new Permission();
        permission602.setPid(permission600.getId());
        permission602.setName("一级质控(县)任务分配");
        permission602.setSort(602);
        permissionList.add(permission602);

        Permission permission603 = new Permission();
        permission603.setPid(permission600.getId());
        permission603.setName("二级质控(市)任务分配");
        permission603.setSort(603);
        permissionList.add(permission603);

        Permission permission604 = new Permission();
        permission604.setPid(permission600.getId());
        permission604.setName("三级质控(省)任务分配");
        permission604.setSort(604);
        permissionList.add(permission604);

        Permission permission605 = new Permission();
        permission605.setPid(permission600.getId());
        permission605.setName("布点质控专家组任务");
        permission605.setSort(605);
        permissionList.add(permission605);

        Permission permission606 = new Permission();
        permission606.setPid(permission600.getId());
        permission606.setName("布点质控意见反馈");
        permission606.setSort(606);
        permissionList.add(permission606);

        Permission permission607 = new Permission();
        permission607.setPid(permission600.getId());
        permission607.setName("布点质控专家查询");
        permission607.setSort(607);
        permissionList.add(permission607);

        Permission permission608 = new Permission();
        permission608.setPid(permission600.getId());
        permission608.setName("布点质控意见退回");
        permission608.setSort(608);
        permissionList.add(permission608);

        Permission permission700 = new Permission();
        permission700.setName("采样调查管理");
        permission700.setSort(700);
        permissionList.add(permission700);

        Permission permission701 = new Permission();
        permission701.setPid(permission700.getId());
        permission701.setName("牵头单位组织实施");
        permission701.setSort(701);
        permissionList.add(permission701);

        Permission permission702 = new Permission();
        permission702.setPid(permission700.getId());
        permission702.setName("采样小组任务分配");
        permission702.setSort(702);
        permissionList.add(permission702);

        Permission permission703 = new Permission();
        permission703.setPid(permission700.getId());
        permission703.setName("采样调查信息查询");
        permission703.setSort(703);
        permissionList.add(permission703);

        Permission permission704 = new Permission();
        permission704.setPid(permission700.getId());
        permission704.setName("检测子样进度查询");
        permission704.setSort(704);
        permissionList.add(permission704);

        Permission permission705 = new Permission();
        permission705.setPid(permission700.getId());
        permission705.setName("取样调查表单下载");
        permission705.setSort(705);
        permissionList.add(permission705);

        Permission permission706 = new Permission();
        permission706.setPid(permission700.getId());
        permission706.setName("采样资料单位内审");
        permission706.setSort(706);
        permissionList.add(permission706);

        Permission permission707 = new Permission();
        permission707.setPid(permission700.getId());
        permission707.setName("质控退回样点查询");
        permission707.setSort(707);
        permissionList.add(permission707);

        Permission permission708 = new Permission();
        permission708.setPid(permission700.getId());
        permission708.setName("批次样品运送表单");
        permission708.setSort(708);
        permissionList.add(permission708);

        Permission permission709 = new Permission();
        permission709.setPid(permission700.getId());
        permission709.setName("单位取样进展统计");
        permission709.setSort(709);
        permissionList.add(permission709);

        Permission permission710 = new Permission();
        permission710.setPid(permission700.getId());
        permission710.setName("资料内审状态查询");
        permission710.setSort(710);
        permissionList.add(permission710);

        Permission permission711 = new Permission();
        permission711.setPid(permission700.getId());
        permission711.setName("资料内审进展统计");
        permission711.setSort(711);
        permissionList.add(permission711);

        Permission permission712 = new Permission();
        permission712.setPid(permission700.getId());
        permission712.setName("重采样品信息查询");
        permission712.setSort(712);
        permissionList.add(permission712);

        Permission permission713 = new Permission();
        permission713.setPid(permission700.getId());
        permission713.setName("严重质量问题申诉");
        permission713.setSort(713);
        permissionList.add(permission713);

        Permission permission800 = new Permission();
        permission800.setName("取样调查");
        permission800.setSort(800);
        permissionList.add(permission800);

        Permission permission801 = new Permission();
        permission801.setPid(permission800.getId());
        permission801.setName("取样调查表单明细");
        permission801.setSort(801);
        permissionList.add(permission801);

        Permission permission802 = new Permission();
        permission802.setPid(permission800.getId());
        permission802.setName("质控退回样点明细");
        permission802.setSort(802);
        permissionList.add(permission802);

        Permission permission803 = new Permission();
        permission803.setPid(permission800.getId());
        permission803.setName("严重问题申诉记录");
        permission803.setSort(803);
        permissionList.add(permission803);

        Permission permission804 = new Permission();
        permission804.setPid(permission800.getId());
        permission804.setName("重采样品信息明细");
        permission804.setSort(804);
        permissionList.add(permission804);

        Permission permission900 = new Permission();
        permission900.setName("采样质控管理");
        permission900.setSort(900);
        permissionList.add(permission900);

        Permission permission901 = new Permission();
        permission901.setPid(permission900.getId());
        permission901.setName("采样一级质控(县)任务");
        permission901.setSort(901);
        permissionList.add(permission901);

        Permission permission902 = new Permission();
        permission902.setPid(permission900.getId());
        permission902.setName("采样二级质控(市)任务");
        permission902.setSort(902);
        permissionList.add(permission902);

        Permission permission903 = new Permission();
        permission903.setPid(permission900.getId());
        permission903.setName("采样三级质控(省)任务");
        permission903.setSort(903);
        permissionList.add(permission903);

        Permission permission904 = new Permission();
        permission904.setPid(permission900.getId());
        permission904.setName("采样质控专家任务");
        permission904.setSort(904);
        permissionList.add(permission904);

        Permission permission905 = new Permission();
        permission905.setPid(permission900.getId());
        permission905.setName("采样质控意见反馈");
        permission905.setSort(905);
        permissionList.add(permission905);

        Permission permission906 = new Permission();
        permission906.setPid(permission900.getId());
        permission906.setName("取样资料质控进度");
        permission906.setSort(906);
        permissionList.add(permission906);

        Permission permission907 = new Permission();
        permission907.setPid(permission900.getId());
        permission907.setName("采样质控专家查询");
        permission907.setSort(907);
        permissionList.add(permission907);

        Permission permission908 = new Permission();
        permission908.setPid(permission900.getId());
        permission908.setName("采样质控意见退回");
        permission908.setSort(908);
        permissionList.add(permission908);

        Permission permission1000 = new Permission();
        permission1000.setName("样品检测管理");
        permission1000.setSort(1000);
        permissionList.add(permission1000);

        Permission permission1001 = new Permission();
        permission1001.setPid(permission1000.getId());
        permission1001.setName("批次送检样交接单");
        permission1001.setSort(1001);
        permissionList.add(permission1001);

        Permission permission1002 = new Permission();
        permission1002.setPid(permission1000.getId());
        permission1002.setName("检测子样信息查询");
        permission1002.setSort(1002);
        permissionList.add(permission1002);

        Permission permission1003 = new Permission();
        permission1003.setPid(permission1000.getId());
        permission1003.setName("检测资质文件报送");
        permission1003.setSort(1003);
        permissionList.add(permission1003);

        Permission permission1004 = new Permission();
        permission1004.setPid(permission1000.getId());
        permission1004.setName("检测资质能力审核");
        permission1004.setSort(1004);
        permissionList.add(permission1004);

        Permission permission1005 = new Permission();
        permission1005.setPid(permission1000.getId());
        permission1005.setName("基本检测方法标准");
        permission1005.setSort(1005);
        permissionList.add(permission1005);

        Permission permission1006 = new Permission();
        permission1006.setPid(permission1000.getId());
        permission1006.setName("地方新增检测方法");
        permission1006.setSort(1006);
        permissionList.add(permission1006);

        Permission permission1007 = new Permission();
        permission1007.setPid(permission1000.getId());
        permission1007.setName("方法验证材料上传");
        permission1007.setSort(1007);
        permissionList.add(permission1007);

        Permission permission1008 = new Permission();
        permission1008.setPid(permission1000.getId());
        permission1008.setName("方法验证材料审核");
        permission1008.setSort(1008);
        permissionList.add(permission1008);

        Permission permission1009 = new Permission();
        permission1009.setPid(permission1000.getId());
        permission1009.setName("统一监控样品管理");
        permission1009.setSort(1009);
        permissionList.add(permission1009);

        Permission permission1010 = new Permission();
        permission1010.setPid(permission1000.getId());
        permission1010.setName("统一监控样品查询");
        permission1010.setSort(1010);
        permissionList.add(permission1010);

        Permission permission1011 = new Permission();
        permission1011.setPid(permission1000.getId());
        permission1011.setName("严重问题样品查询");
        permission1011.setSort(1011);
        permissionList.add(permission1011);

        Permission permission1012 = new Permission();
        permission1012.setPid(permission1000.getId());
        permission1012.setName("批次检测数据报送");
        permission1012.setSort(1012);
        permissionList.add(permission1012);

        Permission permission1013 = new Permission();
        permission1013.setPid(permission1000.getId());
        permission1013.setName("批次检测数据整改");
        permission1013.setSort(1013);
        permissionList.add(permission1013);

        Permission permission1014 = new Permission();
        permission1014.setPid(permission1000.getId());
        permission1014.setName("样品检测数据查询");
        permission1014.setSort(1014);
        permissionList.add(permission1014);

        Permission permission1100 = new Permission();
        permission1100.setName("数据质量审核");
        permission1100.setSort(1100);
        permissionList.add(permission1100);

        Permission permission1101 = new Permission();
        permission1101.setPid(permission1100.getId());
        permission1101.setName("检测一级质控(县)任务");
        permission1101.setSort(1101);
        permissionList.add(permission1101);

        Permission permission1102 = new Permission();
        permission1102.setPid(permission1100.getId());
        permission1102.setName("检测二级质控(市)任务");
        permission1102.setSort(1102);
        permissionList.add(permission1102);

        Permission permission1103 = new Permission();
        permission1103.setPid(permission1100.getId());
        permission1103.setName("检测三级质控(省)任务");
        permission1103.setSort(1103);
        permissionList.add(permission1103);

        Permission permission1104 = new Permission();
        permission1104.setPid(permission1100.getId());
        permission1104.setName("检测质控专家任务");
        permission1104.setSort(1104);
        permissionList.add(permission1104);

        Permission permission1105 = new Permission();
        permission1105.setPid(permission1100.getId());
        permission1105.setName("检测质控意见反馈");
        permission1105.setSort(1105);
        permissionList.add(permission1105);

        Permission permission1106 = new Permission();
        permission1106.setPid(permission1100.getId());
        permission1106.setName("质控退改批次查询");
        permission1106.setSort(1106);
        permissionList.add(permission1106);

        Permission permission1200 = new Permission();
        permission1200.setName("数据统计分析");
        permission1200.setSort(1200);
        permissionList.add(permission1200);

        Permission permission1300 = new Permission();
        permission1300.setName("数据对标评价");
        permission1300.setSort(1300);
        permissionList.add(permission1300);

        Permission permission1400 = new Permission();
        permission1400.setName("工作文件管理");
        permission1400.setSort(1400);
        permissionList.add(permission1400);

        Permission permission1401 = new Permission();
        permission1401.setPid(permission1400.getId());
        permission1401.setName("工作文件下载");
        permission1401.setSort(1401);
        permissionList.add(permission1401);

        Permission permission1500 = new Permission();
        permission1500.setName("后台数据管理");
        permission1500.setSort(1500);
        permissionList.add(permission1500);

        Permission permission1501 = new Permission();
        permission1501.setPid(permission1500.getId());
        permission1501.setName("单位管理");
        permission1501.setSort(1501);
        permissionList.add(permission1501);

        Permission permission1502 = new Permission();
        permission1502.setPid(permission1500.getId());
        permission1502.setName("用户管理");
        permission1502.setSort(1502);
        permissionList.add(permission1502);

        Permission permission1503 = new Permission();
        permission1503.setPid(permission1500.getId());
        permission1503.setName("角色管理");
        permission1503.setSort(1503);
        permissionList.add(permission1503);

        Permission permission1504 = new Permission();
        permission1504.setPid(permission1500.getId());
        permission1504.setName("权限管理");
        permission1504.setSort(1504);
        permissionList.add(permission1504);

        Permission permission1505 = new Permission();
        permission1505.setPid(permission1500.getId());
        permission1505.setName("菜单管理");
        permission1505.setSort(1505);
        permissionList.add(permission1505);

        Permission permission1506 = new Permission();
        permission1506.setPid(permission1500.getId());
        permission1506.setName("字典管理");
        permission1506.setSort(1506);
        permissionList.add(permission1506);
        permissionRepository.saveAll(permissionList);


        List<Menu> menuList = new ArrayList<>();
        Menu menu100 = new Menu();
        menu100.setPermissionId(permission100.getId());
        menu100.setName("基础数据管理");
        menu100.setTitle("基础数据管理");
        menu100.setType("ROUTE");
        menu100.setPath("/basic");
        menu100.setComponent("LAYOUT");
        menu100.setSort(100);
        menu100.setShowed(Boolean.TRUE);
        menu100.setCached(Boolean.TRUE);
        menu100.setExternal(Boolean.FALSE);
        menuList.add(menu100);

        Menu menu101 = new Menu();
        menu101.setPid(menu100.getId());
        menu101.setPermissionId(permission101.getId());
        menu101.setName("业务管理账号开通");
        menu101.setTitle("业务管理账号开通");
        menu101.setType("ROUTE");
        menu101.setPath("/basic/accountOpen");
        menu101.setComponent("/basic/accountOpen/index");
        menu101.setSort(101);
        menu101.setShowed(Boolean.TRUE);
        menu101.setCached(Boolean.TRUE);
        menu101.setExternal(Boolean.FALSE);
        menuList.add(menu101);

        Menu menu102 = new Menu();
        menu102.setPid(menu100.getId());
        menu102.setPermissionId(permission102.getId());
        menu102.setName("业务管理单位授权");
        menu102.setTitle("业务管理单位授权");
        menu102.setType("ROUTE");
        menu102.setPath("/basic/empower");
        menu102.setComponent("/basic/empower/index");
        menu102.setSort(102);
        menu102.setShowed(Boolean.TRUE);
        menu102.setCached(Boolean.TRUE);
        menu102.setExternal(Boolean.FALSE);
        menuList.add(menu102);

        Menu menu103 = new Menu();
        menu103.setPid(menu100.getId());
        menu103.setPermissionId(permission103.getId());
        menu103.setName("技术单位信息管理");
        menu103.setTitle("技术单位信息管理");
        menu103.setType("ROUTE");
        menu103.setPath("/basic/orgInfoManage");
        menu103.setComponent("/basic/orgInfoManage/index");
        menu103.setSort(103);
        menu103.setShowed(Boolean.TRUE);
        menu103.setCached(Boolean.TRUE);
        menu103.setExternal(Boolean.FALSE);
        menuList.add(menu103);

        Menu menu104 = new Menu();
        menu104.setPid(menu100.getId());
        menu104.setPermissionId(permission104.getId());
        menu104.setName("技术专家信息维护");
        menu104.setTitle("技术专家信息维护");
        menu104.setType("ROUTE");
        menu104.setPath("/basic/d");
        menu104.setComponent("/basic/d/index");
        menu104.setSort(104);
        menu104.setShowed(Boolean.TRUE);
        menu104.setCached(Boolean.TRUE);
        menu104.setExternal(Boolean.FALSE);
        menuList.add(menu104);

        Menu menu105 = new Menu();
        menu105.setPid(menu100.getId());
        menu105.setPermissionId(permission105.getId());
        menu105.setName("平台新增用户维护");
        menu105.setTitle("平台新增用户维护");
        menu105.setType("ROUTE");
        menu105.setPath("/basic/newUsersManage");
        menu105.setComponent("/basic/newUsersManage/index");
        menu105.setSort(105);
        menu105.setShowed(Boolean.TRUE);
        menu105.setCached(Boolean.TRUE);
        menu105.setExternal(Boolean.FALSE);
        menuList.add(menu105);

        Menu menu106 = new Menu();
        menu106.setPid(menu100.getId());
        menu106.setPermissionId(permission106.getId());
        menu106.setName("设备终端授权管理");
        menu106.setTitle("设备终端授权管理");
        menu106.setType("ROUTE");
        menu106.setPath("/basic/f");
        menu106.setComponent("/basic/f/index");
        menu106.setSort(106);
        menu106.setShowed(Boolean.TRUE);
        menu106.setCached(Boolean.TRUE);
        menu106.setExternal(Boolean.FALSE);
        menuList.add(menu106);

        Menu menu200 = new Menu();
        menu200.setPermissionId(permission200.getId());
        menu200.setName("用户单位管理");
        menu200.setTitle("用户单位管理");
        menu200.setType("ROUTE");
        menu200.setPath("/organization");
        menu200.setComponent("LAYOUT");
        menu200.setSort(200);
        menu200.setShowed(Boolean.TRUE);
        menu200.setCached(Boolean.TRUE);
        menu200.setExternal(Boolean.FALSE);
        menuList.add(menu200);

        Menu menu201 = new Menu();
        menu201.setPid(menu200.getId());
        menu201.setPermissionId(permission201.getId());
        menu201.setName("管理单位用户维护");
        menu201.setTitle("管理单位用户维护");
        menu201.setType("ROUTE");
        menu201.setPath("/organization/manageUnitUser");
        menu201.setComponent("/organization/manageUnitUser/index");
        menu201.setSort(201);
        menu201.setShowed(Boolean.TRUE);
        menu201.setCached(Boolean.TRUE);
        menu201.setExternal(Boolean.FALSE);
        menuList.add(menu201);

        Menu menu202 = new Menu();
        menu202.setPid(menu200.getId());
        menu202.setPermissionId(permission202.getId());
        menu202.setName("技术单位业务申请");
        menu202.setTitle("技术单位业务申请");
        menu202.setType("ROUTE");
        menu202.setPath("/organization/organizationBusinessApply");
        menu202.setComponent("/organization/organizationBusinessApply/index");
        menu202.setSort(202);
        menu202.setShowed(Boolean.TRUE);
        menu202.setCached(Boolean.TRUE);
        menu202.setExternal(Boolean.FALSE);
        menuList.add(menu202);

        Menu menu203 = new Menu();
        menu203.setPid(menu200.getId());
        menu203.setPermissionId(permission203.getId());
        menu203.setName("技术单位业务审核");
        menu203.setTitle("技术单位业务审核");
        menu203.setType("ROUTE");
        menu203.setPath("/organization/organizationBusinessCheck");
        menu203.setComponent("/organization/organizationBusinessCheck/index");
        menu203.setSort(203);
        menu203.setShowed(Boolean.TRUE);
        menu203.setCached(Boolean.TRUE);
        menu203.setExternal(Boolean.FALSE);
        menuList.add(menu203);

        Menu menu204 = new Menu();
        menu204.setPid(menu200.getId());
        menu204.setPermissionId(permission204.getId());
        menu204.setName("技术单位用户维护");
        menu204.setTitle("技术单位用户维护");
        menu204.setType("ROUTE");
        menu204.setPath("/organization/technicalUnitUser");
        menu204.setComponent("/organization/technicalUnitUser/index");
        menu204.setSort(204);
        menu204.setShowed(Boolean.TRUE);
        menu204.setCached(Boolean.TRUE);
        menu204.setExternal(Boolean.FALSE);
        menuList.add(menu204);

        Menu menu205 = new Menu();
        menu205.setPid(menu200.getId());
        menu205.setPermissionId(permission205.getId());
        menu205.setName("技术专家业务申请");
        menu205.setTitle("技术专家业务申请");
        menu205.setType("ROUTE");
        menu205.setPath("/organization/e");
        menu205.setComponent("/organization/e/index");
        menu205.setSort(205);
        menu205.setShowed(Boolean.TRUE);
        menu205.setCached(Boolean.TRUE);
        menu205.setExternal(Boolean.FALSE);
        menuList.add(menu205);

        Menu menu206 = new Menu();
        menu206.setPid(menu200.getId());
        menu206.setPermissionId(permission206.getId());
        menu206.setName("技术专家业务审核");
        menu206.setTitle("技术专家业务审核");
        menu206.setType("ROUTE");
        menu206.setPath("/organization/f");
        menu206.setComponent("/organization/f/index");
        menu206.setSort(206);
        menu206.setShowed(Boolean.TRUE);
        menu206.setCached(Boolean.TRUE);
        menu206.setExternal(Boolean.FALSE);
        menuList.add(menu206);

        Menu menu207 = new Menu();
        menu207.setPid(menu200.getId());
        menu207.setPermissionId(permission207.getId());
        menu207.setName("调查小组新建维护");
        menu207.setTitle("调查小组新建维护");
        menu207.setType("ROUTE");
        menu207.setPath("/organization/groupAddOrUpt");
        menu207.setComponent("/organization/groupAddOrUpt/index");
        menu207.setSort(207);
        menu207.setShowed(Boolean.TRUE);
        menu207.setCached(Boolean.TRUE);
        menu207.setExternal(Boolean.FALSE);
        menuList.add(menu207);

        Menu menu208 = new Menu();
        menu208.setPid(menu200.getId());
        menu208.setPermissionId(permission208.getId());
        menu208.setName("调查单位信息查询");
        menu208.setTitle("调查单位信息查询");
        menu208.setType("ROUTE");
        menu208.setPath("/organization/unitInformationQuery");
        menu208.setComponent("/organization/unitInformationQuery/index");
        menu208.setSort(208);
        menu208.setShowed(Boolean.TRUE);
        menu208.setCached(Boolean.TRUE);
        menu208.setExternal(Boolean.FALSE);
        menuList.add(menu208);

        Menu menu209 = new Menu();
        menu209.setPid(menu200.getId());
        menu209.setPermissionId(permission209.getId());
        menu209.setName("调查用户信息查询");
        menu209.setTitle("调查用户信息查询");
        menu209.setType("ROUTE");
        menu209.setPath("/organization/userInformationQuery");
        menu209.setComponent("/organization/userInformationQuery/index");
        menu209.setSort(209);
        menu209.setShowed(Boolean.TRUE);
        menu209.setCached(Boolean.TRUE);
        menu209.setExternal(Boolean.FALSE);
        menuList.add(menu209);

        Menu menu300 = new Menu();
        menu300.setPermissionId(permission300.getId());
        menu300.setName("项目任务管理");
        menu300.setTitle("项目任务管理");
        menu300.setType("ROUTE");
        menu300.setPath("/projectTask");
        menu300.setComponent("LAYOUT");
        menu300.setSort(300);
        menu300.setShowed(Boolean.TRUE);
        menu300.setCached(Boolean.TRUE);
        menu300.setExternal(Boolean.FALSE);
        menuList.add(menu300);

        Menu menu301 = new Menu();
        menu301.setPid(menu300.getId());
        menu301.setPermissionId(permission301.getId());
        menu301.setName("调查项目新增维护");
        menu301.setTitle("调查项目新增维护");
        menu301.setType("ROUTE");
        menu301.setPath("/projectTask/a");
        menu301.setComponent("/projectTask/a/index");
        menu301.setSort(301);
        menu301.setShowed(Boolean.TRUE);
        menu301.setCached(Boolean.TRUE);
        menu301.setExternal(Boolean.FALSE);
        menuList.add(menu301);

        Menu menu302 = new Menu();
        menu302.setPid(menu300.getId());
        menu302.setPermissionId(permission302.getId());
        menu302.setName("监管对象新增维护");
        menu302.setTitle("监管对象新增维护");
        menu302.setType("ROUTE");
        menu302.setPath("/projectTask/superviseObjectMaintain");
        menu302.setComponent("/projectTask/superviseObjectMaintain/index");
        menu302.setSort(302);
        menu302.setShowed(Boolean.TRUE);
        menu302.setCached(Boolean.TRUE);
        menu302.setExternal(Boolean.FALSE);
        menuList.add(menu302);

        Menu menu303 = new Menu();
        menu303.setPid(menu300.getId());
        menu303.setPermissionId(permission303.getId());
        menu303.setName("工作任务下发管理");
        menu303.setTitle("工作任务下发管理");
        menu303.setType("ROUTE");
        menu303.setPath("/projectTask/taskDistribution");
        menu303.setComponent("/projectTask/taskDistribution/index");
        menu303.setSort(303);
        menu303.setShowed(Boolean.TRUE);
        menu303.setCached(Boolean.TRUE);
        menu303.setExternal(Boolean.FALSE);
        menuList.add(menu303);

        Menu menu304 = new Menu();
        menu304.setPid(menu300.getId());
        menu304.setPermissionId(permission304.getId());
        menu304.setName("牵头单位组织实施");
        menu304.setTitle("牵头单位组织实施");
        menu304.setType("ROUTE");
        menu304.setPath("/projectTask/leadOrgImplement");
        menu304.setComponent("/projectTask/leadOrgImplement/index");
        menu304.setSort(304);
        menu304.setShowed(Boolean.TRUE);
        menu304.setCached(Boolean.TRUE);
        menu304.setExternal(Boolean.FALSE);
        menuList.add(menu304);

        Menu menu305 = new Menu();
        menu305.setPid(menu300.getId());
        menu305.setPermissionId(permission305.getId());
        menu305.setName("技术单位任务执行");
        menu305.setTitle("技术单位任务执行");
        menu305.setType("ROUTE");
        menu305.setPath("/projectTask/taskExecution");
        menu305.setComponent("/projectTask/taskExecution/index");
        menu305.setSort(305);
        menu305.setShowed(Boolean.TRUE);
        menu305.setCached(Boolean.TRUE);
        menu305.setExternal(Boolean.FALSE);
        menuList.add(menu305);

        Menu menu306 = new Menu();
        menu306.setPid(menu300.getId());
        menu306.setPermissionId(permission306.getId());
        menu306.setName("监管对象信息查询");
        menu306.setTitle("监管对象信息查询");
        menu306.setType("ROUTE");
        menu306.setPath("/projectTask/supervisionInformationQuery");
        menu306.setComponent("/projectTask/supervisionInformationQuery/index");
        menu306.setSort(306);
        menu306.setShowed(Boolean.TRUE);
        menu306.setCached(Boolean.TRUE);
        menu306.setExternal(Boolean.FALSE);
        menuList.add(menu306);

        Menu menu400 = new Menu();
        menu400.setPermissionId(permission400.getId());
        menu400.setName("点位布设管理");
        menu400.setTitle("点位布设管理");
        menu400.setType("ROUTE");
        menu400.setPath("/pointManage");
        menu400.setComponent("LAYOUT");
        menu400.setSort(400);
        menu400.setShowed(Boolean.TRUE);
        menu400.setCached(Boolean.TRUE);
        menu400.setExternal(Boolean.FALSE);
        menuList.add(menu400);

        Menu menu401 = new Menu();
        menu401.setPid(menu400.getId());
        menu401.setPermissionId(permission401.getId());
        menu401.setName("测试项目新增维护");
        menu401.setTitle("测试项目新增维护");
        menu401.setType("ROUTE");
        menu401.setPath("/pointManage/testMaintenance");
        menu401.setComponent("/pointManage/testMaintenance/index");
        menu401.setSort(401);
        menu401.setShowed(Boolean.TRUE);
        menu401.setCached(Boolean.TRUE);
        menu401.setExternal(Boolean.FALSE);
        menuList.add(menu401);

        Menu menu402 = new Menu();
        menu402.setPid(menu400.getId());
        menu402.setPermissionId(permission402.getId());
        menu402.setName("测试项目分类管理");
        menu402.setTitle("测试项目分类管理");
        menu402.setType("ROUTE");
        menu402.setPath("/pointManage/testClassification");
        menu402.setComponent("/pointManage/testClassification/index");
        menu402.setSort(402);
        menu402.setShowed(Boolean.TRUE);
        menu402.setCached(Boolean.TRUE);
        menu402.setExternal(Boolean.FALSE);
        menuList.add(menu402);

        Menu menu403 = new Menu();
        menu403.setPid(menu400.getId());
        menu403.setPermissionId(permission403.getId());
        menu403.setName("新增测试项目审核");
        menu403.setTitle("新增测试项目审核");
        menu403.setType("ROUTE");
        menu403.setPath("/pointManage/addTestItemAudit");
        menu403.setComponent("/pointManage/addTestItemAudit/index");
        menu403.setSort(403);
        menu403.setShowed(Boolean.TRUE);
        menu403.setCached(Boolean.TRUE);
        menu403.setExternal(Boolean.FALSE);
        menuList.add(menu403);

        Menu menu404 = new Menu();
        menu404.setPid(menu400.getId());
        menu404.setPermissionId(permission404.getId());
        menu404.setName("布点人员任务分配");
        menu404.setTitle("布点人员任务分配");
        menu404.setType("ROUTE");
        menu404.setPath("/pointManage/assignPersonnelTask");
        menu404.setComponent("/pointManage/assignPersonnelTask/index");
        menu404.setSort(404);
        menu404.setShowed(Boolean.TRUE);
        menu404.setCached(Boolean.TRUE);
        menu404.setExternal(Boolean.FALSE);
        menuList.add(menu404);

        Menu menu405 = new Menu();
        menu405.setPid(menu400.getId());
        menu405.setPermissionId(permission405.getId());
        menu405.setName("布点数据成果录入");
        menu405.setTitle("布点数据成果录入");
        menu405.setType("ROUTE");
        menu405.setPath("/pointUserTasks/LayOutMethodMaintain");
        menu405.setComponent("/pointUserTasks/LayOutMethodMaintain/index");
        menu405.setSort(405);
        menu405.setShowed(Boolean.TRUE);
        menu405.setCached(Boolean.TRUE);
        menu405.setExternal(Boolean.FALSE);
        menuList.add(menu405);

        Menu menu406 = new Menu();
        menu406.setPid(menu400.getId());
        menu406.setPermissionId(permission406.getId());
        menu406.setName("布点方案数据退回");
        menu406.setTitle("布点方案数据退回");
        menu406.setType("ROUTE");
        menu406.setPath("/pointManage/dotsDataReturned");
        menu406.setComponent("/pointManage/dotsDataReturned/index");
        menu406.setSort(406);
        menu406.setShowed(Boolean.TRUE);
        menu406.setCached(Boolean.TRUE);
        menu406.setExternal(Boolean.FALSE);
        menuList.add(menu406);

        Menu menu407 = new Menu();
        menu407.setPid(menu400.getId());
        menu407.setPermissionId(permission407.getId());
        menu407.setName("布点方案数据查询");
        menu407.setTitle("布点方案数据查询");
        menu407.setType("ROUTE");
        menu407.setPath("/point/f");
        menu407.setComponent("/point/f/index");
        menu407.setSort(407);
        menu407.setShowed(Boolean.TRUE);
        menu407.setCached(Boolean.TRUE);
        menu407.setExternal(Boolean.FALSE);
        menuList.add(menu407);

        Menu menu500 = new Menu();
        menu500.setPermissionId(permission500.getId());
        menu500.setName("点位布设");
        menu500.setTitle("点位布设");
        menu500.setType("ROUTE");
        menu500.setPath("/pointLayout");
        menu500.setComponent("LAYOUT");
        menu500.setSort(500);
        menu500.setShowed(Boolean.TRUE);
        menu500.setCached(Boolean.TRUE);
        menu500.setExternal(Boolean.FALSE);
        menuList.add(menu500);

        Menu menu501 = new Menu();
        menu501.setPid(menu500.getId());
        menu501.setPermissionId(permission501.getId());
        menu501.setName("布点方案数据维护");
        menu501.setTitle("布点方案数据维护");
        menu501.setType("ROUTE");
        menu501.setPath("/pointLayout/planMaintain");
        menu501.setComponent("/pointLayout/planMaintain/index");
        menu501.setSort(501);
        menu501.setShowed(Boolean.TRUE);
        menu501.setCached(Boolean.TRUE);
        menu501.setExternal(Boolean.FALSE);
        menuList.add(menu501);

        Menu menu502 = new Menu();
        menu502.setPid(menu500.getId());
        menu502.setPermissionId(permission502.getId());
        menu502.setName("布点方案问题整改");
        menu502.setTitle("布点方案问题整改");
        menu502.setType("ROUTE");
        menu502.setPath("/pointLayout/planUpdate");
        menu502.setComponent("/pointLayout/planUpdate/index");
        menu502.setSort(502);
        menu502.setShowed(Boolean.TRUE);
        menu502.setCached(Boolean.TRUE);
        menu502.setExternal(Boolean.FALSE);
        menuList.add(menu502);

        Menu menu503 = new Menu();
        menu503.setPid(menu500.getId());
        menu503.setPermissionId(permission503.getId());
        menu503.setName("布点方案信息查询");
        menu503.setTitle("布点方案信息查询");
        menu503.setType("ROUTE");
        menu503.setPath("/pointLayout/planQuery");
        menu503.setComponent("/pointLayout/planQuery/index");
        menu503.setSort(503);
        menu503.setShowed(Boolean.TRUE);
        menu503.setCached(Boolean.TRUE);
        menu503.setExternal(Boolean.FALSE);
        menuList.add(menu503);

        Menu menu600 = new Menu();
        menu600.setPermissionId(permission600.getId());
        menu600.setName("布点质控管理");
        menu600.setTitle("布点质控管理");
        menu600.setType("ROUTE");
        menu600.setPath("/layout");
        menu600.setComponent("LAYOUT");
        menu600.setSort(600);
        menu600.setShowed(Boolean.TRUE);
        menu600.setCached(Boolean.TRUE);
        menu600.setExternal(Boolean.FALSE);
        menuList.add(menu600);

        Menu menu601 = new Menu();
        menu601.setPid(menu600.getId());
        menu601.setPermissionId(permission601.getId());
        menu601.setName("布点质控专家组维护");
        menu601.setTitle("布点质控专家组维护");
        menu601.setType("ROUTE");
        menu601.setPath("/layout/a");
        menu601.setComponent("/layout/a/index");
        menu601.setSort(601);
        menu601.setShowed(Boolean.TRUE);
        menu601.setCached(Boolean.TRUE);
        menu601.setExternal(Boolean.FALSE);
        menuList.add(menu601);

        Menu menu602 = new Menu();
        menu602.setPid(menu600.getId());
        menu602.setPermissionId(permission602.getId());
        menu602.setName("一级质控(县)任务分配");
        menu602.setTitle("一级质控(县)任务分配");
        menu602.setType("ROUTE");
        menu602.setPath("/layout/b");
        menu602.setComponent("/layout/b/index");
        menu602.setSort(602);
        menu602.setShowed(Boolean.TRUE);
        menu602.setCached(Boolean.TRUE);
        menu602.setExternal(Boolean.FALSE);
        menuList.add(menu602);

        Menu menu603 = new Menu();
        menu603.setPid(menu600.getId());
        menu603.setPermissionId(permission603.getId());
        menu603.setName("二级质控(市)任务分配");
        menu603.setTitle("二级质控(市)任务分配");
        menu603.setType("ROUTE");
        menu603.setPath("/layout/c");
        menu603.setComponent("/layout/c/index");
        menu603.setSort(603);
        menu603.setShowed(Boolean.TRUE);
        menu603.setCached(Boolean.TRUE);
        menu603.setExternal(Boolean.FALSE);
        menuList.add(menu603);

        Menu menu604 = new Menu();
        menu604.setPid(menu600.getId());
        menu604.setPermissionId(permission604.getId());
        menu604.setName("三级质控(省)任务分配");
        menu604.setTitle("三级质控(省)任务分配");
        menu604.setType("ROUTE");
        menu604.setPath("/layout/d");
        menu604.setComponent("/layout/d/index");
        menu604.setSort(604);
        menu604.setShowed(Boolean.TRUE);
        menu604.setCached(Boolean.TRUE);
        menu604.setExternal(Boolean.FALSE);
        menuList.add(menu604);

        Menu menu605 = new Menu();
        menu605.setPid(menu600.getId());
        menu605.setPermissionId(permission605.getId());
        menu605.setName("布点质控专家组任务");
        menu605.setTitle("布点质控专家组任务");
        menu605.setType("ROUTE");
        menu605.setPath("/layout/e");
        menu605.setComponent("/layout/e/index");
        menu605.setSort(605);
        menu605.setShowed(Boolean.TRUE);
        menu605.setCached(Boolean.TRUE);
        menu605.setExternal(Boolean.FALSE);
        menuList.add(menu605);

        Menu menu606 = new Menu();
        menu606.setPid(menu600.getId());
        menu606.setPermissionId(permission606.getId());
        menu606.setName("布点质控意见反馈");
        menu606.setTitle("布点质控意见反馈");
        menu606.setType("ROUTE");
        menu606.setPath("/layout/f");
        menu606.setComponent("/layout/f/index");
        menu606.setSort(606);
        menu606.setShowed(Boolean.TRUE);
        menu606.setCached(Boolean.TRUE);
        menu606.setExternal(Boolean.FALSE);
        menuList.add(menu606);

        Menu menu607 = new Menu();
        menu607.setPid(menu600.getId());
        menu607.setPermissionId(permission607.getId());
        menu607.setName("布点质控专家查询");
        menu607.setTitle("布点质控专家查询");
        menu607.setType("ROUTE");
        menu607.setPath("/layout/g");
        menu607.setComponent("/layout/g/index");
        menu607.setSort(607);
        menu607.setShowed(Boolean.TRUE);
        menu607.setCached(Boolean.TRUE);
        menu607.setExternal(Boolean.FALSE);
        menuList.add(menu607);

        Menu menu608 = new Menu();
        menu608.setPid(menu600.getId());
        menu608.setPermissionId(permission608.getId());
        menu608.setName("布点质控意见退回");
        menu608.setTitle("布点质控意见退回");
        menu608.setType("ROUTE");
        menu608.setPath("/layout/h");
        menu608.setComponent("/layout/h/index");
        menu608.setSort(608);
        menu608.setShowed(Boolean.TRUE);
        menu608.setCached(Boolean.TRUE);
        menu608.setExternal(Boolean.FALSE);
        menuList.add(menu608);

        Menu menu700 = new Menu();
        menu700.setPermissionId(permission700.getId());
        menu700.setName("采样调查管理");
        menu700.setTitle("采样调查管理");
        menu700.setType("ROUTE");
        menu700.setPath("/sampleManage");
        menu700.setComponent("LAYOUT");
        menu700.setSort(700);
        menu700.setShowed(Boolean.TRUE);
        menu700.setCached(Boolean.TRUE);
        menu700.setExternal(Boolean.FALSE);
        menuList.add(menu700);

        Menu menu701 = new Menu();
        menu701.setPid(menu700.getId());
        menu701.setPermissionId(permission701.getId());
        menu701.setName("取样小组任务分配");
        menu701.setTitle("取样小组任务分配");
        menu701.setType("ROUTE");
        menu701.setPath("/sampleManage/sampleGroupTask");
        menu701.setComponent("/sampleManage/sampleGroupTask/index");
        menu701.setSort(701);
        menu701.setShowed(Boolean.TRUE);
        menu701.setCached(Boolean.TRUE);
        menu701.setExternal(Boolean.FALSE);
        menuList.add(menu701);

        Menu menu702 = new Menu();
        menu702.setPid(menu700.getId());
        menu702.setPermissionId(permission702.getId());
        menu702.setName("批次样品运送表单");
        menu702.setTitle("批次样品运送表单");
        menu702.setType("ROUTE");
        menu702.setPath("/sampleManage/sampleShippingForm");
        menu702.setComponent("/sampleManage/sampleShippingForm/index");
        menu702.setSort(702);
        menu702.setShowed(Boolean.TRUE);
        menu702.setCached(Boolean.TRUE);
        menu702.setExternal(Boolean.FALSE);
        menuList.add(menu702);

        Menu menu703 = new Menu();
        menu703.setPid(menu700.getId());
        menu703.setPermissionId(permission703.getId());
        menu703.setName("取样调查表单下载");
        menu703.setTitle("取样调查表单下载");
        menu703.setType("ROUTE");
        menu703.setPath("/sampleManage/formDownload");
        menu703.setComponent("/sampleManage/formDownload/index");
        menu703.setSort(703);
        menu703.setShowed(Boolean.TRUE);
        menu703.setCached(Boolean.TRUE);
        menu703.setExternal(Boolean.FALSE);
        menuList.add(menu703);

        Menu menu704 = new Menu();
        menu704.setPid(menu700.getId());
        menu704.setPermissionId(permission704.getId());
        menu704.setName("子样流转进度查询");
        menu704.setTitle("子样流转进度查询");
        menu704.setType("ROUTE");
        menu704.setPath("/sampleManage/sampleProgressQuery");
        menu704.setComponent("/sampleManage/sampleProgressQuery/index");
        menu704.setSort(704);
        menu704.setShowed(Boolean.TRUE);
        menu704.setCached(Boolean.TRUE);
        menu704.setExternal(Boolean.FALSE);
        menuList.add(menu704);

        Menu menu705 = new Menu();
        menu705.setPid(menu700.getId());
        menu705.setPermissionId(permission705.getId());
        menu705.setName("取样资料单位内审");
        menu705.setTitle("取样资料单位内审");
        menu705.setType("ROUTE");
        menu705.setPath("/sampleManage/CYInformationCheck");
        menu705.setComponent("/sampleManage/CYInformationCheck/index");
        menu705.setSort(705);
        menu705.setShowed(Boolean.TRUE);
        menu705.setCached(Boolean.TRUE);
        menu705.setExternal(Boolean.FALSE);
        menuList.add(menu705);

        Menu menu706 = new Menu();
        menu706.setPid(menu700.getId());
        menu706.setPermissionId(permission706.getId());
        menu706.setName("质控退回样点查询");
        menu706.setTitle("质控退回样点查询");
        menu706.setType("ROUTE");
        menu706.setPath("/sampleManage/QCPointQuery");
        menu706.setComponent("/sampleManage/QCPointQuery/index");
        menu706.setSort(706);
        menu706.setShowed(Boolean.TRUE);
        menu706.setCached(Boolean.TRUE);
        menu706.setExternal(Boolean.FALSE);
        menuList.add(menu706);

        Menu menu707 = new Menu();
        menu707.setPid(menu700.getId());
        menu707.setPermissionId(permission707.getId());
        menu707.setName("严重质量问题申诉");
        menu707.setTitle("严重质量问题申诉");
        menu707.setType("ROUTE");
        menu707.setPath("/sampleManage/seriousIssueAppeal");
        menu707.setComponent("/sampleManage/seriousIssueAppeal/index");
        menu707.setSort(707);
        menu707.setShowed(Boolean.TRUE);
        menu707.setCached(Boolean.TRUE);
        menu707.setExternal(Boolean.FALSE);
        menuList.add(menu707);

        Menu menu708 = new Menu();
        menu708.setPid(menu700.getId());
        menu708.setPermissionId(permission708.getId());
        menu708.setName("重采样品信息查询");
        menu708.setTitle("重采样品信息查询");
        menu708.setType("ROUTE");
        menu708.setPath("/sampleManage/resampleSampleQuery");
        menu708.setComponent("/sampleManage/resampleSampleQuery/index");
        menu708.setSort(708);
        menu708.setShowed(Boolean.TRUE);
        menu708.setCached(Boolean.TRUE);
        menu708.setExternal(Boolean.FALSE);
        menuList.add(menu708);

        Menu menu709 = new Menu();
        menu709.setPid(menu700.getId());
        menu709.setPermissionId(permission709.getId());
        menu709.setName("资料内审状态查询");
        menu709.setTitle("资料内审状态查询");
        menu709.setType("ROUTE");
        menu709.setPath("/sampleManage/internalStatusInquiry");
        menu709.setComponent("/sampleManage/internalStatusInquiry/index");
        menu709.setSort(709);
        menu709.setShowed(Boolean.TRUE);
        menu709.setCached(Boolean.TRUE);
        menu709.setExternal(Boolean.FALSE);
        menuList.add(menu709);

        Menu menu710 = new Menu();
        menu710.setPid(menu700.getId());
        menu710.setPermissionId(permission710.getId());
        menu710.setName("资料内审进展统计");
        menu710.setTitle("资料内审进展统计");
        menu710.setType("ROUTE");
        menu710.setPath("/sampleSurveys/ky");
        menu710.setComponent("/sampleSurveys/k/index");
        menu710.setSort(710);
        menu710.setShowed(Boolean.TRUE);
        menu710.setCached(Boolean.TRUE);
        menu710.setExternal(Boolean.FALSE);
        menuList.add(menu710);

        Menu menu711 = new Menu();
        menu711.setPid(menu700.getId());
        menu711.setPermissionId(permission711.getId());
        menu711.setName("采样调查信息查询");
        menu711.setTitle("采样调查信息查询");
        menu711.setType("ROUTE");
        menu711.setPath("/sampleSurveys/l");
        menu711.setComponent("/sampleSurveys/l/index");
        menu711.setSort(711);
        menu711.setShowed(Boolean.TRUE);
        menu711.setCached(Boolean.TRUE);
        menu711.setExternal(Boolean.FALSE);
        menuList.add(menu711);

        Menu menu712 = new Menu();
        menu712.setPid(menu700.getId());
        menu712.setPermissionId(permission712.getId());
        menu712.setName("单位取样进展统计");
        menu712.setTitle("单位取样进展统计");
        menu712.setType("ROUTE");
        menu712.setPath("/sampleSurveys/i");
        menu712.setComponent("/sampleSurveys/i/index");
        menu712.setSort(712);
        menu712.setShowed(Boolean.TRUE);
        menu712.setCached(Boolean.TRUE);
        menu712.setExternal(Boolean.FALSE);
        menuList.add(menu712);

        Menu menu800 = new Menu();
        menu800.setPermissionId(permission800.getId());
        menu800.setName("取样调查");
        menu800.setTitle("取样调查");
        menu800.setType("ROUTE");
        menu800.setPath("/sampleSurvey");
        menu800.setComponent("LAYOUT");
        menu800.setSort(800);
        menu800.setShowed(Boolean.TRUE);
        menu800.setCached(Boolean.TRUE);
        menu800.setExternal(Boolean.FALSE);
        menuList.add(menu800);

        Menu menu801 = new Menu();
        menu801.setPid(menu800.getId());
        menu801.setPermissionId(permission801.getId());
        menu801.setName("取样调查表单明细");
        menu801.setTitle("取样调查表单明细");
        menu801.setType("ROUTE");
        menu801.setPath("/sampleSurvey/formDetails");
        menu801.setComponent("/sampleSurvey/formDetails/index");
        menu801.setSort(801);
        menu801.setShowed(Boolean.TRUE);
        menu801.setCached(Boolean.TRUE);
        menu801.setExternal(Boolean.FALSE);
        menuList.add(menu801);

        Menu menu802 = new Menu();
        menu802.setPid(menu800.getId());
        menu802.setPermissionId(permission802.getId());
        menu802.setName("质控退回样点明细");
        menu802.setTitle("质控退回样点明细");
        menu802.setType("ROUTE");
        menu802.setPath("/sampleSurvey/QCReturnDetails");
        menu802.setComponent("/sampleSurvey/QCReturnDetails/index");
        menu802.setSort(802);
        menu802.setShowed(Boolean.TRUE);
        menu802.setCached(Boolean.TRUE);
        menu802.setExternal(Boolean.FALSE);
        menuList.add(menu802);

        Menu menu803 = new Menu();
        menu803.setPid(menu800.getId());
        menu803.setPermissionId(permission803.getId());
        menu803.setName("严重问题申诉记录");
        menu803.setTitle("严重问题申诉记录");
        menu803.setType("ROUTE");
        menu803.setPath("/sampleSurvey/seriousProblemRecord");
        menu803.setComponent("/sampleSurvey/seriousProblemRecord/index");
        menu803.setSort(803);
        menu803.setShowed(Boolean.TRUE);
        menu803.setCached(Boolean.TRUE);
        menu803.setExternal(Boolean.FALSE);
        menuList.add(menu803);

        Menu menu804 = new Menu();
        menu804.setPid(menu800.getId());
        menu804.setPermissionId(permission804.getId());
        menu804.setName("重采样品信息明细");
        menu804.setTitle("重采样品信息明细");
        menu804.setType("ROUTE");
        menu804.setPath("/sampleSurvey/sampleInformationDetails");
        menu804.setComponent("/sampleSurvey/sampleInformationDetails/index");
        menu804.setSort(804);
        menu804.setShowed(Boolean.TRUE);
        menu804.setCached(Boolean.TRUE);
        menu804.setExternal(Boolean.FALSE);
        menuList.add(menu804);

        Menu menu900 = new Menu();
        menu900.setPermissionId(permission900.getId());
        menu900.setName("采样质控管理");
        menu900.setTitle("采样质控管理");
        menu900.setType("ROUTE");
        menu900.setPath("/samplingQualityControl");
        menu900.setComponent("LAYOUT");
        menu900.setSort(900);
        menu900.setShowed(Boolean.TRUE);
        menu900.setCached(Boolean.TRUE);
        menu900.setExternal(Boolean.FALSE);
        menuList.add(menu900);

        Menu menu901 = new Menu();
        menu901.setPid(menu900.getId());
        menu901.setPermissionId(permission901.getId());
        menu901.setName("采样一级质控(县)任务");
        menu901.setTitle("采样一级质控(县)任务");
        menu901.setType("ROUTE");
        menu901.setPath("/samplingQualityControl/a");
        menu901.setComponent("/samplingQualityControl/a/index");
        menu901.setSort(901);
        menu901.setShowed(Boolean.TRUE);
        menu901.setCached(Boolean.TRUE);
        menu901.setExternal(Boolean.FALSE);
        menuList.add(menu901);

        Menu menu902 = new Menu();
        menu902.setPid(menu900.getId());
        menu902.setPermissionId(permission902.getId());
        menu902.setName("采样二级质控(市)任务");
        menu902.setTitle("采样二级质控(市)任务");
        menu902.setType("ROUTE");
        menu902.setPath("/samplingQualityControl/b");
        menu902.setComponent("/samplingQualityControl/b/index");
        menu902.setSort(902);
        menu902.setShowed(Boolean.TRUE);
        menu902.setCached(Boolean.TRUE);
        menu902.setExternal(Boolean.FALSE);
        menuList.add(menu902);

        Menu menu903 = new Menu();
        menu903.setPid(menu900.getId());
        menu903.setPermissionId(permission903.getId());
        menu903.setName("采样三级质控(省)任务");
        menu903.setTitle("采样三级质控(省)任务");
        menu903.setType("ROUTE");
        menu903.setPath("/samplingQualityControl/c");
        menu903.setComponent("/samplingQualityControl/c/index");
        menu903.setSort(903);
        menu903.setShowed(Boolean.TRUE);
        menu903.setCached(Boolean.TRUE);
        menu903.setExternal(Boolean.FALSE);
        menuList.add(menu903);

        Menu menu904 = new Menu();
        menu904.setPid(menu900.getId());
        menu904.setPermissionId(permission904.getId());
        menu904.setName("采样质控专家任务");
        menu904.setTitle("采样质控专家任务");
        menu904.setType("ROUTE");
        menu904.setPath("/samplingQualityControl/d");
        menu904.setComponent("/samplingQualityControl/d/index");
        menu904.setSort(904);
        menu904.setShowed(Boolean.TRUE);
        menu904.setCached(Boolean.TRUE);
        menu904.setExternal(Boolean.FALSE);
        menuList.add(menu904);

        Menu menu905 = new Menu();
        menu905.setPid(menu900.getId());
        menu905.setPermissionId(permission905.getId());
        menu905.setName("采样质控意见反馈");
        menu905.setTitle("采样质控意见反馈");
        menu905.setType("ROUTE");
        menu905.setPath("/samplingQualityControl/e");
        menu905.setComponent("/samplingQualityControl/e/index");
        menu905.setSort(905);
        menu905.setShowed(Boolean.TRUE);
        menu905.setCached(Boolean.TRUE);
        menu905.setExternal(Boolean.FALSE);
        menuList.add(menu905);

        Menu menu906 = new Menu();
        menu906.setPid(menu900.getId());
        menu906.setPermissionId(permission906.getId());
        menu906.setName("取样资料质控进度");
        menu906.setTitle("取样资料质控进度");
        menu906.setType("ROUTE");
        menu906.setPath("/samplingQualityControl/f");
        menu906.setComponent("/samplingQualityControl/f/index");
        menu906.setSort(906);
        menu906.setShowed(Boolean.TRUE);
        menu906.setCached(Boolean.TRUE);
        menu906.setExternal(Boolean.FALSE);
        menuList.add(menu906);

        Menu menu907 = new Menu();
        menu907.setPid(menu900.getId());
        menu907.setPermissionId(permission907.getId());
        menu907.setName("采样质控专家查询");
        menu907.setTitle("采样质控专家查询");
        menu907.setType("ROUTE");
        menu907.setPath("/samplingQualityControl/g");
        menu907.setComponent("/samplingQualityControl/g/index");
        menu907.setSort(907);
        menu907.setShowed(Boolean.TRUE);
        menu907.setCached(Boolean.TRUE);
        menu907.setExternal(Boolean.FALSE);
        menuList.add(menu907);

        Menu menu908 = new Menu();
        menu908.setPid(menu900.getId());
        menu908.setPermissionId(permission908.getId());
        menu908.setName("采样质控意见退回");
        menu908.setTitle("采样质控意见退回");
        menu908.setType("ROUTE");
        menu908.setPath("/samplingQualityControl/h");
        menu908.setComponent("/samplingQualityControl/h/index");
        menu908.setSort(908);
        menu908.setShowed(Boolean.TRUE);
        menu908.setCached(Boolean.TRUE);
        menu908.setExternal(Boolean.FALSE);
        menuList.add(menu908);

        Menu menu1000 = new Menu();
        menu1000.setPermissionId(permission1000.getId());
        menu1000.setName("样品检测管理");
        menu1000.setTitle("样品检测管理");
        menu1000.setType("ROUTE");
        menu1000.setPath("/sampleTesting");
        menu1000.setComponent("LAYOUT");
        menu1000.setSort(1000);
        menu1000.setShowed(Boolean.TRUE);
        menu1000.setCached(Boolean.TRUE);
        menu1000.setExternal(Boolean.FALSE);
        menuList.add(menu1000);

        Menu menu1001 = new Menu();
        menu1001.setPid(menu1000.getId());
        menu1001.setPermissionId(permission1001.getId());
        menu1001.setName("批次送检样交接单");
        menu1001.setTitle("批次送检样交接单");
        menu1001.setType("ROUTE");
        menu1001.setPath("/sampleTesting/a");
        menu1001.setComponent("/sampleTesting/a/index");
        menu1001.setSort(1001);
        menu1001.setShowed(Boolean.TRUE);
        menu1001.setCached(Boolean.TRUE);
        menu1001.setExternal(Boolean.FALSE);
        menuList.add(menu1001);

        Menu menu1002 = new Menu();
        menu1002.setPid(menu1000.getId());
        menu1002.setPermissionId(permission1002.getId());
        menu1002.setName("检测子样信息查询");
        menu1002.setTitle("检测子样信息查询");
        menu1002.setType("ROUTE");
        menu1002.setPath("/sampleTesting/b");
        menu1002.setComponent("/sampleTesting/b/index");
        menu1002.setSort(1002);
        menu1002.setShowed(Boolean.TRUE);
        menu1002.setCached(Boolean.TRUE);
        menu1002.setExternal(Boolean.FALSE);
        menuList.add(menu1002);

        Menu menu1003 = new Menu();
        menu1003.setPid(menu1000.getId());
        menu1003.setPermissionId(permission1003.getId());
        menu1003.setName("检测资质文件报送");
        menu1003.setTitle("检测资质文件报送");
        menu1003.setType("ROUTE");
        menu1003.setPath("/sampleTesting/TestQualificationDocuments");
        menu1003.setComponent("/sampleTesting/TestQualificationDocuments/index");
        menu1003.setSort(1003);
        menu1003.setShowed(Boolean.TRUE);
        menu1003.setCached(Boolean.TRUE);
        menu1003.setExternal(Boolean.FALSE);
        menuList.add(menu1003);

        Menu menu1004 = new Menu();
        menu1004.setPid(menu1000.getId());
        menu1004.setPermissionId(permission1004.getId());
        menu1004.setName("检测资质能力审核");
        menu1004.setTitle("检测资质能力审核");
        menu1004.setType("ROUTE");
        menu1004.setPath("/sampleTesting/QualificationCompetencyAudit");
        menu1004.setComponent("/sampleTesting/QualificationCompetencyAudit/index");
        menu1004.setSort(1004);
        menu1004.setShowed(Boolean.TRUE);
        menu1004.setCached(Boolean.TRUE);
        menu1004.setExternal(Boolean.FALSE);
        menuList.add(menu1004);

        Menu menu1005 = new Menu();
        menu1005.setPid(menu1000.getId());
        menu1005.setPermissionId(permission1005.getId());
        menu1005.setName("基本检测方法标准");
        menu1005.setTitle("基本检测方法标准");
        menu1005.setType("ROUTE");
        menu1005.setPath("/sampleTesting/e");
        menu1005.setComponent("/sampleTesting/e/index");
        menu1005.setSort(1005);
        menu1005.setShowed(Boolean.TRUE);
        menu1005.setCached(Boolean.TRUE);
        menu1005.setExternal(Boolean.FALSE);
        menuList.add(menu1005);

        Menu menu1006 = new Menu();
        menu1006.setPid(menu1000.getId());
        menu1006.setPermissionId(permission1006.getId());
        menu1006.setName("地方新增检测方法");
        menu1006.setTitle("地方新增检测方法");
        menu1006.setType("ROUTE");
        menu1006.setPath("/sampleTesting/f");
        menu1006.setComponent("/sampleTesting/f/index");
        menu1006.setSort(1006);
        menu1006.setShowed(Boolean.TRUE);
        menu1006.setCached(Boolean.TRUE);
        menu1006.setExternal(Boolean.FALSE);
        menuList.add(menu1006);

        Menu menu1007 = new Menu();
        menu1007.setPid(menu1000.getId());
        menu1007.setPermissionId(permission1007.getId());
        menu1007.setName("方法验证材料上传");
        menu1007.setTitle("方法验证材料上传");
        menu1007.setType("ROUTE");
        menu1007.setPath("/sampleTesting/g");
        menu1007.setComponent("/sampleTesting/g/index");
        menu1007.setSort(1007);
        menu1007.setShowed(Boolean.TRUE);
        menu1007.setCached(Boolean.TRUE);
        menu1007.setExternal(Boolean.FALSE);
        menuList.add(menu1007);

        Menu menu1008 = new Menu();
        menu1008.setPid(menu1000.getId());
        menu1008.setPermissionId(permission1008.getId());
        menu1008.setName("方法验证材料审核");
        menu1008.setTitle("方法验证材料审核");
        menu1008.setType("ROUTE");
        menu1008.setPath("/sampleTesting/h");
        menu1008.setComponent("/sampleTesting/h/index");
        menu1008.setSort(1008);
        menu1008.setShowed(Boolean.TRUE);
        menu1008.setCached(Boolean.TRUE);
        menu1008.setExternal(Boolean.FALSE);
        menuList.add(menu1008);

        Menu menu1009 = new Menu();
        menu1009.setPid(menu1000.getId());
        menu1009.setPermissionId(permission1009.getId());
        menu1009.setName("统一监控样品管理");
        menu1009.setTitle("统一监控样品管理");
        menu1009.setType("ROUTE");
        menu1009.setPath("/sampleTesting/i");
        menu1009.setComponent("/sampleTesting/i/index");
        menu1009.setSort(1009);
        menu1009.setShowed(Boolean.TRUE);
        menu1009.setCached(Boolean.TRUE);
        menu1009.setExternal(Boolean.FALSE);
        menuList.add(menu1009);

        Menu menu1010 = new Menu();
        menu1010.setPid(menu1000.getId());
        menu1010.setPermissionId(permission1010.getId());
        menu1010.setName("统一监控样品查询");
        menu1010.setTitle("统一监控样品查询");
        menu1010.setType("ROUTE");
        menu1010.setPath("/sampleTesting/j");
        menu1010.setComponent("/sampleTesting/j/index");
        menu1010.setSort(1010);
        menu1010.setShowed(Boolean.TRUE);
        menu1010.setCached(Boolean.TRUE);
        menu1010.setExternal(Boolean.FALSE);
        menuList.add(menu1010);

        Menu menu1011 = new Menu();
        menu1011.setPid(menu1000.getId());
        menu1011.setPermissionId(permission1011.getId());
        menu1011.setName("严重问题样品查询");
        menu1011.setTitle("严重问题样品查询");
        menu1011.setType("ROUTE");
        menu1011.setPath("/sampleTesting/k");
        menu1011.setComponent("/sampleTesting/k/index");
        menu1011.setSort(1011);
        menu1011.setShowed(Boolean.TRUE);
        menu1011.setCached(Boolean.TRUE);
        menu1011.setExternal(Boolean.FALSE);
        menuList.add(menu1011);

        Menu menu1012 = new Menu();
        menu1012.setPid(menu1000.getId());
        menu1012.setPermissionId(permission1012.getId());
        menu1012.setName("批次检测数据报送");
        menu1012.setTitle("批次检测数据报送");
        menu1012.setType("ROUTE");
        menu1012.setPath("/sampleTesting/l");
        menu1012.setComponent("/sampleTesting/l/index");
        menu1012.setSort(1012);
        menu1012.setShowed(Boolean.TRUE);
        menu1012.setCached(Boolean.TRUE);
        menu1012.setExternal(Boolean.FALSE);
        menuList.add(menu1012);

        Menu menu1013 = new Menu();
        menu1013.setPid(menu1000.getId());
        menu1013.setPermissionId(permission1013.getId());
        menu1013.setName("批次检测数据整改");
        menu1013.setTitle("批次检测数据整改");
        menu1013.setType("ROUTE");
        menu1013.setPath("/sampleTesting/m");
        menu1013.setComponent("/sampleTesting/m/index");
        menu1013.setSort(1013);
        menu1013.setShowed(Boolean.TRUE);
        menu1013.setCached(Boolean.TRUE);
        menu1013.setExternal(Boolean.FALSE);
        menuList.add(menu1013);

        Menu menu1014 = new Menu();
        menu1014.setPid(menu1000.getId());
        menu1014.setPermissionId(permission1014.getId());
        menu1014.setName("样品检测数据查询");
        menu1014.setTitle("样品检测数据查询");
        menu1014.setType("ROUTE");
        menu1014.setPath("/sampleTesting/n");
        menu1014.setComponent("/sampleTesting/n/index");
        menu1014.setSort(1014);
        menu1014.setShowed(Boolean.TRUE);
        menu1014.setCached(Boolean.TRUE);
        menu1014.setExternal(Boolean.FALSE);
        menuList.add(menu1014);

        Menu menu1100 = new Menu();
        menu1100.setPermissionId(permission1100.getId());
        menu1100.setName("数据质量审核");
        menu1100.setTitle("数据质量审核");
        menu1100.setType("ROUTE");
        menu1100.setPath("/dataQualityAudits");
        menu1100.setComponent("LAYOUT");
        menu1100.setSort(1100);
        menu1100.setShowed(Boolean.TRUE);
        menu1100.setCached(Boolean.TRUE);
        menu1100.setExternal(Boolean.FALSE);
        menuList.add(menu1100);

        Menu menu1101 = new Menu();
        menu1101.setPid(menu1100.getId());
        menu1101.setPermissionId(permission1101.getId());
        menu1101.setName("检测一级质控(县)任务");
        menu1101.setTitle("检测一级质控(县)任务");
        menu1101.setType("ROUTE");
        menu1101.setPath("/dataQualityAudits/a");
        menu1101.setComponent("/dataQualityAudits/a/index");
        menu1101.setSort(1101);
        menu1101.setShowed(Boolean.TRUE);
        menu1101.setCached(Boolean.TRUE);
        menu1101.setExternal(Boolean.FALSE);
        menuList.add(menu1101);

        Menu menu1102 = new Menu();
        menu1102.setPid(menu1100.getId());
        menu1102.setPermissionId(permission1102.getId());
        menu1102.setName("检测二级质控(市)任务");
        menu1102.setTitle("检测二级质控(市)任务");
        menu1102.setType("ROUTE");
        menu1102.setPath("/dataQualityAudits/b");
        menu1102.setComponent("/dataQualityAudits/b/index");
        menu1102.setSort(1102);
        menu1102.setShowed(Boolean.TRUE);
        menu1102.setCached(Boolean.TRUE);
        menu1102.setExternal(Boolean.FALSE);
        menuList.add(menu1102);

        Menu menu1103 = new Menu();
        menu1103.setPid(menu1100.getId());
        menu1103.setPermissionId(permission1103.getId());
        menu1103.setName("检测三级质控(省)任务");
        menu1103.setTitle("检测三级质控(省)任务");
        menu1103.setType("ROUTE");
        menu1103.setPath("/dataQualityAudits/c");
        menu1103.setComponent("/dataQualityAudits/c/index");
        menu1103.setSort(1103);
        menu1103.setShowed(Boolean.TRUE);
        menu1103.setCached(Boolean.TRUE);
        menu1103.setExternal(Boolean.FALSE);
        menuList.add(menu1103);

        Menu menu1104 = new Menu();
        menu1104.setPid(menu1100.getId());
        menu1104.setPermissionId(permission1104.getId());
        menu1104.setName("检测质控专家任务");
        menu1104.setTitle("检测质控专家任务");
        menu1104.setType("ROUTE");
        menu1104.setPath("/dataQualityAudits/d");
        menu1104.setComponent("/dataQualityAudits/d/index");
        menu1104.setSort(1104);
        menu1104.setShowed(Boolean.TRUE);
        menu1104.setCached(Boolean.TRUE);
        menu1104.setExternal(Boolean.FALSE);
        menuList.add(menu1104);

        Menu menu1105 = new Menu();
        menu1105.setPid(menu1100.getId());
        menu1105.setPermissionId(permission1105.getId());
        menu1105.setName("检测质控意见反馈");
        menu1105.setTitle("检测质控意见反馈");
        menu1105.setType("ROUTE");
        menu1105.setPath("/dataQualityAudits/e");
        menu1105.setComponent("/dataQualityAudits/e/index");
        menu1105.setSort(1105);
        menu1105.setShowed(Boolean.TRUE);
        menu1105.setCached(Boolean.TRUE);
        menu1105.setExternal(Boolean.FALSE);
        menuList.add(menu1105);

        Menu menu1106 = new Menu();
        menu1106.setPid(menu1100.getId());
        menu1106.setPermissionId(permission1106.getId());
        menu1106.setName("质控退改批次查询");
        menu1106.setTitle("质控退改批次查询");
        menu1106.setType("ROUTE");
        menu1106.setPath("/dataQualityAudits/f");
        menu1106.setComponent("/dataQualityAudits/f/index");
        menu1106.setSort(1106);
        menu1106.setShowed(Boolean.TRUE);
        menu1106.setCached(Boolean.TRUE);
        menu1106.setExternal(Boolean.FALSE);
        menuList.add(menu1106);

        Menu menu1200 = new Menu();
        menu1200.setPermissionId(permission1200.getId());
        menu1200.setName("数据统计分析");
        menu1200.setTitle("数据统计分析");
        menu1200.setType("ROUTE");
        menu1200.setPath("/statisticalAnalysisOfData");
        menu1200.setComponent("LAYOUT");
        menu1200.setSort(1200);
        menu1200.setShowed(Boolean.TRUE);
        menu1200.setCached(Boolean.TRUE);
        menu1200.setExternal(Boolean.FALSE);
        menuList.add(menu1200);

        Menu menu1300 = new Menu();
        menu1300.setPermissionId(permission1300.getId());
        menu1300.setName("数据对标评价");
        menu1300.setTitle("数据对标评价");
        menu1300.setType("ROUTE");
        menu1300.setPath("/dataBenchmarkingEvaluation");
        menu1300.setComponent("LAYOUT");
        menu1300.setSort(1300);
        menu1300.setShowed(Boolean.TRUE);
        menu1300.setCached(Boolean.TRUE);
        menu1300.setExternal(Boolean.FALSE);
        menuList.add(menu1300);

        Menu menu1400 = new Menu();
        menu1400.setPermissionId(permission1400.getId());
        menu1400.setName("工作文件管理");
        menu1400.setTitle("工作文件管理");
        menu1400.setType("ROUTE");
        menu1400.setPath("/workFileManagement");
        menu1400.setComponent("LAYOUT");
        menu1400.setSort(1400);
        menu1400.setShowed(Boolean.TRUE);
        menu1400.setCached(Boolean.TRUE);
        menu1400.setExternal(Boolean.FALSE);
        menuList.add(menu1400);

        Menu menu1401 = new Menu();
        menu1401.setPid(menu1400.getId());
        menu1401.setPermissionId(permission1401.getId());
        menu1401.setName("工作文件下载");
        menu1401.setTitle("工作文件下载");
        menu1401.setType("ROUTE");
        menu1401.setPath("/workFileManagement/a");
        menu1401.setComponent("LAYOUT");
        menu1401.setSort(1401);
        menu1401.setShowed(Boolean.TRUE);
        menu1401.setCached(Boolean.TRUE);
        menu1401.setExternal(Boolean.FALSE);
        menuList.add(menu1401);

        Menu menu1500 = new Menu();
        menu1500.setPermissionId(permission1500.getId());
        menu1500.setName("后台数据管理");
        menu1500.setTitle("后台数据管理");
        menu1500.setType("ROUTE");
        menu1500.setPath("/system");
        menu1500.setComponent("LAYOUT");
        menu1500.setSort(1500);
        menu1500.setShowed(Boolean.TRUE);
        menu1500.setCached(Boolean.TRUE);
        menu1500.setExternal(Boolean.FALSE);
        menuList.add(menu1500);

        Menu menu1501 = new Menu();
        menu1501.setPid(menu1500.getId());
        menu1501.setPermissionId(permission1501.getId());
        menu1501.setName("单位管理");
        menu1501.setTitle("单位管理");
        menu1501.setType("ROUTE");
        menu1501.setPath("/system/organization");
        menu1501.setComponent("/system/organization/index");
        menu1501.setSort(1501);
        menu1501.setShowed(Boolean.TRUE);
        menu1501.setCached(Boolean.TRUE);
        menu1501.setExternal(Boolean.FALSE);
        menuList.add(menu1501);

        Menu menu1502 = new Menu();
        menu1502.setPid(menu1500.getId());
        menu1502.setPermissionId(permission1502.getId());
        menu1502.setName("用户管理");
        menu1502.setTitle("用户管理");
        menu1502.setType("ROUTE");
        menu1502.setPath("/system/user");
        menu1502.setComponent("/system/user/index");
        menu1502.setSort(1502);
        menu1502.setShowed(Boolean.TRUE);
        menu1502.setCached(Boolean.TRUE);
        menu1502.setExternal(Boolean.FALSE);
        menuList.add(menu1502);

        Menu menu1503 = new Menu();
        menu1503.setPid(menu1500.getId());
        menu1503.setPermissionId(permission1503.getId());
        menu1503.setName("角色管理");
        menu1503.setTitle("角色管理");
        menu1503.setType("ROUTE");
        menu1503.setPath("/system/role");
        menu1503.setComponent("/system/role/index");
        menu1503.setSort(1503);
        menu1503.setShowed(Boolean.TRUE);
        menu1503.setCached(Boolean.TRUE);
        menu1503.setExternal(Boolean.FALSE);
        menuList.add(menu1503);

        Menu menu1504 = new Menu();
        menu1504.setPid(menu1500.getId());
        menu1504.setPermissionId(permission1504.getId());
        menu1504.setName("权限管理");
        menu1504.setTitle("权限管理");
        menu1504.setType("ROUTE");
        menu1504.setPath("/system/permission");
        menu1504.setComponent("/system/permission/index");
        menu1504.setSort(1504);
        menu1504.setShowed(Boolean.TRUE);
        menu1504.setCached(Boolean.TRUE);
        menu1504.setExternal(Boolean.FALSE);
        menuList.add(menu1504);

        Menu menu1505 = new Menu();
        menu1505.setPid(menu1500.getId());
        menu1505.setPermissionId(permission1505.getId());
        menu1505.setName("菜单管理");
        menu1505.setTitle("菜单管理");
        menu1505.setType("ROUTE");
        menu1505.setPath("/system/menu");
        menu1505.setComponent("/system/menu/index");
        menu1505.setSort(1505);
        menu1505.setShowed(Boolean.TRUE);
        menu1505.setCached(Boolean.TRUE);
        menu1505.setExternal(Boolean.FALSE);
        menuList.add(menu1505);

        Menu menu1506 = new Menu();
        menu1506.setPid(menu1500.getId());
        menu1506.setPermissionId(permission1506.getId());
        menu1506.setName("字典管理");
        menu1506.setTitle("字典管理");
        menu1506.setType("ROUTE");
        menu1506.setPath("/system/dict");
        menu1506.setComponent("/system/dict/index");
        menu1506.setSort(1506);
        menu1506.setShowed(Boolean.TRUE);
        menu1506.setCached(Boolean.TRUE);
        menu1506.setExternal(Boolean.FALSE);
        menuList.add(menu1506);
        menuRepository.saveAll(menuList);
    }

    /**
     * 保存区域数据
     */
    private void saveArea(Dict parent, Area child, List<Dict> dictList) {
        Dict dict = new Dict();
        dict.setValue(Integer.valueOf(child.adCode));
        dict.setName(child.name);
        dict.setType("area");
        dict.setSort(areaSort++);
        dict.setPid(parent != null ? parent.getValue() : null);
        dictList.add(dict);
        // 保存下级区域
        for (Area c : child.children) {
            saveArea(dict, c, dictList);
        }
    }

    private static class Area {
        /**
         * 名称
         */
        String name;
        /**
         * 编码
         */
        String adCode;
        /**
         * zip编码
         */
        String cityCode;
        /**
         * 上级编码
         */
        String parentAdCode;
        /**
         * 下级区域
         */
        List<Area> children = new ArrayList<>();
    }

}
