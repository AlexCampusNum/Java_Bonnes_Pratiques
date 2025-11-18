plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.17")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("org.json:json:20250517")
    implementation("log4j:log4j:2.20.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}