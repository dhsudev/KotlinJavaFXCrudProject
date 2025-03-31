plugins {
    kotlin("jvm") version "1.8.10"
    id("org.openjfx.javafxplugin") version "0.0.13"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.openjfx:javafx-base:14")
    implementation("org.openjfx:javafx-controls:14")
    implementation("org.openjfx:javafx-fxml:14")
}

javafx {
    version = "14"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("MainKt")
}
