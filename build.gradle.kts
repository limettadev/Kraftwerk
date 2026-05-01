plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "pink.mino.Kraftwerk"
version = "1.3.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/nexus/repository/public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://maven.elmakers.com/repository/")
    maven(url = "https://maven.citizensnpcs.co/repo")
    maven(url = "https://jitpack.io/")
    maven(url = "https://repo.lucko.me/")
    maven(url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://maven.enginehub.org/repo/")
    maven(url = "https://repo.lunarclient.dev")
    mavenLocal()
}

dependencies {
    implementation("redis.clients:jedis:5.2.0-beta4")
    implementation("me.lucko:helper:5.6.14")
    implementation("me.lucko:helper-profiles:1.2.0")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.5"))
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("me.lucko:spark-api:0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("net.dv8tion:JDA:5.5.1") {
        exclude(module = "opus-java")
    }
    implementation("com.google.code.gson:gson:2.9.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("net.luckperms:api:5.4")
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:5.0.0")
    compileOnly("com.lunarclient:apollo-api:1.2.5")
    compileOnly(group = "org.popcraft", name = "chunky-common", version = "1.3.38")
    compileOnly("com.lunarclient:apollo-extra-adventure4:1.2.5")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.9.9")
}

tasks.test {
    useJUnitPlatform()
}