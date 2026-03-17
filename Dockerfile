# ビルドステージ
FROM eclipse-temurin:21-jdk-jammy AS build

# 作業ディレクトリを明示的に指定
WORKDIR /app

# プロジェクトファイルをコピー
COPY . .

# mvnwに実行権限を与えて実行
RUN chmod +x mvnw && ./mvnw clean install -DskipTests

# 実行ステージ
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# ビルドステージからJARファイルをコピー
# Workdirを指定したためパスが /app/target/... になります
COPY --from=build /app/target/Arisuki-0.0.1-SNAPSHOT.jar app.jar

# ポート設定
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT:8080}"]