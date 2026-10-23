package ar.edu.ipesmi.docentes.vista;

import ar.edu.ipesmi.docentes.excepciones.DocenteNoEncontradoException;
import ar.edu.ipesmi.docentes.excepciones.EmailInvalidoException;
import ar.edu.ipesmi.docentes.modelo.Docente;
import ar.edu.ipesmi.docentes.servicio.DocenteServicio;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DocentesController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtEspecialidad;
    @FXML private Label lblMensaje;
    @FXML private Label lblFormulario;

    @FXML private TableView<Docente> tablaDocentes;
    @FXML private TableColumn<Docente, Long> colId;
    @FXML private TableColumn<Docente, String> colNombre;
    @FXML private TableColumn<Docente, String> colEmail;
    @FXML private TableColumn<Docente, String> colEspecialidad;
    @FXML private TableColumn<Docente, Integer> colCarga;

    private final DocenteServicio servicio = new DocenteServicio();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colCarga.setCellValueFactory(new PropertyValueFactory<>("cargaHorariaTotal"));

        tablaDocentes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, d) -> {
            if (d != null) {
                txtNombre.setText(d.getNombre());
                txtEmail.setText(d.getEmail());
                txtEspecialidad.setText(d.getEspecialidad());
                lblFormulario.setText("Editando docente #" + d.getId());
            }
        });
    }

    public void refrescar() {
        tablaDocentes.setItems(FXCollections.observableArrayList(servicio.listar()));
    }

    @FXML
    private void onNuevo() {
        tablaDocentes.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtEmail.clear();
        txtEspecialidad.clear();
        lblFormulario.setText("Nuevo docente");
        lblMensaje.setText("");
        txtNombre.requestFocus();
    }

    @FXML
    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.error(lblMensaje, "El nombre es obligatorio");
            return;
        }
        Docente seleccionado = tablaDocentes.getSelectionModel().getSelectedItem();
        try {
            if (seleccionado == null) {
                Docente d = servicio.registrar(nombre, txtEmail.getText(), txtEspecialidad.getText().trim());
                Mensajes.ok(lblMensaje, "Docente registrado con id " + d.getId());
            } else {
                servicio.actualizar(seleccionado.getId(), nombre, txtEmail.getText(), txtEspecialidad.getText().trim());
                Mensajes.ok(lblMensaje, "Docente actualizado");
            }
            refrescar();
            onNuevoSinMensaje();
        } catch (EmailInvalidoException | DocenteNoEncontradoException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblMensaje, "No se pudo guardar. ¿El email ya está registrado?");
        }
    }

    @FXML
    private void onEliminar() {
        Docente d = tablaDocentes.getSelectionModel().getSelectedItem();
        if (d == null) {
            Mensajes.error(lblMensaje, "Seleccioná un docente de la tabla");
            return;
        }
        if (!Mensajes.confirmar("Eliminar docente",
                "¿Eliminar a " + d.getNombre() + "? También se quitan sus asignaciones.")) {
            return;
        }
        try {
            servicio.eliminar(d.getId());
            Mensajes.ok(lblMensaje, "Docente eliminado");
            refrescar();
            onNuevoSinMensaje();
        } catch (DocenteNoEncontradoException e) {
            Mensajes.error(lblMensaje, e.getMessage());
        }
    }

    private void onNuevoSinMensaje() {
        String mensaje = lblMensaje.getText();
        onNuevo();
        lblMensaje.setText(mensaje);
    }
}
