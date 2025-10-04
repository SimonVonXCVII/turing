/**
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
}

/**
 * 设置该项目的组。
 */
group = "com.simonvonxcvii.turing"
/**
 * 设置此项目的版本。
 */
version = "0.0.1-SNAPSHOT"
/**
 * 设置此项目的描述。
 */
description = "turing 项目后端脚手架"

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
        languageVersion = JavaLanguageVersion.of(libs.versions.java.language.get())
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
     * 配置阿里依赖仓库
     */
//    maven {
//        url = uri("https://mvn.cloud.alipay.com/nexus/content/repositories/open/")
//    }
//    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
//    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/google") }

    /**
     * 添加一个存储库，该存储库在 Maven 中央存储库中查找依赖项。 用于访问此存储库的 URL 是“https://repo.maven.apache.org/maven2/”。
     * 存储库的名称是“MavenRepo”。
     */
    mavenCentral()
}

/**
 * 配置此项目的依赖项。
 * 针对该项目的 DependencyHandlerScope 执行给定的配置块。
 */
dependencies {
    annotationProcessor(libs.com.github.therapi.therapi.runtime.javadoc.scribe)
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // lombok
    compileOnly("org.projectlombok:lombok")

    // https://central.sonatype.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // therapi
    // https://central.sonatype.com/artifact/com.github.therapi/therapi-runtime-javadoc
    implementation(libs.com.github.therapi.therapi.runtime.javadoc)
    // google
    // https://central.sonatype.com/artifact/com.google.guava/guava
//    implementation(libs.com.google.guava.guava)
    // https://central.sonatype.com/artifact/com.google.zxing/javase
//    implementation(libs.com.google.zxing.javase)
    // thumbnailator
    // https://central.sonatype.com/artifact/net.coobird/thumbnailator
//    implementation(libs.net.coobird.thumbnailator)
    // apache
    // https://central.sonatype.com/artifact/org.apache.poi/poi
    implementation(libs.org.apache.poi.poi)
    // https://central.sonatype.com/artifact/org.apache.velocity/velocity-engine-core
//    implementation(libs.org.apache.velocity.velocity.engine.core)
    // kotlin 非必需
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // springdoc
    // https://central.sonatype.com/namespace/org.springdoc
    implementation(libs.org.springdoc.springdoc.openapi.starter.common)
    implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.api)
    implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)
    // springframework
//    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // TODO 是否可以用 postgresql 替代 elasticsearch
//    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // 用于保存 session，作用在于即使后端重启，前端依然可以在不重新登录的情况下正常刷新网页
    implementation("org.springframework.session:spring-session-data-redis")
    // PDF TODO 尝试换成 apache 的，或者试试 itext
    // https://central.sonatype.com/artifact/org.xhtmlrenderer/flying-saucer-pdf
//    implementation(libs.org.xhtmlrenderer.flying.saucer.pdf)
//    implementation("org.apache.pdfbox:pdfbox:3.0.1")

    // postgresql
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
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
