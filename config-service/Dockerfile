FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
# 현재 디렉토리 내의 apiEncryptionKey.jks 을 컨테이너의 루트 디렉토리에 복사
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY target/config-service-1.0.jar ConfigServer.jar
ENTRYPOINT ["java", "-jar", "ConfigServer.jar"]