package ar.edu.ipesmi.docentes.vista;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import ar.edu.ipesmi.docentes.excepciones.DocenteNoEncontradoException;
import ar.edu.ipesmi.docentes.modelo.Asignacion;
import ar.edu.ipesmi.docentes.modelo.Asignatura;
import ar.edu.ipesmi.docentes.modelo.Docente;
import ar.edu.ipesmi.docentes.notificacion.Notificacion;
import ar.edu.ipesmi.docentes.notificacion.NotificacionFactory;
import ar.edu.ipesmi.docentes.servicio.AsignacionServicio;
import ar.edu.ipesmi.docentes.servicio.AsignaturaServicio;
import ar.edu.ipesmi.docentes.servicio.DocenteServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public class AsignacionesController {

    @FXML private TreeView<Object> arbolAsignaturas;
    @FXML private ComboBox<Docente> cmbDocente;
    @FXML private ComboBox<Asignatura> cmbAsignatura;
    @FXML private Spinner<Integer> spnHoras;
    @FXML private ComboBox<String> cmbNotificacion;
    @FXML private Label lblMensaje;
    @FXML private Label lblNotificacion;

    private final DocenteServicio docenteServicio = new DocenteServicio();
    private final AsignaturaServicio asignaturaServicio = new AsignaturaServicio();
    private final AsignacionServicio asignacionServicio = new AsignacionServicio();

    @FXML
    private void initialize() {
        spnHoras.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
        cmbNotificacion.setItems(FXCollections.observableArrayList("EMAIL", "SMS", "PUSH"));
        cmbNotificacion.getSelectionModel().selectFirst();

        lblNotificacion.visibleProperty().bind(lblNotificacion.textProperty().isNotEmpty());
        lblNotificacion.managedProperty().bind(lblNotificacion.visibleProperty());

        arbolAsignaturas.setShowRoot(false);
        arbolAsignaturas.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("nodo-asignatura", "nodo-docente");
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof Asignatura a) {
                    setText("📘 " + a + "   (" + a.getHorasAsignadas() + "/" + a.getHorasSemanales() + " hs)");
                    getStyleClass().add("nodo-asignatura");
                } else if (item instanceof Asignacion asig) {
                    setText("👤 " + asig.getDocente().getNombre() + "  —  " + asig.getHoras() + " hs");
                    getStyleClass().add("nodo-docente");
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    public void refrescar() {
        List<Asignatura> asignaturas = asignaturaServicio.listar();

        TreeItem<Object> raiz = new TreeItem<>("Asignaturas");
        for (Asignatura a : asignaturas) {
            TreeItem<Object> rama = new TreeItem<>(a);
            for (Asignacion asig : a.getAsignaciones()) {
                rama.getChildren().add(new TreeItem<>(asig));
            }
            rama.setExpanded(true);
            raiz.getChildren().add(rama);
        }
        arbolAsignaturas.setRoot(raiz);

        cmbDocente.setItems(FXCollections.observableArrayList(docenteServicio.listar()));
        cmbAsignatura.setItems(FXCollections.observableArrayList(asignaturas));
    }

    @FXML
    private void onAsignar() {
        Docente docente = cmbDocente.getValue();
        Asignatura asignatura = cmbAsignatura.getValue();
        if (docente == null || asignatura == null) {
            Mensajes.error(lblMensaje, "Elegí un docente y una asignatura");
            return;
        }
        int horas = spnHoras.getValue();
        try {
            asignacionServicio.asignar(docente.getId(), asignatura.getCodigo(), horas);
            Mensajes.ok(lblMensaje, docente.getNombre() + " asignado/a a " + asignatura.getCodigo()
                    + " con " + horas + " hs");

            Notificacion notificacion = NotificacionFactory.crear(cmbNotificacion.getValue());
            String destinatario = notificacion.getTipo().equals("EMAIL") ? docente.getEmail() : docente.getNombre();
            String enviado = notificacion.enviar(destinatario,
                    "Se le asignó " + asignatura.getNombre() + " con " + horas + " hs semanales");
            lblNotificacion.setText(enviado);

            refrescar();
        } catch (CargaHorariaExcedidaException | DocenteNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error(lblMensaje, e.getMessage());
            lblNotificacion.setText("");
        }
    }

    @FXML
    private void onQuitar() {
        TreeItem<Object> nodo = arbolAsignaturas.getSelectionModel().getSelectedItem();
        if (nodo == null || !(nodo.getValue() instanceof Asignacion asig)) {
            Mensajes.error(lblMensaje, "Seleccioná un docente (👤) dentro del árbol");
            return;
        }
        if (!Mensajes.confirmar("Quitar asignación",
                "¿Quitar a " + asig.getDocente().getNombre() + " de " + asig.getAsignatura().getCodigo() + "?")) {
            return;
        }
        asignacionServicio.quitar(asig.getId());
        Mensajes.ok(lblMensaje, "Asignación quitada");
        lblNotificacion.setText("");
        refrescar();
    }
}
