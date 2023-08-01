import kotlin.io.path.absolutePathString

repositories {
    mavenCentral()
}

plugins {
    val kotlinVersion = "1.9.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.1"
    id("nu.studer.jooq") version "8.2"
}

group = "com.personio"

dependencies {
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

    jooqGenerator("org.postgresql:postgresql")
    jooqGenerator("org.jooq:jooq-meta-extensions")
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties = listOf(
                            org.jooq.meta.jaxb.Property().apply {
                                key = "scripts"
                                value = sourceSets.main.map {
                                    it.resources.sourceDirectories.singleFile
                                        .toPath()
                                        .resolve("db/migration/V0001__init.sql")
                                        .absolutePathString()
                                }.get()
                            },
                            org.jooq.meta.jaxb.Property().apply {
                                key = "defaultNameCase"
                                value = "lower"
                            }
                        )
                        forcedTypes.addAll(listOf(
                            org.jooq.meta.jaxb.ForcedType().apply {
                                userType = "java.time.Instant"
                                includeTypes = "TIMESTAMP\\ WITH\\ TIME\\ ZONE"
                                converter = """
                                    org.jooq.Converter.ofNullable(
                                        java.time.OffsetDateTime.class,
                                        java.time.Instant.class,
                                        java.time.OffsetDateTime::toInstant,
                                        instant ->
                                            java.time.OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC))
                                    """.trimIndent()
                            }
                        ))
                    }
                }
            }
        }
    }
}
tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") { allInputsDeclared.set(true) }

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
