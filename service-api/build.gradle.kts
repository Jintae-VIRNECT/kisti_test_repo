import org.springframework.boot.gradle.tasks.bundling.BootJar

//—————————————————————————————————————————————————————————————————————————————————————————————————
// GRADLE PLUGINS.
//—————————————————————————————————————————————————————————————————————————————————————————————————
plugins {
    id("org.springframework.boot")
    id("java")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

//—————————————————————————————————————————————————————————————————————————————————————————————————
// DEPENDENCIES.
//—————————————————————————————————————————————————————————————————————————————————————————————————
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

//—————————————————————————————————————————————————————————————————————————————————————————————————
// GRADLE TASKS.
//—————————————————————————————————————————————————————————————————————————————————————————————————
tasks.withType<Jar> {
    enabled= true
}

tasks.withType<BootJar> {
    enabled= false
}
