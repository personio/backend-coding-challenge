package com.personio.reminders.infra.postgres.container

import java.sql.*
import java.sql.Date
import java.time.Instant
import java.time.LocalDate
import java.util.*
import java.util.Objects.hash
import javax.sql.DataSource

/**
 * A facade allowing tests access to the database.
 *
 * Tests using this should be annotated with [PostgresTest]. This class can then be added as a class
 * constructor or method parameter.
 */
class TestDatabase(private val dataSource: DataSource) {
    /**
     * The [TimeZone] to be used when interacting with the database.
     *
     * All operations to store and retrieve data first set the time zone to this value before
     * interacting with the database.
     *
     * By default, the time zone is set to UTC.
     */
    var timeZone: TimeZone = TimeZone.getTimeZone("UTC")

    /**
     * Inserts the data given in [rows] into the table [tableName].
     *
     * The data in [rows] are a list of [Map] mapping column name to value. This method prepares a
     * statement inserting data for the columns which are the union of all keys found in all maps in
     * [rows].
     *
     * The following types are supported for values in each row:
     *
     *  * [Int], for numeric SQL types;
     *  * [String], for string SQL types;
     *  * [DateTime] and [Instant], for SQL timestamps;
     *  * [LocalDate], for SQL `date` type;
     *  * [JsonElement], for the PostgreSQL `json` type;
     *  * [List], for the SQL `ARRAY` type; and
     *  * any PostgreSQL objects.
     *
     * Missing and null values are set as `null` in the respective columns.
     *
     * Values of other types are ignored in the insert statements.
     */
    fun provisionTable(tableName: String, rows: List<Map<String, Any?>>) {
        val columns = identifyColumns(rows)
        val placeholders = (1..columns.size).joinToString(",") { "?" }
        dataSource.connection
            .use { connection ->
                connection.createStatement()
                    .use { statement -> statement.execute("set time zone '${timeZone.id}'") }
                val columnNames = columns.joinToString(",") { "\"${it.name}\"" }
                connection
                    .prepareStatement(
                        "insert into $tableName ($columnNames) values ($placeholders)"
                    )
                    .use { statement ->
                        for (row in rows) {
                            storeRowUsingStatement(statement, row, columns)
                        }
                    }
            }
    }

    private fun identifyColumns(rows: List<Map<String, Any?>>): List<ColumnSpec> {
        val result = mutableSetOf<ColumnSpec>()
        rows.forEach { row ->
            result.addAll(row.map { (key, value) -> ColumnSpec.from(key, value) })
        }
        return result.toList()
    }

    private data class ColumnSpec(val name: String, val typeCode: Int) {
        override fun equals(other: Any?): Boolean =
            if (other is ColumnSpec) {
                name == other.name
            } else {
                false
            }

        override fun hashCode(): Int = hash(name)

        companion object {
            fun from(name: String, sampleValue: Any?) = ColumnSpec(name, toTypeCode(sampleValue))

            private fun toTypeCode(sampleValue: Any?): Int {
                return when (sampleValue) {
                    is String -> Types.VARCHAR
                    is Int -> Types.INTEGER
                    is Long -> Types.BIGINT
                    is Instant -> Types.TIMESTAMP
                    is LocalDate -> Types.DATE
                    is List<*> -> Types.ARRAY
                    else -> Types.OTHER
                }
            }
        }
    }

    private fun storeRowUsingStatement(
        statement: PreparedStatement,
        row: Map<String, Any?>,
        columns: List<ColumnSpec>
    ) {
        for ((index, column) in columns.withIndex()) {
            if (row.containsKey(column.name)) {
                when (val columnValue = row[column.name]) {
                    null -> statement.setNull(index + 1, column.typeCode)
                    is String -> statement.setString(index + 1, columnValue)
                    is Int -> statement.setInt(index + 1, columnValue)
                    is Long -> statement.setLong(index + 1, columnValue)
                    is LocalDate -> statement.setDate(index + 1, Date.valueOf(columnValue))
                    is Instant ->
                        statement.setTimestamp(index + 1, Timestamp.from(columnValue), utcCalendar)
                    is List<*> -> statement.setStringArray(index + 1, columnValue)
                    else -> statement.setObject(index + 1, columnValue)
                }
            } else {
                statement.setNull(index + 1, column.typeCode)
            }
        }
        statement.execute()
    }

    private fun PreparedStatement.setStringArray(index: Int, columnValue: List<*>) {
        val valueAsStringList = columnValue.map { it.toString() }
        setObject(index, valueAsStringList.toTypedArray(), Types.ARRAY)
    }

    /**
     * Returns a list of all rows of the table [tableName].
     *
     * Each row is represented as a map whose keys are the column name and values are a string
     * representation of the column value.
     */
    fun fetchRowsOfTable(tableName: String): List<Map<String, String>> {
        dataSource.connection
            .use { connection ->
                connection.createStatement()
                    .use { statement -> statement.execute("set time zone '${timeZone.id}'") }
                connection.createStatement()
                    .use { statement ->
                        statement.execute("select * from $tableName")
                        statement.resultSet
                            .use { resultSet ->
                                val result = mutableListOf<Map<String, String>>()
                                while (resultSet.next()) {
                                    result.add(extractRow(resultSet))
                                }
                                return result
                            }
                    }
            }
    }

    private fun extractRow(resultSet: ResultSet): Map<String, String> {
        val row = mutableMapOf<String, String>()
        val metaData = resultSet.metaData
        for (i in 1..metaData.columnCount) {
            val columnValue = resultSet.getString(i)
            if (!resultSet.wasNull()) {
                row.put(metaData.getColumnName(i), columnValue)
            }
        }
        return row
    }

    /** Clears all content from the table with the name [tableName]. */
    fun clearTable(tableName: String) {
        dataSource.connection
            .use { connection ->
                connection.createStatement()
                    .use { statement -> statement.execute("delete from $tableName") }
            }
    }

    /**
     * Resets the counter which sets autoincremented values of the column [idColumnName] on the
     * table [tableName] to the given [initialValue].
     *
     * This uses the PostgreSQL convention for columns of type `SERIAL`. See
     * [PostgreSQL serial types documentation](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL)
     * for more information.
     *
     * Normally one applies this to ID columns between tests to ensure that autoincremented IDs are
     * assigned in a predictable way.
     */
    fun resetIdCounter(tableName: String, idColumnName: String = "id", initialValue: Int = 1) {
        dataSource.connection
            .use { connection ->
                connection.createStatement()
                    .use { statement ->
                        statement.execute(
                            "alter sequence ${tableName}_${idColumnName}_seq restart with " +
                                "$initialValue"
                        )
                    }
            }
    }

    companion object {
        private val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    }
}
