package ar.edu.ipesmi.docentes.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("docentesPU");

    private JPAUtil() { }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void cerrar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}