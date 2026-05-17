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

val javaLanguage = libs.versions.java.language.get().toInt()
val springBootAdmin = libs.de.codecentric.spring.boot.admin.dependencies.get().toString()

subprojects {
    apply {
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
            mavenBom(springBootAdmin)
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