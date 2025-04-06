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
import models.Persona
import models.Role
import services.persona.PersonaService
import java.sql.Date

class GUIApp : Application() {

    private val controller = PersonaController(PersonaService())
    private var currentOffset = 0
    private var maxPages = 0

    override fun start(stage: Stage) {
        val operationSelector = createOperationSelector()
        val header = createHeader(operationSelector)
        val (labelsContainer, inputContainer) = createFormContainers()
        val output = Label()
        val buttonContainer = createPaginationButtons(operationSelector, labelsContainer, inputContainer, output)
        val executeButton = createExecuteButton(operationSelector, labelsContainer, inputContainer, output, buttonContainer)

        val app = VBox(
            10.0,
            header,
            HBox(10.0, labelsContainer, inputContainer),
            VBox(10.0, executeButton.apply { alignment = Pos.CENTER }, output, buttonContainer)
        )
        app.padding = Insets(20.0)
        app.alignment = Pos.CENTER

        stage.scene = Scene(app, 420.0, 600.0)
        stage.title = "CRUD App for Medical Center"
        stage.show()

        updateForm(operationSelector.value, labelsContainer, inputContainer)
        operationSelector.setOnAction {
            println("SetOnAction de operationSelector")
            updateForm(operationSelector.value, labelsContainer, inputContainer)
            currentOffset = 0
            buttonContainer.isVisible = operationSelector.value == "List"
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

    private fun createExecuteButton(
        operationSelector: ComboBox<String>,
        labelsContainer: VBox,
        inputContainer: VBox,
        output: Label,
        buttonContainer: HBox
    ): Button {
        return Button("Execute Operation").apply {
            setOnAction {
                handleExecution(operationSelector.value, labelsContainer, inputContainer, output)
                buttonContainer.isVisible = operationSelector.value == "List"
            }
        }
    }

    private fun createPaginationButtons(operationSelector: ComboBox<String>,
                                        labelsContainer: VBox,
                                        inputContainer: VBox,
                                        output: Label): HBox {
        val nextButton = Button("▶️")
        val prevButton = Button("️◀️")
        val buttonContainer = HBox(10.0, prevButton, nextButton).apply {
            padding = Insets(10.0)
            alignment = Pos.CENTER
            isVisible = false // Initially hidden
        }

        prevButton.setOnAction {
            if (currentOffset - 10 >= 0) currentOffset -= 10
            handleExecution(operationSelector.value, labelsContainer, inputContainer, output)
        }
        nextButton.setOnAction {
            println("currentOffset: $currentOffset maxPages: $maxPages")
            if(currentOffset + 10 <= maxPages) {
                println("updatedOffset: $currentOffset")
                currentOffset += 10
            }
            handleExecution(operationSelector.value, labelsContainer, inputContainer, output)
        }
        return buttonContainer
    }


    private fun createLabeledField(labelText: String, inputControl: javafx.scene.Node): HBox {
        val label = Label(labelText).apply {
            minWidth = 100.0
            alignment = Pos.CENTER_RIGHT
        }
        val hbox = HBox(10.0, label, inputControl)
        hbox.alignment = Pos.CENTER_LEFT
        return hbox
    }
    private fun updateForm(selected: String, labelsContainer: VBox, inputContainer: VBox, selectedRole : String = Role.NONE_EN) {
        labelsContainer.children.clear()
        inputContainer.children.clear()

        val dniField = TextField().apply { promptText = "DNI" }
        val firstNameField = TextField().apply { promptText = "First Name" }
        val lastName1Field = TextField().apply { promptText = "Last Name 1" }
        val lastName2Field = TextField().apply { promptText = "Last Name 2" }
        val birthDateField = TextField().apply { promptText = "YYYY-MM-DD" }
        val phoneField = TextField().apply { promptText = "Phone" }
        val emailField = TextField().apply { promptText = "Email" }
        val roleSelector = ComboBox<String>().apply {
            items.addAll(Role.NONE_EN, Role.DOCTOR_EN, Role.TECHNICIAN_EN, Role.PATIENT_EN)
            value = selectedRole
        }

        roleSelector.setOnAction {
            currentOffset = 0
            maxPages = controller.getRegistersCount(roleSelector.value)
            println("SetOnAction de roleSelector")
            updateForm(selected, labelsContainer, inputContainer, roleSelector.value) // Update form on role change
        }

        val additionalFields = when (roleSelector.value) {
            Role.DOCTOR_EN -> {
                val speciality = TextField().apply { promptText = "Speciality" }
                listOf(createLabeledField("Speciality: ", speciality))
            }
            Role.PATIENT_EN -> {
                val nss = TextField().apply { promptText = "NSS" }
                val gender = TextField().apply { promptText = "Gender" }
                listOf(
                    createLabeledField("NSS: ", nss),
                    createLabeledField("Gender: ", gender)
                )
            }
            Role.TECHNICIAN_EN -> {
                val startDate = TextField().apply { promptText = "Start Date (YYYY-MM-DD)" }
                listOf(createLabeledField("Start Date: ", startDate))
            }
            else -> emptyList()
        }

        when (selected) {
            "Insert", "Update" -> {
                inputContainer.children.addAll(
                    createLabeledField("Dni: ", dniField),
                    createLabeledField("First Name: ", firstNameField),
                    createLabeledField("Last Name 1: ", lastName1Field),
                    createLabeledField("Last Name 2: ", lastName2Field),
                    createLabeledField("Birth Date: ", birthDateField),
                    createLabeledField("Phone: ", phoneField),
                    createLabeledField("Email: ", emailField),
                    createLabeledField("Role: ", roleSelector),
                )
                additionalFields.forEach { inputContainer.children.add(it) }
            }

            "Delete" -> {
                inputContainer.children.add(createLabeledField("Dni: ", dniField))
            }

            "List" -> {
                inputContainer.children.add(createLabeledField("Role: ", roleSelector))
            }
        }
    }
    private fun handleExecution(
        selected: String,
        labelsContainer: VBox,
        inputContainer: VBox,
        output: Label
    ) {
        when (selected) {
            "Insert", "Update" -> {
                val dni = (inputContainer.children[0] as HBox).children[1] as TextField
                val name = (inputContainer.children[1] as HBox).children[1] as TextField
                val surname1 = (inputContainer.children[2] as HBox).children[1] as TextField
                val surname2 = (inputContainer.children[3] as HBox).children[1] as TextField
                val birthDate = (inputContainer.children[4] as HBox).children[1] as TextField
                val phone = (inputContainer.children[5] as HBox).children[1] as TextField
                val email = (inputContainer.children[6] as HBox).children[1] as TextField
                val role = (inputContainer.children[7] as HBox).children[1] as ComboBox<String>

                val additionalData = mutableMapOf<String, Any>()
                when (role.value) {
                    Role.DOCTOR_EN -> {
                        val speciality = (inputContainer.children[8] as HBox).children[1] as TextField
                        additionalData["especialitat"] = speciality.text
                    }
                    Role.PATIENT_EN -> {
                        val nss = (inputContainer.children[8] as HBox).children[1] as TextField
                        val gender = (inputContainer.children[9] as HBox).children[1] as TextField
                        additionalData["nss"] = nss.text
                        additionalData["genere"] = gender.text[0]
                    }
                    Role.TECHNICIAN_EN -> {
                        val startDate = (inputContainer.children[8] as HBox).children[1] as TextField
                        additionalData["data_inici"] = Date.valueOf(startDate.text)
                    }
                }

                output.text = if (selected == "Update") {
                    controller.updatePersona(
                        dni.text.toInt(),
                        name.text,
                        surname1.text,
                        surname2.text,
                        birthDate.text,
                        phone.text,
                        email.text,
                        role.value,
                        additionalData
                    )
                } else {
                    controller.insertPersona(
                        dni.text.toInt(),
                        name.text,
                        surname1.text,
                        surname2.text,
                        birthDate.text,
                        phone.text,
                        email.text,
                        role.value,
                        additionalData
                    )
                }
            }

            "Delete" -> {
                val dni = (inputContainer.children[0] as HBox).children[1] as TextField
                output.text = controller.deletePersona(dni.text.toInt())
            }

            "List" -> {
                val role = (inputContainer.children[0] as HBox).children[1] as ComboBox<String>
                output.text = formatList(controller.listPersonas(
                    currentOffset,
                    Role.toCatalan(role.value).lowercase()
                ))
            }
        }
    }
    private fun formatList(personas: List<Persona>): String{
        var result = "Llista de ${personas[0].role}s:\n"
        result += (personas.joinToString("\n") { it.toString() })
        return result
    }
}

