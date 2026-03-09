FROM eclipse-temurin:21-jdk-jammy AS build
COPY . .
# mvnwに実行権限を与えて実行
RUN chmod +x mvnw && ./mvnw clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy
# 【修正箇所】buildステージの「target/〜」をコピーするようにパスを指定
COPY --from=build /target/Arisuki-0.0.1-SNAPSHOT.jar app.jar

# ポート設定（Renderの$PORTに対応）
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:8080}"]