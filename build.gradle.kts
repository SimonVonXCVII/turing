/**
 * todo 尝试 nativeCompile、bootBuildImage
 * 配置该项目的插件依赖项。
 */
plugins {
    /**
     * 由 org.gradle.api.plugins.JavaPlugin 实现的内置 Gradle 插件。
     */
    java
    /**
     * 添加对具有给定 id 的插件的依赖关系。
     */
    alias(libs.plugins.org.springframework.boot)
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.org.graalvm.buildtools.native)
    /**
     * 应用给定的 Kotlin 插件模块。
     */
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)

    id("io.spring.javaformat") version "0.0.47"
    id("checkstyle")
}

/**
 * 配置此项目及其每个子项目。
 * 此方法针对该项目及其每个子项目执行给定的操作。
 */
allprojects {
    /**
     * 设置该项目的组。
     */
    group = "com.simonvonxcvii.turing"
    /**
     * 设置此项目的版本。
     */
    version = "0.0.1-SNAPSHOT"

    /**
     * 配置该项目的存储库。
     * 针对该项目的 RepositoryHandler 执行给定的配置块。
     * 注：依赖下载速度取决于 repositories 内存储库的先后顺序
     */
    repositories {
        /**
         * 添加一个存储库，该存储库在本地 Maven 缓存中查找依赖项。 存储库的名称是“MavenLocal”。
         * 例子：
         *    repositories {
         *        mavenLocal()
         *    }
         *
         * 存储库的位置确定如下（按优先顺序）：
         * 系统属性“maven.repo.local”的值（如果已设置）；
         * 如果该文件存在并且设置了 ~/.m2/settings.xml 的元素 <localRepository> 的值；
         * 如果此文件存在并且设置了元素，则 $M2_HOME/conf/settings.xml 的元素 <localRepository> 的值（其中 $M2_HOME 是具有该名称的环境变量的值）；
         * 路径~/.m2/repository。
         */
        mavenLocal()
        /**
         * 添加一个存储库，该存储库在 Maven 中央存储库中查找依赖项。 用于访问此存储库的 URL 是“https://repo.maven.apache.org/maven2/”。
         * 存储库的名称是“MavenRepo”。
         */
        mavenCentral()
    }
}

/**
 * 配置该项目的子项目
 * 此方法针对该项目的子项目执行给定的操作。
 */
subprojects {
    /**
     * 应用零个或多个插件或脚本。
     * 给定的闭包用于配置 ObjectConfigurationAction，它“构建”插件应用程序。
     * 此方法与 apply(Map) 的不同之处在于，它允许多次调用配置操作的方法。
     */
    apply {
        /**
         * 添加一个插件用于配置目标对象。 您可以多次调用此方法，以使用多个插件。 脚本和插件按照添加顺序应用。
         */
        plugin("java")
        plugin("idea")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("java-library")
        plugin("org.graalvm.buildtools.native")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("io.spring.javaformat")
        plugin("checkstyle")
    }

    /**
     * 配置 java 扩展。
     */
    java {
        /**
         * 配置需要工具链中的工具的任务的项目范围工具链要求（例如 org.gradle.api.tasks.compile.JavaCompile）。
         * 配置工具链不能与此扩展上的 sourceCompatibility 或 targetCompatibility 一起使用。这两个值都将来自工具链。
         */
        toolchain {
            /**
             * 工具链需要支持的 Java 语言的确切版本。
             */
            languageVersion = JavaLanguageVersion.of(25)
//            languageVersion = JavaLanguageVersion.of(libs.versions.java.language.get()) todo
        }
    }

    /**
     * 返回该项目的配置。
     */
    configurations {
        /**
         * 提供现有的 compileOnly 元素。
         */
        compileOnly {
            /**
             * 将给定配置添加到此配置所扩展的配置集中。
             */
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    /**
     * 配置 org.gradle.api.plugins.quality.CheckstyleExtension 扩展。
     */
    checkstyle {
        toolVersion = "9.3"
    }

//springJavaFormat { todo
//    checkstyle {
//        applyDefaultConfig()
//    }
//}

    /**
     * 配置该项目的子项目都有的依赖
     */
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    /**
     * spring 以外依赖的统一管理
     */
    dependencyManagement {
        /**
         * 使用给定操作配置托管依赖项。
         */
//        dependencies { todo
//            /**
//             * 为给定 id 标识的依赖项配置依赖项管理。 id 是一个格式为 group:name:version 的字符串。
//             */
//            dependency("com.simonvonxcvii:turing-resource-server:${property("projectVersion")}")
//
//            dependency("com.github.therapi:therapi-runtime-javadoc:${property("therapiRuntimeJavadocVersion")}")
//            dependency("com.github.therapi:therapi-runtime-javadoc-scribe:${property("therapiRuntimeJavadocVersion")}")
//            dependency("org.springdoc:springdoc-openapi-javadoc:${property("springdocVersion")}")
//            dependency("org.springdoc:springdoc-openapi-ui:${property("springdocVersion")}")
//        }
        /**
         * 使用给定操作配置依赖项管理导入。
         */
        imports {
            /**
             * 导入具有给定坐标（格式为 group:name:version）的 Maven bom。
             */
            mavenBom("de.codecentric:spring-boot-admin-dependencies:3.5.6")
//            mavenBom(libs.de.codecentric.spring.boot.admin.dependencies) todo
        }
    }

    /**
     * tasks: 返回该项目的任务。
     * withType: 返回一个集合，其中包含此集合中给定类型的对象。 相当于调用 withType(type).all(configureAction)。
     * Test: 执行 JUnit（3.8.x、4.x 或 5.x）或 TestNG 测试。 测试始终在（一个或多个）单独的 JVM 中运行。
     */
    tasks.withType<Test> {
        /**
         * 指定应使用 JUnit Platform 来发现和执行测试。
         * 如果您的测试使用 JUnit Jupiter/JUnit5，请使用此选项。
         * JUnit Platform 支持多个测试引擎，允许在其之上构建其他测试框架。 即使您不直接使用 JUnit，您也可能需要使用此选项。
         */
        useJUnitPlatform()
    }
}
