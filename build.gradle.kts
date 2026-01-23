plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

allprojects {
    version = "0.11.0"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}
