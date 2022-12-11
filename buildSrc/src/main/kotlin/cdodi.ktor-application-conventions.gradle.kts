plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.ktor.plugin")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.2.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.1")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.22")
}
