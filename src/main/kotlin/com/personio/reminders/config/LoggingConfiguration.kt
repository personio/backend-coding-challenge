package com.personio.reminders.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class LoggingConfiguration {

    @Bean
    @Scope("prototype")
    fun logger(injectionPoint: InjectionPoint): Logger {
        val methodParameterClass = injectionPoint.methodParameter?.containingClass
        val fieldClass = injectionPoint.field?.declaringClass
        val targetClass = methodParameterClass ?: fieldClass
        return LoggerFactory.getLogger(targetClass)
    }
}
