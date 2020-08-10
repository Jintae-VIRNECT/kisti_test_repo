import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    kotlin("plugin.jpa") version "1.3.72" apply false
    id("java")
}

allprojects {
    group = "com.virnect"
    version = "2.0.0"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

}

subprojects {
    apply {
        plugin("java")
        plugin("io.spring.dependency-management")
    }
    /*configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }*/

    repositories {
        mavenCentral()
    }
}
project(":service-server") {
    dependencies {
        implementation(project(":service-client"))
        implementation(project(":service-java-client"))
        implementation(project(":service-api"))
    }
    /*val jar: Jar by tasks
    val bootJar: BootJar by tasks

    bootJar.enabled = false
    jar.enabled = true*/

}
project(":service-common") {

}
project(":service-api") {

}
project(":service-kms") {

}
project(":service-client") {

}
project(":service-java-client") {

}
