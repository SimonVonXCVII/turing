dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")
//    implementation(project(":spring-security-oauth2-authorization-server"))

    runtimeOnly("com.h2database:h2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.htmlunit:htmlunit")
}