package ar.edu.ipesmi.docentes.notificacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionFactoryTest {

    @ParameterizedTest(name = "\"{0}\" crea {1}")
    @CsvSource({
            "EMAIL, NotificacionEmail",
            "email, NotificacionEmail",
            "SMS,   NotificacionSMS",
            "PUSH,  NotificacionPush"
    })
    @DisplayName("Tipos válidos crean la notificación correspondiente")
    void tiposValidos(String tipo, String claseEsperada) {
        Notificacion n = NotificacionFactory.crear(tipo);
        assertEquals(claseEsperada, n.getClass().getSimpleName());
    }

    @ParameterizedTest(name = "tipo inválido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"whatsapp", "FAX"})
    @DisplayName("Tipos nulos, vacíos o desconocidos lanzan IllegalArgumentException")
    void tiposInvalidos(String tipo) {
        assertThrows(IllegalArgumentException.class, () -> NotificacionFactory.crear(tipo));
    }
}
