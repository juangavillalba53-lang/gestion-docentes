package ar.edu.ipesmi.docentes.notificacion;

public final class NotificacionFactory {

    private NotificacionFactory() { }

    public static Notificacion crear(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de notificación no puede estar vacío");
        }
        return switch (tipo.trim().toUpperCase()) {
            case "EMAIL" -> new NotificacionEmail();
            case "SMS"   -> new NotificacionSMS();
            case "PUSH"  -> new NotificacionPush();
            default -> throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        };
    }
}
