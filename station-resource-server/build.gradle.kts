group = "dev.aventix.station.guide"
version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    kotlin("plugin.jpa") version "1.9.25"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-testcontainers")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.testcontainers:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    "developmentOnly"("org.springframework.boot:spring-boot-devtools")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("org.postgresql:postgresql")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    publish.set(true)
    docker {
        publishRegistry {
            url.set("ghcr.io")
            username.set(project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR"))
            password.set(project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN"))
            imageName.set("ghcr.io/cloud-bastion/station-guide/station-resource-server:${project.properties["bootImageTag"]}")
            tags.set(listOf("ghcr.io/cloud-bastion/station-guide/station-resource-server:latest"))
        }
    }
}

