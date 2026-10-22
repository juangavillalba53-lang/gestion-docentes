package ar.edu.ipesmi.docentes.excepciones;

public class EmailInvalidoException extends Exception {
    public EmailInvalidoException(String email) {
        super("El email '" + email + "' no tiene un formato válido");
    }
}
