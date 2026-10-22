package ar.edu.ipesmi.docentes.excepciones;

public class DocenteNoEncontradoException extends Exception {
    public DocenteNoEncontradoException(Long id) {
        super("No se encontró el docente con id " + id);
    }
}
