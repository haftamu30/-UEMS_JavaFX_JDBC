package employeemanagementsystem.controller;

import employeemanagementsystem.dao.Database;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddEmployeeController {

    // Basic Information Fields
    @FXML private TextField employeeIdField;
    @FXML private TextField joinDateField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    // Contact Information Fields
    @FXML private TextField altPhoneField;
    @FXML private DatePicker dobDatePicker;
    @FXML private TextField emergencyContactField;
    @FXML private TextField emergencyPhoneField;
    @FXML private TextArea addressTextArea;
    @FXML private TextField nationalityField;
    @FXML private TextField emergencyRelationshipField;

    // Employment Details
    @FXML private ComboBox<String> employeeTypeComboBox;
    @FXML private ComboBox<String> specializationComboBox;
    @FXML private ComboBox<String> contractTypeComboBox;
    @FXML private ComboBox<String> workScheduleComboBox;
    @FXML private TextField officeLocationField;
    @FXML private TextField supervisorField;
    @FXML private TextField teamField;
    @FXML private DatePicker hireDatePicker;

    // Qualifications
    @FXML private ComboBox<String> qualificationComboBox;
    @FXML private TextField fieldOfStudyField;
    @FXML private TextField universityField;
    @FXML private TextField graduationYearField;
    @FXML private TextArea certificationsTextArea;
    @FXML private TextArea skillsTextArea;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private TextField previousEmployerField;

    // Salary & Benefits
    @FXML private TextField basicSalaryField;
    @FXML private ComboBox<String> salaryGradeComboBox;
    @FXML private TextField allowancesField;
    @FXML private TextField deductionsField;
    @FXML private TextField bankNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField taxNumberField;
    @FXML private TextField pensionNumberField;
    @FXML private TextField insuranceNumberField;
    @FXML private TextField insuranceProviderField;

    // Documents & Photo
    @FXML private ImageView photoImageView;
    @FXML private Button uploadPhotoButton;
    @FXML private Button removePhotoButton;
    @FXML private Button uploadCVButton;
    @FXML private Button uploadDegreeButton;
    @FXML private Button uploadIDButton;
    @FXML private Button uploadContractButton;
    @FXML private Label cvFileNameLabel;
    @FXML private Label degreeFileNameLabel;
    @FXML private Label idFileNameLabel;
    @FXML private Label contractFileNameLabel;
    @FXML private TextArea notesTextArea;

    // Form Status
    @FXML private Label formStatusLabel;
    @FXML private ProgressBar formCompletionProgress;
    @FXML private Label completionPercentageLabel;
    @FXML private Label validationMessageLabel;

    // Search and Table
    @FXML private TextField searchField;
    @FXML private TableView<EmployeeData> employeeTableView;
    @FXML private TableColumn<EmployeeData, Integer> colEmployeeId;
    @FXML private TableColumn<EmployeeData, String> colUsername;
    @FXML private TableColumn<EmployeeData, String> colFullName;
    @FXML private TableColumn<EmployeeData, String> colGender;
    @FXML private TableColumn<EmployeeData, String> colPhoneNum;
    @FXML private TableColumn<EmployeeData, String> colPosition;
    @FXML private TableColumn<EmployeeData, String> colDepartment;
    @FXML private TableColumn<EmployeeData, String> colStatus;
    @FXML private TableColumn<EmployeeData, String> colJoinDate;

    // Action Buttons
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    @FXML private Button printButton;

    // Database connection
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // Photo path
    private String photoPath = "";

    @FXML
    public void initialize() {
        setupForm();
        loadEmployeeTable();
        setDefaultDates();
        setupFormListeners();
    }

    private void setupForm() {
        // Initialize gender combo box (only Male and Female)
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female"));

        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        // Initialize employee type
        employeeTypeComboBox.setItems(FXCollections.observableArrayList("Academic", "Administrative"));

        // Employee type listener to update positions and departments
        employeeTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePositionsAndDepartments(newVal);
        });

        // Initialize other combo boxes
        contractTypeComboBox.setItems(FXCollections.observableArrayList("Permanent", "Contract", "Part-time", "Temporary"));
        workScheduleComboBox.setItems(FXCollections.observableArrayList("Full-time", "Part-time", "Shift", "Flexible"));
        qualificationComboBox.setItems(FXCollections.observableArrayList("PhD", "MSc", "MA", "BSc", "BA", "Diploma", "Certificate"));
        salaryGradeComboBox.setItems(FXCollections.observableArrayList("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
                "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10"));

        // Set default values
        hireDatePicker.setValue(LocalDate.now());

        // Add action handlers
        uploadPhotoButton.setOnAction(e -> uploadPhoto());
        removePhotoButton.setOnAction(e -> removePhoto());
        saveButton.setOnAction(e -> saveEmployee());
        updateButton.setOnAction(e -> updateEmployee());
        clearButton.setOnAction(e -> clearForm());
        deleteButton.setOnAction(e -> deleteEmployee());

        // Initialize table columns
        setupTableColumns();
    }

    private void setDefaultDates() {
        // Set join date to current date (read-only)
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        joinDateField.setText(currentDate.format(formatter));

        // Set hire date to current date
        hireDatePicker.setValue(currentDate);

        // Set default date of birth (25 years ago)
        dobDatePicker.setValue(currentDate.minusYears(25));
    }

    private void updatePositionsAndDepartments(String employeeType) {
        positionComboBox.getItems().clear();
        departmentComboBox.getItems().clear();
        specializationComboBox.getItems().clear();

        if ("Academic".equals(employeeType)) {
            // Academic Positions
            positionComboBox.setItems(FXCollections.observableArrayList(
                    "Lecturer", "Researcher", "Teaching Assistant",
                    "Technical Assistant", "Lab Instructor", "Professor"
            ));

            // Academic Departments
            departmentComboBox.setItems(FXCollections.observableArrayList(
                    "Computer Science", "Mathematics", "Physics", "Chemistry",
                    "Biology", "Economics", "Electrical Engineering",
                    "Mechanical Engineering", "Civil Engineering", "Business Administration"
            ));

        } else if ("Administrative".equals(employeeType)) {
            // Administrative Positions
            positionComboBox.setItems(FXCollections.observableArrayList(
                    "HR Officer", "Finance Officer", "Registrar Officer",
                    "Legal Officer", "ICT Officer", "Network Engineer",
                    "Librarian", "Procurement Officer", "Maintenance Staff",
                    "Security Staff", "Driver"
            ));

            // Administrative Departments
            departmentComboBox.setItems(FXCollections.observableArrayList(
                    "Human Resources", "Finance", "Registrar Office",
                    "Legal Affairs", "ICT Services", "Library",
                    "Procurement", "General Services", "Research and Development",
                    "Quality Assurance"
            ));
        }

        // Update specializations based on position and department
        positionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSpecializations(newVal, departmentComboBox.getValue());
        });
        departmentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSpecializations(positionComboBox.getValue(), newVal);
        });
    }

    private void updateSpecializations(String position, String department) {
        specializationComboBox.getItems().clear();

        if (position != null && department != null) {
            switch (department) {
                case "Computer Science":
                    switch (position) {
                        case "Lecturer":
                        case "Professor":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Artificial Intelligence", "Data Science", "Software Engineering",
                                    "Networks & Security", "Database Systems", "Computer Architecture"
                            ));
                            break;
                        case "Researcher":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Machine Learning", "Computer Vision", "Natural Language Processing",
                                    "Robotics", "Big Data Analytics", "Computer Networks"
                            ));
                            break;
                        case "Teaching Assistant":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Programming", "Algorithms", "Data Structures",
                                    "Web Development", "Mobile Development", "Database Systems"
                            ));
                            break;
                    }
                    break;
                case "Electrical Engineering":
                     switch (position) {
                        case "Lecturer":
                        case "Professor":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Power Systems", "Control Systems", "Electronics",
                                    "Telecommunications", "Embedded Systems"
                            ));
                            break;
                        case "Researcher":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Renewable Energy", "Smart Grids", "Wireless Communications",
                                    "VLSI Design", "Signal Processing"
                            ));
                            break;
                    }
                    break;
                case "Human Resources":
                     switch (position) {
                        case "HR Officer":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Recruitment", "Employee Relations", "Training & Development",
                                    "Compensation & Benefits", "HR Analytics"
                            ));
                            break;
                    }
                    break;
                case "Finance":
                    switch (position) {
                        case "Finance Officer":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Accounting", "Financial Management", "Payroll Management",
                                    "Budgeting & Forecasting", "Auditing"
                            ));
                            break;
                    }
                    break;
                case "ICT Services":
                    switch (position) {
                        case "ICT Officer":
                        case "Network Engineer":
                            specializationComboBox.setItems(FXCollections.observableArrayList(
                                    "Networking", "System Administration", "Cybersecurity",
                                    "Cloud Computing", "IT Support"
                            ));
                            break;
                    }
                    break;
                default:
                    specializationComboBox.setItems(FXCollections.observableArrayList("General"));
            }
        }
    }

    private void setupFormListeners() {
        // Add validation listeners to required fields
        addValidationListener(usernameField);
        addValidationListener(fullNameField);
        addValidationListener(phoneField);
        addValidationListener(emailField);

        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
                emailField.setStyle("-fx-border-color: #e74c3c;");
            } else {
                emailField.setStyle("-fx-border-color: #27ae60;");
            }
            updateFormCompletion();
        });

        // Phone validation
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("^\\+2519\\d{8}$")) {
                phoneField.setStyle("-fx-border-color: #e74c3c;");
            } else {
                phoneField.setStyle("-fx-border-color: #27ae60;");
            }
            updateFormCompletion();
        });
    }

    private void addValidationListener(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                field.setStyle("-fx-border-color: #e74c3c;");
            } else {
                field.setStyle("-fx-border-color: #27ae60;");
            }
            updateFormCompletion();
        });
    }

    private void updateFormCompletion() {
        int totalFields = 7; // Your 7 required fields
        int completedFields = 0;

        // Check each required field
        if (usernameField.getText() != null && !usernameField.getText().trim().isEmpty()) completedFields++;
        if (fullNameField.getText() != null && !fullNameField.getText().trim().isEmpty()) completedFields++;
        if (genderComboBox.getValue() != null) completedFields++;
        if (phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) completedFields++;
        if (positionComboBox.getValue() != null) completedFields++;
        if (departmentComboBox.getValue() != null) completedFields++;
        if (statusComboBox.getValue() != null) completedFields++;

        double completionPercentage = (double) completedFields / totalFields;
        formCompletionProgress.setProgress(completionPercentage);
        completionPercentageLabel.setText(String.format("%.0f%% Complete", completionPercentage * 100));

        if (completedFields == totalFields) {
            formStatusLabel.setText("Complete");
            formStatusLabel.setStyle("-fx-text-fill: #27ae60;");
        } else {
            formStatusLabel.setText("Incomplete");
            formStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void setupTableColumns() {
        colEmployeeId.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty().asObject());
        colUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colFullName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        colGender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
        colPhoneNum.setCellValueFactory(cellData -> cellData.getValue().phoneNumProperty());
        colPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colDepartment.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colJoinDate.setCellValueFactory(cellData -> cellData.getValue().joinDateProperty());
    }

    private void loadEmployeeTable() {
        ObservableList<EmployeeData> employeeList = FXCollections.observableArrayList();
        String sql = "SELECT e.employee_id, e.username, e.fullName, e.gender, e.phoneNum, " +
                "e.position, e.department, e.employee_type, e.status, e.date " +
                "FROM employee e ORDER BY e.employee_id DESC";

        connect = Database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                EmployeeData employee = new EmployeeData(
                        result.getInt("employee_id"),
                        result.getString("username"),
                        result.getString("fullName"),
                        result.getString("gender"),
                        result.getString("phoneNum"),
                        result.getString("position"),
                        result.getString("department"),
                        result.getString("employee_type"),
                        result.getString("status"),
                        result.getString("date")
                );
                employeeList.add(employee);
            }

            employeeTableView.setItems(employeeList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load employee data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Employee Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            photoPath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            photoImageView.setImage(image);
        }
    }

    @FXML
    private void removePhoto() {
        photoImageView.setImage(null);
        photoPath = "";
    }

    @FXML
    private void saveEmployee() {
        if (!validateForm()) {
            showAlert("Validation Error", "Please fill all required fields correctly.", Alert.AlertType.ERROR);
            return;
        }

        String sql = "INSERT INTO employee (username, fullName, gender, phoneNum, position, " +
                "department, status, date, email, employee_type, specialization, " +
                "qualification, address, date_of_birth, emergency_contact, " +
                "emergency_phone, image, hire_date, salary_grade, basic_salary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set parameters
            prepare.setString(1, usernameField.getText());
            prepare.setString(2, fullNameField.getText());
            prepare.setString(3, genderComboBox.getValue());
            prepare.setString(4, phoneField.getText());
            prepare.setString(5, positionComboBox.getValue());
            prepare.setString(6, departmentComboBox.getValue());
            prepare.setString(7, statusComboBox.getValue());
            prepare.setString(8, joinDateField.getText()); // Current date
            prepare.setString(9, emailField.getText());
            prepare.setString(10, employeeTypeComboBox.getValue());
            prepare.setString(11, specializationComboBox.getValue());
            prepare.setString(12, qualificationComboBox.getValue());
            prepare.setString(13, addressTextArea.getText());

            // Date of birth
            if (dobDatePicker.getValue() != null) {
                prepare.setDate(14, Date.valueOf(dobDatePicker.getValue()));
            } else {
                prepare.setDate(14, null);
            }

            prepare.setString(15, emergencyContactField.getText());
            prepare.setString(16, emergencyPhoneField.getText());
            prepare.setString(17, photoPath);

            // Hire date
            if (hireDatePicker.getValue() != null) {
                prepare.setDate(18, Date.valueOf(hireDatePicker.getValue()));
            } else {
                prepare.setDate(18, Date.valueOf(LocalDate.now()));
            }

            prepare.setString(19, salaryGradeComboBox.getValue());

            // Basic salary
            if (!basicSalaryField.getText().isEmpty()) {
                prepare.setDouble(20, Double.parseDouble(basicSalaryField.getText()));
            } else {
                prepare.setDouble(20, 0.0);
            }

            int affectedRows = prepare.executeUpdate();

            if (affectedRows > 0) {
                // Get generated employee ID
                ResultSet generatedKeys = prepare.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);
                    employeeIdField.setText(String.valueOf(employeeId));

                    // Also add to employee_salary table
                    addEmployeeInfo(employeeId);
                }

                showAlert("Success", "Employee saved successfully!", Alert.AlertType.INFORMATION);
                clearForm();
                loadEmployeeTable();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save employee: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addEmployeeInfo(int employeeId) {
        String sql = "INSERT INTO employee_salary (employee_id, fullName, position, salary, date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, employeeId);
            prepare.setString(2, fullNameField.getText());
            prepare.setString(3, positionComboBox.getValue());

            // Set salary
            double salary = 0.0;
            if (!basicSalaryField.getText().isEmpty()) {
                salary = Double.parseDouble(basicSalaryField.getText());
            }
            prepare.setDouble(4, salary);
            prepare.setString(5, joinDateField.getText());

            prepare.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateEmployee() {
        if (employeeIdField.getText().isEmpty()) {
            showAlert("Error", "Please select an employee to update.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateForm()) {
            showAlert("Validation Error", "Please fill all required fields correctly.", Alert.AlertType.ERROR);
            return;
        }

        String sql = "UPDATE employee SET username = ?, fullName = ?, gender = ?, phoneNum = ?, " +
                "position = ?, department = ?, status = ?, email = ?, employee_type = ?, " +
                "specialization = ?, qualification = ?, address = ?, date_of_birth = ?, " +
                "emergency_contact = ?, emergency_phone = ?, image = ?, hire_date = ?, " +
                "salary_grade = ?, basic_salary = ? WHERE employee_id = ?";

        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);

            // Set parameters
            prepare.setString(1, usernameField.getText());
            prepare.setString(2, fullNameField.getText());
            prepare.setString(3, genderComboBox.getValue());
            prepare.setString(4, phoneField.getText());
            prepare.setString(5, positionComboBox.getValue());
            prepare.setString(6, departmentComboBox.getValue());
            prepare.setString(7, statusComboBox.getValue());
            prepare.setString(8, emailField.getText());
            prepare.setString(9, employeeTypeComboBox.getValue());
            prepare.setString(10, specializationComboBox.getValue());
            prepare.setString(11, qualificationComboBox.getValue());
            prepare.setString(12, addressTextArea.getText());

            // Date of birth
            if (dobDatePicker.getValue() != null) {
                prepare.setDate(13, Date.valueOf(dobDatePicker.getValue()));
            } else {
                prepare.setDate(13, null);
            }

            prepare.setString(14, emergencyContactField.getText());
            prepare.setString(15, emergencyPhoneField.getText());
            prepare.setString(16, photoPath);

            // Hire date
            if (hireDatePicker.getValue() != null) {
                prepare.setDate(17, Date.valueOf(hireDatePicker.getValue()));
            } else {
                prepare.setDate(17, Date.valueOf(LocalDate.now()));
            }

            prepare.setString(18, salaryGradeComboBox.getValue());

            // Basic salary
            if (!basicSalaryField.getText().isEmpty()) {
                prepare.setDouble(19, Double.parseDouble(basicSalaryField.getText()));
            } else {
                prepare.setDouble(19, 0.0);
            }

            prepare.setInt(20, Integer.parseInt(employeeIdField.getText()));

            int affectedRows = prepare.executeUpdate();

            if (affectedRows > 0) {
                // Update employee_salary table
                updateEmployeeInfo();
                showAlert("Success", "Employee updated successfully!", Alert.AlertType.INFORMATION);
                loadEmployeeTable();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update employee: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateEmployeeInfo() {
        String sql = "UPDATE employee_salary SET fullName = ?, position = ?, salary = ? WHERE employee_id = ?";

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, fullNameField.getText());
            prepare.setString(2, positionComboBox.getValue());

            // Set salary
            double salary = 0.0;
            if (!basicSalaryField.getText().isEmpty()) {
                salary = Double.parseDouble(basicSalaryField.getText());
            }
            prepare.setDouble(3, salary);
            prepare.setInt(4, Integer.parseInt(employeeIdField.getText()));

            prepare.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEmployee() {
        if (employeeIdField.getText().isEmpty()) {
            showAlert("Error", "Please select an employee to delete.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Employee");
        confirmAlert.setContentText("Are you sure you want to delete employee #" + employeeIdField.getText() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM employee WHERE employee_id = ?";

            try {
                connect = Database.connectDb();
                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, Integer.parseInt(employeeIdField.getText()));

                int affectedRows = prepare.executeUpdate();

                if (affectedRows > 0) {
                    showAlert("Success", "Employee deleted successfully!", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadEmployeeTable();
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete employee: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void clearForm() {
        // Clear all fields
        employeeIdField.clear();
        joinDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        usernameField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        fullNameField.clear();
        positionComboBox.getSelectionModel().clearSelection();
        genderComboBox.getSelectionModel().clearSelection();
        departmentComboBox.getSelectionModel().clearSelection();
        phoneField.clear();
        emailField.clear();
        altPhoneField.clear();
        dobDatePicker.setValue(LocalDate.now().minusYears(25));
        emergencyContactField.clear();
        emergencyPhoneField.clear();
        addressTextArea.clear();
        nationalityField.clear();
        emergencyRelationshipField.clear();
        employeeTypeComboBox.getSelectionModel().clearSelection();
        specializationComboBox.getSelectionModel().clearSelection();
        contractTypeComboBox.getSelectionModel().clearSelection();
        workScheduleComboBox.getSelectionModel().clearSelection();
        officeLocationField.clear();
        supervisorField.clear();
        teamField.clear();
        hireDatePicker.setValue(LocalDate.now());
        qualificationComboBox.getSelectionModel().clearSelection();
        fieldOfStudyField.clear();
        universityField.clear();
        graduationYearField.clear();
        certificationsTextArea.clear();
        skillsTextArea.clear();
        languagesField.clear();
        experienceField.clear();
        previousEmployerField.clear();
        basicSalaryField.clear();
        salaryGradeComboBox.getSelectionModel().clearSelection();
        allowancesField.clear();
        deductionsField.clear();
        bankNameField.clear();
        accountNumberField.clear();
        taxNumberField.clear();
        pensionNumberField.clear();
        insuranceNumberField.clear();
        insuranceProviderField.clear();
        photoImageView.setImage(null);
        photoPath = "";
        cvFileNameLabel.setText("No file selected");
        degreeFileNameLabel.setText("No file selected");
        idFileNameLabel.setText("No file selected");
        contractFileNameLabel.setText("No file selected");
        notesTextArea.clear();

        // Reset styles
        resetFieldStyles();

        // Reset form status
        formStatusLabel.setText("Incomplete");
        formStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        formCompletionProgress.setProgress(0);
        completionPercentageLabel.setText("0% Complete");
        validationMessageLabel.setText("");
    }



    private void resetFieldStyles() {
        usernameField.setStyle("");
        fullNameField.setStyle("");
        phoneField.setStyle("");
        emailField.setStyle("");
    }

    private boolean validateForm() {
        // Check required fields
        if (usernameField.getText().trim().isEmpty()) {
            validationMessageLabel.setText("Username is required");
            return false;
        }

        if (fullNameField.getText().trim().isEmpty()) {
            validationMessageLabel.setText("Full Name is required");
            return false;
        }

        if (genderComboBox.getValue() == null) {
            validationMessageLabel.setText("Gender is required");
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            validationMessageLabel.setText("Phone Number is required");
            return false;
        }

        if (!phoneField.getText().matches("^\\+2519\\d{8}$")) {
            validationMessageLabel.setText("Phone must be in format +2519xxxxxxxx");
            return false;
        }

        if (positionComboBox.getValue() == null) {
            validationMessageLabel.setText("Position is required");
            return false;
        }

        if (departmentComboBox.getValue() == null) {
            validationMessageLabel.setText("Department is required");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            validationMessageLabel.setText("Status is required");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            validationMessageLabel.setText("Email is required");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            validationMessageLabel.setText("Email must be @gmail.com");
            return false;
        }

        validationMessageLabel.setText("");
        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for employee table data
    public static class EmployeeData {
        private final IntegerProperty employeeId;
        private final StringProperty username;
        private final StringProperty fullName;
        private final StringProperty gender;
        private final StringProperty phoneNum;
        private final StringProperty position;
        private final StringProperty department;
        private final StringProperty employeeType;
        private final StringProperty status;
        private final StringProperty joinDate;

        public EmployeeData(int employeeId, String username, String fullName, String gender,
                            String phoneNum, String position, String department,
                            String employeeType, String status, String joinDate) {
            this.employeeId = new SimpleIntegerProperty(employeeId);
            this.username = new SimpleStringProperty(username);
            this.fullName = new SimpleStringProperty(fullName);
            this.gender = new SimpleStringProperty(gender);
            this.phoneNum = new SimpleStringProperty(phoneNum);
            this.position = new SimpleStringProperty(position);
            this.department = new SimpleStringProperty(department);
            this.employeeType = new SimpleStringProperty(employeeType);
            this.status = new SimpleStringProperty(status);
            this.joinDate = new SimpleStringProperty(joinDate);
        }

        // Property getters
        public IntegerProperty employeeIdProperty() { return employeeId; }
        public StringProperty usernameProperty() { return username; }
        public StringProperty fullNameProperty() { return fullName; }
        public StringProperty genderProperty() { return gender; }
        public StringProperty phoneNumProperty() { return phoneNum; }
        public StringProperty positionProperty() { return position; }
        public StringProperty departmentProperty() { return department; }
        public StringProperty employeeTypeProperty() { return employeeType; }
        public StringProperty statusProperty() { return status; }
        public StringProperty joinDateProperty() { return joinDate; }

        // Value getters
        public int getEmployeeId() { return employeeId.get(); }
        public String getUsername() { return username.get(); }
        public String getFullName() { return fullName.get(); }
        public String getGender() { return gender.get(); }
        public String getPhoneNum() { return phoneNum.get(); }
        public String getPosition() { return position.get(); }
        public String getDepartment() { return department.get(); }
        public String getEmployeeType() { return employeeType.get(); }
        public String getStatus() { return status.get(); }
        public String getJoinDate() { return joinDate.get(); }
    }
}