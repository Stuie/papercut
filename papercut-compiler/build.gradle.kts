import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":papercut-annotations"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    implementation("com.google.auto:auto-common:0.11")
    implementation("com.github.zafarkhaja:java-semver:0.9.0")
    compileOnly(files(Jvm.current().toolsJar))

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("io.mockk:mockk:1.10.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

apply(from = rootProject.file("gradle/gradle-mvn-push.gradle"))
