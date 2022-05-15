plugins {
    application
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    val libGdxVersion: String by project
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$libGdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$libGdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$libGdxVersion:natives-desktop")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
    }
    jar {
        archiveBaseName.set("tilegame-" + archiveBaseName.get())
    }
}

application {
    mainClass.set("io.github.hider.tilegame.desktop.DesktopLauncherKt")
}
