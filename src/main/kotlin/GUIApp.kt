import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import controllers.PersonaController
import models.Role
import services.persona.PersonaService

class GUIApp : Application() {

    private val controller = PersonaController(PersonaService())
    private var currentOffset = 0 // For pagination

    override fun start(stage: Stage) {
        val operationSelector = createOperationSelector()
        val header = createHeader(operationSelector)
        val (labelsContainer, inputContainer) = createFormContainers()
        val output = Label()
        val executeButton = createExecuteButton(operationSelector, labelsContainer, inputContainer, output)
        val buttonContainer = createPaginationButtons(output)

        val app = VBox(10.0, header, HBox(10.0, labelsContainer, inputContainer), VBox(10.0, executeButton, output, buttonContainer))
        app.padding = Insets(20.0)
        app.alignment = Pos.CENTER

        stage.scene = Scene(app, 420.0, 600.0)
        stage.title = "Person Management (CRUD)"
        stage.show()

        updateForm(operationSelector.value, labelsContainer, inputContainer)
        operationSelector.setOnAction {
            updateForm(operationSelector.value, labelsContainer, inputContainer)
        }
    }

    private fun createOperationSelector(): ComboBox<String> {
        return ComboBox<String>().apply {
            items.addAll("Insert", "Update", "Delete", "List")
            value = "Insert"
        }
    }

    private fun createHeader(operationSelector: ComboBox<String>): HBox {
        return HBox(10.0, operationSelector).apply {
            alignment = Pos.CENTER
        }
    }

    private fun createFormContainers(): Pair<VBox, VBox> {
        val labelsContainer = VBox(10.0).apply { alignment = Pos.TOP_RIGHT }
        val inputContainer = VBox(10.0).apply { padding = Insets(20.0) }
        return Pair(labelsContainer, inputContainer)
    }

    private fun createExecuteButton(operationSelector: ComboBox<String>, labelsContainer: VBox, inputContainer: VBox, output: Label): Button {
        return Button("Execute Operation").apply {
            setOnAction {
                handleExecuteButtonAction(operationSelector.value, labelsContainer, inputContainer, output)
            }
        }
    }

    private fun createPaginationButtons(output: Label): HBox {
        val nextButton = Button("▶️")
        val prevButton = Button("️◀️")
        val buttonContainer = HBox(10.0, prevButton, nextButton).apply {
            padding = Insets(10.0)
            alignment = Pos.CENTER
        }

        prevButton.setOnAction {
            if (currentOffset > 0) currentOffset -= 10
            output.text = controller.listPersonas(currentOffset)
        }

        nextButton.setOnAction {
            currentOffset += 10
            output.text = controller.listPersonas(currentOffset)
        }

        return buttonContainer
    }

    private fun updateForm(selected: String, labelsContainer: VBox, inputContainer: VBox) {
        labelsContainer.children.clear()
        inputContainer.children.clear()

        val dniField = TextField().apply { promptText = "DNI" }
        val firstNameField = TextField().apply { promptText = "First Name" }
        val lastName1Field = TextField().apply { promptText = "Last Name 1" }
        val lastName2Field = TextField().apply { promptText = "Last Name 2" }
        val birthDateField = TextField().apply { promptText = "Birth Date (YYYY-MM-DD)" }
        val phoneField = TextField().apply { promptText = "Phone" }
        val emailField = TextField().apply { promptText = "Email" }
        val roleSelector = ComboBox<String>().apply {
            items.addAll(Role.NONE, Role.DOCTOR, Role.TECHNICIAN, Role.PATIENT)
            value = Role.NONE
        }

        when (selected) {
            "Insert", "Update" -> {
                labelsContainer.children.addAll(Label("Dni: "), Label("Name :"), Label("First lastName: "), Label("Second lastName: "), Label("Birth date: "), Label("Phone: "), Label("Email: "), Label("Role: "))
                inputContainer.children.addAll(dniField, firstNameField, lastName1Field, lastName2Field, birthDateField, phoneField, emailField, roleSelector)
            }
            "Delete" -> {
                labelsContainer.children.add(Label("Dni: "))
                inputContainer.children.add(dniField)
            }
            "List" -> {
                labelsContainer.children.add(Label("Role: "))
                inputContainer.children.add(roleSelector)
            }
        }
    }

    private fun handleExecuteButtonAction(selected: String, labelsContainer: VBox, inputContainer: VBox, output: Label) {
        val dniField = inputContainer.children[0] as TextField
        val dni = dniField.text.toInt()

        when (selected) {
            "Insert", "Update" -> {
                val name = (inputContainer.children[1] as TextField).text
                val surname1 = (inputContainer.children[2] as TextField).text
                val surname2 = (inputContainer.children[3] as TextField).text
                val birthDate = (inputContainer.children[4] as TextField).text
                val phone = (inputContainer.children[5] as TextField).text
                val email = (inputContainer.children[6] as TextField).text
                val role = (inputContainer.children[7] as ComboBox<String>).value

                output.text = if (selected == "Update") {
                    controller.updatePersona(dni, name, surname1, surname2, birthDate, phone, email, role)
                } else {
                    controller.insertPersona(dni, name, surname1, surname2, birthDate, phone, email, role)
                }
            }
            "Delete" -> {
                output.text = controller.deletePersona(dni)
            }
            "List" -> {
                val role = (inputContainer.children[0] as ComboBox<String>).value.lowercase()
                output.text = controller.listPersonas(currentOffset, if(role == "None") "persona" else role)
            }
        }
    }
}