#!/bin/sh

./gradlew dependencies --write-locks --no-daemon
./gradlew :shared:dependencies --write-locks --no-daemon
./gradlew :server:dependencies --write-locks --no-daemon
./gradlew :proxyApiClient:dependencies --write-locks --no-daemon
./gradlew :proxyApiServer:dependencies --write-locks --no-daemon
./gradlew :proxyApiTypeSpec:dependencies --write-locks --no-daemon
./gradlew :relayApp:dependencies --write-locks --no-daemon
./gradlew :receiverApp:dependencies --write-locks --no-daemon
./gradlew :buildSrc:dependencies --write-locks --no-daemon
