package sample;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Pre loader class to handle gui while program is launching.
 */
public class DataTaskLoader extends Preloader {
    private Stage preLoaderStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.preLoaderStage = primaryStage; // stage from main
        this.preLoaderStage.setScene(scene); // set your custom scene
        this.preLoaderStage.initStyle(StageStyle.UNDECORATED); // undecorated style - use case.
        this.preLoaderStage.show(); // show the splash content
    }

    @Override
    public void init() throws Exception {
        //load your fxml file
        AnchorPane pane = FXMLLoader.load(getClass().getResource("splash.fxml"));
        //attach it to the new scene
        scene = new Scene(pane);
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        //progress listener
        if (info instanceof ProgressNotification) { // if its Progress Notification object
            double progress = ((ProgressNotification) info).getProgress(); // get the number
            //do work on the gui - use case.
            if (progress == - 2)
                SplashController.label.setText("Establishing connection");
            else if (progress == -1)
                SplashController.label.setText("Creating Database");
            else
                SplashController.label.setText("Analyzing data from web " + progress + "%");
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        //states changes listener.
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_START:
                //start method in main (app) is about to load.
                this.preLoaderStage.hide(); // hide your custom scene.
                break;
            case BEFORE_INIT:
                //do work before init
                break;
            case BEFORE_LOAD:
                //do work before load
                break;
        }
    }
}
