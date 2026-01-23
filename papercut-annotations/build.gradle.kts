val papercutPublishingUsername: String? =
    System.getenv("PAPERCUT_MAVEN_USERNAME")
        ?: findProperty("papercutPublishingUsername") as String?

val papercutPublishingPassword: String? =
    System.getenv("PAPERCUT_MAVEN_PASSWORD")
        ?: findProperty("papercutPublishingPassword") as String?

// Map papercut-specific signing properties to standard signing plugin properties
findProperty("papercutSigningKeyId")?.let { extra["signing.keyId"] = it }
findProperty("papercutSigningPassword")?.let { extra["signing.password"] = it }
findProperty("papercutSigningSecretKeyRingFile")?.let { extra["signing.secretKeyRingFile"] = it }

plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications["annotations"])
}
