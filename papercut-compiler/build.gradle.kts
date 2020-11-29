import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val papercutPublishingUsername: String by project
val papercutPublishingPassword: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":papercut-annotations"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
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

publishing {
    publications {
        create<MavenPublication>("compiler") {
            groupId = "ie.stu"
            artifactId = "papercut-compiler"
            from(components["java"])
            pom {
                name.set("Papercut Compiler")
                description.set("Keep your codebase simple.")
                url.set("https://github.com/Stuie/papercut/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("stuartgilbert")
                        name.set("Stuart Gilbert")
                        email.set("stuart.gilbert@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Stuie/papercut.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Stuie/papercut.git")
                    url.set("https://github.com/Stuie/papercut/")
                }
            }
        }
    }
    repositories {
        maven {
            credentials {
                username = papercutPublishingUsername
                password = papercutPublishingPassword
            }
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications["compiler"])
}
