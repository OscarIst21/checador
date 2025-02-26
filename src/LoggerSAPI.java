import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerSAPI {
    private static final String LOG_FILE;
    private static LoggerSAPI instance;
    private PrintWriter writer;

    static {
        // Obtener la ruta del JAR o del IDE (dependiendo de dónde se ejecute)
        String jarPath;
        try {
            jarPath = new File(LoggerSAPI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent();
        } catch (Exception e) {
            // Si no se puede obtener la ruta, usamos la ruta actual como fallback
            jarPath = new File(".").getAbsolutePath();
        }

        // Definir la ruta de la carpeta 'logs' donde se almacenará el archivo de log
        String logsPath = jarPath + File.separator + "logs";
        // Crear la carpeta 'logs' si no existe
        crearCarpetaSiNoExiste(logsPath);

        // Configurar la ruta completa para el archivo de log
        LOG_FILE = logsPath + File.separator + "sapi_log.txt";
        // Verificar si el archivo de log existe y crearlo si no
        verificarArchivoLog(LOG_FILE);
    }

    // Método para crear la carpeta si no existe
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

    // Método para verificar si el archivo de log existe
    private static void verificarArchivoLog(String rutaArchivoLog) {
        File archivoLog = new File(rutaArchivoLog);
        if (!archivoLog.exists()) {
            try {
                if (archivoLog.createNewFile()) {
                    System.out.println("Archivo de log creado: " + archivoLog.getAbsolutePath());
                } else {
                    System.err.println("No se pudo crear el archivo de log: " + archivoLog.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El archivo de log ya existe: " + archivoLog.getAbsolutePath());
        }
    }

    // Constructor privado (Singleton)
    private LoggerSAPI() {
        try {
            // Abrir el archivo en modo append para no sobrescribir los logs anteriores
            writer = new PrintWriter(new FileWriter(LOG_FILE, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obtener la instancia única de LoggerSAPI
    public static synchronized LoggerSAPI getInstance() {
        if (instance == null) {
            instance = new LoggerSAPI();
        }
        return instance;
    }

    // Método para escribir en el log
    public void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        writer.println("[" + timestamp + "] " + message);
        writer.flush(); // Asegurar que se escriba inmediatamente
    }

    // Método para registrar excepciones
    public void logError(String message, Exception e) {
        log("ERROR: " + message);
        e.printStackTrace(writer);
        writer.flush();
    }

    // Método para borrar el contenido del archivo de log
    public void clearLogFile() {
        try (PrintWriter clearWriter = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            clearWriter.print(""); // Escribir una cadena vacía para borrar el contenido
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}