plugins {
    id("org.springframework.boot")
    id("java")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    //kotlin("jvm")
    //kotlin("plugin.spring")
}

//configurations {
//    compileOnly {
//        extendsFrom(configurations.annotationProcessor.get())
//    }
//}



ext {
    set("springCloudVersion", "Hoxton.SR1")
    /*val profiles = if(System.getProperty("spring.profiles") != null) System.getProperty("spring.profiles").toString() else "default"
    set("profiles", profiles)*/
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${ext["springCloudVersion"]}")
    }
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.data:spring-data-envers")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    //testAnnotationProcessor("org.projectlombok:lombok")
    //testCompileOnly("org.projectlombok:lombok")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // ModelMapper
    implementation(group = "org.modelmapper", name = "modelmapper", version = "2.3.0")
    // Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // Netflex Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // Swagger
    api("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
}
tasks.getByName<Jar>("jar") {
    println("service data Jar task invoked....")
    enabled= true
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    println("service data boot Jar task invoked....")
    enabled= false
}