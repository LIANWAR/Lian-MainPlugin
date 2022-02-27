import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "2.0.2"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
    jcenter()
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:kommand-api:2.6.6")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.4")
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

tasks {
    val archive = project.properties["pluginName"].toString()

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveBaseName.set(archive)
        archiveClassifier.set("")
        archiveVersion.set("")

        from(
            shade.map {
                if (it.isDirectory)
                    it
                else
                    zipTree(it)
            }
        )

        from(sourceSets["main"].output)

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".server/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}