package modelos;

public class EmpleadoDatosExtra {
    private String id;
    private String diaN;
    private String horaEntradaReal;
    private String horaSalidaReal;

    public EmpleadoDatosExtra() {
    }

    public EmpleadoDatosExtra(String id, String diaN, String horaEntradaReal,
                               String horaSalidaReal) {
        this.id = id;
        this.diaN = diaN;
        this.horaEntradaReal = horaEntradaReal;
        this.horaSalidaReal = horaSalidaReal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   

    public String getDiaN() {
        return diaN;
    }

    public void setDiaN(String diaN) {
        this.diaN = diaN;
    }

   
	public String getHoraEntradaReal() {
        return horaEntradaReal;
    }

    public void setHoraEntradaReal(String horaEntradaReal) {
        this.horaEntradaReal = horaEntradaReal;
    }

    public String getHoraSalidaReal() {
        return horaSalidaReal;
    }

    public void setHoraSalidaReal(String horaSalidaReal) {
        this.horaSalidaReal = horaSalidaReal;
    }

	@Override
	public String toString() {
		return "EmpleadoDatosExtra [id=" + id + ", diaN=" + diaN + ", horaEntradaReal=" + horaEntradaReal
				+ ", horaSalidaReal=" + horaSalidaReal + ", getId()=" + getId() + ", getDiaN()=" + getDiaN()
				+ ", getHoraEntradaReal()=" + getHoraEntradaReal() + ", getHoraSalidaReal()=" + getHoraSalidaReal()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

  
   
}
