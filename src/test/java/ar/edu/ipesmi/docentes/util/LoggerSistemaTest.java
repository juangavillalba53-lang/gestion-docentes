package ar.edu.ipesmi.docentes.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggerSistemaTest {

    @Test
    @DisplayName("getInstance() siempre devuelve la misma instancia (Singleton)")
    void unicidad() {
        LoggerSistema a = LoggerSistema.getInstance();
        LoggerSistema b = LoggerSistema.getInstance();
        assertNotNull(a);
        assertSame(a, b);
    }

    @Test
    @DisplayName("Un mensaje válido se escribe en sistema.log")
    void escribeEnArchivo() throws IOException {
        String mensaje = "Prueba JUnit " + System.nanoTime();
        LoggerSistema.getInstance().info(mensaje);

        List<String> lineas = Files.readAllLines(Path.of("sistema.log"));
        String ultima = lineas.get(lineas.size() - 1);
        assertTrue(ultima.contains("[INFO]"));
        assertTrue(ultima.endsWith(mensaje));
    }

    @ParameterizedTest(name = "mensaje inválido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Mensajes nulos o vacíos lanzan IllegalArgumentException")
    void mensajesInvalidos(String mensaje) {
        LoggerSistema log = LoggerSistema.getInstance();
        assertThrows(IllegalArgumentException.class, () -> log.info(mensaje));
        assertThrows(IllegalArgumentException.class, () -> log.error(mensaje));
    }
}
