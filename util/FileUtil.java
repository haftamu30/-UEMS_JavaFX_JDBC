package employeemanagementsystem.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileUtil {

    public static String selectImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Employee Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(stage);
        return file != null ? file.getAbsolutePath() : null;
    }
}