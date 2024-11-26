plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.3"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
}

group = "com.simonvonxcvii.turing"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()

//    maven {
//        url = uri("https://mvn.cloud.alipay.com/nexus/content/repositories/open/")
//    }
//    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
//    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/google") }

    mavenCentral()
}

extra["therapiRuntimeJavadocVersion"] = "0.15.0"
extra["googleGuavaVersion"] = "33.3.1-jre"
extra["googleZxingVersion"] = "3.5.3"
extra["apachePoiVersion"] = "5.3.0"
extra["apacheVelocityVersion"] = "2.4.1"
extra["springdocVersion"] = "2.7.0"
extra["flyingSaucerPdfVersion"] = "9.10.2"

dependencies {
    annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:${property("therapiRuntimeJavadocVersion")}")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // lombok
    compileOnly("org.projectlombok:lombok")

    // therapi
    // https://central.sonatype.com/artifact/com.github.therapi/therapi-runtime-javadoc
    implementation("com.github.therapi:therapi-runtime-javadoc:${property("therapiRuntimeJavadocVersion")}")
    // google
    // https://central.sonatype.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:${property("googleGuavaVersion")}")
    // https://central.sonatype.com/artifact/com.google.zxing/javase
    implementation("com.google.zxing:javase:${property("googleZxingVersion")}")
    // apache
    // https://central.sonatype.com/artifact/org.apache.poi/poi
    implementation("org.apache.poi:poi:${property("apachePoiVersion")}")
    // https://central.sonatype.com/artifact/org.apache.velocity/velocity-engine-core
    implementation("org.apache.velocity:velocity-engine-core:${property("apacheVelocityVersion")}")
    // kotlin 非必需
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // springdoc
    // https://central.sonatype.com/namespace/org.springdoc
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("springdocVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${property("springdocVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocVersion")}")
    // springframework
//    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // PDF TODO 尝试换成 apache 的，或者试试 itext
    // https://central.sonatype.com/artifact/org.xhtmlrenderer/flying-saucer-pdf
    implementation("org.xhtmlrenderer:flying-saucer-pdf:${property("flyingSaucerPdfVersion")}")
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

tasks.withType<Test> {
    useJUnitPlatform()
}
