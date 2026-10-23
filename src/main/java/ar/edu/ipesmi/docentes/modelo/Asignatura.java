package ar.edu.ipesmi.docentes.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asignaturas")
public class Asignatura {

    @Id
    @Column(length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "horas_semanales", nullable = false)
    private int horasSemanales;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();

    public Asignatura() { }

    public Asignatura(String codigo, String nombre, int horasSemanales) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.horasSemanales = horasSemanales;
    }

    public int getHorasAsignadas() {
        return asignaciones.stream().mapToInt(Asignacion::getHoras).sum();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getHorasSemanales() { return horasSemanales; }
    public void setHorasSemanales(int horasSemanales) { this.horasSemanales = horasSemanales; }
    public List<Asignacion> getAsignaciones() { return asignaciones; }

    @Override
    public String toString() { return codigo + " - " + nombre; }
}
