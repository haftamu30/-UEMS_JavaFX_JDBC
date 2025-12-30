package employeemanagementsystem.controller;

import employeemanagementsystem.model.EmployeeData;
import employeemanagementsystem.model.GetData;
import employeemanagementsystem.service.EmployeeService;
import javafx.application.Platform;
import employeemanagementsystem.service.EmployeeTypeService;
import employeemanagementsystem.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    // FXML Injections
    @FXML private AnchorPane main_form;
    @FXML private Button close, minimize, logout;
    @FXML private Label username;
    @FXML private Button home_btn, addEmployee_btn, salary_btn;
    @FXML private AnchorPane home_form, addEmployee_form, salary_form;
    @FXML private Label home_totalEmployees, home_totalPresents, home_totalInactiveEm;
    @FXML private BarChart<String, Number> home_chart;

    // Add Employee Form
    @FXML private TableView<EmployeeData> addEmployee_tableView;
    @FXML private TableColumn<EmployeeData, Integer> addEmployee_col_employeeID;
    @FXML private TableColumn<EmployeeData, String> addEmployee_col_username, addEmployee_col_fullName;
    @FXML private TableColumn<EmployeeData, String> addEmployee_col_gender, addEmployee_col_phoneNum;
    @FXML private TableColumn<EmployeeData, String> addEmployee_col_position, addEmployee_col_date;
    @FXML private TableColumn<EmployeeData, String> addEmployee_col_department, addEmployee_col_status;
    @FXML private TextField addEmployee_search;
    @FXML private TextField addEmployee_employeeID, addEmployee_username, addEmployee_fullName;
    @FXML private TextField addEmployee_phoneNum, addEmployee_email, addEmployee_emergencyContact;
    @FXML private ComboBox<String> addEmployee_address;
    @FXML private ComboBox<String> addEmployee_gender, addEmployee_position, addEmployee_status;
    @FXML private ComboBox<String> addEmployee_qualification, addEmployee_specialization;
    @FXML private ComboBox<String> addEmployee_department, addEmployee_type;
    @FXML private ComboBox<String> addEmployee_dob_day, addEmployee_dob_month, addEmployee_dob_year;
    @FXML private DatePicker addEmployee_hireDate;
    @FXML private ImageView addEmployee_image;
    @FXML private Button addEmployee_importBtn, addEmployee_addBtn, addEmployee_updateBtn;
    @FXML private Button addEmployee_deleteBtn, addEmployee_clearBtn, addEmployee_refreshBtn;

    // Salary Form
    @FXML private TextField salary_employeeID, salary_salary;
    @FXML private Label salary_fullName, salary_position;
    @FXML private Button salary_updateBtn, salary_clearBtn;
    @FXML private TableView<EmployeeData> salary_tableView;
    @FXML private TableColumn<EmployeeData, Integer> salary_col_employeeID;
    @FXML private TableColumn<EmployeeData, String> salary_col_fullName, salary_col_position;
    @FXML private TableColumn<EmployeeData, Double> salary_col_salary;

    // Services
    private EmployeeService employeeService = new EmployeeService();
    private EmployeeTypeService employeeTypeService = new EmployeeTypeService();

    // Data
    private ObservableList<EmployeeData> addEmployeeList = FXCollections.observableArrayList();
    private ObservableList<EmployeeData> salaryList;
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;
    private Image image;
    private double x = 0;
    private double y = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            displayUsername();
            defaultNav();

            // Initialize TableView columns first
            initializeTableView();

            // Home
            homeTotalEmployees();
            homeEmployeeTotalPresent();
            homeTotalInactive();
            homeChart();

            // Add Employee
            initializeComboBoxes();
            addEmployeeShowListData();
            addEmployeeSearch(); // Setup live search
            addEmployee_hireDate.setValue(LocalDate.now());
            setupValidationListeners();

            // Salary
            salaryShowListData();
            
            // Make sure the table is visible
            Platform.runLater(() -> {
                addEmployee_form.setVisible(true);
                addEmployee_form.toFront();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Error initializing application", e.getMessage());
        }
    }
    
    private void initializeTableView() {
        // Set up cell value factories
        addEmployee_col_employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        addEmployee_col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        addEmployee_col_fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        addEmployee_col_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addEmployee_col_phoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        addEmployee_col_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        addEmployee_col_department.setCellValueFactory(new PropertyValueFactory<>("department"));
        addEmployee_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        addEmployee_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Format the employee ID to show leading zeros
        addEmployee_col_employeeID.setCellFactory(column -> new TableCell<EmployeeData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%04d", item));
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
                }
            }
        });
        
        // Center align all columns
        for (TableColumn<EmployeeData, ?> column : addEmployee_tableView.getColumns()) {
            column.setStyle("-fx-alignment: CENTER;");
        }
        
        // Set row factory to highlight rows on hover
        addEmployee_tableView.setRowFactory(tv -> {
            TableRow<EmployeeData> row = new TableRow<>();
            row.hoverProperty().addListener((obs) -> {
                if (row.isHover()) {
                    row.setStyle("-fx-background-color: #e3f2fd;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    // ============ HOME METHODS ============
    public void homeTotalEmployees() {
        String sql = "SELECT COUNT(employee_id) FROM employee";
        connect = employeemanagementsystem.dao.Database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            if (result.next()) {
                home_totalEmployees.setText(String.valueOf(result.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeEmployeeTotalPresent() {
        String sql = "SELECT COUNT(id) FROM employee_salary WHERE salary > 0";
        connect = employeemanagementsystem.dao.Database.connectDb();
        try {
            statement = connect.createStatement();
            result = statement.executeQuery(sql);
            if (result.next()) {
                home_totalPresents.setText(String.valueOf(result.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeTotalInactive() {
        String sql = "SELECT COUNT(id) FROM employee_salary WHERE salary = 0";
        connect = employeemanagementsystem.dao.Database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            if (result.next()) {
                home_totalInactiveEm.setText(String.valueOf(result.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeChart() {
        home_chart.getData().clear();
        String sql = "SELECT date, COUNT(employee_id) FROM employee GROUP BY date ORDER BY date ASC LIMIT 7";
        connect = employeemanagementsystem.dao.Database.connectDb();

        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                series.getData().add(new XYChart.Data<>(result.getString(1), result.getInt(2)));
            }

            home_chart.getData().add(series);
            series.setName("Employees Joined");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============ ADD EMPLOYEE METHODS ============
    private void initializeComboBoxes() {
        // Gender
        ObservableList<String> genders = FXCollections.observableArrayList("Male", "Female");
        addEmployee_gender.setItems(genders);

        // Employee Type
        ObservableList<String> employeeTypes = FXCollections.observableArrayList("Academic", "Administrative");
        addEmployee_type.setItems(employeeTypes);

        // Status
        ObservableList<String> statuses = FXCollections.observableArrayList("Active", "Inactive");
        addEmployee_status.setItems(statuses);

        // Address (Ethiopian Cities)
        ObservableList<String> cities = FXCollections.observableArrayList(
            "Addis Ababa", "Dire Dawa", "Mekelle", "Gondar", "Bahir Dar", "Hawassa", "Jimma", "Jijiga",
            "Shashamane", "Arba Minch", "Hosaena", "Dilla", "Nekemte", "Debre Birhan", "Asella", "Debre Markos",
            "Kombolcha", "Dessie", "Adama", "Bishoftu", "Harar", "Sodo", "Gambela", "Axum", "Adigrat", "Woldia",
            "Wolaita Sodo", "Ambo", "Bule Hora", "Robe", "Ziway", "Butajira", "Goba", "Assosa", "Semera"
        );
        addEmployee_address.setItems(cities);
        
        // Enable autocomplete for address ComboBox
        addEmployee_address.setEditable(true);
        addEmployee_address.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Filter items that contain the new value (case-insensitive)
                List<String> filtered = cities.stream()
                    .filter(city -> city.toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
                
                if (!filtered.isEmpty()) {
                    addEmployee_address.setItems(FXCollections.observableArrayList(filtered));
                    addEmployee_address.show();
                } else {
                    addEmployee_address.hide();
                }
            } else {
                addEmployee_address.setItems(cities);
                addEmployee_address.hide();
            }
        });
        
        // Hide popup when focus is lost
        addEmployee_address.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                addEmployee_address.hide();
            }
        });
        
        // Handle selection
        addEmployee_address.setOnAction(event -> {
            String selected = addEmployee_address.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addEmployee_address.getEditor().setText(selected);
            }
        });

        // Set listeners
        addEmployee_type.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePositionsBasedOnType(newVal);
        });

        addEmployee_position.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateQualificationsBasedOnPosition(newVal);
        });

        addEmployee_department.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSpecializationsBasedOnDepartmentAndPosition(newVal, addEmployee_position.getValue());
        });

        // Initialize date of birth ComboBoxes
        ObservableList<String> days = FXCollections.observableArrayList();
        for (int i = 1; i <= 31; i++) {
            days.add(String.format("%02d", i));
        }
        addEmployee_dob_day.setItems(days);

        ObservableList<String> months = FXCollections.observableArrayList(
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        );
        addEmployee_dob_month.setItems(months);

        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 18; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        addEmployee_dob_year.setItems(years);
    }

    private void updatePositionsBasedOnType(String employeeType) {
        if (employeeType == null) return;

        List<String> positions = new ArrayList<>();
        if (employeeType.equals("Academic")) {
            positions.addAll(Arrays.asList("Lecturer", "Researcher", "Teaching Assistant",
                    "Technical Assistant", "Lab Instructor", "Professor"));
        } else if (employeeType.equals("Administrative")) {
            positions.addAll(Arrays.asList("HR Officer", "Finance Officer", "Registrar Officer",
                    "Legal Officer", "ICT Officer", "Network Engineer",
                    "Librarian", "Procurement Officer", "Maintenance Staff",
                    "Security Staff", "Driver"));
        }

        addEmployee_position.setItems(FXCollections.observableArrayList(positions));
        addEmployee_department.getItems().clear();
        addEmployee_qualification.getItems().clear();
        addEmployee_specialization.getItems().clear();
    }

    private void updateQualificationsBasedOnPosition(String position) {
        if (position == null) return;

        Map<String, List<String>> qualificationsMap = employeeTypeService.getQualificationsByPosition(position);
        List<String> qualifications = qualificationsMap.get(position);

        if (qualifications != null) {
            addEmployee_qualification.setItems(FXCollections.observableArrayList(qualifications));
        }

        // Update departments based on position
        updateDepartmentsBasedOnPosition(position);
    }

    private void updateDepartmentsBasedOnPosition(String position) {
        if (position == null || addEmployee_type.getValue() == null) return;

        List<String> departments = new ArrayList<>();
        if (addEmployee_type.getValue().equals("Academic")) {
            Map<String, List<String>> academicPositions = employeeTypeService.getAcademicPositions();
            departments = academicPositions.get(position);
        } else {
            Map<String, List<String>> adminPositions = employeeTypeService.getAdministrativePositions();
            departments = adminPositions.get(position);
        }

        if (departments != null) {
            addEmployee_department.setItems(FXCollections.observableArrayList(departments));
        }
    }

    private void updateSpecializationsBasedOnDepartmentAndPosition(String department, String position) {
        if (department == null || position == null) return;

        Map<String, List<String>> specializationsMap =
                employeeTypeService.getSpecializationsByDepartmentAndPosition(department, position);
        String key = department + "_" + position;
        List<String> specializations = specializationsMap.get(key);

        if (specializations != null) {
            addEmployee_specialization.setItems(FXCollections.observableArrayList(specializations));
        }
    }

    @FXML
    public void addEmployee() {
        // Validation
        if (!validateEmployeeForm()) return;

        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "INSERT INTO employee (username, fullName, gender, phoneNum, position, "
                + "email, image, date, status, address, qualification, specialization, "
                + "department, employee_type, date_of_birth, emergency_contact, hire_date) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        connect = employeemanagementsystem.dao.Database.connectDb();

        try {
            prepare = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setString(1, addEmployee_username.getText());
            prepare.setString(2, addEmployee_fullName.getText());
            prepare.setString(3, addEmployee_gender.getValue());
            prepare.setString(4, addEmployee_phoneNum.getText());
            prepare.setString(5, addEmployee_position.getValue());
            prepare.setString(6, addEmployee_email.getText());

            String uri = GetData.path != null ? GetData.path.replace("\\", "\\\\") : null;
            prepare.setString(7, uri);

            prepare.setString(8, String.valueOf(sqlDate));
            prepare.setString(9, addEmployee_status.getValue());
            prepare.setString(10, addEmployee_address.getValue());
            prepare.setString(11, addEmployee_qualification.getValue());
            prepare.setString(12, addEmployee_specialization.getValue());
            prepare.setString(13, addEmployee_department.getValue());
            prepare.setString(14, addEmployee_type.getValue());

            // Get date of birth from ComboBox components
            LocalDate dob = null;
            if (addEmployee_dob_day.getValue() != null && addEmployee_dob_month.getValue() != null && addEmployee_dob_year.getValue() != null) {
                try {
                    int day = Integer.parseInt(addEmployee_dob_day.getValue());
                    String monthStr = addEmployee_dob_month.getValue();
                    int year = Integer.parseInt(addEmployee_dob_year.getValue());
                    int month = java.time.Month.valueOf(monthStr.toUpperCase()).getValue();
                    dob = LocalDate.of(year, month, day);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid date of birth: " + e.getMessage());
                    return;
                }
            }
            prepare.setDate(15, dob != null ? java.sql.Date.valueOf(dob) : null);

            prepare.setString(16, addEmployee_emergencyContact.getText());

            LocalDate hireDate = addEmployee_hireDate.getValue();
            prepare.setDate(17, hireDate != null ? java.sql.Date.valueOf(hireDate) : sqlDate);

            prepare.executeUpdate();

            // Get generated employee ID
            ResultSet generatedKeys = prepare.getGeneratedKeys();
            int generatedId = 0;
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }

            // Add to employee_salary table
            String insertInfo = "INSERT INTO employee_salary (employee_id, fullName, position, salary, date) "
                    + "VALUES(?,?,?,?,?)";
            prepare = connect.prepareStatement(insertInfo);
            prepare.setInt(1, generatedId);
            prepare.setString(2, addEmployee_fullName.getText());
            prepare.setString(3, addEmployee_position.getValue());
            prepare.setDouble(4, 0.0);
            prepare.setDate(5, sqlDate);
            prepare.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Employee added successfully!");
            addEmployeeShowListData();
            clearFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to add employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateDateOfBirth() {
        // Check if all date components are selected
        if (addEmployee_dob_day.getValue() == null || 
            addEmployee_dob_month.getValue() == null || 
            addEmployee_dob_year.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a complete date of birth (day, month, and year)");
            return false;
        }
        
        try {
            // Parse the selected date components
            int day = Integer.parseInt(addEmployee_dob_day.getValue());
            String monthStr = addEmployee_dob_month.getValue();
            int year = Integer.parseInt(addEmployee_dob_year.getValue());
            
            // Convert month name to number (1-12)
            int month = java.time.Month.valueOf(monthStr.toUpperCase()).getValue();
            
            // Create LocalDate object
            LocalDate dob = LocalDate.of(year, month, day);
            
            // Calculate minimum allowed date (20 years ago from today)
            LocalDate today = LocalDate.now();
            LocalDate minAgeDate = today.minusYears(20);
            
            if (dob.isAfter(minAgeDate)) {
                showAlert(Alert.AlertType.ERROR, "Error", null, "Employee must be at least 20 years old");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid date of birth: " + e.getMessage());
            return false;
        }
    }

    private boolean validateEmployeeForm() {
        // Check required fields
        if (ValidationUtil.isFieldEmpty(addEmployee_username.getText()) ||
                ValidationUtil.isFieldEmpty(addEmployee_fullName.getText()) ||
                addEmployee_gender.getValue() == null ||
                ValidationUtil.isFieldEmpty(addEmployee_phoneNum.getText()) ||
                addEmployee_position.getValue() == null ||
                ValidationUtil.isFieldEmpty(addEmployee_email.getText()) ||
                addEmployee_status.getValue() == null ||
                addEmployee_type.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all required fields (*)");
            return false;
        }

        // Validate email
        if (!ValidationUtil.isValidEmail(addEmployee_email.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid email format. Must be @gmail.com");
            return false;
        }

        // Validate phone
        if (!ValidationUtil.isValidPhone(addEmployee_phoneNum.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Phone number must be +2519xxxxxxxx");
            return false;
        }

        // Validate name
        if (!ValidationUtil.isValidName(addEmployee_fullName.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Full name must contain only characters");
            return false;
        }
        
        // Validate date of birth
        if (!validateDateOfBirth()) {
            return false;
        }

        return true;
    }

    @FXML
    public void addEmployeeUpdate() {
        if (addEmployee_employeeID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select an employee to update");
            return;
        }

        if (!validateEmployeeForm()) return;

        String uri = GetData.path != null ? GetData.path.replace("\\", "\\\\") : null;
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "UPDATE employee SET username = ?, fullName = ?, gender = ?, phoneNum = ?, "
                + "position = ?, email = ?, image = ?, date = ?, status = ?, address = ?, "
                + "qualification = ?, specialization = ?, department = ?, employee_type = ?, "
                + "date_of_birth = ?, emergency_contact = ?, hire_date = ? "
                + "WHERE employee_id = ?";

        connect = employeemanagementsystem.dao.Database.connectDb();

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to update employee #" + addEmployee_employeeID.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, addEmployee_username.getText());
                prepare.setString(2, addEmployee_fullName.getText());
                prepare.setString(3, addEmployee_gender.getValue());
                prepare.setString(4, addEmployee_phoneNum.getText());
                prepare.setString(5, addEmployee_position.getValue());
                prepare.setString(6, addEmployee_email.getText());
                prepare.setString(7, uri);
                prepare.setDate(8, sqlDate);
                prepare.setString(9, addEmployee_status.getValue());
                prepare.setString(10, addEmployee_address.getValue());
                prepare.setString(11, addEmployee_qualification.getValue());
                prepare.setString(12, addEmployee_specialization.getValue());
                prepare.setString(13, addEmployee_department.getValue());
                prepare.setString(14, addEmployee_type.getValue());

                // Get date of birth from ComboBox components
                LocalDate dob = null;
                if (addEmployee_dob_day.getValue() != null && 
                    addEmployee_dob_month.getValue() != null && 
                    addEmployee_dob_year.getValue() != null) {
                    try {
                        int day = Integer.parseInt(addEmployee_dob_day.getValue());
                        String monthStr = addEmployee_dob_month.getValue();
                        int year = Integer.parseInt(addEmployee_dob_year.getValue());
                        int month = java.time.Month.valueOf(monthStr.toUpperCase()).getValue();
                        dob = LocalDate.of(year, month, day);
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid date of birth: " + e.getMessage());
                        return;
                    }
                }
                prepare.setDate(15, dob != null ? java.sql.Date.valueOf(dob) : null);

                prepare.setString(16, addEmployee_emergencyContact.getText());

                LocalDate hireDate = addEmployee_hireDate.getValue();
                prepare.setDate(17, hireDate != null ? java.sql.Date.valueOf(hireDate) : sqlDate);

                prepare.setInt(18, Integer.parseInt(addEmployee_employeeID.getText()));

                prepare.executeUpdate();

                // Update employee_salary table
                String updateInfo = "UPDATE employee_salary SET fullName = ?, position = ? WHERE employee_id = ?";
                prepare = connect.prepareStatement(updateInfo);
                prepare.setString(1, addEmployee_fullName.getText());
                prepare.setString(2, addEmployee_position.getValue());
                prepare.setInt(3, Integer.parseInt(addEmployee_employeeID.getText()));
                prepare.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Employee updated successfully!");
                addEmployeeShowListData();
                clearFields();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to update employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteEmployee() {
        if (addEmployee_employeeID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select an employee to delete");
            return;
        }

        String sql = "DELETE FROM employee WHERE employee_id = ?";
        connect = employeemanagementsystem.dao.Database.connectDb();

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete employee #" + addEmployee_employeeID.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, Integer.parseInt(addEmployee_employeeID.getText()));
                prepare.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Employee deleted successfully!");
                addEmployeeShowListData();
                clearFields();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to delete employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void clearFields() {
        addEmployee_employeeID.clear();
        addEmployee_username.clear();
        addEmployee_fullName.clear();
        addEmployee_gender.getSelectionModel().clearSelection();
        addEmployee_phoneNum.clear();
        addEmployee_email.clear();
        addEmployee_emergencyContact.clear();
        addEmployee_address.getSelectionModel().clearSelection();
        addEmployee_position.getSelectionModel().clearSelection();
        addEmployee_status.getSelectionModel().clearSelection();
        addEmployee_qualification.getSelectionModel().clearSelection();
        addEmployee_specialization.getSelectionModel().clearSelection();
        addEmployee_department.getSelectionModel().clearSelection();
        addEmployee_type.getSelectionModel().clearSelection();
        addEmployee_dob_day.getSelectionModel().clearSelection();
        addEmployee_dob_month.getSelectionModel().clearSelection();
        addEmployee_dob_year.getSelectionModel().clearSelection();
        addEmployee_hireDate.setValue(LocalDate.now());
        addEmployee_image.setImage(null);
        GetData.path = "";
        
        // Clear validation errors
        updateValidation(addEmployee_username, false, null);
        updateValidation(addEmployee_fullName, false, null);
        updateValidation(addEmployee_phoneNum, false, null);
        updateValidation(addEmployee_email, false, null);
        updateValidation(addEmployee_gender, false, null);
        updateValidation(addEmployee_position, false, null);
        updateValidation(addEmployee_department, false, null);
        updateValidation(addEmployee_status, false, null);
        updateValidation(addEmployee_type, false, null);
        updateValidation(addEmployee_qualification, false, null);
    }

    @FXML
    public void addEmployeeInsertImage() {
        FileChooser open = new FileChooser();
        File file = open.showOpenDialog(main_form.getScene().getWindow());

        if (file != null) {
            GetData.path = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 150, 150, false, true);
            addEmployee_image.setImage(image);
        }
    }

    @FXML
    public void addEmployeeSearch() {
        // Make sure the table view has items
        if (addEmployeeList == null) {
            addEmployeeList = FXCollections.observableArrayList();
        }
        
        // Create a filtered list from the original list
        FilteredList<EmployeeData> filter = new FilteredList<>(addEmployeeList, e -> true);

        // Add listener to search field
        addEmployee_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(employee -> {
                // If search field is empty, display all employees
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Compare with all fields
                if (employee.getEmployeeId().toString().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getUsername() != null && employee.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getFullName() != null && employee.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getPosition() != null && employee.getPosition().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getDepartment() != null && employee.getDepartment().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getEmail() != null && employee.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getPhoneNum() != null && employee.getPhoneNum().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getStatus() != null && employee.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            
            // Update the table view with filtered results
            SortedList<EmployeeData> sortedData = new SortedList<>(filter);
            sortedData.comparatorProperty().bind(addEmployee_tableView.comparatorProperty());
            addEmployee_tableView.setItems(sortedData);
        });
        
        // Initial sort
        SortedList<EmployeeData> sortedData = new SortedList<>(filter);
        sortedData.comparatorProperty().bind(addEmployee_tableView.comparatorProperty());
        addEmployee_tableView.setItems(sortedData);
    }

    public void addEmployeeShowListData() {
        try {
            // Clear existing items
            addEmployeeList.clear();
            
            // Load data from service
            List<EmployeeData> employees = employeeService.getAllEmployees();
            
            if (employees != null && !employees.isEmpty()) {
                addEmployeeList.addAll(employees);
                addEmployee_tableView.setItems(addEmployeeList);
                
                // Auto-resize columns to fit content
                addEmployee_tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                
                // Refresh the table
                addEmployee_tableView.refresh();
                
                System.out.println("Loaded " + employees.size() + " employees into the table.");
            } else {
                System.out.println("No employee data found in the database.");
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No Employees Found", "There are no employee records in the database.");
            }
        } catch (Exception e) {
            System.err.println("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load employees", "Could not load employee data: " + e.getMessage());
        }
    }

    @FXML
    public void addEmployeeSelect(MouseEvent event) {
        EmployeeData employee = addEmployee_tableView.getSelectionModel().getSelectedItem();
        if (employee == null) return;

        addEmployee_employeeID.setText(employee.getFormattedEmployeeId());
        addEmployee_username.setText(employee.getUsername());
        addEmployee_fullName.setText(employee.getFullName());
        addEmployee_phoneNum.setText(employee.getPhoneNum());
        addEmployee_email.setText(employee.getEmail());
        addEmployee_address.setValue(employee.getAddress());
        addEmployee_emergencyContact.setText(employee.getEmergencyContact());

        // Set combo box values
        addEmployee_gender.setValue(employee.getGender());
        addEmployee_status.setValue(employee.getStatus());
        addEmployee_type.setValue(employee.getEmployeeType());
        addEmployee_position.setValue(employee.getPosition());
        addEmployee_department.setValue(employee.getDepartment());
        addEmployee_qualification.setValue(employee.getQualification());
        addEmployee_specialization.setValue(employee.getSpecialization());

        // Set dates
        if (employee.getDateOfBirth() != null) {
            LocalDate dob = employee.getDateOfBirth().toLocalDate();
            addEmployee_dob_day.setValue(String.format("%02d", dob.getDayOfMonth()));
            addEmployee_dob_month.setValue(dob.getMonth().toString());
            addEmployee_dob_year.setValue(String.valueOf(dob.getYear()));
        }
        if (employee.getDate() != null) {
            addEmployee_hireDate.setValue(employee.getDate().toLocalDate());
        }

        // Set image
        if (employee.getImage() != null && !employee.getImage().isEmpty()) {
            GetData.path = employee.getImage();
            try {
                image = new Image("file:" + employee.getImage(), 150, 150, false, true);
                addEmployee_image.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Clear validation errors when selecting an employee
        updateValidation(addEmployee_username, false, null);
        updateValidation(addEmployee_fullName, false, null);
        updateValidation(addEmployee_phoneNum, false, null);
        updateValidation(addEmployee_email, false, null);
        updateValidation(addEmployee_gender, false, null);
        updateValidation(addEmployee_position, false, null);
        updateValidation(addEmployee_department, false, null);
        updateValidation(addEmployee_status, false, null);
        updateValidation(addEmployee_type, false, null);
        updateValidation(addEmployee_qualification, false, null);
    }

    // ============ SALARY METHODS ============
    @FXML
    public void salaryUpdate() {
        if (salary_employeeID.getText().isEmpty() || salary_salary.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select an employee and enter salary");
            return;
        }

        try {
            double salary = Double.parseDouble(salary_salary.getText());
            
            // Validate salary range
            if (salary < 10000 || salary > 100000) {
                showAlert(Alert.AlertType.ERROR, "Error", null, "Salary must be between 10,000 and 100,000 ETB");
                return;
            }
            
            String sql = "UPDATE employee_salary SET salary = ? WHERE employee_id = ?";
            connect = employeemanagementsystem.dao.Database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setDouble(1, salary);
            prepare.setInt(2, Integer.parseInt(salary_employeeID.getText()));
            prepare.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Salary updated successfully!");
            salaryShowListData();
            salaryReset();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please enter a valid number for salary");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to update salary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void salaryReset() {
        salary_employeeID.clear();
        salary_fullName.setText("");
        salary_position.setText("");
        salary_salary.clear();
        updateValidation(salary_salary, false, null);
    }

    public ObservableList<EmployeeData> salaryListData() {
        ObservableList<EmployeeData> listData = FXCollections.observableArrayList();
        String sql = "SELECT ei.*, e.position FROM employee_salary ei "
                + "JOIN employee e ON ei.employee_id = e.employee_id "
                + "ORDER BY ei.employee_id DESC";
        connect = employeemanagementsystem.dao.Database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                EmployeeData employee = new EmployeeData(
                        result.getInt("employee_id"),
                        result.getString("fullName"),
                        result.getString("position"),
                        result.getDouble("salary")
                );
                listData.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    public void salaryShowListData() {
        salaryList = salaryListData();

        salary_col_employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        salary_col_fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        salary_col_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        salary_col_salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        salary_tableView.setItems(salaryList);
    }

    @FXML
    public void salarySelect(MouseEvent event) {
        EmployeeData employee = salary_tableView.getSelectionModel().getSelectedItem();
        if (employee == null) return;

        salary_employeeID.setText(String.valueOf(employee.getEmployeeId()));
        salary_fullName.setText(employee.getFullName());
        salary_position.setText(employee.getPosition());
        salary_salary.setText(String.valueOf(employee.getSalary()));
        
        // Clear validation when selecting
        updateValidation(salary_salary, false, null);
    }

    // ============ NAVIGATION METHODS ============
    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == home_btn) {
            setActiveForm(home_form, home_btn);
            homeTotalEmployees();
            homeEmployeeTotalPresent();
            homeTotalInactive();
            homeChart();

        } else if (event.getSource() == addEmployee_btn) {
            setActiveForm(addEmployee_form, addEmployee_btn);
            addEmployeeShowListData();

        } else if (event.getSource() == salary_btn) {
            setActiveForm(salary_form, salary_btn);
            salaryShowListData();
        }
    }

    private void setActiveForm(AnchorPane form, Button button) {
        home_form.setVisible(false);
        addEmployee_form.setVisible(false);
        salary_form.setVisible(false);

        home_btn.setStyle("-fx-background-color: transparent");
        addEmployee_btn.setStyle("-fx-background-color: transparent");
        salary_btn.setStyle("-fx-background-color: transparent");

        form.setVisible(true);
        button.setStyle("-fx-background-color: linear-gradient(to bottom right, #3a4368, #28966c);");
    }

    public void defaultNav() {
        setActiveForm(home_form, home_btn);
    }

    public void displayUsername() {
        if (username != null && GetData.username != null) {
            username.setText(GetData.username);
        }
    }

    @FXML
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get() == ButtonType.OK) {
            try {
                logout.getScene().getWindow().hide();
                URL resource = getClass().getResource("/view/FXMLDocument.fxml");
                if (resource == null) {
                    throw new IllegalStateException("Cannot find FXML file: /view/FXMLDocument.fxml");
                }
                Parent root = FXMLLoader.load(resource);
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                    stage.setOpacity(.8);
                });

                root.setOnMouseReleased(event -> stage.setOpacity(1));

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void close() {
        System.exit(0);
    }

    @FXML
    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void setupValidationListeners() {
        // Phone number validation
        addEmployee_phoneNum.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = ValidationUtil.isFieldEmpty(newValue);
            boolean isValid = ValidationUtil.isValidPhone(newValue);
            
            if (isEmpty) {
                updateValidation(addEmployee_phoneNum, true, "Phone number is required");
            } else if (!isValid) {
                updateValidation(addEmployee_phoneNum, true, "Phone must be +2519xxxxxxxx");
            } else {
                updateValidation(addEmployee_phoneNum, false, null);
            }
        });

        // Email validation
        addEmployee_email.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = ValidationUtil.isFieldEmpty(newValue);
            boolean isValid = ValidationUtil.isValidEmail(newValue);
            
            if (isEmpty) {
                updateValidation(addEmployee_email, true, "Email is required");
            } else if (!isValid) {
                updateValidation(addEmployee_email, true, "Must be a valid email (@gmail.com)");
            } else {
                updateValidation(addEmployee_email, false, null);
            }
        });
        
        // Full name validation
        addEmployee_fullName.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = ValidationUtil.isFieldEmpty(newValue);
            boolean isValid = ValidationUtil.isValidName(newValue);
            
            if (isEmpty) {
                updateValidation(addEmployee_fullName, true, "Full name is required");
            } else if (!isValid) {
                updateValidation(addEmployee_fullName, true, "Name must contain only letters and spaces");
            } else {
                updateValidation(addEmployee_fullName, false, null);
            }
        });

        // Username validation
        addEmployee_username.textProperty().addListener((observable, oldValue, newValue) -> {
            if (ValidationUtil.isFieldEmpty(newValue)) {
                updateValidation(addEmployee_username, true, "Username is required");
            } else if (newValue.length() < 3) {
                updateValidation(addEmployee_username, true, "Username must be at least 3 characters");
            } else {
                updateValidation(addEmployee_username, false, null);
            }
        });

        // ComboBox validations
        addEmployee_gender.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_gender, newVal == null, "Please select gender");
        });

        addEmployee_position.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_position, newVal == null, "Please select position");
        });

        addEmployee_department.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_department, newVal == null, "Please select department");
        });

        addEmployee_status.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_status, newVal == null, "Please select status");
        });
        
        // Employee Type validation
        addEmployee_type.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_type, newVal == null, "Please select employee type");
        });
        
        // Qualification validation
        addEmployee_qualification.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateValidation(addEmployee_qualification, newVal == null, "Please select qualification");
        });
        
        // Salary validation
        salary_salary.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                updateValidation(salary_salary, true, "Salary is required");
                return;
            }
            
            try {
                double salary = Double.parseDouble(newValue);
                if (salary < 10000 || salary > 100000) {
                    updateValidation(salary_salary, true, "Salary must be between 10,000 and 100,000 ETB");
                } else {
                    updateValidation(salary_salary, false, null);
                }
            } catch (NumberFormatException e) {
                updateValidation(salary_salary, true, "Please enter a valid number");
            }
        });
    }
    
    private void updateValidation(Control control, boolean isInvalid, String errorMessage) {
        if (isInvalid) {
            if (!control.getStyleClass().contains("error")) {
                control.getStyleClass().add("error");
            }
            control.setTooltip(createTooltip(errorMessage));
        } else {
            control.getStyleClass().removeAll(Collections.singleton("error"));
            control.setTooltip(null);
        }
    }
    
    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 5px 10px; " +
            "-fx-background-radius: 3px;"
        );
        return tooltip;
    }
}