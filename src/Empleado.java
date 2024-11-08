public class Empleado {
    private String id;
    private String nombre;
    private String empleadoPuesto;
    private String horarioMixto;
    private String horaEntradaReal;
    private String horaSalidaReal;
    private String cctNo;
    private String diaN;
    private String jornada;

    //ID,NombreCOmpleto, empleado puesto,  tipo de jornada,
    public Empleado() {
    }

    public Empleado(String id, String nombre, String empleadoPuesto, String horarioMixto,
                    String horaEntradaReal, String horaSalidaReal, String cctNo, 
                    String diaN, String jornada) {
        this.id = id;
        this.nombre = nombre;
        this.empleadoPuesto = empleadoPuesto;
        this.horarioMixto = horarioMixto;
        this.horaEntradaReal = horaEntradaReal;
        this.horaSalidaReal = horaSalidaReal;
        this.cctNo = cctNo;
        this.diaN = diaN;
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

    public String getHorarioMixto() {
        return horarioMixto;
    }

    public void setHorarioMixto(String horarioMixto) {
        this.horarioMixto = horarioMixto;
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

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    // Método toString para representación del objeto
    @Override
    public String toString() {
        return "Empleado [id=" + id + ", nombre=" + nombre + ", empleadoPuesto=" + empleadoPuesto +
               ", horarioMixto=" + horarioMixto + ", horaEntradaReal=" + horaEntradaReal + 
               ", horaSalidaReal=" + horaSalidaReal + ", cctNo=" + cctNo + ", diaN=" + diaN + 
               ", jornada=" + jornada + "]";
    }
}
