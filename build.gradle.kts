import dev.monosoul.jooq.GenerateJooqClassesTask
import dev.monosoul.jooq.RecommendedVersions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    val kotlinVersion = "1.9.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.1"
    id("dev.monosoul.jooq-docker") version "3.0.22"
}

group = "com.personio"

dependencies {
    project.extra["jooq.version"] = RecommendedVersions.JOOQ_VERSION
    project.extra["flyway.version"] = RecommendedVersions.FLYWAY_VERSION

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    jooqCodegen("org.postgresql:postgresql")
}

tasks.withType<GenerateJooqClassesTask> {
    withContainer {
        image {
            name = "postgres:15-alpine"
        }
    }
    usingJavaConfig {
        database.withForcedTypes(
            org.jooq.meta.jaxb.ForcedType()
                .withUserType("java.time.Instant")
                .withIncludeTypes("TIMESTAMP\\ WITH\\ TIME\\ ZONE")
                .withConverter(
                    """
                    org.jooq.Converter.ofNullable(
                        java.time.OffsetDateTime.class,
                        java.time.Instant.class,
                        java.time.OffsetDateTime::toInstant,
                        instant ->
                            java.time.OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC))
                        """.trimIndent()
                )
        )
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
