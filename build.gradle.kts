import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.revolut.jooq-docker") version "0.3.3"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "com.personio"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
    testImplementation("io.kotest:kotest-assertions-json:5.0.3")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("org.json:json:20220320")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    val testContainersVersion = "1.17.2"
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    val exposedVersion = "0.37.3"
    testImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    jdbc("org.postgresql:postgresql:42.4.0")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin", "build/generated-jooq")
}

tasks {
    generateJooqClasses {
        customizeGenerator {
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
                                    java.time.OffsetDateTime.ofInstant(
                                        instant, java.time.ZoneId.of("UTC")))
                        """.trimIndent()
                    )
            )
        }
    }
}

configurations.testImplementation {
    // prevent multiple occurrences of org.json.JSONObject on the class path
    exclude("com.vaadin.external.google", "android-json")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
        excludeEngines("junit-vintage")
    }
}
