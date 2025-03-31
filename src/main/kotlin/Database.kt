import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
    private const val URL = "jdbc:postgresql://localhost:5432/ds2"
    private const val USER = "postgres"
    private const val PASSWORD = "lua"

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            println("Error al conectar: ${e.message}")
            null
        }
    }
}