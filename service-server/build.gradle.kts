import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("java")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

// This block encapsulates custom properties and makes them available to
// all modules in the project.
ext {
    set("springCloudVersion", "Hoxton.SR1")
    /*val profiles = if(System.getProperty("spring.profiles") != null) System.getProperty("spring.profiles").toString() else "default"
    set("profiles", profiles)*/

}

sourceSets {
    main {
        java.srcDir("src/main/java")
        resources.srcDir("src/main/resources")
        /*resources.srcDirs(listOf(
            "src/main/resources",
            "src/main/resources-${System.getProperty("spring.profiles")}"
        ))*/
    }
}

springBoot {
    /*buildInfo {
        properties {
            group = rootProject.group.toString()
            version = rootProject.version.toString()

        }
    }*/
    //buildInfo()
    buildInfo {
        properties {
            //additional = mapOf("version.remoteservice.server" to "v2.2")
            additional = mapOf("version.remoteservice.server" to "v2.0")
        }
    }
    mainClassName = "com.virnect.serviceserver.ServiceServerApplication"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${ext["springCloudVersion"]}")
    }
}

dependencies {
    //implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
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
    runtimeOnly("org.hsqldb:hsqldb")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    //============================ SERVICE DEPENDENCIES ===========================================//
    // Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // Netflex Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // ModelMapper
    implementation(group = "org.modelmapper", name = "modelmapper", version = "2.3.0")
    // Query DSL
    implementation("com.querydsl:querydsl-jpa")
    implementation("com.querydsl:querydsl-apt")
    // Common
    implementation(group = "commons-io", name = "commons-io", version = "2.4")
    //implementation("com.google.code.gson:gson:2.8.5")
    // Apache Common Lang and Http Component
    implementation(group = "org.apache.commons", name= "commons-lang3", version = "3.10")
    implementation(group = "org.apache.httpcomponents", name = "httpclient", version = "4.5.9")
    // aws s3
    //implementation(group = "com.amazonaws", name = "aws-java-sdk-s3", version = "1.11.415") // AWS Storage Service
    implementation(group = "com.amazonaws", name = "aws-java-sdk-s3") // AWS Storage Service
    // minio
    //implementation(group = "io.minio", name = "minio", version = "7.1.0") // minio Storage Service
    implementation("io.minio:minio:7.1.0")
    // feign oktthp
    implementation("io.github.openfeign:feign-okhttp")

    //============================ MEDIA SERVER DEPENDENCIES ===========================================//
    implementation("org.kurento:kurento-jsonrpc-server:6.14.0")
    implementation("org.kurento:kurento-client:6.14.0")
    implementation("com.github.docker-java:docker-java:3.1.5")
    implementation("org.codehaus.janino:janino:3.1.0")
    //implementation("org.apache.commons:commons-lang3:3.10")
    implementation("com.google.code.gson:gson:2.8.6")

    testImplementation("org.powermock:powermock-module-junit4:2.0.7")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    println("boot Jar task invoked....")
    println("Note: sourceSet has changed with current spring profiles")
    //println("spring.profiles: " + System.getProperty("spring.profiles"))
    //println("extra version: " + System.getProperty("service.extra-version"))

    enabled= true
    mainClassName = "com.virnect.serviceserver.ServiceServerApplication"
    /*manifest {
            attributes(
                "Implementation-Title" to  "Remote Service Server",
                "Implementation-Version" to "2.2.21 revision 9")
    }*/
    manifest {
        attributes(
            "Implementation-Title" to  "Remote Service Server",
            "Implementation-Version" to "2.0 revision 2222v14")
    }
    archiveFileName.set("RM-Service-${archiveVersion.get()}.${archiveExtension.get()}")

    //destinationDirectory.set(project.file("${rootProject.buildDir}/libs/${archiveBaseName}"))
    /*manifest {
        attributes("Start-Class" to "com.virnect.serviceserver.ServiceServerApplication")
    }*/
}
