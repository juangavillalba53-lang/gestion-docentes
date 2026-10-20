package ar.edu.ipesmi.docentes.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 80)
    private String especialidad;

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();

    public Docente() { }  // JPA necesita un constructor vacío

    public Docente(String nombre, String email, String especialidad) {
        this.nombre = nombre;
        this.email = email;
        this.especialidad = especialidad;
    }

    /** Suma de horas de todas las asignaturas que dicta. */
    public int getCargaHorariaTotal() {
        return asignaciones.stream().mapToInt(Asignacion::getHoras).sum();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public List<Asignacion> getAsignaciones() { return asignaciones; }

    @Override
    public String toString() { return nombre; }
}