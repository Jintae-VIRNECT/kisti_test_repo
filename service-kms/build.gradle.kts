plugins {
    kotlin("jvm") //`java-library`
}


dependencies {
    testImplementation("junit", "junit", "4.12")
    //============================ MEDIA SERVER DEPENDENCIES ===========================================//
    api(project(":service-client"))
    api(project(":service-java-client"))
    api(group = "org.kurento", name = "kurento-jsonrpc-server", version = "6.14.0")
    api(group = "org.kurento", name = "kurento-client", version = "6.14.0")
    api("com.google.code.gson:gson:2.8.6")
}
