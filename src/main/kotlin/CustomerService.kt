import java.sql.*

fun insertCustomer(firstName: String, lastName: String, email: String) {
    val sql = """
        INSERT INTO customers (firstname, lastname, email, address1, city, country, region, creditcardtype, creditcard, creditcardexpiration, username, "password")
        VALUES (?, ?, ?, 'Calle Falsa 123', 'Ciudad', 'País', 1, 1, '1234567890123456', '12/25', ?, ?)
    """.trimIndent()

    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setString(1, firstName)
            it.setString(2, lastName)
            it.setString(3, email)
            it.setString(4, firstName.lowercase())
            it.setString(5, "pass123")
            it.executeUpdate()
            println("Cliente insertado correctamente.")
        }
    }
}

fun listCustomers() {
    val sql = "SELECT customerid, firstname, lastname, email FROM customers"
    Database.getConnection()?.use { conn ->
        conn.createStatement().use {
            val rs = it.executeQuery(sql)
            while (rs.next()) {
                println("${rs.getInt("customerid")} - ${rs.getString("firstname")} ${rs.getString("lastname")} (${rs.getString("email")})")
            }
        }
    }
}

fun updateCustomerEmail(id: Int, newEmail: String) {
    val sql = "UPDATE customers SET email = ? WHERE customerid = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setString(1, newEmail)
            it.setInt(2, id)
            it.executeUpdate()
            println("Email del cliente actualizado.")
        }
    }
}

fun deleteCustomer(id: Int) {
    val sql = "DELETE FROM customers WHERE customerid = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
            println("Cliente eliminado.")
        }
    }
}