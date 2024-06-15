plugins {
    id("java")
    application
}

group = "com.shop.server"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.hibernate.orm:hibernate-core:6.5.2.Final")
    implementation(project(":common"))
}

application {
    mainClass = "com.shop.server.Server"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}