package ar.edu.ipesmi.docentes.servicio;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import ar.edu.ipesmi.docentes.excepciones.EmailInvalidoException;

import java.util.regex.Pattern;

public final class Validaciones {

    public static final int MAX_HORAS_DOCENTE = 40;

    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validaciones() { }

    public static void validarEmail(String email) throws EmailInvalidoException {
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new EmailInvalidoException(email);
        }
    }

    public static void validarCargaHoraria(int cargaActualDocente, int horasYaAsignadas,
                                           int horasSemanales, int horasNuevas)
            throws CargaHorariaExcedidaException {
        if (horasNuevas <= 0) {
            throw new IllegalArgumentException("Las horas a asignar deben ser mayores a 0");
        }
        if (cargaActualDocente + horasNuevas > MAX_HORAS_DOCENTE) {
            throw new CargaHorariaExcedidaException(String.format(
                    "El docente quedaría con %d hs semanales (máximo %d)",
                    cargaActualDocente + horasNuevas, MAX_HORAS_DOCENTE));
        }
        if (horasYaAsignadas + horasNuevas > horasSemanales) {
            throw new CargaHorariaExcedidaException(String.format(
                    "La asignatura tiene %d hs semanales y ya hay %d asignadas: no se pueden sumar %d",
                    horasSemanales, horasYaAsignadas, horasNuevas));
        }
    }
}
