package employeemanagementsystem.controller;

import employeemanagementsystem.model.GetData;
import employeemanagementsystem.service.AdminService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    @FXML private AnchorPane main_form;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginBtn;
    @FXML private Button close;

    private AdminService adminService = new AdminService();
    private double x = 0;
    private double y = 0;

    @FXML
    public void loginAdmin() {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Please fill all blank fields");
            return;
        }

        if (adminService.validateAdmin(username.getText(), password.getText())) {
            GetData.username = username.getText();

            try {
                loginBtn.getScene().getWindow().hide();
                URL resource = getClass().getResource("/view/Dashboard.fxml");
                if (resource == null) {
                    throw new IllegalStateException("Cannot find FXML file: /view/Dashboard.fxml");
                }
                Parent root = FXMLLoader.load(resource);
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                makeStageDraggable(root, stage);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Wrong Username/Password");
        }
    }

    @FXML
    public void close() {
        System.exit(0);
    }

    private void makeStageDraggable(Parent root, Stage stage) {
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
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code if needed
    }
}