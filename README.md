# Turing

Turing 是一个基于 Spring Boot 4、Spring Security OAuth2、JPA、Redis、PostgreSQL 和 Angular
的多模块示例/业务后端项目。当前仓库同时包含核心业务资源服务、OAuth2 授权服务器示例、SPA Backend-for-Frontend
示例、资源服务示例、证书工具和 Docker 编排配置。

## 模块概览

| 模块                          | 说明                                                                 | 默认端口 |
|-------------------------------|----------------------------------------------------------------------|----------|
| `turing-common`               | 公共实体、DTO、JPA Repository、审计、返回结构和工具类                | -        |
| `turing-resource-server`      | 核心业务资源服务，包含用户、角色、菜单、权限、组织、字典、文件等 API | `8080`   |
| `default-authorizationserver` | 最小化 Spring Authorization Server 示例                              | `9000`   |
| `demo-authorizationserver`    | 带页面、JDBC、社交登录、mTLS 配置的授权服务器示例                    | `9443`   |
| `demo-client`                 | OAuth2/OIDC 客户端示例                                               | `8080`   |
| `backend-for-spa-client`      | SPA 的 BFF/Gateway 示例，向授权服务器和资源服务转发请求              | `8080`   |
| `messages-resource`           | 消息资源服务示例，启用 mTLS                                          | `8443`   |
| `users-resource`              | 用户资源服务示例                                                     | `8091`   |
| `x509-certificate-generator`  | X.509 证书生成工具                                                   | -        |
| `spa-client`                  | Angular 18 SPA 示例客户端                                            | `4200`   |

注意：多个示例模块默认都使用 `8080`，同时运行时需要改端口或只启动当前需要的模块。

## 技术栈

- Java 25
- Kotlin 2.3.x
- Spring Boot 4.1.x
- Spring Security / OAuth2 Authorization Server / OAuth2 Resource Server
- Spring Data JPA、Hibernate
- PostgreSQL、Redis
- Gradle Kotlin DSL
- Angular 18
- Docker Compose、Nginx、Portainer

## 环境准备

本地开发建议准备：

- JDK 25
- Docker Desktop 或兼容 Docker Engine
- Node.js 和 npm，用于运行 `spa-client`
- 使用仓库自带 Gradle Wrapper：`./gradlew`

创建本地 `.env`：

```properties
HOST=127.0.0.1
POSTGRES_PASSWORD=password
REDIS_PASSWORD=password
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
```

当前 `docker/postgresql/sh/init-user-db.sh` 会创建数据库用户 `turing`，密码硬编码为 `password`。如果修改
`POSTGRES_PASSWORD`，需要同步修改初始化脚本或应用数据源配置。

## 快速启动

启动基础设施：

```bash
docker compose up -d turing-db turing-redis
```

启动核心资源服务：

```bash
./gradlew :turing-resource-server:bootRun
```

启动默认授权服务器示例：

```bash
./gradlew :default-authorizationserver:bootRun
```

启动 Angular SPA：

```bash
cd spa-client
npm install
npm start
```

访问地址：

- SPA: `http://127.0.0.1:4200`
- 核心 API: `http://127.0.0.1:8080`
- Swagger UI: `http://127.0.0.1:8080/swagger-ui/index.html`
- Actuator health: `http://127.0.0.1:8080/actuator/health`
- 默认授权服务器: `http://127.0.0.1:9000`

## Docker 运行

后端镜像目前使用 `docker/turing-backend/Dockerfile`，它会复制已经构建好的 jar：

```bash
./gradlew :turing-resource-server:bootJar
mkdir -p docker/turing-backend/jar
cp turing-resource-server/build/libs/turing-resource-server-0.0.1-SNAPSHOT.jar docker/turing-backend/jar/
docker compose up -d --build
```

Nginx 静态资源目录为 `docker/nginx/html`。如果要部署新的前端构建产物，需要先构建前端并同步到该目录。

## 测试

运行全部测试：

```bash
./gradlew test
```

运行核心资源服务测试：

```bash
./gradlew :turing-resource-server:test
```

当前已知状态：

- `default-authorizationserver` 的部分 OAuth2 流程测试会跟随 redirect 到 `127.0.0.1:8080` 或 `127.0.0.1`，如果没有对应客户端服务会失败。
- `turing-resource-server` 的 `contextLoads` 目前默认使用 `dev` profile，并尝试连接真实 PostgreSQL。建议后续改为 `test`
  profile 或接入 Testcontainers。

## 配置说明

核心资源服务配置入口：

- `turing-resource-server/src/main/resources/application.yaml`
- `turing-resource-server/src/main/resources/application-dev.yaml`
- `turing-resource-server/src/main/resources/application-test.yaml`
- `turing-resource-server/src/main/resources/application-prod.yaml`

当前默认激活 `dev` profile。生产环境建议通过环境变量或部署平台显式指定：

```bash
SPRING_PROFILES_ACTIVE=prod
```

## 安全注意事项

当前配置更偏开发体验，生产部署前至少需要处理：

- 收敛 `custom.security.whitelist`，避免无认证开放敏感接口。
- 限制 Actuator 暴露范围，不要在公网暴露全部端点。
- 关闭生产环境 Spring Security DEBUG/TRACE 日志。
- 移出仓库内的私钥、测试 keystore 和真实密钥，改用环境变量、Docker secret 或挂载文件。
- 根据业务权限为用户、角色、菜单、权限、组织等管理 API 补充方法级授权。
- 收紧文件上传大小、文件类型校验和下载授权。

## 常用命令

```bash
# 编译全部后端模块
./gradlew build

# 只编译核心资源服务
./gradlew :turing-resource-server:build

# 启动核心资源服务
./gradlew :turing-resource-server:bootRun

# 启动默认授权服务器
./gradlew :default-authorizationserver:bootRun

# 启动 Docker 编排
docker compose up -d

# 停止 Docker 编排
docker compose down

# 查看 Docker 服务状态
docker compose ps
```

## 目录结构

```text
.
├── backend-for-spa-client
├── default-authorizationserver
├── demo-authorizationserver
├── demo-client
├── docker
├── messages-resource
├── spa-client
├── turing-common
├── turing-resource-server
├── users-resource
├── x509-certificate-generator
├── compose.yaml
├── build.gradle.kts
└── settings.gradle.kts
```

## 后续优化建议

- 为测试环境接入 Testcontainers，让 `./gradlew test` 不依赖本机数据库和外部服务。
- 将 Docker 镜像构建改为 `bootBuildImage`、Jib 或 multi-stage Dockerfile，减少手动复制 jar。
- 增加 `.env.example`，并将 `.env`、Docker volume、前端构建产物、jar 产物加入 `.gitignore`。
- 启用 Gradle build cache、parallel build、格式化和静态检查。
- 将文件存储根目录改为配置项或对象存储，并改造为流式 hash/写入。
