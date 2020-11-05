package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashController {
    @FXML
    private ImageView splashImage;
    @FXML
    private Label updateLabel;
    public static Label label;

    public void initialize() {
        label = updateLabel;
        Image temp = new Image("file:loading_Gif.gif");
        splashImage.setImage(temp);
        centerImage();
    }
    public void centerImage() {
        Image img = splashImage.getImage();
        if (img != null) {
            double w,h;
            double ratioX = splashImage.getFitWidth() / img.getWidth();
            double ratioY = splashImage.getFitHeight() / img.getHeight();
            double middle = Math.min(ratioX, ratioY);
            w = img.getWidth() * middle;
            h = img.getHeight() * middle;
            splashImage.setX((splashImage.getFitWidth() - w) / 2);
            splashImage.setY((splashImage.getFitHeight() - h) / 2);
        }
    }

}
