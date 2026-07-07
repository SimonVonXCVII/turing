/**
 * todo 尝试 nativeCompile、bootBuildImage
 */
plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management)
//    id("org.graalvm.buildtools.native") version libs.versions.org.graalvm.buildtools.native
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
//    id("io.spring.javaformat") version libs.versions.io.spring.javaformat
//    id("checkstyle")
    alias(libs.plugins.kotlin.jpa) apply false
}

allprojects {
    group = "com.simonvonxcvii.turing"
    version = "0.0.1-SNAPSHOT"
}

val javaLanguage = libs.versions.java.language.get().toInt()
val springBootVersion = libs.versions.org.springframework.boot.get()
val springCloudVersion = libs.versions.org.springframework.cloud.get()
val springBootAdminVersion = libs.versions.de.codecentric.spring.boot.admin.dependencies.get()

subprojects {
    apply {
//        plugin("java")
//        plugin("java-library")
//        plugin("idea")
//        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
//        plugin("org.graalvm.buildtools.native")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
//        plugin("io.spring.javaformat")
//        plugin("checkstyle")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaLanguage)
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(javaLanguage)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

//    checkstyle {
//        toolVersion = "9.3"
//    }

//springJavaFormat { todo
//    checkstyle {
//        applyDefaultConfig()
//    }
//}

    dependencies {
//        implementation("org.springframework.boot:spring-boot-starter")
//        testImplementation("org.springframework.boot:spring-boot-starter-test")
//        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//        checkstyle("io.spring.javaformat:spring-javaformat-checkstyle")
    }

    dependencyManagement {
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
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
            mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
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