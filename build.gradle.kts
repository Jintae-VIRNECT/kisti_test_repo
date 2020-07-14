plugins {
    java
    //kotlin("jvm") version "1.3.72"
}

allprojects {
    group = "com.virnect"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenLocal()
        /*maven {
            url = uri("http://repo.maven.apache.org/maven2")
        }*/
    }

    /*publishing {
        publications {
            maven(MavenPublication) {
                from(components.java)
            }
        }de
    }
*/
    /*tasks.withType(JavaCompile) {
        options.encoding = 'UTF-8'
    }*/

    /*tasks {
        compileJava {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }*/

    /*apply plugin: 'maven-publish'

    repositories {
        mavenLocal()
        maven {
            url = uri('http://repo.maven.apache.org/maven2')
        }
    }

    sourceCompatibility = '1.8'

    configurations.all {
    }

    publishing {
        publications {
            maven(MavenPublication) {
                from(components.java)
            }
        }
    }

    tasks.withType(JavaCompile) {
        options.encoding = 'UTF-8'
    }*/
}
project(":service-server") {

}

project(":service-client") {

}

project(":service-java-client") {

}

dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
}


