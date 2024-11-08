public class Checadas {
    private String id;
    private String nombre;
    private String empleadoPuesto;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String horaEntrada2;
    private String horaSalida2;
    private String horarioMixto;
    private String horaEntradaReal;
    private String horaSalidaReal;
    private String cctNo;
    private String diaN;
    private String jornada;
    
    public Checadas() {
    }

    public Checadas(String id, String nombre, String empleadoPuesto, String fecha, String horaEntrada, String horaSalida,
                    String horaEntrada2, String horaSalida2, String horarioMixto, String horaEntradaReal, 
                    String horaSalidaReal, String cctNo, String diaN, String jornada) {
        this.id = id;
        this.nombre = nombre;
        this.empleadoPuesto = empleadoPuesto;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.horaEntrada2 = horaEntrada2;
        this.horaSalida2 = horaSalida2;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraEntrada() {
        return horaEntrada;
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

    @Override
    public String toString() {
        return "Checadas{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", empleadoPuesto='" + empleadoPuesto + '\'' +
                ", fecha='" + fecha + '\'' +
                ", horaEntrada='" + horaEntrada + '\'' +
                ", horaSalida='" + horaSalida + '\'' +
                ", horaEntrada2='" + horaEntrada2 + '\'' +
                ", horaSalida2='" + horaSalida2 + '\'' +
                ", horarioMixto='" + horarioMixto + '\'' +
                ", horaEntradaReal='" + horaEntradaReal + '\'' +
                ", horaSalidaReal='" + horaSalidaReal + '\'' +
                ", cctNo='" + cctNo + '\'' +
                ", diaN='" + diaN + '\'' +
                ", jornada='" + jornada + '\'' +
                '}';
    }
}
