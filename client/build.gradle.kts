plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "com.shop.client"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls", "javafx.base", "javafx.graphics")
}

application {
    mainClass = "com.shop.client.Starter"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}