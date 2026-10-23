package ar.edu.ipesmi.docentes.vista;

import ar.edu.ipesmi.docentes.excepciones.EmailInvalidoException;
import ar.edu.ipesmi.docentes.modelo.Docente;
import ar.edu.ipesmi.docentes.servicio.DocenteServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormularioDocenteSwing extends JFrame {

    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtEmail = new JTextField(22);
    private final JTextField txtEspecialidad = new JTextField(22);
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final DocenteServicio servicio = new DocenteServicio();
    private final Runnable alGuardar;

    public FormularioDocenteSwing(Runnable alGuardar) {
        super("Alta de docente (Swing)");
        this.alGuardar = alGuardar;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Nuevo docente");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(new EmptyBorder(14, 16, 4, 16));
        add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(new EmptyBorder(8, 16, 8, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        agregarFila(campos, c, 0, "Nombre:", txtNombre);
        agregarFila(campos, c, 1, "Email:", txtEmail);
        agregarFila(campos, c, 2, "Especialidad:", txtEspecialidad);

        add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botones.add(btnLimpiar);
        botones.add(btnGuardar);
        add(botones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnLimpiar.addActionListener(e -> limpiar());
        getRootPane().setDefaultButton(btnGuardar);

        pack();
        setLocationRelativeTo(null);
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JTextField campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Falta un dato",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Docente d = servicio.registrar(txtNombre.getText(), txtEmail.getText(), txtEspecialidad.getText().trim());
            JOptionPane.showMessageDialog(this, "Docente registrado con id " + d.getId(), "Guardado",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            if (alGuardar != null) {
                alGuardar.run();
            }
        } catch (EmailInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Email inválido", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar. ¿El email ya está registrado?",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtEspecialidad.setText("");
        txtNombre.requestFocus();
    }
}
