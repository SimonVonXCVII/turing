# turing-backend 项目后端脚手架-单体应用

## 使用 Docker 进行容器化部署，Redis 7.0.12、Elastic stack 8.7.1、Nginx 1.25.1

## JDK 17 + Kotlin 1.9 + Gradle 8 + Spring Boot 3 + Spring Security + Mybatis Plus + Kingbase + SpringDoc + Thymeleaf + JWT + Lombok

## 开发规范

1. swagger ui 路径：http://localhost:9699/swagger-ui/index.html

2. 如要获取字符串 "UTF-8" 推荐使用 StandardCharsets.UTF_8.name()

3. 自己写的 SQL 请一定规范书写，SELECT 中禁止用 * 替代所有，~~有启用 MP 非法 SQL 拦截器，有不规范的 SQL 将收到警告~~

4. 实体类和数据库中的字段如果涉及到状态取名时，建议使用 state

5. 在有其他办法的情况下，禁止使用以下三个属性，因为这在某些时候会导致一些未知的严重问题：
    ````yaml
    # 是否允许 Bean 定义覆盖。默认为 false
    allow-bean-definition-overriding: true
    # 是否允许循环依赖。默认为 false
    allow-circular-references: true
    # 设置是否应延迟初始化 bean。默认为 false
    lazy-initialization: true
    ````

6. 在注入依赖 Bean PasswordEncoder 时，请使用以下这种方式之一：
    ````java
    public class A {
        // 构造器注入（推荐）
        private final PasswordEncoder passwordEncoder;
        public A(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        // setter 注入
        private PasswordEncoder passwordEncoder;
        @Autowired
        public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        // 字段注入（不推荐）
        @Autowired
        private PasswordEncoder passwordEncoder;
    }
    ````

7. 用户登录后，token 的有效期为一周，后会提示用户“凭据不够可信”

8. 个人写的文件、类、方法、字段等该加的地方请加上注释，做什么用的，或者有什么效果等

9. 接口方法请加上注释

10. 继承自父类 BaseEntity 的数据库实体类无需再在该类中编写父类 BaseEntity 中的任一字段

11. 一个 controller 对应一个 service，不建议注入多个 service

12. service impl 层注入依赖时，推荐优先选择构造器方式注入，并优先选择 mapper 注入，并根据实际需要在构造器上方添加 @Lazy 注解

13. 建议在 mapper 接口上使用注解 @Mapper

14. 任何实体类（entity、dto、cmd、query、param 等）中，建议使用 /** */ JavaDoc 类型注释，方便在其他地方查看字段含义

15. controller 层不应编写业务逻辑代码，业务逻辑代码都应在 service impl 中编写

16. 建议代码一行的长度最长到达右边 idea 分割线附近，满屏时不出现滚动条

17. API 接口的参数如果为不必填，请在入参前面添加注解 @RequestParam(required = false) 或者 @Parameter，这样在 swagger ui
    界面中便显示为非必填项

18. 请使用 jakarta 而不是 javax

19. 请重视警告

20. 获取 IP 地址的工具类：org.apache.tomcat.util.net.IPv6Utils#canonize(String)，参数为 request#getRemoteAddr() 或者
    request#getRemoteHost()