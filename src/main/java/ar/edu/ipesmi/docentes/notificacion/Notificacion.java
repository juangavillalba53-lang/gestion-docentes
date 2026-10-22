package ar.edu.ipesmi.docentes.notificacion;

public interface Notificacion {

    String getTipo();

    String enviar(String destinatario, String mensaje);
}
