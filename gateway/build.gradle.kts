plugins {
    id("cdodi.ktor-application-conventions")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
}

group = "cdodi.com"
version = "0.1.0"


dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-auth:2.1.3")
    implementation("io.ktor:ktor-server-auth-jwt:2.1.3")
    implementation("io.ktor:ktor-server-websockets:2.1.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.1.3")
    implementation(project(":api"))
    runtimeOnly("io.grpc:grpc-netty:1.47.0")
}

// Waaaaaaattttaaa fuuuuck
// [https://stackoverflow.com/questions/73286776/grpc-unsupportedaddresstypeexception-but-only-when-packaged-with-shadowjar]
// ¯\_(ツ)_/¯
tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    mergeServiceFiles()
}