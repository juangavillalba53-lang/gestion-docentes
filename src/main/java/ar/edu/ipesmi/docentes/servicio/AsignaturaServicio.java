package ar.edu.ipesmi.docentes.servicio;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import ar.edu.ipesmi.docentes.modelo.Asignatura;
import ar.edu.ipesmi.docentes.util.JPAUtil;
import ar.edu.ipesmi.docentes.util.LoggerSistema;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;

public class AsignaturaServicio {

    private final HashMap<String, Asignatura> asignaturas = new HashMap<>();
    private final LoggerSistema log = LoggerSistema.getInstance();

    public Asignatura registrar(String codigo, String nombre, int horasSemanales) {
        String cod = normalizar(codigo);
        if (horasSemanales <= 0) {
            throw new IllegalArgumentException("Las horas semanales deben ser mayores a 0");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (em.find(Asignatura.class, cod) != null) {
                throw new IllegalArgumentException("Ya existe una asignatura con código " + cod);
            }
            Asignatura a = new Asignatura(cod, nombre.trim(), horasSemanales);
            em.getTransaction().begin();
            em.persist(a);
            em.getTransaction().commit();
            asignaturas.put(cod, a);
            log.info("Alta asignatura: " + a);
            return a;
        } catch (RuntimeException e) {
            log.error("Alta de asignatura rechazada: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
    }

    public List<Asignatura> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Asignatura> resultado = em.createQuery(
                    "SELECT DISTINCT a FROM Asignatura a LEFT JOIN FETCH a.asignaciones ORDER BY a.codigo",
                    Asignatura.class).getResultList();
            asignaturas.clear();
            for (Asignatura a : resultado) {
                asignaturas.put(a.getCodigo(), a);
            }
            return resultado;
        } finally {
            cerrar(em);
        }
    }

    public Asignatura buscar(String codigo) {
        String cod = normalizar(codigo);
        Asignatura a = asignaturas.get(cod);
        if (a == null) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                a = em.find(Asignatura.class, cod);
            } finally {
                cerrar(em);
            }
        }
        if (a == null) {
            throw new IllegalArgumentException("No existe la asignatura " + cod);
        }
        return a;
    }

    public void actualizar(String codigo, String nombre, int horasSemanales)
            throws CargaHorariaExcedidaException {
        String cod = normalizar(codigo);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Asignatura a = em.find(Asignatura.class, cod);
            if (a == null) {
                throw new IllegalArgumentException("No existe la asignatura " + cod);
            }

            if (horasSemanales < a.getHorasAsignadas()) {
                throw new CargaHorariaExcedidaException(String.format(
                        "%s ya tiene %d hs asignadas a docentes; no puede quedar en %d",
                        cod, a.getHorasAsignadas(), horasSemanales));
            }
            a.setNombre(nombre.trim());
            a.setHorasSemanales(horasSemanales);
            em.getTransaction().commit();
            asignaturas.put(cod, a);
            log.info("Modificación asignatura " + cod);
        } catch (CargaHorariaExcedidaException | RuntimeException e) {
            log.error("Modificación de asignatura rechazada: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
    }

    public void eliminar(String codigo) {
        String cod = normalizar(codigo);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Asignatura a = em.find(Asignatura.class, cod);
            if (a == null) {
                throw new IllegalArgumentException("No existe la asignatura " + cod);
            }
            em.remove(a);
            em.getTransaction().commit();
            asignaturas.remove(cod);
            log.info("Baja asignatura " + cod);
        } catch (RuntimeException e) {
            log.error("Baja de asignatura rechazada: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
    }

    private String normalizar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        return codigo.trim().toUpperCase();
    }

    private void cerrar(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }
}
