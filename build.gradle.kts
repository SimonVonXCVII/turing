/**
 * todo 尝试 nativeCompile、bootBuildImage
 */
plugins {
    java
    id("org.springframework.boot") version libs.versions.org.springframework.boot
    id("io.spring.dependency-management") version libs.versions.io.spring.dependency.management
//    id("org.graalvm.buildtools.native") version libs.versions.org.graalvm.buildtools.native
    id("org.jetbrains.kotlin.jvm") version libs.versions.org.jetbrains.kotlin
    id("org.jetbrains.kotlin.plugin.spring") version libs.versions.org.jetbrains.kotlin
//    id("io.spring.javaformat") version libs.versions.io.spring.javaformat
//    id("checkstyle")
    kotlin("plugin.jpa") version libs.versions.org.jetbrains.kotlin
}

allprojects {
    group = "com.simonvonxcvii.turing"
    version = "0.0.1-SNAPSHOT"
}

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
        plugin("java-library")
        plugin("idea")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
//        plugin("org.graalvm.buildtools.native")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
//        plugin("io.spring.javaformat")
//        plugin("checkstyle")
    }

    /**
     * 配置 org.gradle.api.plugins.JavaPluginExtension 扩展。
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
            languageVersion = JavaLanguageVersion.of(26)
//            languageVersion = JavaLanguageVersion.of(libs.versions.java.language.get()) todo
        }
    }

    /**
     * 配置 org.gradle.api.file.SourceDirectorySet 扩展。
     */
    kotlin {
        /**
         * 配置项目中 Kotlin JVM 和 Java 任务的 Java 工具链。
         */
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(26)
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
//    checkstyle {
//        toolVersion = "9.3"
//    }

//springJavaFormat { todo
//    checkstyle {
//        applyDefaultConfig()
//    }
//}

    /**
     * 配置该项目的子项目都有的依赖
     */
    dependencies {
//        implementation("org.springframework.boot:spring-boot-starter")
//        testImplementation("org.springframework.boot:spring-boot-starter-test")
//        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//        checkstyle("io.spring.javaformat:spring-javaformat-checkstyle")
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

        imports {
            mavenBom("de.codecentric:spring-boot-admin-dependencies:4.0.4")
//            mavenBom(libs.de.codecentric.spring.boot.admin.dependencies.get().toString())
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
