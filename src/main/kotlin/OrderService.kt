import java.sql.*

fun insertOrder(customerId: Int, netAmount: Double, tax: Double) {
    // Calcula el total sumando el neto + porcentaje del impuesto
    val total = netAmount + (netAmount * tax / 100)

    val sql = "INSERT INTO orders (orderdate, customerid, netamount, tax, totalamount) VALUES (current_date, ?, ?, ?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, customerId)       // ID del cliente
            it.setDouble(2, netAmount)     // Total neto
            it.setDouble(3, tax)           // Porcentaje de impuesto
            it.setDouble(4, total)         // Total calculado
            it.executeUpdate()
            println("Pedido registrado correctamente.")
        }
    }
}

fun listOrders() {
    val sql = "SELECT orderid, customerid, totalamount FROM orders"
    Database.getConnection()?.use { conn ->
        conn.createStatement().use {
            val rs = it.executeQuery(sql)
            while (rs.next()) {
                println("Pedido #${rs.getInt("orderid")} - Cliente: ${rs.getInt("customerid")} - Total: $${rs.getDouble("totalamount")}")
            }
        }
    }
}

fun updateOrderTotal(orderId: Int, newTotal: Double) {
    val sql = "UPDATE orders SET totalamount = ? WHERE orderid = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setDouble(1, newTotal)
            it.setInt(2, orderId)
            it.executeUpdate()
            println("Total del pedido actualizado.")
        }
    }
}

fun deleteOrder(orderId: Int) {
    val sql = "DELETE FROM orders WHERE orderid = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, orderId)
            it.executeUpdate()
            println("Pedido eliminado.")
        }
    }
}