package com.genes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class App extends Application {

    private static final String TITULO = "ForensiQ - PDF Forensic Analyzer";
    private static final String FXML_PATH = "/com/genes/Interface.fxml";
    private static final String ICON_PATH = "/com/genes/icone.png";

    @Override
    public void start(Stage palcoPrincipal) {
        try {
            // Carrega o FXML
            URL fxmlUrl = getClass().getResource(FXML_PATH);
            Objects.requireNonNull(fxmlUrl, "FXML not found at: " + FXML_PATH);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent raiz = loader.load();

            Scene cena = new Scene(raiz, 860, 680);

            palcoPrincipal.setTitle(TITULO);
            palcoPrincipal.setScene(cena);
            palcoPrincipal.setResizable(false);
            palcoPrincipal.centerOnScreen();

            // Carrega o ícone com null-check — ícone faltando não derruba o app
            InputStream iconStream = getClass().getResourceAsStream(ICON_PATH);
            if (iconStream != null) {
                palcoPrincipal.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("[WARN] Icon not found at: " + ICON_PATH);
            }

            palcoPrincipal.show();

        } catch (NullPointerException e) {
            System.err.println("[FATAL] Required resource not found: " + e.getMessage());
            System.exit(1);

        } catch (IOException e) {
            System.err.println("[FATAL] Failed to load interface: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}