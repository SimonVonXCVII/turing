# 初始化一个新的构建阶段并为后续指令设置基础镜像。
#FROM ghcr.io/graalvm/graalvm-ce:latest
#FROM ghcr.io/graalvm/graalvm-ce:22.3.3
#FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22.3.3
#FROM ghcr.io/graalvm/graalvm-community:17.0.8
#FROM eclipse-temurin:22
FROM amazoncorretto:24.0.2-al2023-headless

# 将元数据添加到图像。 LABEL 是键值对。
LABEL MAINTAINER="g1672943850@gmail.com"

# 从路径复制新文件或目录并将它们添加到容器的文件系统。
COPY ./jar/turing-0.0.1-SNAPSHOT.jar ./

# 为正在执行的容器提供默认值。如果未指定可执行文件，则还必须指定入口点。Dockerfile 中只能有一个 CMD 指令。
CMD java -jar turing-0.0.1-SNAPSHOT.jar
