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
    id("org.jetbrains.kotlin.kapt")
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

dependencies {
    implementation(project(":papercut-annotations"))
    kapt(libs.auto.service)
    kapt(libs.incap.processor)
    implementation(libs.auto.service)
    implementation(libs.auto.common)
    implementation(libs.java.semver)
    compileOnly(libs.incap)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
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
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications["compiler"])
}
