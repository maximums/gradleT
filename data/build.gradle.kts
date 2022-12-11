val exposed_version: String by project
val postgresql_version: String by project

plugins {
    id("cdodi.kotlin-application-conventions")
}

application {
    mainClass.set("cdodi.com.data.Main")
}

tasks.register<Jar>("fatJar") {
    dependsOn.addAll(
        listOf(
            "compileJava",
            "compileKotlin",
            "processResources"
        )
    )
//    archiveClassifier.set("standalone") // Naming the jar
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } + sourceSets.main.get().output
    from(contents)
}

group = "cdodi.com.data"
version = "0.1.0"

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")

    implementation(project(":api"))
    runtimeOnly("io.grpc:grpc-netty:1.47.0")
}
