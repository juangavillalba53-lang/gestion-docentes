package ar.edu.ipesmi.docentes.servicio;

import ar.edu.ipesmi.docentes.excepciones.DocenteNoEncontradoException;
import ar.edu.ipesmi.docentes.excepciones.EmailInvalidoException;
import ar.edu.ipesmi.docentes.modelo.Docente;
import ar.edu.ipesmi.docentes.util.JPAUtil;
import ar.edu.ipesmi.docentes.util.LoggerSistema;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class DocenteServicio {

    private final ArrayList<Docente> docentes = new ArrayList<>();
    private final LoggerSistema log = LoggerSistema.getInstance();

    public Docente registrar(String nombre, String email, String especialidad)
            throws EmailInvalidoException {
        try {
            Validaciones.validarEmail(email);
        } catch (EmailInvalidoException e) {
            log.error("Alta de docente rechazada: " + e.getMessage());
            throw e;
        }

        Docente d = new Docente(nombre.trim(), email.trim(), especialidad);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(d);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            log.error("Error al registrar docente: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
        docentes.add(d);
        log.info("Alta docente: " + d.getNombre() + " (id " + d.getId() + ")");
        return d;
    }

    public List<Docente> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Docente> resultado = em.createQuery(
                    "SELECT DISTINCT d FROM Docente d LEFT JOIN FETCH d.asignaciones ORDER BY d.nombre",
                    Docente.class).getResultList();
            docentes.clear();
            docentes.addAll(resultado);
            return docentes;
        } finally {
            cerrar(em);
        }
    }

    public Docente buscarPorId(Long id) throws DocenteNoEncontradoException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Docente d = em.find(Docente.class, id);
            if (d == null) {
                log.error("Búsqueda fallida: docente id " + id);
                throw new DocenteNoEncontradoException(id);
            }
            return d;
        } finally {
            cerrar(em);
        }
    }

    public void actualizar(Long id, String nombre, String email, String especialidad)
            throws DocenteNoEncontradoException, EmailInvalidoException {
        Validaciones.validarEmail(email);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Docente d = em.find(Docente.class, id);
            if (d == null) {
                throw new DocenteNoEncontradoException(id);
            }
            d.setNombre(nombre.trim());
            d.setEmail(email.trim());
            d.setEspecialidad(especialidad);
            em.getTransaction().commit();
            log.info("Modificación docente id " + id);
        } catch (DocenteNoEncontradoException e) {
            log.error("Modificación fallida: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
    }

    public void eliminar(Long id) throws DocenteNoEncontradoException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Docente d = em.find(Docente.class, id);
            if (d == null) {
                throw new DocenteNoEncontradoException(id);
            }
            em.remove(d);
            em.getTransaction().commit();
            docentes.removeIf(x -> x.getId().equals(id));
            log.info("Baja docente id " + id);
        } catch (DocenteNoEncontradoException e) {
            log.error("Baja fallida: " + e.getMessage());
            throw e;
        } finally {
            cerrar(em);
        }
    }

    private void cerrar(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }
}
