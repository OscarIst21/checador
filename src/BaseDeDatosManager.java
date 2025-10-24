import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            verificarYCrearTablas(rutaBaseDatos);
        }
    }

    private static void inicializarBaseDatos(String rutaBaseDatos) {
        verificarYCrearTablas(rutaBaseDatos);
    }

    private static void verificarYCrearTablas(String rutaBaseDatos) {
        String crearTablaEmpleadosSQL = """
                CREATE TABLE IF NOT EXISTS empleados (
                    id TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    puesto TEXT,
                    jornada TEXT,
                    cct TEXT,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;

        String crearTablaHorariosSQL = """
                CREATE TABLE IF NOT EXISTS horarios (
                    horario_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    empleado_id TEXT NOT NULL,
                    dia TEXT NOT NULL,
                    hora_entrada TEXT NOT NULL,
                    hora_salida TEXT,
                    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE,
                    UNIQUE(empleado_id, dia, hora_entrada)
                );
                """;

        String crearIndicesSQL = """
                CREATE INDEX IF NOT EXISTS idx_horarios_empleado_id ON horarios(empleado_id);
                CREATE INDEX IF NOT EXISTS idx_horarios_dia ON horarios(dia);
                CREATE INDEX IF NOT EXISTS idx_empleados_cct ON empleados(cct);
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos);
                Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute(crearTablaEmpleadosSQL);
            stmt.execute(crearTablaHorariosSQL);
            stmt.execute(crearIndicesSQL);

            System.out.println("Esquema de base de datos relacional creado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al verificar/crear tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarYCrearTablasSiEsNecesario() {
        String rutaBaseDatos = DB_URL.replace("jdbc:sqlite:", "").replace("?journal_mode=WAL", "");
        verificarYCrearTablas(rutaBaseDatos);
        verificarContenidoTablas();
    }

    private void verificarContenidoTablas() {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM empleados")) {
                if (rs.next()) {
                    System.out.println("Tabla 'empleados' existe con " + rs.getInt("total") + " registros");
                }
            }

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM horarios")) {
                if (rs.next()) {
                    System.out.println("Tabla 'horarios' existe con " + rs.getInt("total") + " registros");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar contenido de tablas: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        ((SQLiteConnection) conn).setBusyTimeout(5000);
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        
        return conn;
    }

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

    // MÉTODO QUE FALTABA - RESTAURADO
    public List<Empleado> obtenerEmpleadosNombre() {
        List<Empleado> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, puesto, jornada, cct FROM empleados ORDER BY nombre";
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

    public List<Empleado> obtenerTodosLosEmpleados() {
        return obtenerEmpleadosNombre(); // Ahora usa el mismo método
    }

    public void actualizarDatos(List<EmpleadoDatosExtra> empleadosDatos) {
        if (empleadosDatos.isEmpty()) {
            System.out.println("La lista de empleados está vacía. No se realizaron cambios.");
            return;
        }

        verificarYCrearTablasSiEsNecesario();

        synchronized (DB_LOCK) {
            String insertarHorariosSQL = """
                    INSERT OR REPLACE INTO horarios (empleado_id, dia, hora_entrada, hora_salida)
                    VALUES (?, ?, ?, ?);
                    """;

            Connection conn = null;
            try {
                conn = getConnection();
                conn.setAutoCommit(false);

                System.out.println("Iniciando actualización de " + empleadosDatos.size() + " registros de horarios");

                try (PreparedStatement pstmtInsertar = conn.prepareStatement(insertarHorariosSQL)) {

                    Map<String, List<EmpleadoDatosExtra>> empleadosPorId = empleadosDatos.stream()
                            .collect(Collectors.groupingBy(EmpleadoDatosExtra::getId));

                    System.out.println("Empleados únicos a procesar: " + empleadosPorId.size());

                    for (Map.Entry<String, List<EmpleadoDatosExtra>> entry : empleadosPorId.entrySet()) {
                        String idEmpleado = entry.getKey();
                        List<EmpleadoDatosExtra> registros = entry.getValue();

                        Map<String, EmpleadoDatosExtra> registrosUnicos = new HashMap<>();
                        for (EmpleadoDatosExtra empleado : registros) {
                            String clave = empleado.getId() + "|" + empleado.getDiaN() + "|" + empleado.getHoraEntradaReal();
                            registrosUnicos.put(clave, empleado);
                        }
                        
                        List<EmpleadoDatosExtra> registrosSinDuplicados = new ArrayList<>(registrosUnicos.values());
                        
                        System.out.println(
                                "Procesando empleado ID: " + idEmpleado + " con " + registrosSinDuplicados.size() + " horarios únicos");

                        for (EmpleadoDatosExtra empleado : registrosSinDuplicados) {
                            pstmtInsertar.setString(1, empleado.getId());
                            pstmtInsertar.setString(2, empleado.getDiaN());
                            pstmtInsertar.setString(3, empleado.getHoraEntradaReal());
                            pstmtInsertar.setString(4, empleado.getHoraSalidaReal());
                            pstmtInsertar.addBatch();

                            System.out.println("Agregado a batch: " + empleado.toString());
                        }
                    }
                    
                    pstmtInsertar.executeBatch();
                    System.out.println("Todos los batches ejecutados correctamente");

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
        String query = "SELECT id, nombre, puesto, jornada, cct FROM empleados WHERE id = ?";
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
        String query = "SELECT empleado_id, dia, hora_entrada, hora_salida FROM horarios WHERE empleado_id = ? ORDER BY dia, hora_entrada";
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
                        rs.getString("empleado_id"),
                        rs.getString("dia"),
                        rs.getString("hora_entrada"),
                        rs.getString("hora_salida"));
                horarios.add(horario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return horarios;
    }

    public void actualizarHorarioPorDia(String id, String dia, String horaEntradaReal, String nuevaHoraSalidaReal) {
        synchronized (DB_LOCK) {
            String actualizarSQL = """
                    UPDATE horarios
                    SET hora_salida = ?
                    WHERE empleado_id = ? AND dia = ? AND hora_entrada = ?;
                    """;
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(actualizarSQL);
                pstmt.setString(1, nuevaHoraSalidaReal);
                pstmt.setString(2, id);
                pstmt.setString(3, dia);
                pstmt.setString(4, horaEntradaReal);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeResources(pstmt, conn);
            }
        }
    }

    public void eliminarHorarioPorDia(String id, String dia, String horaEntradaReal) {
        synchronized (DB_LOCK) {
            String eliminarSQL = """
                    DELETE FROM horarios
                    WHERE empleado_id = ? AND dia = ? AND hora_entrada = ?;
                    """;
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(eliminarSQL);
                pstmt.setString(1, id);
                pstmt.setString(2, dia);
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
            verificarYCrearTablasSiEsNecesario();

            String insertarOActualizarSQL = """
                    INSERT INTO empleados (id, nombre, puesto, jornada, cct, fecha_actualizacion)
                    VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                    ON CONFLICT(id) DO UPDATE SET
                        nombre = excluded.nombre,
                        puesto = excluded.puesto,
                        jornada = excluded.jornada,
                        cct = excluded.cct,
                        fecha_actualizacion = CURRENT_TIMESTAMP
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
        List<EmpleadoDatosExtra> horarios = new ArrayList<>();
        String query = "SELECT empleado_id, dia, hora_entrada, hora_salida FROM horarios ORDER BY empleado_id, dia, hora_entrada";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                EmpleadoDatosExtra horario = new EmpleadoDatosExtra(
                        rs.getString("empleado_id"),
                        rs.getString("dia"),
                        rs.getString("hora_entrada"),
                        rs.getString("hora_salida"));
                horarios.add(horario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return horarios;
    }

    public List<Empleado> obtenerEmpleadosPorCCT(String cct) {
        String sql = "SELECT * FROM empleados WHERE cct = ? ORDER BY nombre";
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
        String sql = "SELECT h.empleado_id, h.dia, h.hora_entrada, h.hora_salida " +
                "FROM horarios h " +
                "JOIN empleados e ON h.empleado_id = e.id " +
                "WHERE e.cct = ? " +
                "ORDER BY h.empleado_id, h.dia, h.hora_entrada";
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
                        rs.getString("empleado_id"),
                        rs.getString("dia"),
                        rs.getString("hora_entrada"),
                        rs.getString("hora_salida"));
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