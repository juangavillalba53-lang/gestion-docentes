package ar.edu.ipesmi.docentes;

import ar.edu.ipesmi.docentes.util.JPAUtil;
import ar.edu.ipesmi.docentes.util.LoggerSistema;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/principal.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1150, 700);
        scene.getStylesheets().add(App.class.getResource("/css/estilos.css").toExternalForm());

        stage.setTitle("Gestión de Docentes y Asignaturas - IPESMI");
        stage.setMinWidth(950);
        stage.setMinHeight(600);
        stage.setScene(scene);

        root.setOpacity(0);
        stage.show();
        FadeTransition fade = new FadeTransition(Duration.millis(900), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        LoggerSistema.getInstance().info("Aplicación iniciada");
    }

    @Override
    public void stop() {
        LoggerSistema.getInstance().info("Aplicación cerrada");
        JPAUtil.cerrar();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
