import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val papercutPublishingUsername: String by project
val papercutPublishingPassword: String by project

plugins {
    kotlin("jvm")
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

publishing {
    publications {
        create<MavenPublication>("annotations") {
            groupId = "ie.stu"
            artifactId = "papercut-annotations"
            from(components["java"])
            pom {
                name.set("Papercut Annotations")
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
    sign(publishing.publications["annotations"])
}
