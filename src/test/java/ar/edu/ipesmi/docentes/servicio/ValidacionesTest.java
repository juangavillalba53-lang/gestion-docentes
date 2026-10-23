package ar.edu.ipesmi.docentes.servicio;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionesTest {

    @Test
    @DisplayName("Caso normal: asignación dentro de los límites")
    void casoNormal() {
        assertDoesNotThrow(() -> Validaciones.validarCargaHoraria(10, 0, 6, 4));
    }

    @Test
    @DisplayName("Borde: el docente llega exactamente a 40 hs")
    void docenteJustoEnElMaximo() {
        assertDoesNotThrow(() -> Validaciones.validarCargaHoraria(36, 0, 6, 4));
    }

    @Test
    @DisplayName("Borde: el docente pasaría a 41 hs")
    void docenteSePasaPorUnaHora() {
        assertThrows(CargaHorariaExcedidaException.class,
                () -> Validaciones.validarCargaHoraria(37, 0, 6, 4));
    }

    @Test
    @DisplayName("Borde: la asignatura queda exactamente completa")
    void asignaturaJustoCompleta() {
        assertDoesNotThrow(() -> Validaciones.validarCargaHoraria(0, 4, 6, 2));
    }

    @Test
    @DisplayName("Borde: la asignatura se pasaría por 1 hora")
    void asignaturaSePasaPorUnaHora() {
        assertThrows(CargaHorariaExcedidaException.class,
                () -> Validaciones.validarCargaHoraria(0, 4, 6, 3));
    }

    @ParameterizedTest(name = "horas = {0}")
    @ValueSource(ints = {0, -1})
    @DisplayName("Horas en cero o negativas lanzan IllegalArgumentException")
    void horasInvalidas(int horas) {
        assertThrows(IllegalArgumentException.class,
                () -> Validaciones.validarCargaHoraria(0, 0, 6, horas));
    }
}
