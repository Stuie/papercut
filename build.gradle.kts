plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

allprojects {
    version = "0.10.0"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
