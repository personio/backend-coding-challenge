package com.personio.reminders

import java.time.Clock
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * This class is a configuration class.
 * It informs Spring Framework that we will use the auto-configuration feature from Spring Boot.
 * It enables the task scheduler (similar to a cron job).
 */
@SpringBootApplication
@EnableScheduling
class Application {
    /**
     * This method exposes a UTC clock to the Spring Dependency Injection Framework.
     * This clock defines the service timezone as UTC.
     */
    @Bean
    fun clock(): Clock = Clock.systemUTC()
}

/**
 * Execute the main method from an IDE to start the service.
 */
fun main(args: Array<String>) {
    // We are starting the Spring Framework with its HTTP server/task scheduler
    SpringApplication.run(Application::class.java, *args)
}
