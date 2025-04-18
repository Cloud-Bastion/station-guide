plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "dev.aventix.station.guide"
version = "0.0.1-SNAPSHOT"


kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    //implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
    // https://mvnrepository.com/artifact/io.hypersistence/hypersistence-utils-hibernate-63
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.9.8")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    implementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
            imageName.set("ghcr.io/cloud-bastion/station-guide/auth-server:${project.properties["bootImageTag"]}")
            tags.set(listOf("ghcr.io/cloud-bastion/station-guide/auth-server:latest"))
        }
    }
}
