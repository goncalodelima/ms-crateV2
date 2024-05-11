plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "com.minecraftsolutions"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("me.devnatan:inventory-framework-platform-bukkit:3.0.8")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("me.clip:placeholderapi:2.11.3")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

bukkit {
    name = "ms-cratesFREE"
    version = "${project.version}"
    main = "com.minecraftsolutions.freecrate.CratePlugin"
    authors = listOf("yDioguin_", "ReeachyZ_")
    depend = listOf("MinecraftSolutions", "InventoryFramework-latest")
    description = "Free Crates Plugin"
    website = "https://minecraft-solutions.com"
    commands {
        register("crate"){
            aliases = listOf("crates", "caixas", "caixa")
        }
    }
}