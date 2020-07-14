plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    java //id("java")
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/java")
        resources.srcDirs("src/main/resources")
    }

    /*test {
        java.srcDirs("src/main/test/java")
        resources.srcDirs("src/main/test/resources")
    }*/
}

/*jar {
    enabled = true
}*/

springBoot {
    buildInfo()
    mainClassName = "com.virnect.remoteserviceserver.RemoteServiceServerApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    //implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    //implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    /*implementation("org.springframework.boot:spring-boot-starter-websocket") {
        exclude(group = "org.apache.tomcat", module = "spring-boot-starter-tomcat")
    }*/
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-tomcat")
    //implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    //runtimeOnly("mysql:mysql-connector-java")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    /*===============================================================================================*/
    implementation("org.kurento:kurento-jsonrpc-server:6.13.1")
    implementation("org.kurento:kurento-client:6.13.1")
    implementation("com.github.docker-java:docker-java:3.1.5")
    implementation("org.codehaus.janino:janino:3.1.0")
    implementation("org.apache.commons:commons-lang3:3.10")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(project(":service-client"))
    implementation(project(":service-java-client"))
    testImplementation("org.powermock:powermock-module-junit4:2.0.7")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    // https://mvnrepository.com/artifact/org.kurento/kurento-jsonrpc-server
    /*implementation(group: 'org.kurento', name: 'kurento-jsonrpc-server', version: '6.13.1') {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-web'
        exclude group: 'org.slf4j', module: 'slf4j-api'
    }
    // https://mvnrepository.com/artifact/org.kurento/kurento-client
    implementation(group: 'org.kurento', name: 'kurento-client', version: '6.13.1') {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
    }*/
}

tasks.withType<Test> {
    useJUnitPlatform()
}

/*tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}*/

/*test {
    useJUnitPlatform()
}*/
