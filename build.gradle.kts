plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.5" // the latest version can be found on the Gradle Plugin Portal
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}
group = "io.github.safeluren"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.sparky983.me/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
dependencies {
//    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    implementation ("fr.mrmicky:fastboard:2.0.0")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.1")
    }
}
