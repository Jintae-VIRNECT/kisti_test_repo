import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    // org.springframework.boot version 2.2.5 version has file upload problem.
    id("org.springframework.boot") version "2.2.6.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    kotlin("plugin.jpa") version "1.3.72" apply false
    id("java")
}

allprojects {
    group = "com.virnect"
    version = "2.1.0"

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
    dependencies {
        //todo:
    }


    repositories {
        mavenCentral()
    }
}
project(":service-server") {
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        //implementation(project(":service-kms"))
        implementation(project(":service-client"))
        implementation(project(":service-java-client"))
        implementation(project(":service-data"))
        implementation("org.springframework.cloud:spring-cloud-config-client")
    }
    /*val jar: Jar by tasks
    val bootJar: BootJar by tasks

    bootJar.enabled = false
    jar.enabled = true*/

}
project(":service-data") {
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}

project(":service-common") {

}
project(":service-kms") {
    dependencies {
        //implementation(project(":service-kms"))
        implementation(project(":service-client"))
        implementation(project(":service-java-client"))
    }
}
project(":service-client") {

}
project(":service-java-client") {

}
