import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelos.Empleado;
import modelos.EmpleadoDatosExtra;

public class BaseDeDatosManager {
    private static final String DB_URL;

    static {
        String jarPath;
        try {
            // Obtener la ruta del JAR o del IDE (dependiendo de dónde se ejecute)
            jarPath = new File(BaseDeDatosManager.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent();
        } catch (Exception e) {
            // Si no se puede obtener la ruta, usamos la ruta actual como fallback
            jarPath = new File(".").getAbsolutePath();
        }

        // Definir la ruta de la carpeta 'data' donde se almacenará la base de datos
        String dataPath = jarPath + File.separator + "data";
        // Crear la carpeta 'data' si no existe
        crearCarpetaSiNoExiste(dataPath);

        // Configurar la ruta completa para la base de datos
        String dbPath = dataPath + File.separator + "bd_empleados.db";
        // Verificar si el archivo de la base de datos existe y crearlo si no
        verificarArchivoBaseDatos(dbPath);

        // Configurar la URL para SQLite
        DB_URL = "jdbc:sqlite:" + dbPath;
        System.out.println("Ruta de la base de datos: " + DB_URL); // Imprimir la ruta de la base de datos
    }

    // Método para crear la carpeta si no existe
    private static void crearCarpetaSiNoExiste(String rutaCarpeta) {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            // Crear la carpeta
            if (carpeta.mkdirs()) {
                System.out.println("Carpeta creada: " + rutaCarpeta);
            } else {
                System.err.println("No se pudo crear la carpeta: " + rutaCarpeta);
            }
        }
    }

    // Método para verificar si el archivo de la base de datos existe
    private static void verificarArchivoBaseDatos(String rutaBaseDatos) {
        File archivoBD = new File(rutaBaseDatos);
        if (!archivoBD.exists()) {
            try {
                if (archivoBD.createNewFile()) {
                    // Si el archivo no existe, crear uno nuevo
                    System.out.println("Base de datos creada en: " + archivoBD.getAbsolutePath());
                    // Inicializar la base de datos (crear las tablas)
                    inicializarBaseDatos(rutaBaseDatos);
                } else {
                    System.err.println("No se pudo crear la base de datos: " + archivoBD.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("La base de datos ya existe en: " + archivoBD.getAbsolutePath());
        }
    }

    // Método para inicializar la base de datos y crear las tablas
    private static void inicializarBaseDatos(String rutaBaseDatos) {
        String crearTablaHorariosSQL = """
            CREATE TABLE IF NOT EXISTS horarios (
                id TEXT,
                diaN TEXT,
                horaEntradaReal TEXT,
                horaSalidaReal TEXT,
                PRIMARY KEY (id, diaN, horaEntradaReal)
            );
        """;

        String crearTablaEmpleadosNombreSQL = """
            CREATE TABLE IF NOT EXISTS horarios (
			    horario_id INTEGER PRIMARY KEY AUTOINCREMENT,
			    id TEXT,
			    diaN TEXT,
			    horaEntradaReal TEXT,
			    horaSalidaReal TEXT
			);
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + rutaBaseDatos);
             Statement stmt = conn.createStatement()) {
            stmt.execute(crearTablaHorariosSQL); // Crear la tabla 'horarios'
            stmt.execute(crearTablaEmpleadosNombreSQL); // Crear la tabla 'empleadosNombre'
            System.out.println("Tablas 'horarios' y 'empleadosNombre' creadas (o ya existen).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar datos de empleados
    public void actualizarDatos(List<EmpleadoDatosExtra> empleadosDatos) {
        String insertarOActualizarSQL = """
            INSERT INTO horarios (id, diaN, horaEntradaReal, horaSalidaReal)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(id, diaN, horaEntradaReal) DO UPDATE SET
                horaSalidaReal = excluded.horaSalidaReal;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertarOActualizarSQL)) {
            for (EmpleadoDatosExtra empleado : empleadosDatos) {
                pstmt.setString(1, empleado.getId());
                pstmt.setString(2, empleado.getDiaN());
                pstmt.setString(3, empleado.getHoraEntradaReal());
                pstmt.setString(4, empleado.getHoraSalidaReal());
                pstmt.addBatch();
                System.out.println("Insertando/Actualizando: " + empleado.toString()); // Depuración
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener un empleado por su ID
    public Empleado obtenerEmpleadoPorId(String id) {
        String query = "SELECT id, nombre, puesto, jornada FROM empleadosNombre WHERE id = ?";
        Empleado empleado = null;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                empleado = new Empleado();
                empleado.setId(rs.getString("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setEmpleadoPuesto(rs.getString("puesto"));
                empleado.setJornada(rs.getString("jornada"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleado;
    }

    // Método para obtener horarios por ID
    public List<EmpleadoDatosExtra> obtenerHorariosPorId(String id) {
        List<EmpleadoDatosExtra> horarios = new ArrayList<>();
        String query = "SELECT id, diaN, horaEntradaReal, horaSalidaReal FROM horarios WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmpleadoDatosExtra horario = new EmpleadoDatosExtra(
                    rs.getString("id"),
                    rs.getString("diaN"),
                    rs.getString("horaEntradaReal"),
                    rs.getString("horaSalidaReal")
                );
                horarios.add(horario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return horarios;
    }

    // Método para actualizar el horario de un día específico
    public void actualizarHorarioPorDia(String id, String diaN, String horaEntradaReal, String nuevaHoraSalidaReal) {
        String actualizarSQL = """
            UPDATE horarios
            SET horaSalidaReal = ?
            WHERE id = ? AND diaN = ? AND horaEntradaReal = ?;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(actualizarSQL)) {
            pstmt.setString(1, nuevaHoraSalidaReal);
            pstmt.setString(2, id);
            pstmt.setString(3, diaN);
            pstmt.setString(4, horaEntradaReal);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para eliminar un horario por día
    public void eliminarHorarioPorDia(String id, String diaN, String horaEntradaReal) {
        String eliminarSQL = """
            DELETE FROM horarios
            WHERE id = ? AND diaN = ? AND horaEntradaReal = ?;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(eliminarSQL)) {
            pstmt.setString(1, id);
            pstmt.setString(2, diaN);
            pstmt.setString(3, horaEntradaReal);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para insertar o actualizar empleados
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
    public List<EmpleadoDatosExtra> obtenerTodosLosHorarios() {
        List<EmpleadoDatosExtra> empleados = new ArrayList<>();
        String query = "SELECT id, diaN, horaEntradaReal, horaSalidaReal FROM horarios";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                EmpleadoDatosExtra empleado = new EmpleadoDatosExtra(
                    rs.getString("id"),
                    rs.getString("diaN"),
                    rs.getString("horaEntradaReal"),
                    rs.getString("horaSalidaReal")
                );
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empleados;
    }
    // Método para obtener todos los empleados
    public List<Empleado> obtenerEmpleadosNombre() {
        List<Empleado> empleados = new ArrayList<>();
        String query = "SELECT id, nombre, puesto, jornada FROM empleadosNombre";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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