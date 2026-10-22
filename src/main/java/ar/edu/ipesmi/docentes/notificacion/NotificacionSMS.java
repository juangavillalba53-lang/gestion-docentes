package ar.edu.ipesmi.docentes.notificacion;

import ar.edu.ipesmi.docentes.util.LoggerSistema;

public class NotificacionSMS implements Notificacion {

    @Override
    public String getTipo() { return "SMS"; }

    @Override
    public String enviar(String destinatario, String mensaje) {
        NotificacionEmail.validar(destinatario, mensaje);
        String texto = "📱 SMS a " + destinatario + " | " + mensaje;
        LoggerSistema.getInstance().info("Notificación " + texto);
        return texto;
    }
}
