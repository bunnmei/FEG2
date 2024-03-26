// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

//@Suppress("DSL_SCOPE_VIOLATION")
plugins {
//    kotlin("jvm") version "1.9.21" apply false

    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    alias(libs.plugins.ksp) apply false
}