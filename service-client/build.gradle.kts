dependencies {
    //implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    implementation(group="org.kurento", name="kurento-jsonrpc-client", version="6.14.0")
    implementation(group="org.kurento", name="kurento-jsonrpc-client-jetty", version="6.14.0")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    testImplementation(group="org.mockito", name="mockito-core", version="2.23.4")
}
/*configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}*/

/*
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}*/
