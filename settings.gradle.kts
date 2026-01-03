rootProject.name = "turing"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // 强制所有项目使用这里的仓库配置，如果项目中单独定义了 repositories 则报错
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

include(
    "turing-resource-server",
    "backend-for-spa-client",
    "default-authorizationserver",
    "demo-authorizationserver",
    "demo-client",
    "messages-resource",
    "users-resource",
    "x509-certificate-generator"
)