plugins {
    id("org.springframework.boot")
    id("java")
    kotlin("jvm")
    kotlin("plugin.spring")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
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
    //implementation(kotlin("stdlib-jdk8"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("junit", "junit", "4.12")

    implementation("org.springframework.data:spring-data-envers")


    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // ModelMapper
    implementation(group = "org.modelmapper", name = "modelmapper", version = "2.3.0")

    // Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // Netflex Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // Swagger
    implementation("io.springfox:springfox-swagger2:2.9.2")
    //implementation("io.springfox:springfox-swagger-ui:2.9.2")
}
