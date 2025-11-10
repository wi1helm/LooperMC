plugins {
    id("java")
}

group = "nub.wi1helm"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    implementation("net.minestom:minestom:2025.10.31-1.21.10")

    implementation("net.kyori:adventure-text-minimessage:4.17.0") // MiniMessage
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")

    implementation("org.slf4j:slf4j-api:2.0.15")
    implementation("ch.qos.logback:logback-classic:1.5.7")

}

tasks.test {
    useJUnitPlatform()
}