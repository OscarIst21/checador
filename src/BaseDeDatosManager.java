import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelos.Empleado;
import modelos.EmpleadoDatosExtra;

public class BaseDeDatosManager {
    private static final String DB_URL = "jdbc:sqlite:data/bd_layout.db";

    public BaseDeDatosManager() {
        // Crear el directorio 'data' si no existe
        crearDirectorio();
        // Crear la base de datos y la tabla si no existen
        crearBaseDeDatos();
        crearBaseDeDatosEmpleado();
    }

    private void crearDirectorio() {
        File directorio = new File("data");
        if (!directorio.exists()) {
            directorio.mkdirs(); // Crea el directorio si no existe
        }
    }

    private void crearBaseDeDatos() {
        String crearTablaSQL = """
            CREATE TABLE IF NOT EXISTS empleados (
                id TEXT,
                cct TEXT,
                cctNo TEXT,
                diaN TEXT,
                horaEntradaReal TEXT,
                horaSalidaReal TEXT,
                horaSalidaDiaSiguiente TEXT,
                horarioMixto TEXT,
                anio TEXT,
                periodo_inicio TEXT,
                periodo_termino TEXT,
                PRIMARY KEY (id, diaN)
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(crearTablaSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarDatos(List<EmpleadoDatosExtra> empleadosDatos) {
        String insertarOActualizarSQL = """
            INSERT INTO empleados (id, cct, cctNo, diaN, horaEntradaReal, horaSalidaReal, horaSalidaDiaSiguiente, horarioMixto, anio, periodo_inicio, periodo_termino)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id, diaN) DO UPDATE SET
                cct = excluded.cct,
                cctNo = excluded.cctNo,
                diaN = excluded.diaN,
                horaEntradaReal = excluded.horaEntradaReal,
                horaSalidaReal = excluded.horaSalidaReal,
                horaSalidaDiaSiguiente = excluded.horaSalidaDiaSiguiente,
                horarioMixto = excluded.horarioMixto,
                anio = excluded.anio,
                periodo_inicio = excluded.periodo_inicio,
                periodo_termino = excluded.periodo_termino;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertarOActualizarSQL)) {
            for (EmpleadoDatosExtra empleado : empleadosDatos) {
                pstmt.setString(1, empleado.getId());
                pstmt.setString(2, empleado.getCct());
                pstmt.setString(3, empleado.getCctNo());
                pstmt.setString(4, empleado.getDiaN());
                pstmt.setString(5, empleado.getHoraEntradaReal());
                pstmt.setString(6, empleado.getHoraSalidaReal());
                pstmt.setString(7, empleado.getHoraSalidaDiaSiguiente());
                pstmt.setString(8, empleado.getHorarioMixto());
                pstmt.setString(9, empleado.getAnio());
                pstmt.setString(10, empleado.getPeriodo_inicio());
                pstmt.setString(11, empleado.getPeriodo_termino());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EmpleadoDatosExtra> obtenerEmpleados() {
        List<EmpleadoDatosExtra> empleados = new ArrayList<>();
        String query = "SELECT id, cct, cctNo, diaN, horaEntradaReal, horaSalidaReal, horaSalidaDiaSiguiente, horarioMixto, anio, periodo_inicio, periodo_termino FROM empleados";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Recorrer el resultado de la consulta
            while (rs.next()) {
                EmpleadoDatosExtra empleado = new EmpleadoDatosExtra();
                empleado.setId(rs.getString("id"));
                empleado.setCct(rs.getString("cct"));
                empleado.setCctNo(rs.getString("cctNo"));
                empleado.setDiaN(rs.getString("diaN"));
                empleado.setHoraEntradaReal(rs.getString("horaEntradaReal"));
                empleado.setHoraSalidaReal(rs.getString("horaSalidaReal"));
                empleado.setHoraSalidaDiaSiguiente(rs.getString("horaSalidaDiaSiguiente"));
                empleado.setHorarioMixto(rs.getString("horarioMixto"));
                empleado.setAnio(rs.getString("anio"));
                empleado.setPeriodo_inicio(rs.getString("periodo_inicio"));
                empleado.setPeriodo_termino(rs.getString("periodo_termino"));
                empleados.add(empleado); // Añadir a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empleados; // Devolver la lista de empleados
    }
    
    private void crearBaseDeDatosEmpleado() {
        String crearTablaSQL = """
            CREATE TABLE IF NOT EXISTS empleadosNombre (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                puesto TEXT,
                jornada TEXT
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(crearTablaSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertarOActualizarEmpleado(Empleado empleado) {
        String insertarOActualizarSQL = """
            INSERT INTO empleadosNombre (id, nombre, puesto, jornada)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                nombre = excluded.nombre,
                puesto = excluded.puesto,
                jornada = excluded.jornada;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertarOActualizarSQL)) {
            pstmt.setString(1, empleado.getId());
            pstmt.setString(2, empleado.getNombre());
            pstmt.setString(3, empleado.getEmpleadoPuesto());
            pstmt.setString(4, empleado.getJornada());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener todos los empleados desde la base de datos
    public List<Empleado> obtenerEmpleadosNombre() {
        List<Empleado> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, puesto, jornada FROM empleadosNombre";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Recorrer el resultado de la consulta
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId(rs.getString("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmpleadoPuesto(rs.getString("puesto"));
                empleado.setJornada(rs.getString("jornada"));
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleados;
    }
}