import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.support.unzipTo
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    id("de.undercouch.download") version "5.0.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


val remoteFilePath = "https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2021-06/R/eclipse-java-2021-06-R-win32-x86_64.zip"
val zipFileName = "eclipse-java-2021-06-R-win32-x86_64.zip"

tasks {
    val downloadEclipseTask by registering(Download::class) {
        group = "my_tasks"
        description = "Downloads an important file"

        src(remoteFilePath)
        dest(File(buildDir.absolutePath, zipFileName))
        overwrite(false)
        download()
    }

    val downloadAndUnpackEclipseTask by registering() {
        dependsOn(downloadEclipseTask)
        group = "my_tasks"
        description = "Unpacks the important file"

        println("Unpacking ${downloadEclipseTask.get().dest} into $buildDir")
        unzipTo(buildDir, downloadEclipseTask.get().dest)
    }
}


project.subprojects.forEach {subproject ->
    subproject.afterEvaluate {

        val cleanTask = tasks.clean.get()
        val assembleTask = tasks.assemble.get()
        val buildTask = tasks.build.get()
        assembleTask.dependsOn(cleanTask)

        tasks {
            val copyStuff = register<Copy>("CopyStuff") {
                group = "my_tasks"
                description = "Copies important stuff in the real project, but just duplicates stuff here"
                dependsOn(buildTask)
                println("Copying ${subproject.buildDir.resolve("classes").absolutePath} to ${subproject.buildDir.resolve("classes-copied").absolutePath}")
                from(subproject.buildDir.resolve("classes").absolutePath)
                into(subproject.buildDir.resolve("classes-copied").absolutePath)
            }

            val copyStuff2 = register<Copy>("CopyStuff2") {
                group = "my_tasks"
                description = "Copies important stuff in the real project, but just duplicates stuff here"
                dependsOn(buildTask)
                from(subproject.buildDir.resolve("generated"))
                into(subproject.buildDir.resolve("generated-copied"))
            }

            register("CopyAll") {
                group = "my_tasks"
                description = "Runs all the copy tasks"

                dependsOn(copyStuff.get())
                dependsOn(copyStuff2.get())
            }
        }
    }
}
