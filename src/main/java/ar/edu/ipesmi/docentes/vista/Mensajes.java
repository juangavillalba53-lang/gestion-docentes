package ar.edu.ipesmi.docentes.vista;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

final class Mensajes {

    private Mensajes() { }

    static void ok(Label label, String texto) {
        label.getStyleClass().removeAll("mensaje-error", "mensaje-ok");
        label.getStyleClass().add("mensaje-ok");
        label.setText("✔ " + texto);
    }

    static void error(Label label, String texto) {
        label.getStyleClass().removeAll("mensaje-error", "mensaje-ok");
        label.getStyleClass().add("mensaje-error");
        label.setText("✖ " + texto);
    }

    static boolean confirmar(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
