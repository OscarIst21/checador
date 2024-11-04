
public class Empleado {
    private String id;
    private String nombre;
    private String categoria;
    private String estado;
    private String jornada;
    private String total;
    public Empleado() {
    }

    public Empleado(String id, String nombre, String categoria, String estado, String jornada,String total) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.estado = estado;
        this.jornada = jornada;
        this.total=total;
    }

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
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

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getJornada() {
		return jornada;
	}

	public void setJornada(String jornada) {
		this.jornada = jornada;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", categoria=" + categoria + ", estado=" + estado
				+ ", jornada=" + jornada + "]";
	}


	

}
