plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
}

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}

allprojects {
    version = "0.9.1"

    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}
