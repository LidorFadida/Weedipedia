package sample;

import data_model.Strain;
import data_model.StrainManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

import static proj_contract.SQLContract.NO_DESC;
import static proj_contract.SQLContract.UNKNOWN;
import static proj_contract.StrainsContracts.TypeContract.*;

/**
 * Controller class updating gui via the {@link StrainManager} data model.
 */
public class Controller {
    @FXML
    private ListView<String> resultsListView, strainPositiveList, strainNegativeList, strainMedicalList;
    @FXML
    private Label strainTitle, raceStrainTitle, resultsCount;
    @FXML
    private Label flavorTitle1, flavorTitle2, flavorTitle3;
    @FXML
    private TextField inStrainName;
    @FXML
    private ImageView strainImage;
    @FXML
    private ScrollPane textPan;
    private StrainManager strainManager;


    @FXML
    public void initialize() {
        strainManager = new StrainManager();
        ObservableList<String> observableList = FXCollections.observableList(strainManager.getStrainsNames());
        resultsListView.getItems().addAll(observableList);
        resultsListView.setOnMouseClicked((mouseEvent) -> {
            establishViewsData(resultsListView.getSelectionModel().getSelectedItem());
        });
        resultsListView.getSelectionModel().selectFirst();
        strainImage.setImage(new Image("file:coming_Soon.jpg"));
        establishViewsData(resultsListView.getSelectionModel().getSelectedItem());
        updateResultCounter();
    }

    private void establishViewsData(String strainName) {
        if (strainName != null && !strainName.isEmpty())
            updateData(strainManager.getStrainData(strainName));
    }

    private void updateData(Strain strain) {
        strainTitle.setText(strain.getName());
        raceStrainTitle.setText(strain.getRace());
        Text text = new Text();
        text.setText(strain.getDescription() != null ? strain.getDescription() : NO_DESC);
        text.wrappingWidthProperty().bind(textPan.widthProperty());
        textPan.setFitToWidth(true);
        textPan.setContent(text);
        //maximum 1-3 flavors for each strain according to the api.
        try {
            flavorTitle1.setText(strain.getFlavors().get(0));
        } catch (IndexOutOfBoundsException e) {
            flavorTitle1.setText(UNKNOWN);
        }
        try {
            flavorTitle2.setText(strain.getFlavors().get(1));
        } catch (IndexOutOfBoundsException e) {
            flavorTitle2.setText(UNKNOWN);
        }
        try {
            flavorTitle3.setText(strain.getFlavors().get(2));
        } catch (IndexOutOfBoundsException e) {
            flavorTitle3.setText(UNKNOWN);
        }
        Map<String, List<String>> effects = strain.getEffects();
        for (String s : effects.keySet()) {
            switch (s) {
                case POSITIVE:
                    ObservableList<String> positives = FXCollections.observableList(effects.get(s));
                    strainPositiveList.getItems().setAll(positives);
                    break;
                case MEDICAL:
                    ObservableList<String> medical = FXCollections.observableList(effects.get(s));
                    strainMedicalList.getItems().setAll(medical);
                    break;
                case NEGATIVE:
                    ObservableList<String> negatives = FXCollections.observableList(effects.get(s));
                    strainNegativeList.getItems().setAll(negatives);
                    break;
            }
        }
    }

    private void updateResultCounter() {
        resultsCount.setText("Total results : " + resultsListView.getItems().size());
    }


    @FXML
    void showAllStrains() {
        resultsListView.getItems().setAll(strainManager.getStrainsNames());
        updateResultCounter();
    }

    @FXML
    void showHybridStrains(ActionEvent event) {
        resultsListView.getItems().setAll(strainManager.getStrainsByRace("hybrid"));
        updateResultCounter();
    }

    @FXML
    void showIndicaStrains() {
        resultsListView.getItems().setAll(strainManager.getStrainsByRace("indica"));
        updateResultCounter();
    }

    @FXML
    void showSativaStrains() {
        resultsListView.getItems().setAll(strainManager.getStrainsByRace("sativa"));
        updateResultCounter();
    }


    public void showSearchResult() {
        String nameLike = this.inStrainName.getText();
        if (nameLike != null && !nameLike.isEmpty()) {
            ObservableList<String> searchedStrains = strainManager.getSearchedStrains(nameLike);
            this.resultsListView.getItems().setAll(searchedStrains);
        }
    }
}

