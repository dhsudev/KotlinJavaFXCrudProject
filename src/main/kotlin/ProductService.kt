import java.sql.*

fun insertProduct(category: Int, title: String, actor: String, price: Double, common_prod_id: Int) {
    val sql = "INSERT INTO products (category, title, actor, price, common_prod_id) VALUES (?, ?, ?, ?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, category)
            it.setString(2, title)
            it.setString(3, actor)
            it.setDouble(4, price)
            it.setInt(5, common_prod_id)
            it.executeUpdate()
            println("Producto insertado correctamente.")
        }
    }
}

fun listProducts() {
    val sql = "SELECT prod_id, title, price FROM products"
    Database.getConnection()?.use { conn ->
        conn.createStatement().use {
            val rs = it.executeQuery(sql)
            while (rs.next()) {
                println("${rs.getInt("prod_id")} - ${rs.getString("title")} ($${rs.getDouble("price")})")
            }
        }
    }
}

fun updateProductTitle(id: Int, newTitle: String) {
    val sql = "UPDATE products SET title = ? WHERE prod_id = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setString(1, newTitle)
            it.setInt(2, id)
            it.executeUpdate()
            println("Título del producto actualizado.")
        }
    }
}

fun deleteProduct(id: Int) {
    val sql = "DELETE FROM products WHERE prod_id = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, id)
            it.executeUpdate()
            println("Producto eliminado.")
        }
    }
}