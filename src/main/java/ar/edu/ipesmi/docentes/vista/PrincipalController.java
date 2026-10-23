package ar.edu.ipesmi.docentes.vista;

import ar.edu.ipesmi.docentes.util.LoggerSistema;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.swing.SwingUtilities;

public class PrincipalController {

    @FXML private HBox encabezado;
    @FXML private TabPane pestanias;
    @FXML private Tab tabDocentes;
    @FXML private Tab tabAsignaturas;
    @FXML private Tab tabAsignaciones;

    @FXML private DocentesController vistaDocentesController;
    @FXML private AsignaturasController vistaAsignaturasController;
    @FXML private AsignacionesController vistaAsignacionesController;

    @FXML
    private void initialize() {
        DropShadow sombra = new DropShadow();
        sombra.setRadius(14);
        sombra.setOffsetY(4);
        sombra.setColor(Color.rgb(15, 23, 42, 0.30));
        encabezado.setEffect(sombra);

        DropShadow sombraSuave = new DropShadow(10, 0, 2, Color.rgb(15, 23, 42, 0.12));
        pestanias.setEffect(sombraSuave);

        pestanias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, tab) -> refrescar(tab));

        Platform.runLater(() -> refrescar(pestanias.getSelectionModel().getSelectedItem()));
    }

    private void refrescar(Tab tab) {
        try {
            if (tab == tabDocentes) {
                vistaDocentesController.refrescar();
            } else if (tab == tabAsignaturas) {
                vistaAsignaturasController.refrescar();
            } else if (tab == tabAsignaciones) {
                vistaAsignacionesController.refrescar();
            }
        } catch (RuntimeException | ExceptionInInitializerError e) {
            LoggerSistema.getInstance().error("Error al cargar datos: " + e);
        }
    }

    @FXML
    private void onAbrirSwing() {
        SwingUtilities.invokeLater(() -> new FormularioDocenteSwing(
                () -> Platform.runLater(() -> refrescar(pestanias.getSelectionModel().getSelectedItem()))
        ).setVisible(true));
    }
}
