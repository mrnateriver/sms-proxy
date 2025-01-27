#!/bin/sh

./gradlew dependencies --write-locks
./gradlew :shared:dependencies --write-locks
./gradlew :server:dependencies --write-locks
./gradlew :proxyApiClient:dependencies --write-locks
./gradlew :proxyApiServer:dependencies --write-locks
./gradlew :proxyApiTypeSpec:dependencies --write-locks
./gradlew :relayApp:dependencies --write-locks
./gradlew :receiverApp:dependencies --write-locks
./gradlew :buildSrc:dependencies --write-locks
