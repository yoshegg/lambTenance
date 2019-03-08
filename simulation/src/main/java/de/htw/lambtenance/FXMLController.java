package de.htw.lambtenance;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class FXMLController implements Initializable {

    private Model _model;

    @FXML
    private Button startFactoryButton;

    @FXML
    private Button stopFactoryButton;

    @FXML
    private Label label;

    @FXML
    private void startFactory(ActionEvent event) {
        _model.startFactory();
        label.setText("Factory started.");
        startFactoryButton.setDisable(true);
        stopFactoryButton.setDisable(false);
    }

    @FXML
    private void stopFactory(ActionEvent event) {
        _model.stopFactory();
        label.setText("Factory stopped.");
        startFactoryButton.setDisable(false);
        stopFactoryButton.setDisable(true);
    }

    @FXML
    private void generatePropertyAnomaly() {
        _model.getFactory().generatePropertyAnomaly();
    }

    @FXML
    private void generatePropertyError() {
        _model.getFactory().generatePropertyError();
    }

    @FXML
    private void generateMachineAnomaly() {
        _model.getFactory().generateMachineAnomaly();
    }

    @FXML
    private void generateMachineError() {
        _model.getFactory().generateMachineError();
    }

    @FXML
    private void repairFactory() {
        _model.getFactory().normalizeMachines();
    }

    public void linkToModel(Model model) {
        _model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
