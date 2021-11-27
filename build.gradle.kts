import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    jcenter()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:tap-api:4.1.10")
    compileOnly("io.github.monun:kommand-api:2.6.6")
    implementation("org.reflections:reflections:0.10.1")
}

tasks {
    val archive = project.properties["pluginName"].toString()

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = "UTF-8"
    }
    register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("paperJar") {
        archiveBaseName.set(archive)
        archiveClassifier.set("")
        archiveVersion.set("")

        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.underconnor.lian.plugin.LianPlugin"))
        }

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