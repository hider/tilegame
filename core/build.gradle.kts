plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib"))
    val libGdxVersion: String by project
    implementation("com.badlogicgames.gdx:gdx:$libGdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$libGdxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

sourceSets {
    main {
        resources {
            srcDirs("assets")
        }
    }
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
    }
    jar {
        archiveBaseName.set("tilegame-" + archiveBaseName.get())
    }
}
