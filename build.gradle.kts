plugins {
    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.serialization") version "1.6.21" apply false
    id("org.sonarqube") version "3.3"
}

allprojects {
    group = "io.github.hider"
    version = "0.0.4"
    description = "libGDX based tile platformer"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

subprojects {
    tasks {
        withType<Test> {
            useJUnitPlatform()
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
                jvmTarget = "11"
            }
        }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "hider_tilegame")
        property("sonar.organization", "hider")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
