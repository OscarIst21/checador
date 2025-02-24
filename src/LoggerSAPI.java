import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerSAPI {
    private static final String LOG_FILE = "sapi_log.txt";
    private static LoggerSAPI instance;
    private PrintWriter writer;

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
}
