dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    //============================ MEDIA SERVER DEPENDENCIES ===========================================//
    implementation("org.kurento:kurento-jsonrpc-server:6.14.0")
    implementation("org.kurento:kurento-client:6.14.0")
    implementation("com.github.docker-java:docker-java:3.1.5")
    implementation("org.codehaus.janino:janino:3.1.0")
}
