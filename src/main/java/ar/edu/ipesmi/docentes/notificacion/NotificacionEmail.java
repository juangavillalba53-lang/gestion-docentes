package ar.edu.ipesmi.docentes.notificacion;

import ar.edu.ipesmi.docentes.util.LoggerSistema;

public class NotificacionEmail implements Notificacion {

    @Override
    public String getTipo() { return "EMAIL"; }

    @Override
    public String enviar(String destinatario, String mensaje) {
        validar(destinatario, mensaje);
        String texto = "📧 Email a " + destinatario + " | Asunto: Asignación docente | " + mensaje;
        LoggerSistema.getInstance().info("Notificación " + texto);
        return texto;
    }

    static void validar(String destinatario, String mensaje) {
        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("El destinatario no puede estar vacío");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }
    }
}
