import dev.monosoul.jooq.RecommendedVersions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    id("org.springframework.boot") version "2.7.11"
    id("io.spring.dependency-management") version "1.1.0"
    id("dev.monosoul.jooq-docker") version "3.0.18"

    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

group = "com.personio"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

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
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.18.1"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    jooqCodegen("org.postgresql:postgresql")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin", "build/generated-jooq")
}

tasks {
    generateJooqClasses {
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
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
        excludeEngines("junit-vintage")
    }
}
