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
    val downloadEclipseTask by creating(Download::class) {
        group = "my_tasks"
        description = "Downloads an important file ASAP"

        src(remoteFilePath)
        dest(buildDir.absolutePath)
        overwrite(false)
        download()
    }

    val unpackEclipseTask by creating() {
        group = "my_tasks"
        description = "Unpacks an important file ASAP"

        println("Unpacking $zipFileName")
        dependsOn(downloadEclipseTask)
        unzipTo(buildDir, buildDir.resolve(zipFileName))
        println("File unzip finished.")
    }
}

subprojects.forEach {
    val cleanTask = it.tasks.clean.get()
    val buildTask = it.tasks.build.get()
    it.tasks {
        buildTask.dependsOn(cleanTask)

        val copyStuffTask = this.create<Copy>("copyStuffTask") {
            group = "my_tasks"
            description = "Copies important stuff in the real project, but just duplicates stuff here"
            mustRunAfter(buildTask)

            doLast {
                from(it.buildDir.resolve("classes"))
                to(it.buildDir.resolve("classes-copied"))
            }
        }

        val copyStuff2Task = this.create<Copy>("copyStuffTask") {
            group = "my_tasks"
            description = "Copies important stuff in the real project, but just duplicates stuff here"
            mustRunAfter(buildTask)

            doLast {
                from(it.buildDir.resolve("generated"))
                to(it.buildDir.resolve("generated-copied"))
            }
        }

        this.create("copyAllTheThingsTask") {
            group = "my_tasks"
            description = "Executes all the copy tasks"

            this.dependsOn(copyStuffTask)
            this.dependsOn(copyStuff2Task)
        }
    }

}