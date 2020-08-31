dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    //============================ MEDIA SERVER DEPENDENCIES ===========================================//
    implementation(group = "org.kurento", name = "kurento-jsonrpc-server", version = "6.14.0")
    implementation(group = "org.kurento", name = "kurento-client", version = "6.14.0")
    implementation(group = "com.github.docker-java", name = "docker-java", version = "3.1.5")
    implementation(group = "org.codehaus.janino", name = "janino", version = "3.1.0")
    implementation(group = "org.apache.commons", name= "commons-lang3", version = "3.10")
    implementation("com.google.code.gson:gson:2.8.6")

    //testImplementation("org.powermock:powermock-module-junit4:2.0.7")
    //testImplementation("org.hamcrest:hamcrest-core:2.2")
    //testImplementation("org.hamcrest:hamcrest-library:2.2")
}
