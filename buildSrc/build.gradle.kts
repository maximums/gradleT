plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
    implementation("io.ktor.plugin:plugin:2.2.1")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.1")
}