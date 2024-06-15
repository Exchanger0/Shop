plugins {
    id("java")
}

group = "com.shop.common"
version = "unspecified"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}