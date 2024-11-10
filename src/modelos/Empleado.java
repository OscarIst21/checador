package modelos;
public class Empleado {
    private String id;
    private String nombre;
    private String empleadoPuesto;
    private String jornada;

    //ID,NombreCOmpleto, empleado puesto,  tipo de jornada,
    public Empleado() {
    }

    public Empleado(String id, String nombre, String empleadoPuesto,  String jornada) {
        this.id = id;
        this.nombre = nombre;
        this.empleadoPuesto = empleadoPuesto;
        this.jornada = jornada;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(String empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public String getJornada() {
		return jornada;
	}

	public void setJornada(String jornada) {
		this.jornada = jornada;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", empleadoPuesto=" + empleadoPuesto + ", jornada="
				+ jornada + "]";
	}
    
}
