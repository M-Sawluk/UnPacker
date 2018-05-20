package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.PropertiesUtils.loadProperties;

public class WebViewController implements Initializable {
    private static final String KIBANA_URL_PROP = "kibana_url";
    @FXML
    private WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.load("http:/" + loadProperties(KIBANA_URL_PROP)+":5601/app/kibana#/home?_g=()");
    }

    public void loadUploader(MouseEvent mouseEvent) throws IOException {
        Parent parent =(Parent) FXMLLoader.load(getClass().getResource("Upload.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) (((Node) mouseEvent.getSource()).getScene().getWindow());
        window.setScene(scene);
        window.show();
    }

    public void refreshKibana(MouseEvent mouseEvent) { webView.getEngine().reload(); }
}
