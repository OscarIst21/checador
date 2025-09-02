import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sqlite.SQLiteConnection;

import modelos.Empleado;
import modelos.EmpleadoDatosExtra;

public class BaseDeDatosManager {
    private static final String DB_URL;
    private static final Object DB_LOCK = new Object();

    static {
        String jarPath;
        try {
            jarPath = new File(BaseDeDatosManager.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent();
        } catch (Exception e) {
            jarPath = new File(".").getAbsolutePath();
        }

        String dataPath = jarPath + File.separator + "data";
        crearCarpetaSiNoExiste(dataPath);

        String dbPath = dataPath + File.separator + "bd_empleados.db";
        verificarArchivoBaseDatos(dbPath);

        // Añadido modo WAL para mejor concurrencia
        DB_URL = "jdbc:sqlite:" + dbPath + "?journal_mode=WAL";
        System.out.println("Ruta de la base de datos: " + DB_URL);
    }

    private static void crearCarpetaSiNoExiste(String rutaCarpeta) {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("Carpeta creada: " + rutaCarpeta);
            } else {
                System.err.println("No se pudo crear la carpeta: " + rutaCarpeta);
            }
        }
    }

    private static void verificarArchivoBaseDatos(String rutaBaseDatos) {
        File archivoBD = new File(rutaBaseDatos);
        if (!archivoBD.exists()) {
            try {
                if (archivoBD.createNewFile()) {
                    System.out.println("Base de datos creada en: " + archivoBD.getAbsolutePath());
                    inicializarBaseDatos(rutaBaseDatos);
                } else {
                    System.err.println("No se pudo crear la base de datos: " + archivoBD.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("La base de datos ya existe en: " + archivoBD.getAbsolutePath());
            // Verificar que las tablas existan incluso si la base de datos ya existe
            verificarYCrearTablas(rutaBaseDatos);
        }
    }

    private static void inicializarBaseDatos(String rutaBaseDatos) {
        verificarYCrearTablas(rutaBaseDatos);
    }

    private static void verificarYCrearTablas(String rutaBaseDatos) {
        String crearTablaHorariosSQL = """
                CREATE TABLE IF NOT EXISTS horarios (
                    id TEXT,
                    diaN TEXT,
                    horaEntradaReal TEXT,
                    horaSalidaReal TEXT,
                    PRIMARY KEY (id, diaN, horaEntradaReal, horaSalidaReal)
                );
                """;

        String crearTablaEmpleadosNombreSQL = """
                CREATE TABLE IF NOT EXISTS empleadosNombre (
                    id TEXT PRIMARY KEY,
                    nombre TEXT,
                    puesto TEXT,
                    jornada TEXT,
                    cct TEXT
                );
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos);
                Statement stmt = conn.createStatement()) {

            // Recrear la tabla horarios para asegurar la estructura correcta
            try {
                stmt.execute("DROP TABLE IF EXISTS horarios");
                stmt.execute(crearTablaHorariosSQL);
                System.out.println("Tabla horarios recreada con nueva estructura.");
            } catch (SQLException e) {
                System.err.println("Error al recrear tabla horarios: " + e.getMessage());
                // Si falla, intentar crear normalmente
                stmt.execute(crearTablaHorariosSQL);
            }

            stmt.execute(crearTablaEmpleadosNombreSQL);
            System.out.println("Tablas 'horarios' y 'empleadosNombre' verificadas/creadas correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al verificar/crear tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarYCrearTablasSiEsNecesario() {
        // Obtener la ruta de la base de datos desde la URL
        String rutaBaseDatos = DB_URL.replace("jdbc:sqlite:", "").replace("?journal_mode=WAL", "");
        verificarYCrearTablas(rutaBaseDatos);

        // Verificar el contenido de las tablas después de crearlas
        verificarContenidoTablas();
    }

    private void verificarContenidoTablas() {
        try (Connection conn = getConnection()) {
            // Verificar tabla horarios
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM horarios")) {
                if (rs.next()) {
                    System.out.println("Tabla 'horarios' existe con " + rs.getInt("total") + " registros");
                }
            } catch (SQLException e) {
                System.err.println("Error al verificar tabla 'horarios': " + e.getMessage());
            }

            // Verificar tabla empleadosNombre
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM empleadosNombre")) {
                if (rs.next()) {
                    System.out.println("Tabla 'empleadosNombre' existe con " + rs.getInt("total") + " registros");
                }
            } catch (SQLException e) {
                System.err.println("Error al verificar tabla 'empleadosNombre': " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar contenido de tablas: " + e.getMessage());
        }
    }

    // Método helper para obtener conexiones
    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        ((SQLiteConnection) conn).setBusyTimeout(5000); // 5 seconds timeout
        return conn;
    }

    // Método helper para cerrar recursos
    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error al cerrar recurso: " + e.getMessage());
                }
            }
        }
    }

    public void actualizarDatos(List<EmpleadoDatosExtra> empleadosDatos) {
        if (empleadosDatos.isEmpty()) {
            System.out.println("La lista de empleados está vacía. No se realizaron cambios.");
            return;
        }

        // Verificar que las tablas existan antes de proceder
        verificarYCrearTablasSiEsNecesario();

        synchronized (DB_LOCK) {
            String eliminarHorariosSQL = "DELETE FROM horarios WHERE id = ?";
            String insertarHorariosSQL = """
                    INSERT INTO horarios (id, diaN, horaEntradaReal, horaSalidaReal)
                    VALUES (?, ?, ?, ?);
                    """;

            Connection conn = null;
            try {
                conn = getConnection();
                conn.setAutoCommit(false);

                System.out.println("Iniciando actualización de " + empleadosDatos.size() + " registros de horarios");

                try (PreparedStatement pstmtEliminar = conn.prepareStatement(eliminarHorariosSQL);
                        PreparedStatement pstmtInsertar = conn.prepareStatement(insertarHorariosSQL)) {

                    Map<String, List<EmpleadoDatosExtra>> empleadosPorId = empleadosDatos.stream()
                            .collect(Collectors.groupingBy(EmpleadoDatosExtra::getId));

                    System.out.println("Empleados únicos a procesar: " + empleadosPorId.size());

                    for (Map.Entry<String, List<EmpleadoDatosExtra>> entry : empleadosPorId.entrySet()) {
                        String idEmpleado = entry.getKey();
                        List<EmpleadoDatosExtra> registros = entry.getValue();

                        System.out.println(
                                "Procesando empleado ID: " + idEmpleado + " con " + registros.size() + " horarios");

                        // Eliminar horarios existentes para este empleado
                        pstmtEliminar.setString(1, idEmpleado);
                        int filasEliminadas = pstmtEliminar.executeUpdate();
                        System.out.println("Horarios eliminados para empleado " + idEmpleado + ": " + filasEliminadas);

                        // Insertar nuevos horarios
                        for (EmpleadoDatosExtra empleado : registros) {
                            pstmtInsertar.setString(1, empleado.getId());
                            pstmtInsertar.setString(2, empleado.getDiaN());
                            pstmtInsertar.setString(3, empleado.getHoraEntradaReal());
                            pstmtInsertar.setString(4, empleado.getHoraSalidaReal());
                            pstmtInsertar.addBatch();

                            System.out.println("Agregado a batch: " + empleado.toString());
                        }
                        pstmtInsertar.executeBatch();
                        System.out.println("Batch ejecutado para empleado " + idEmpleado);
                    }

                    conn.commit();
                    System.out.println("Transacción completada exitosamente");
                } catch (SQLException e) {
                    if (conn != null) {
                        try {
                            conn.rollback();
                            System.err.println("Rollback realizado debido a error: " + e.getMessage());
                        } catch (SQLException ex) {
                            System.err.println("Error al hacer rollback: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    System.err.println("Error en actualizarDatos: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.err.println("Error de conexión en actualizarDatos: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeResources(conn);
            }
        }
    }

    public Empleado obtenerEmpleadoPorId(String id) {
        String query = "SELECT id, nombre, puesto, jornada, cct FROM empleadosNombre WHERE id = ?";
        Empleado empleado = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                empleado = new Empleado();
                empleado.setId(rs.getString("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmpleadoPuesto(rs.getString("puesto"));
                empleado.setJornada(rs.getString("jornada"));
                empleado.setCct(rs.getString("cct"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return empleado;
    }

    public List<EmpleadoDatosExtra> obtenerHorariosPorId(String id) {
        List<EmpleadoDatosExtra> horarios = new ArrayList<>();
        String query = "SELECT id, diaN, horaEntradaReal, horaSalidaReal FROM horarios WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                EmpleadoDatosExtra horario = new EmpleadoDatosExtra(
                        rs.getString("id"),
                        rs.getString("diaN"),
                        rs.getString("horaEntradaReal"),
                        rs.getString("horaSalidaReal"));
                horarios.add(horario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return horarios;
    }

    public void actualizarHorarioPorDia(String id, String diaN, String horaEntradaReal, String nuevaHoraSalidaReal) {
        synchronized (DB_LOCK) {
            String actualizarSQL = """
                    UPDATE horarios
                    SET horaSalidaReal = ?
                    WHERE id = ? AND diaN = ? AND horaEntradaReal = ?;
                    """;
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(actualizarSQL);
                pstmt.setString(1, nuevaHoraSalidaReal);
                pstmt.setString(2, id);
                pstmt.setString(3, diaN);
                pstmt.setString(4, horaEntradaReal);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeResources(pstmt, conn);
            }
        }
    }

    public void eliminarHorarioPorDia(String id, String diaN, String horaEntradaReal) {
        synchronized (DB_LOCK) {
            String eliminarSQL = """
                    DELETE FROM horarios
                    WHERE id = ? AND diaN = ? AND horaEntradaReal = ?;
                    """;
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(eliminarSQL);
                pstmt.setString(1, id);
                pstmt.setString(2, diaN);
                pstmt.setString(3, horaEntradaReal);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeResources(pstmt, conn);
            }
        }
    }

    public void insertarOActualizarEmpleado(Empleado empleado) {
        synchronized (DB_LOCK) {
            // Primero verificar que las tablas existan
            verificarYCrearTablasSiEsNecesario();

            String insertarOActualizarSQL = """
                    INSERT INTO empleadosNombre (id, nombre, puesto, jornada, cct)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT(id) DO UPDATE SET
                        nombre = excluded.nombre,
                        puesto = excluded.puesto,
                        jornada = excluded.jornada,
                        cct = excluded.cct
                    """;

            Connection conn = null;
            try {
                conn = getConnection();
                conn.setAutoCommit(false);

                try (PreparedStatement pstmt = conn.prepareStatement(insertarOActualizarSQL)) {
                    pstmt.setString(1, empleado.getId());
                    pstmt.setString(2, empleado.getNombre());
                    pstmt.setString(3, empleado.getEmpleadoPuesto());
                    pstmt.setString(4, empleado.getJornada());
                    pstmt.setString(5, empleado.getCct());
                    pstmt.executeUpdate();

                    conn.commit();
                    System.out.println("Empleado insertado/actualizado correctamente: " + empleado.getId());
                } catch (SQLException e) {
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            System.err.println("Error al hacer rollback: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    System.err.println("Error al insertar/actualizar empleado: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.err.println("Error de conexión: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeResources(conn);
            }
        }
    }

    public List<EmpleadoDatosExtra> obtenerTodosLosHorarios() {
        List<EmpleadoDatosExtra> empleados = new ArrayList<>();
        String query = "SELECT id, diaN, horaEntradaReal, horaSalidaReal FROM horarios";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                EmpleadoDatosExtra empleado = new EmpleadoDatosExtra(
                        rs.getString("id"),
                        rs.getString("diaN"),
                        rs.getString("horaEntradaReal"),
                        rs.getString("horaSalidaReal"));
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return empleados;
    }

    public List<Empleado> obtenerEmpleadosNombre() {
        List<Empleado> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, puesto, jornada, cct FROM empleadosNombre";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId(rs.getString("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmpleadoPuesto(rs.getString("puesto"));
                empleado.setJornada(rs.getString("jornada"));
                empleado.setCct(rs.getString("cct"));
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return empleados;
    }

    public List<Empleado> obtenerEmpleadosPorCCT(String cct) {
        String sql = "SELECT * FROM empleadosNombre WHERE cct = ?";
        List<Empleado> empleados = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cct);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId(rs.getString("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmpleadoPuesto(rs.getString("puesto"));
                empleado.setJornada(rs.getString("jornada"));
                empleado.setCct(rs.getString("cct"));
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return empleados;
    }

    public List<EmpleadoDatosExtra> obtenerHorariosPorCCT(String cct) {
        String sql = "SELECT h.* FROM horarios h " +
                "JOIN empleadosNombre e ON h.id = e.id WHERE e.cct = ?";
        List<EmpleadoDatosExtra> horarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cct);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                EmpleadoDatosExtra horario = new EmpleadoDatosExtra(
                        rs.getString("id"),
                        rs.getString("diaN"),
                        rs.getString("horaEntradaReal"),
                        rs.getString("horaSalidaReal"));
                horarios.add(horario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return horarios;
    }
}