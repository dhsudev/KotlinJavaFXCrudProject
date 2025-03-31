import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import services.persona.*
import java.sql.Date

class GUIApp : Application() {

    private var currentOffset = 0 // For pagination

    override fun start(stage: Stage) {
        val operationSelector = ComboBox<String>()
        operationSelector.items.addAll("Insertar", "Actualizar", "Eliminar", "Listar")
        operationSelector.value = "Insertar"

        // Reusable fields
        val dniField = TextField().apply { promptText = "DNI" }
        val firstNameField = TextField().apply { promptText = "Nombre" }
        val lastName1Field = TextField().apply { promptText = "Primer Apellido" }
        val lastName2Field = TextField().apply { promptText = "Segundo Apellido" }
        val birthDateField = TextField().apply { promptText = "Fecha de Nacimiento (YYYY-MM-DD)" }
        val phoneField = TextField().apply { promptText = "Teléfono" }
        val emailField = TextField().apply { promptText = "Email" }
        val roleSelector = ComboBox<String>().apply {
            items.addAll("Ninguno", "Metge", "Tecnic", "Pacient")
            value = "Ninguno"
        }

        val output = Label()
        val executeButton = Button("Ejecutar operación")

        val container = VBox(10.0)
        container.padding = Insets(20.0)

        // Function to update visible fields based on the selected operation
        fun updateForm(selected: String) {
            container.children.setAll(operationSelector)
            when (selected) {
                "Insertar", "Actualizar" -> {
                    container.children.addAll(dniField, firstNameField, lastName1Field, lastName2Field, birthDateField, phoneField, emailField, roleSelector, executeButton, output)
                }
                "Eliminar" -> {
                    container.children.addAll(dniField, executeButton, output)
                }
                "Listar" -> {
                    container.children.addAll(executeButton, output)
                }
            }
        }

        // Button action
        executeButton.setOnAction {
            when (operationSelector.value) {
                "Insertar" -> {
                    val dni = dniField.text.toInt()
                    val nom = firstNameField.text
                    val cognom1 = lastName1Field.text
                    val cognom2 = lastName2Field.text
                    val dataNaix = try {
                        Date.valueOf(birthDateField.text)
                    } catch (e: IllegalArgumentException) {
                        output.text = "Invalid date format. Please use YYYY-MM-DD."
                        return@setOnAction
                    }
                    val telefon = phoneField.text
                    val mail = emailField.text
                    val role = roleSelector.value
                    insertPersona(dni, nom, cognom1, cognom2, dataNaix, telefon, mail, role)
                    output.text = "Persona insertada correctamente."
                }
                "Actualizar" -> {
                    val dni = dniField.text.toInt()
                    val nom = firstNameField.text
                    val cognom1 = lastName1Field.text
                    val cognom2 = lastName2Field.text
                    val dataNaix = try {
                        Date.valueOf(birthDateField.text)
                    } catch (e: IllegalArgumentException) {
                        output.text = "Invalid date format. Please use YYYY-MM-DD."
                        return@setOnAction
                    }
                    val telefon = phoneField.text
                    val mail = emailField.text
                    val role = roleSelector.value
                    updatePersona(dni, nom, cognom1, cognom2, dataNaix, telefon, mail, role)
                    output.text = "Persona actualizada correctamente."
                }
                "Eliminar" -> {
                    val dni = dniField.text.toInt()
                    deletePersona(dni)
                    output.text = "Persona eliminada correctamente."
                }
                "Listar" -> {
                    output.text = listPersonas(currentOffset)
                }
            }
        }

        // Change form when operation changes
        operationSelector.setOnAction {
            updateForm(operationSelector.value)
        }

        // Show initial form
        updateForm(operationSelector.value)

        // Show window
        stage.scene = Scene(container, 420.0, 600.0)
        stage.title = "Gestión de Personas (CRUD)"
        stage.show()
    }
}