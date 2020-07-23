import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE" //change version to 2.2.5.RELEASE //2.3.1.RELEASE
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("java")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

ext {
    set("springCloudVersion", "Hoxton.SR1")
}

springBoot {
    buildInfo()
    mainClassName = "com.virnect.serviceserver.ServiceServerApplication"
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${ext["springCloudVersion"]}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    implementation("org.springframework.data:spring-data-envers")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("org.projectlombok:lombok")
    //developmentOnly("org.springframework.boot:spring-boot-devtools") //Unresolved reference: developmentOnly under boot version 2.3.1
    runtimeOnly("mysql:mysql-connector-java")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    //============================ SERVICE DEPENDENCIES ===========================================//
    // Eureka
    //implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    // Netflex Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // Swagger
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
    // ModelMapper
    implementation(group = "org.modelmapper", name = "modelmapper", version = "2.3.0")
    // Query DSL
    implementation("com.querydsl:querydsl-jpa") // querydsl
    implementation("com.querydsl:querydsl-apt") // querydsl
    // Common
    implementation(group = "commons-io", name = "commons-io", version = "2.4")
    //implementation("com.google.code.gson:gson:2.8.5")
    // Apache Common Lang and Http Component
    implementation(group = "org.apache.commons", name= "commons-lang3", version = "3.10")
    implementation(group = "org.apache.httpcomponents", name = "httpclient", version = "4.5.9")
    // aws s3
    implementation(group = "com.amazonaws", name = "aws-java-sdk-s3", version = "1.11.415") // AWS Storage Service
    //============================ MEDIA SERVER DEPENDENCIES ===========================================//
    implementation("org.kurento:kurento-jsonrpc-server:6.14.0")
    implementation("org.kurento:kurento-client:6.14.0")
    implementation("com.github.docker-java:docker-java:3.1.5")
    implementation("org.codehaus.janino:janino:3.1.0")
    //implementation("org.apache.commons:commons-lang3:3.10")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(project(":service-client"))
    implementation(project(":service-java-client"))
    testImplementation("org.powermock:powermock-module-junit4:2.0.7")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

/*tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    main = "com.virnect.serviceserver.ServiceServerApplication" // MainClass 경로
}*/

tasks.getByName<BootJar>("bootJar") {
    enabled= true
    mainClassName = "com.virnect.serviceserver.ServiceServerApplication"
}

/*
tasks.getByName<BootJar>("bootJar") {
    manifest {
        attributes("Start-Class" to "com.virnect.serviceserver.ServiceServerApplication")
    }
}
*/

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
