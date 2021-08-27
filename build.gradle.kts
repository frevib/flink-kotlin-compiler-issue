plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "com.eventloopsoftware"
version = "1.0-SNAPSHOT"


val kotlinVersion: String by project
val log4jVersion: String by project
val flinkVersion: String by project


repositories {
    mavenCentral()
}

application {
    mainClass.set("AppKt")
}

dependencies {
    // core
    implementation(kotlin("stdlib"))

    // Flink
    implementation("org.apache.flink:flink-java:$flinkVersion")
    implementation("org.apache.flink:flink-streaming-java_2.12:$flinkVersion")
    implementation("org.apache.flink:flink-walkthrough-common_2.12:$flinkVersion")
    implementation("org.apache.flink:flink-clients_2.12:$flinkVersion")

    // logging
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:$log4jVersion")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass,
                "Multi-Release" to "true"
            )
        )
    }
}