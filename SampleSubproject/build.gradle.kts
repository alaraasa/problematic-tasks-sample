plugins {
    base
    id("java")
}

group = "org.example"

repositories {
    mavenCentral()
}

val eclipseFiletree = fileTree(rootProject.buildDir.resolve("eclipse").resolve("plugins"))

dependencies {
    implementation(files(eclipseFiletree.files.find { it.name.contains("org.eclipse.core.runtime_") }!!.path))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}