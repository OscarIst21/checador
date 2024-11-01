
public class Checadas {
	private int id;
	private String fecha;
	private String horaEntrada;
	private String horaSalida;
	private String horaEntrada2;
	private String horaSalida2;
	private String retardo;
	
	
    public Checadas() {
    }
    
    public Checadas(int id, String fecha, String horaEntrada, String horaSalida, String horaEntrada2, String horaSalida2,String retardo) {
        this.id=id;
    	this.fecha=fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.horaEntrada2 = horaEntrada2;
        this.horaSalida2 = horaSalida2;
        this.retardo=retardo;
    }

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHoraEntrada() {
		return horaEntrada;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHoraEntrada(String horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public String getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(String horaSalida) {
		this.horaSalida = horaSalida;
	}

	public String getHoraEntrada2() {
		return horaEntrada2;
	}

	public void setHoraEntrada2(String horaEntrada2) {
		this.horaEntrada2 = horaEntrada2;
	}

	public String getHoraSalida2() {
		return horaSalida2;
	}

	public void setHoraSalida2(String horaSalida2) {
		this.horaSalida2 = horaSalida2;
	}

	public String getRetardo() {
		return retardo;
	}

	public void setRetardo(String retardo) {
		this.retardo = retardo;
	}
    
}
