package ar.edu.ipesmi.docentes.servicio;

import ar.edu.ipesmi.docentes.excepciones.CargaHorariaExcedidaException;
import ar.edu.ipesmi.docentes.excepciones.DocenteNoEncontradoException;
import ar.edu.ipesmi.docentes.modelo.Asignacion;
import ar.edu.ipesmi.docentes.modelo.Asignatura;
import ar.edu.ipesmi.docentes.modelo.Docente;
import ar.edu.ipesmi.docentes.util.JPAUtil;
import ar.edu.ipesmi.docentes.util.LoggerSistema;
import jakarta.persistence.EntityManager;

public class AsignacionServicio {

    private final LoggerSistema log = LoggerSistema.getInstance();

    public Asignacion asignar(Long docenteId, String codigoAsignatura, int horas)
            throws DocenteNoEncontradoException, CargaHorariaExcedidaException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Docente d = em.find(Docente.class, docenteId);
            if (d == null) {
                throw new DocenteNoEncontradoException(docenteId);
            }
            String cod = codigoAsignatura.trim().toUpperCase();
            Asignatura a = em.find(Asignatura.class, cod);
            if (a == null) {
                throw new IllegalArgumentException("No existe la asignatura " + cod);
            }
            boolean yaAsignado = d.getAsignaciones().stream()
                    .anyMatch(x -> x.getAsignatura().getCodigo().equals(cod));
            if (yaAsignado) {
                throw new IllegalArgumentException(d.getNombre() + " ya dicta " + cod);
            }

            Validaciones.validarCargaHoraria(
                    d.getCargaHorariaTotal(), a.getHorasAsignadas(), a.getHorasSemanales(), horas);

            Asignacion asignacion = new Asignacion(d, a, horas);
            d.getAsignaciones().add(asignacion);
            a.getAsignaciones().add(asignacion);
            em.persist(asignacion);
            em.getTransaction().commit();

            log.info(String.format("Asignación: %s -> %s (%d hs)", d.getNombre(), cod, horas));
            return asignacion;

        } catch (DocenteNoEncontradoException | CargaHorariaExcedidaException | RuntimeException e) {
            log.error("Asignación rechazada: " + e.getMessage());
            throw e;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public void quitar(Long asignacionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Asignacion asig = em.find(Asignacion.class, asignacionId);
            if (asig == null) {
                throw new IllegalArgumentException("No existe la asignación " + asignacionId);
            }
            asig.getDocente().getAsignaciones().remove(asig);
            asig.getAsignatura().getAsignaciones().remove(asig);
            em.remove(asig);
            em.getTransaction().commit();
            log.info("Asignación quitada id " + asignacionId);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
