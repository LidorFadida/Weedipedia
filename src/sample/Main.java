package sample;

import com.sun.javafx.application.LauncherImpl;
import data_model.StrainManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * DO NOT FORGET TO CHANGE THE PASSWORD IN {@link proj_contract.SQLContract} {@param DB_PASSWORD}
 */
public class Main extends Application {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, DataTaskLoader.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene dataScene = new Scene(root, 1000, 800);
        primaryStage.setScene(dataScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void init() {
        //do some heavy work -> (connect to DB ,preform DB logic , download web content etc..)
        StrainManager.constructStrainsJDBC(this);
    }
}
