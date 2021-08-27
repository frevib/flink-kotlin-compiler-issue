# Kotlin compiler issue with Apache Flink

Several different Kotlin compiler versions give different exceptions, see:

https://youtrack.jetbrains.com/issue/KT-48422

## Reproduce
Build Jar

`./gradlew shadowJar`

Run in Apache Flink

`./bin/flink run build/libs/hello_world-1.0-SNAPSHOT-all.jar`

Use different compiler versions:

__Gives exception:__

```kotlin
kotlin("jvm") version "1.5.10"
```


__Works properly, no exception__
```kotlin
kotlin("jvm") version "1.4.32"
```
