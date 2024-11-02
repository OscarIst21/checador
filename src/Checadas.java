public class Checadas {
    private String id;
    private String nombre;
    private String departamento;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String horaEntrada2;
    private String horaSalida2;
    private String retardo;
    private String salida;
    private String falta;
    private String total;
    private String notas;

    // Constructor vacío
    public Checadas() {
    }

    // Constructor con todos los parámetros
    public Checadas(String id, String nombre, String departamento, String fecha, String horaEntrada, String horaSalida,
                    String horaEntrada2, String horaSalida2, String retardo, String salida, String falta,
                    String total, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.horaEntrada2 = horaEntrada2;
        this.horaSalida2 = horaSalida2;
        this.retardo = retardo;
        this.salida = salida;
        this.falta = falta;
        this.total = total;
        this.notas = notas;
    }

    // Métodos getter y setter
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
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

    public String getRetardo() {
        return retardo;
    }

    public void setRetardo(String retardo) {
        this.retardo = retardo;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getFalta() {
        return falta;
    }

    public void setFalta(String falta) {
        this.falta = falta;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "Checadas{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", departamento='" + departamento + '\'' +
                ", fecha='" + fecha + '\'' +
                ", horaEntrada='" + horaEntrada + '\'' +
                ", horaSalida='" + horaSalida + '\'' +
                ", horaEntrada2='" + horaEntrada2 + '\'' +
                ", horaSalida2='" + horaSalida2 + '\'' +
                ", retardo='" + retardo + '\'' +
                ", salida='" + salida + '\'' +
                ", falta='" + falta + '\'' +
                ", total='" + total + '\'' +
                ", notas='" + notas + '\'' +
                '}';
    }
}
