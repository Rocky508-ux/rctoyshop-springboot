# --- 採用「多階段構建 (Multi-Stage Build)」---
# 階段 1 用含 Maven 的大 Image 編譯，階段 2 只把 Jar 丟進輕量 JRE Image 執行
# 結果：產出的 Image 很小（約 200MB），而不是 800MB+

# [階段 1]：負責編譯 (Builder)
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 先只複製 pom.xml 並預先下載依賴
# 這樣只要 pom.xml 沒變，Docker 就會用快取，不用每次重新下載一堆 jar
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再複製原始碼進來編譯
COPY src ./src

# 打包成可執行的 Jar
# -DskipTests: 跳過測試（省時間）
# -B: batch mode，不要互動式輸出
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------

# [階段 2]：負責執行 (Runner)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 從 [階段 1] 把編譯好的 Jar 複製過來
# Maven 的產出在 target/ 底下（Gradle 是 build/libs/）
# backend-*.jar 不會比對到 backend-*.jar.original，所以只會拿到可執行的那顆
COPY --from=builder /app/target/backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
