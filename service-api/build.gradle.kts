plugins {
    id("org.springframework.boot")
    id("java")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

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
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // ModelMapper
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(group = "org.modelmapper", name = "modelmapper", version = "2.3.0")

    // Swagger
    api("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")

    // service base
    api(project(":service-data"))
    api(project(":service-file-data"))
}
tasks.getByName<Jar>("jar") {
    println("service api Jar task invoked....")
    enabled= true
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    println("service api boot Jar task invoked....")
    enabled= false
}
