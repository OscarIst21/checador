package modelos;

public class EmpleadoDatosExtra {
    private String id;
    private String cctNo;
    private String diaN;
    private String horaEntradaReal;
    private String horaSalidaReal;
    private String horarioMixto;

    public EmpleadoDatosExtra() {   
    }

    public EmpleadoDatosExtra(String id, String cctNo, String diaN, String horaEntradaReal,
    							String horaSalidaReal, String horarioMixto) {
        this.id = id;
        this.cctNo = cctNo;
        this.diaN = diaN;
        this.horaEntradaReal = horaEntradaReal;
        this.horaSalidaReal = horaSalidaReal;
        this.horarioMixto = horarioMixto;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    // Método toString para representar los datos del empleado
    @Override
    public String toString() {
        return "EmpleadoDatosExtra{" +
                "id='" + id + '\'' +
                ", cctNo='" + cctNo + '\'' +
                ", diaN='" + diaN + '\'' +
                ", horaEntradaReal='" + horaEntradaReal + '\'' +
                ", horaSalidaReal='" + horaSalidaReal + '\'' +
                ", horarioMixto='" + horarioMixto + '\'' +
                '}';
    }
}
