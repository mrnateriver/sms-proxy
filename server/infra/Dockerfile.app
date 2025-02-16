FROM gradle:8.9-alpine AS cache

COPY --chown=gradle:gradle ../../gradle.properties build.gradle.kts settings.gradle.kts /home/gradle/app/
COPY --chown=gradle:gradle ../../gradle/libs.versions.toml /home/gradle/app/gradle/libs.versions.toml
COPY --chown=gradle:gradle ../../proxyApiClient/build.gradle.kts /home/gradle/app/proxyApiClient/build.gradle.kts
COPY --chown=gradle:gradle ../../proxyApiClient/gradle.lockfile /home/gradle/app/proxyApiClient/gradle.lockfile
COPY --chown=gradle:gradle ../../proxyApiServer/build.gradle.kts /home/gradle/app/proxyApiServer/build.gradle.kts
COPY --chown=gradle:gradle ../../proxyApiServer/gradle.lockfile /home/gradle/app/proxyApiServer/gradle.lockfile
COPY --chown=gradle:gradle ../../proxyApiTypeSpec/build.gradle.kts /home/gradle/app/proxyApiTypeSpec/build.gradle.kts
COPY --chown=gradle:gradle ../../receiverApp/build.gradle.kts /home/gradle/app/receiverApp/build.gradle.kts
COPY --chown=gradle:gradle ../../receiverApp/gradle.lockfile /home/gradle/app/receiverApp/gradle.lockfile
COPY --chown=gradle:gradle ../../relayApp/build.gradle.kts /home/gradle/app/relayApp/build.gradle.kts
COPY --chown=gradle:gradle ../../relayApp/gradle.lockfile /home/gradle/app/relayApp/gradle.lockfile
COPY --chown=gradle:gradle ../../server/build.gradle.kts /home/gradle/app/server/build.gradle.kts
COPY --chown=gradle:gradle ../../server/gradle.lockfile /home/gradle/app/server/gradle.lockfile
COPY --chown=gradle:gradle ../../shared/build.gradle.kts /home/gradle/app/shared/build.gradle.kts
COPY --chown=gradle:gradle ../../buildSrc /home/gradle/app/buildSrc

WORKDIR /home/gradle/app

RUN gradle :server:dependencies --no-daemon

FROM gradle:8.9-alpine AS build

COPY --from=cache /home/gradle/.gradle /home/gradle/.gradle
COPY --chown=gradle:gradle .. /home/gradle/app/
WORKDIR /home/gradle/app

RUN gradle :server:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN mkdir /app
COPY --from=build /home/gradle/app/server/build/libs/*.jar /app/server.jar

ENTRYPOINT ["java","-jar","/app/server.jar"]
