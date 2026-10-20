package ar.edu.ipesmi.docentes.util;

import jakarta.persistence.EntityManager;

public class PruebaConexion {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        System.out.println("✅ Conexión OK: " + em.isOpen());
        em.close();
        JPAUtil.cerrar();
    }
}