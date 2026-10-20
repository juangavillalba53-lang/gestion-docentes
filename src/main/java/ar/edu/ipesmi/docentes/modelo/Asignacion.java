package ar.edu.ipesmi.docentes.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"docente_id", "asignatura_codigo"}))
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asignatura_codigo")
    private Asignatura asignatura;

    @Column(nullable = false)
    private int horas;

    public Asignacion() { }

    public Asignacion(Docente docente, Asignatura asignatura, int horas) {
        this.docente = docente;
        this.asignatura = asignatura;
        this.horas = horas;
    }

    public Long getId() { return id; }
    public Docente getDocente() { return docente; }
    public Asignatura getAsignatura() { return asignatura; }
    public int getHoras() { return horas; }
    public void setHoras(int horas) { this.horas = horas; }
}