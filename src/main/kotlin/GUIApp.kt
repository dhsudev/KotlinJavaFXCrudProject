import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Stage

class GUIApp : Application() {

    private var currentOffset = 0 // Para paginar la lista de clientes

    override fun start(stage: Stage) {
        val operationSelector = ComboBox<String>()
        operationSelector.items.addAll("Insertar", "Actualizar", "Eliminar", "Listar")
        operationSelector.value = "Insertar"

        // Campos reutilizables
        val idField = TextField().apply { promptText = "ID Cliente" }
        val firstNameField = TextField().apply { promptText = "Nombre" }
        val lastNameField = TextField().apply { promptText = "Apellido" }
        val emailField = TextField().apply { promptText = "Email" }
        val newEmailField = TextField().apply { promptText = "Nuevo Email" }

        val output = Label()
        val executeButton = Button("Ejecutar operación")

        val container = VBox(10.0)
        container.padding = Insets(20.0)

        // Función para actualizar los campos visibles según la operación
        fun updateForm(selected: String) {
            container.children.setAll(operationSelector)
            when (selected) {
                "Insertar" -> container.children.addAll(firstNameField, lastNameField, emailField, executeButton, output)
                "Actualizar" -> container.children.addAll(idField, newEmailField, executeButton, output)
                "Eliminar" -> container.children.addAll(idField, executeButton, output)
                "Listar" -> container.children.addAll(executeButton, output)
            }
        }

        // Acción del botón
        executeButton.setOnAction {
            when (operationSelector.value) {
                "Insertar" -> {
                    if (firstNameField.text.isNotBlank() && lastNameField.text.isNotBlank() && emailField.text.isNotBlank()) {
                        insertCustomer(firstNameField.text, lastNameField.text, emailField.text)
                        output.text = "✅ Cliente insertado correctamente."
                        firstNameField.clear(); lastNameField.clear(); emailField.clear()
                    } else {
                        output.text = "❌ Todos los campos son obligatorios."
                    }
                }
                "Actualizar" -> {
                    val id = idField.text.toIntOrNull()
                    if (id != null && newEmailField.text.isNotBlank()) {
                        updateCustomerEmail(id, newEmailField.text)
                        output.text = "✅ Email actualizado."
                        idField.clear(); newEmailField.clear()
                    } else {
                        output.text = "❌ ID inválido o email vacío."
                    }
                }
                "Eliminar" -> {
                    val id = idField.text.toIntOrNull()
                    if (id != null) {
                        deleteCustomer(id)
                        output.text = "✅ Cliente eliminado."
                        idField.clear()
                    } else {
                        output.text = "❌ ID inválido."
                    }
                }
                "Listar" -> {
                    output.text = listCustomers(offset = currentOffset)
                    currentOffset += 10
                }
            }
        }

        // Cambiar formulario al cambiar operación
        operationSelector.setOnAction {
            updateForm(operationSelector.value)
        }

        // Mostrar formulario inicial
        updateForm(operationSelector.value)

        // Mostrar ventana
        stage.scene = Scene(container, 420.0, 400.0)
        stage.title = "Gestión de Clientes (CRUD)"
        stage.show()
    }

    // Mostrar hasta 10 clientes en texto plano
    private fun listCustomers(offset: Int = 0): String {
        val sql = "SELECT customerid, firstname, lastname, email FROM customers ORDER BY customerid ASC LIMIT 10 OFFSET $offset"
        val builder = StringBuilder("📋 Clientes:\n")
        try {
            Database.getConnection()?.use { conn ->
                conn.createStatement().use { stmt ->
                    val rs = stmt.executeQuery(sql)
                    var found = false
                    while (rs.next()) {
                        found = true
                        builder.append("${rs.getInt("customerid")} - ${rs.getString("firstname")} ${rs.getString("lastname")} (${rs.getString("email")})\n")
                    }
                    if (!found) {
                        builder.append("No hay más clientes.\n")
                        currentOffset = 0 // Reiniciar si ya no hay más
                    }
                }
            }
        } catch (e: Exception) {
            return "❌ Error al consultar clientes: ${e.message}"
        }
        return builder.toString()
    }
}