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
//    val downloadEclipseTask by creating() {
//        group = "my_tasks"
//        description = "Downloads an important file"
//        download.run {
//            src(remoteFilePath)
//            dest(File(buildDir.absolutePath, zipFileName))
//            overwrite(false)
//        }
//    }
//
//    val downloadAndUnpackEclipseTask by creating() {
//        dependsOn(downloadEclipseTask)
//        group = "my_tasks"
//        description = "Unpacks the important file"
//
//        println("Unpacking $buildDir/$zipFileName into $buildDir")
//        unzipTo(buildDir, File(buildDir.resolve(zipFileName).toURI()))
//    }
}


project.subprojects.forEach {subproject ->
    subproject.afterEvaluate {

        val cleanTask = tasks.clean.get()
        val assembleTask = tasks.assemble.get()
        val buildTask = tasks.build.get()
        assembleTask.dependsOn(cleanTask)

        tasks {
//            build {
//                doLast {
//                    println("Build finished! And it was the first task that was finished... wasn't it? I mean its in front of the command... I mean it literally says 'clean build CopyAll EpicWin ThisMayFail' - not, for example, 'EpicWin ThisMayFail clean build' which should definitely run the 'build' task last...")
//                }
//            }

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

            register("ThisMayFail") {
                group = "my_tasks"
                description = "EPIC WIN/FAIL TASK"

                val willFail = false
                doLast {
                    println("Will it fail? Only time will tell...")
                    if (subproject.buildDir.resolve("classes").exists().not()) throw GradleException("LOL you haven't built your stuff yet. What a dummy!")
                    println("Guess the build task worked... Sure hope you didn't run the build task AGAIN even if you already did before!")
                }
            }
        }
    }
}
