package ar.edu.ipesmi.docentes.notificacion;

import ar.edu.ipesmi.docentes.util.LoggerSistema;

public class NotificacionPush implements Notificacion {

    @Override
    public String getTipo() { return "PUSH"; }

    @Override
    public String enviar(String destinatario, String mensaje) {
        NotificacionEmail.validar(destinatario, mensaje);
        String texto = "🔔 Push a " + destinatario + " | " + mensaje;
        LoggerSistema.getInstance().info("Notificación " + texto);
        return texto;
    }
}
