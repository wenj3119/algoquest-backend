# ==============================
# 1. 构建阶段
# ==============================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# 先复制 Gradle 相关文件，利用缓存
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties ./

# 给 gradlew 执行权限
RUN chmod +x ./gradlew

# 先拉依赖，尽量利用 Docker 缓存
RUN ./gradlew dependencies --no-daemon || true

# 再复制源码
COPY src src

# 构建 jar（跳过测试可加快构建）
RUN ./gradlew clean bootJar -x test --no-daemon

# ==============================
# 2. 运行阶段
# ==============================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""

# 从构建阶段复制 jar
COPY --from=builder /app/build/libs/algoquest-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java \
  -XX:+UseContainerSupport \
  -XX:InitialRAMPercentage=50.0 \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -Djava.security.egd=file:/dev/./urandom \
  -Dfile.encoding=UTF-8 \
  $JAVA_OPTS \
  -jar /app/app.jar"]