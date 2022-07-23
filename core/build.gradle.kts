plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    jacoco
}

dependencies {
    implementation(kotlin("stdlib"))
    val libGdxVersion: String by project
    implementation("com.badlogicgames.gdx:gdx:$libGdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$libGdxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.4")
}

sourceSets {
    main {
        resources {
            srcDirs("assets")
        }
    }
}

tasks {
    jar {
        archiveBaseName.set("tilegame-" + archiveBaseName.get())
    }
    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}
