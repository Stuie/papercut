plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "papercut-parent"
include(":papercut-annotations")
include(":papercut-compiler")
