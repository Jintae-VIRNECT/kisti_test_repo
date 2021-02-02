import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//—————————————————————————————————————————————————————————————————————————————————————————————————
// BUILDSCRIPT.
//—————————————————————————————————————————————————————————————————————————————————————————————————
buildscript {
    repositories {
        mavenCentral()
    }
}

//—————————————————————————————————————————————————————————————————————————————————————————————————
// GRADLE PLUGINS.
//—————————————————————————————————————————————————————————————————————————————————————————————————
plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.72" apply false
    kotlin("plugin.jpa") version "1.3.72" apply false
    id("java")
}

//—————————————————————————————————————————————————————————————————————————————————————————————————
// CONFIGURATION.
//—————————————————————————————————————————————————————————————————————————————————————————————————
allprojects {
    group = "com.virnect"
    version = "2.0"
    val springCloudVersion by extra("Hoxton.SR1")

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
        implementation(project(":service-kms"))
        implementation(project(":service-data"))
        implementation(project(":service-file-data"))
        implementation("org.springframework.cloud:spring-cloud-config-client")
    }
}
project(":service-data") {
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}

project(":service-file-data") {
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}

project(":service-kms") {
    dependencies {
        implementation(group = "com.github.docker-java", name = "docker-java", version = "3.1.5")
        implementation(group = "org.codehaus.janino", name = "janino", version = "3.1.0")
        implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.10")
    }
}

project(":service-client") {

}

project(":service-java-client") {

}
