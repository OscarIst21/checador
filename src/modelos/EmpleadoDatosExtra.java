package modelos;

public class EmpleadoDatosExtra {
    private String id;
    private String cct;
    private String cctNo;
    private String diaN;
    private String horaEntradaReal;
    private String horaSalidaReal;
    private String horaSalidaDiaSiguiente;
    private String horarioMixto;
    private String anio;
    private String periodo_inicio;
    private String periodo_termino;

    public EmpleadoDatosExtra() {
    }

    public EmpleadoDatosExtra(String id, String cct, String cctNo, String diaN, String horaEntradaReal,
                               String horaSalidaReal,String horaSalidaDiaSiguiente, String horarioMixto, String anio, String periodo_inicio,
                               String periodo_termino) {
        this.id = id;
        this.cct = cct;
        this.cctNo = cctNo;
        this.diaN = diaN;
        this.horaEntradaReal = horaEntradaReal;
        this.horaSalidaReal = horaSalidaReal;
        this.horaSalidaDiaSiguiente=horaSalidaDiaSiguiente;
        this.horarioMixto = horarioMixto;
        this.anio = anio;
        this.periodo_inicio = periodo_inicio;
        this.periodo_termino = periodo_termino;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCct() {
        return cct;
    }

    public void setCct(String cct) {
        this.cct = cct;
    }

    public String getCctNo() {
        return cctNo;
    }

    public void setCctNo(String cctNo) {
        this.cctNo = cctNo;
    }

    public String getDiaN() {
        return diaN;
    }

    public void setDiaN(String diaN) {
        this.diaN = diaN;
    }

    public String getHoraSalidaDiaSiguiente() {
		return horaSalidaDiaSiguiente;
	}

	public void setHoraSalidaDiaSiguiente(String horaSalidaDiaSiguiente) {
		this.horaSalidaDiaSiguiente = horaSalidaDiaSiguiente;
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

    public String getHorarioMixto() {
        return horarioMixto;
    }

    public void setHorarioMixto(String horarioMixto) {
        this.horarioMixto = horarioMixto;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPeriodo_inicio() {
        return periodo_inicio;
    }

    public void setPeriodo_inicio(String periodo_inicio) {
        this.periodo_inicio = periodo_inicio;
    }

    public String getPeriodo_termino() {
        return periodo_termino;
    }

    public void setPeriodo_termino(String periodo_termino) {
        this.periodo_termino = periodo_termino;
    }

    @Override
    public String toString() {
        return "EmpleadoDatosExtra{" +
                "id='" + id + '\'' +
                ", cct='" + cct + '\'' +
                ", cctNo='" + cctNo + '\'' +
                ", diaN='" + diaN + '\'' +
                ", horaEntradaReal='" + horaEntradaReal + '\'' +
                ", horaSalidaReal='" + horaSalidaReal + '\'' +
                ", horarioMixto='" + horarioMixto + '\'' +
                ", anio='" + anio + '\'' +
                ", periodo_inicio='" + periodo_inicio + '\'' +
                ", periodo_termino='" + periodo_termino + '\'' +
                '}';
    }
}
