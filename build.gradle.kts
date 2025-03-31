plugins {
    kotlin("jvm") version "1.8.10"
    id("org.openjfx.javafxplugin") version "0.0.14"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.5.0")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("MainKt")
}