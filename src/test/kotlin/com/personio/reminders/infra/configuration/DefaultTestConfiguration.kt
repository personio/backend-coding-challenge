package com.personio.reminders.infra.configuration

import javax.sql.DataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean

@TestConfiguration
class DefaultTestConfiguration {
    @MockBean
    lateinit var dataSource: DataSource
}
