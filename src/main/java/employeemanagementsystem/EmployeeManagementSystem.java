package employeemanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

public class EmployeeManagementSystem extends Application {

    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource("/view/FXMLDocument.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find FXML file: /view/FXMLDocument.fxml");
        }
        Parent root = FXMLLoader.load(resource);
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

        root.setOnMouseReleased((MouseEvent event) -> stage.setOpacity(1));

        // Set application icon
        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream("/images/avatar.png");
            if (imageStream != null) {
                Image icon = new Image(imageStream);
                stage.getIcons().add(icon);
            } else {
                // Create a default icon if the image is not found
                System.out.println("Avatar image not found. Using default icon.");
                // You can add a default icon here if you want
                // Image defaultIcon = new Image("path/to/default/icon.png");
                // stage.getIcons().add(defaultIcon);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}