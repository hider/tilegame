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
}

tasks {
    jar {
        archiveBaseName.set("tilegame-" + archiveBaseName.get())
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("io.github.hider.tilegame.desktop.DesktopLauncherKt")
}
