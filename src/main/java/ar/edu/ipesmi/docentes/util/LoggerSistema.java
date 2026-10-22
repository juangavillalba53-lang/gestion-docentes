package ar.edu.ipesmi.docentes.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerSistema {

    private static LoggerSistema instancia;

    private static final String ARCHIVO = "sistema.log";
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LoggerSistema() { }

    public static synchronized LoggerSistema getInstance() {
        if (instancia == null) {
            instancia = new LoggerSistema();
        }
        return instancia;
    }

    public void info(String mensaje) {
        escribir("INFO", mensaje);
    }

    public void error(String mensaje) {
        escribir("ERROR", mensaje);
    }

    private synchronized void escribir(String nivel, String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje del log no puede ser nulo ni vacío");
        }
        String linea = String.format("[%s] [%s] %s", LocalDateTime.now().format(FORMATO), nivel, mensaje);

        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO, true))) {
            out.println(linea);
        } catch (IOException e) {
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }
}
