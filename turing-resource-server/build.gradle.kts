/**
 * 设置此项目的描述。
 */
description = "turing resource server"

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
//    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")
//    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
//    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    // 用于保存 session，作用在于即使后端重启，前端依然可以在不重新登录的情况下正常刷新网页 todo master 分支中有使用它
//    implementation("org.springframework.session:spring-session-data-redis")
    // todo 使用它
//    developmentOnly("org.springframework.boot:spring-boot-devtools")
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
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    // todo 使用它
//    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
