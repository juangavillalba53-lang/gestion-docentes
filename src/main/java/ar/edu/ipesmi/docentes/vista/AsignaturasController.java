package ar.edu.ipesmi.docentes.vista;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import ar.edu.ipesmi.docentes.modelo.Asignatura;
import ar.edu.ipesmi.docentes.servicio.AsignaturaServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AsignaturasController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private Spinner<Integer> spnHoras;
    @FXML private Label lblMensaje;
    @FXML private Label lblFormulario;

    @FXML private TableView<Asignatura> tablaAsignaturas;
    @FXML private TableColumn<Asignatura, String> colCodigo;
    @FXML private TableColumn<Asignatura, String> colNombre;
    @FXML private TableColumn<Asignatura, Integer> colHoras;
    @FXML private TableColumn<Asignatura, Integer> colAsignadas;

    private final AsignaturaServicio servicio = new AsignaturaServicio();

    @FXML
    private void initialize() {
        spnHoras.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 4));

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colHoras.setCellValueFactory(new PropertyValueFactory<>("horasSemanales"));
        colAsignadas.setCellValueFactory(new PropertyValueFactory<>("horasAsignadas"));

        tablaAsignaturas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, a) -> {
            if (a != null) {
                txtCodigo.setText(a.getCodigo());
                txtCodigo.setDisable(true);
                txtNombre.setText(a.getNombre());
                spnHoras.getValueFactory().setValue(a.getHorasSemanales());
                lblFormulario.setText("Editando " + a.getCodigo());
            }
        });
    }

    public void refrescar() {
        tablaAsignaturas.setItems(FXCollections.observableArrayList(servicio.listar()));
    }

    @FXML
    private void onNuevo() {
        tablaAsignaturas.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtCodigo.setDisable(false);
        txtNombre.clear();
        spnHoras.getValueFactory().setValue(4);
        lblFormulario.setText("Nueva asignatura");
        lblMensaje.setText("");
        txtCodigo.requestFocus();
    }

    @FXML
    private void onGuardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            Mensajes.error(lblMensaje, "Código y nombre son obligatorios");
            return;
        }
        int horas = spnHoras.getValue();
        Asignatura seleccionada = tablaAsignaturas.getSelectionModel().getSelectedItem();
        try {
            if (seleccionada == null) {
                servicio.registrar(codigo, nombre, horas);
                Mensajes.ok(lblMensaje, "Asignatura " + codigo.toUpperCase() + " registrada");
            } else {
                servicio.actualizar(seleccionada.getCodigo(), nombre, horas);
                Mensajes.ok(lblMensaje, "Asignatura " + seleccionada.getCodigo() + " actualizada");
            }
            String mensaje = lblMensaje.getText();
            refrescar();
            onNuevo();
            lblMensaje.setText(mensaje);
        } catch (CargaHorariaExcedidaException | IllegalArgumentException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Asignatura a = tablaAsignaturas.getSelectionModel().getSelectedItem();
        if (a == null) {
            Mensajes.error(lblMensaje, "Seleccioná una asignatura de la tabla");
            return;
        }
        if (!Mensajes.confirmar("Eliminar asignatura",
                "¿Eliminar " + a + "? También se quitan los docentes asignados.")) {
            return;
        }
        try {
            servicio.eliminar(a.getCodigo());
            refrescar();
            onNuevo();
            Mensajes.ok(lblMensaje, "Asignatura eliminada");
        } catch (IllegalArgumentException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }
}
