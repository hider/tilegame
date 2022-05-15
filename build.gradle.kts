plugins {
    kotlin("jvm") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
}

allprojects {
    group = "io.github.hider"
    version = "0.0.1"

    repositories {
        mavenCentral()
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
