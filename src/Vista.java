import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import modelos.Checadas;
import modelos.Empleado;
import modelos.EmpleadoDatosExtra;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.swing.ImageIcon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.GridLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class Vista extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private List<Empleado> listaEmpleados;
    private List<Checadas> checadas;
    private List<EmpleadoDatosExtra> empleadosDatos;
    private ReportePDF reporte;
    public String periodo = "";
    public String checadasExcel = "";
    public String empleadosExcel = "";
    public String datosExtraExcel = "";
    JLabel lblNewLabel = new JLabel("");
    JLabel lblNewLabel_1 = new JLabel("");
    JLabel lblNewLabel_2 = new JLabel("");
    JLabel cargando = new JLabel("");
    JPanel panelTablasExcel = new JPanel();
    JButton btnSelectFile = new JButton();
    JButton btnSelectFile2 = new JButton();
    JButton btnSelectFile3 = new JButton();
    public boolean reporteExcepcionesCorrecto = false;
    private String lastPath = System.getProperty("user.home");
    private boolean incluirEncabezado = true; // Por defecto, incluir encabezado
    private boolean incluirNumeroPagina = true;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private String filtroIdGuardado = ""; // Variable para almacenar las IDs ingresadas
    private LoggerSAPI logger;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Vista frame = new Vista();
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public Vista() {
        logger = LoggerSAPI.getInstance();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SAPI");
        setResizable(false);
        setBounds(100, 100, 1280, 720);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/img/logoApp.png")).getImage());
        contentPane = new JPanel();
        contentPane.setBackground(Color.white);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        listaEmpleados = new ArrayList<>();
        checadas = new ArrayList<>();
        empleadosDatos = new ArrayList<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.clearLogFile(); // Borrar el contenido del archivo de log
            logger.log("Aplicación cerrada. Archivo de log reiniciado."); // Opcional: registrar el cierre
        }));

        JButton btnAyuda = new JButton("Ayuda");
        btnAyuda.setIcon(new ImageIcon(Vista.class.getResource("/img/iconAyuda.png")));
        btnAyuda.setBounds(1164, 8, 92, 35);
        btnAyuda.setBackground(new Color(54, 165, 85));
        btnAyuda.setFocusable(false);
        btnAyuda.setHorizontalAlignment(SwingConstants.LEFT);
        btnAyuda.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnAyuda.setMargin(new Insets(0, 10, 0, 0));
        btnAyuda.setForeground(new Color(255, 255, 255));
        contentPane.add(btnAyuda);

        JLabel lblNewLabel_4 = new JLabel(
                "Colegio de Estudios Científicos y Tecnológicos del estado de Baja California Sur");
        lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_4.setFont(new Font("Arial Narrow", Font.BOLD, 24));
        lblNewLabel_4.setBounds(0, 48, 1266, 50);
        contentPane.add(lblNewLabel_4);

        JLabel lblNewLabel_3 = new JLabel("Versiòn 1.7  18/06/25");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setBounds(1125, 654, 131, 29);
        contentPane.add(lblNewLabel_3);

        btnSelectFile = new JButton("   Seleccionar Archivo Checadas");
        btnSelectFile.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnSelectFile.setIcon(new ImageIcon(Vista.class.getResource("/img/iconXls.png")));
        btnSelectFile.setMargin(new Insets(0, 10, 0, 0));
        btnSelectFile.setForeground(new Color(255, 255, 255));
        btnSelectFile.setFocusable(false);
        btnSelectFile.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile.setBackground(new Color(54, 165, 85));
        btnSelectFile.setBounds(68, 244, 300, 50);
        contentPane.add(btnSelectFile);

        btnSelectFile2 = new JButton("  Seleccionar Archivo Empleados");
        btnSelectFile2.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile2.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnSelectFile2.setIcon(new ImageIcon(Vista.class.getResource("/img/IconEmpleado.png")));
        btnSelectFile2.setMargin(new Insets(0, 10, 0, 0));

        btnSelectFile2.setForeground(Color.WHITE);
        btnSelectFile2.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile2.setFocusable(false);
        btnSelectFile2.setBackground(new Color(54, 165, 85));
        btnSelectFile2.setBounds(68, 324, 245, 50);
        btnSelectFile2.setEnabled(false);
        contentPane.add(btnSelectFile2);

        btnSelectFile3 = new JButton("  Seleccionar Archivo Horarios");
        btnSelectFile3.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile3.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnSelectFile3.setIcon(new ImageIcon(Vista.class.getResource("/img/IconLayout.png")));
        btnSelectFile3.setMargin(new Insets(0, 10, 0, 0));

        btnSelectFile3.setForeground(new Color(255, 255, 255));
        btnSelectFile3.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile3.setFocusable(false);
        btnSelectFile3.setBackground(new Color(54, 165, 85));
        btnSelectFile3.setBounds(68, 404, 245, 50);
        btnSelectFile3.setEnabled(false);

        contentPane.add(btnSelectFile3);

        JLabel headerGreen = new JLabel("");
        headerGreen.setOpaque(true);
        headerGreen.setBounds(new Rectangle(594, 115, 611, 22));
        headerGreen.setBackground(new Color(54, 165, 85));
        contentPane.add(headerGreen);

        JButton btnSave = new JButton("          Generar Reporte");
        btnSave.setEnabled(false);
        btnSave.setHorizontalAlignment(SwingConstants.LEFT);
        btnSave.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnSave.setIcon(new ImageIcon(Vista.class.getResource("/img/IconReporte.png")));
        btnSave.setMargin(new Insets(0, 10, 0, 0));

        btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnSave.setFocusable(false);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(54, 165, 85));
        btnSave.setBounds(68, 524, 245, 50);

        contentPane.add(btnSave);

        JLabel logo = new JLabel("");
        ImageIcon icon = new ImageIcon(Vista.class.getResource("/img/logo.png"));
        Image image = icon.getImage();

        int width = 150;
        int height = 132;
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        logo.setIcon(new ImageIcon(resizedImage));
        logo.setBounds(20, 10, width, height);
        contentPane.add(logo);

        JLabel encabezado = new JLabel();
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(244, 124, 0));
        encabezado.setBounds(0, 0, 1280, 50);
        contentPane.add(encabezado);

        JLabel footer = new JLabel("");
        footer.setFont(new Font("Tahoma", Font.BOLD, 10));
        ImageIcon iconFooter = new ImageIcon(Vista.class.getResource("/img/footer.png"));
        Image footerImage = iconFooter.getImage();

        int footerWidth = 1280;
        int footerHeight = 170;

        BufferedImage resizedFooterImage = new BufferedImage(footerWidth, footerHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2dFooter = resizedFooterImage.createGraphics();
        g2dFooter.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2dFooter.drawImage(footerImage, 0, 0, footerWidth, footerHeight, null);
        g2dFooter.dispose();

        footer.setIcon(new ImageIcon(resizedFooterImage));
        footer.setBounds(-14, 541, 1280, 155);

        panelTablasExcel = new JPanel();
        panelTablasExcel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panelTablasExcel.setBounds(594, 136, 610, 475);
        contentPane.add(panelTablasExcel);
        panelTablasExcel.setLayout(new GridLayout(0, 1, 0, 0));

        contentPane.add(footer);
        lblNewLabel = new JLabel("");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(378, 244, 183, 50);
        contentPane.add(lblNewLabel);

        lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1.setBounds(378, 324, 183, 50);
        contentPane.add(lblNewLabel_1);

        lblNewLabel_2 = new JLabel("");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_2.setBounds(378, 404, 183, 50);
        contentPane.add(lblNewLabel_2);

        JButton btnReiniciar = new JButton("            Reiniciar valores ");
        btnReiniciar.setIcon(new ImageIcon(Vista.class.getResource("/img/IconReset.png")));
        btnReiniciar.setHorizontalAlignment(SwingConstants.LEFT);
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnReiniciar.setFocusable(false);
        btnReiniciar.setBackground(new Color(54, 165, 85));
        btnReiniciar.setBounds(68, 164, 300, 50);
        contentPane.add(btnReiniciar);

        JButton btnConfig = new JButton("");
        btnConfig.setIcon(new ImageIcon(Vista.class.getResource("/img/IconConfiguracion.png")));
        btnConfig.setBounds(318, 524, 50, 50);
        btnConfig.setFocusable(false);
        btnConfig.setBackground(new Color(54, 165, 85));
        contentPane.add(btnConfig);

        JButton btnEditarHorario = new JButton("");
        btnEditarHorario.setIcon(new ImageIcon(Vista.class.getResource("/img/IconEditar.png")));
        btnEditarHorario.setFocusable(false);
        btnEditarHorario.setBackground(new Color(54, 165, 85));
        btnEditarHorario.setBounds(318, 404, 50, 50);
        contentPane.add(btnEditarHorario);

        JButton btnEditarEmpleado = new JButton("");
        btnEditarEmpleado.setIcon(new ImageIcon(Vista.class.getResource("/img/IconEditar.png")));
        btnEditarEmpleado.setFocusable(false);
        btnEditarEmpleado.setBackground(new Color(54, 165, 85));
        btnEditarEmpleado.setBounds(318, 324, 50, 50);
        contentPane.add(btnEditarEmpleado);

        logger.log("Aplicación SAPI iniciada.");
        btnAyuda.addActionListener(e -> {
            // Mensaje explicativo del flujo de trabajo y la función de cada botón
            String mensaje = "Flujo de trabajo del sistema SAPI:\n\n" +
                    "1. **Subir Archivo de Checadas**: Selecciona el archivo que contiene las checadas de los empleados. Este archivo debe estar en formato Excel (.xls, .xlsx) o .dat.\n\n"
                    +
                    "2. **Subir Archivo de Empleados**: Selecciona el archivo que contiene la información de los empleados. Este archivo debe estar en formato Excel (.xls, .xlsx).\n\n"
                    +
                    "3. **Subir Archivo de Horarios**: Selecciona el archivo que contiene los horarios de los empleados. Este archivo debe estar en formato Excel (.xls, .xlsx).\n\n"
                    +
                    "4. **Generar Reporte**: Una vez que hayas subido los archivos necesarios, puedes generar un reporte en formato PDF con la información de asistencia de los empleados.\n\n"
                    +
                    "5. **Reiniciar Valores**: Este botón te permite reiniciar todos los valores y comenzar de nuevo el proceso.\n\n"
                    +
                    "6. **Configuración**: Aquí puedes configurar opciones adicionales como incluir encabezado, número de página y filtrar por ID.\n\n"
                    +
                    "7. **Editar Empleado**: Permite editar la información de un empleado específico.\n\n" +
                    "8. **Editar Horario**: Permite editar el horario de un empleado específico.\n\n" +
                    "Para más detalles, consulte la documentación del sistema.";

            // Mostrar el mensaje en un JOptionPane
            JOptionPane.showMessageDialog(null, mensaje, "Ayuda - Flujo de trabajo y funciones",
                    JOptionPane.INFORMATION_MESSAGE);

            logger.log("Boton ayuda presionado");

        });
        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Resetear etiquetas de los archivos cargados
                lblNewLabel.setText("");
                lblNewLabel_1.setText("");
                lblNewLabel_2.setText("");

                lblNewLabel.setForeground(new Color(0, 0, 0)); // Restaurar colores por defecto
                lblNewLabel_1.setForeground(new Color(0, 0, 0));
                lblNewLabel_2.setForeground(new Color(0, 0, 0));

                // Limpiar las listas de datos
                checadas.clear();
                listaEmpleados.clear();
                empleadosDatos.clear();
                tabbedPane.removeAll();

                // Restaurar estados de botones
                btnSelectFile.setEnabled(true);
                btnSelectFile2.setEnabled(false);
                btnSelectFile3.setEnabled(false);
                btnSave.setEnabled(false);

                reporteExcepcionesCorrecto = false;
                // Mensaje de confirmación
                JOptionPane.showMessageDialog(Vista.this, "Todos los datos y configuraciones han sido reseteados.");
                logger.log("Valores restablecidos en la lista checadas y eliminación de tablas.");
            }
        });
        btnConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario abrió la ventana de configuración
                logger.log("Botón configuración abierto");

                // Crear panel de configuración con 5 filas
                JPanel configPanel = new JPanel(new GridLayout(5, 1));

                // Checkboxes para encabezado y número de página
                JCheckBox encabezadoCheckBox = new JCheckBox("Incluir encabezado", incluirEncabezado);
                JCheckBox numeroPaginaCheckBox = new JCheckBox("Incluir número de página", incluirNumeroPagina);

                // Campo de texto para IDs
                JLabel filtroIdLabel = new JLabel("IDs (separados por coma):");
                JTextField filtroIdField = new JTextField(20);
                filtroIdField.setText(filtroIdGuardado);

                // ComboBox para elegir tipo de filtrado
                JLabel tipoFiltroLabel = new JLabel("Tipo de filtro:");
                String[] opcionesFiltro = { "Filtrar por ID", "Excluir ID" };
                JComboBox<String> comboTipoFiltro = new JComboBox<>(opcionesFiltro);
                comboTipoFiltro.setSelectedIndex(0); // por defecto: Filtrar

                // Añadir todo al panel
                configPanel.add(encabezadoCheckBox);
                configPanel.add(numeroPaginaCheckBox);
                configPanel.add(filtroIdLabel);
                configPanel.add(filtroIdField);
                configPanel.add(tipoFiltroLabel);
                configPanel.add(comboTipoFiltro);

                // Mostrar diálogo
                int result = JOptionPane.showConfirmDialog(
                        Vista.this,
                        configPanel,
                        "Configuración de Reporte",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    incluirEncabezado = encabezadoCheckBox.isSelected();
                    incluirNumeroPagina = numeroPaginaCheckBox.isSelected();
                    filtroIdGuardado = filtroIdField.getText().trim();
                    String tipoFiltro = (String) comboTipoFiltro.getSelectedItem();

                    // Parsear IDs ingresados
                    Set<String> ids = Arrays.stream(filtroIdGuardado.split(","))
                            .map(String::trim)
                            .filter(id -> !id.isEmpty())
                            .collect(Collectors.toSet());

                    List<Checadas> filteredChecadas;

                    if (ids.isEmpty()) {
                        filteredChecadas = new ArrayList<>(checadas); // No se filtra nada
                    } else if ("Filtrar por ID".equals(tipoFiltro)) {
                        filteredChecadas = checadas.stream()
                                .filter(checada -> ids.contains(checada.getId()))
                                .collect(Collectors.toList());
                        filtroIdGuardado = "filtrar:" + String.join(",", ids); // ← aquí se marca
                    } else { // "Excluir ID"
                        filteredChecadas = checadas.stream()
                                .filter(checada -> !ids.contains(checada.getId()))
                                .collect(Collectors.toList());
                        filtroIdGuardado = "excluir:" + String.join(",", ids); // ← aquí se marca
                    }

                    if (filteredChecadas.isEmpty()) {
                        logger.log("No se encontraron registros con los IDs y opción seleccionada.");
                    } else {
                        logger.log("Registros filtrados (" + tipoFiltro + "): " + filtroIdGuardado);
                        logger.log(filteredChecadas.toString());
                    }

                    logger.log("Configuración guardada: " +
                            "Incluir encabezado = " + incluirEncabezado + ", " +
                            "Incluir número de página = " + incluirNumeroPagina + ", " +
                            "Filtro de IDs = " + filtroIdGuardado + ", " +
                            "Modo de filtro = " + tipoFiltro);
                } else {
                    logger.log("Configuración cancelada por el usuario.");
                }
            }
        });

        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para seleccionar un
                // archivo
                logger.log("Botón 'Seleccionar Archivo Checadas' presionado.");

                // Configurar el JFileChooser con la última ruta utilizada por el usuario
                JFileChooser fileChooser = new JFileChooser(lastPath);

                // Establecer un filtro para mostrar solo archivos con extensiones .xls, .xlsx y
                // .dat
                fileChooser.setFileFilter(
                        new FileNameExtensionFilter("Archivos permitidos (.xls, .xlsx, .dat)", "xls", "xlsx", "dat"));

                // Mostrar un mensaje de "Cargando..." en la etiqueta correspondiente
                lblNewLabel.setText("Cargando...");

                // Mostrar el diálogo para que el usuario seleccione un archivo
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                // Verificar si el usuario seleccionó un archivo y confirmó la selección
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    // Obtener el archivo seleccionado por el usuario
                    File fileToOpen = fileChooser.getSelectedFile();

                    // Actualizar la última ruta utilizada para futuras selecciones de archivos
                    lastPath = fileToOpen.getParent();

                    if (!lblNewLabel.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel.getText());
                    }

                    lblNewLabel.setText(fileToOpen.getName());

                    // Registrar en el log que el usuario seleccionó un archivo
                    logger.log("Archivo seleccionado: " + fileToOpen.getName());

                    // Obtener la extensión del archivo seleccionado
                    String extension = getFileExtension(fileToOpen);

                    try {
                        // Procesar el archivo según su extensión
                        if (extension.equals("xls") || extension.equals("xlsx")) {
                            // Si es un archivo Excel (.xls o .xlsx)
                            String hoja = "Reporte de Excepciones"; // Nombre de la hoja a cargar
                            mostrarTablaDesdeExcel(fileToOpen, hoja); // Mostrar la tabla desde el archivo Excel
                            cargarChecador(fileToOpen); // Cargar los datos del checador desde el archivo Excel

                            // Registrar en el log que se cargó un archivo Excel
                            logger.log("Archivo Excel cargado: " + fileToOpen.getName());
                        } else if (extension.equals("dat")) {
                            // Si es un archivo .dat
                            mostrarTablaDesdeDat(fileToOpen); // Mostrar la tabla desde el archivo .dat
                            leerArchivoDAT(fileToOpen); // Leer y procesar el archivo .dat

                            // Registrar en el log que se cargó un archivo .dat
                            logger.log("Archivo .dat cargado: " + fileToOpen.getName());
                        } else {
                            // Si el formato del archivo no es compatible, mostrar un mensaje de error
                            JOptionPane.showMessageDialog(Vista.this, "Formato de archivo no soportado.");
                            lblNewLabel.setText(""); // Limpiar la etiqueta del archivo

                            // Registrar en el log que el archivo no es compatible
                            logger.log("Formato de archivo no soportado: " + fileToOpen.getName());
                        }

                        // Habilitar los botones correspondientes si el archivo se procesó correctamente
                        if (reporteExcepcionesCorrecto) {
                            btnSelectFile2.setEnabled(true); // Habilitar el botón para seleccionar archivo de empleados
                            btnSelectFile3.setEnabled(true); // Habilitar el botón para seleccionar archivo de horarios
                            btnSave.setEnabled(true); // Habilitar el botón para generar el reporte

                            // Registrar en el log que los botones fueron habilitados
                            logger.log("Botones habilitados después de cargar el archivo correctamente.");
                        }

                    } catch (Exception ex) {
                        // Si ocurre un error al procesar el archivo, mostrar un mensaje de error
                        reporteExcepcionesCorrecto = false;
                        JOptionPane.showMessageDialog(Vista.this, "Error al procesar el archivo: " + ex.getMessage());
                        lblNewLabel.setForeground(new Color(104, 4, 0)); // Cambiar el color de la etiqueta a rojo

                        // Registrar en el log el error ocurrido
                        logger.logError("Error al procesar el archivo: " + fileToOpen.getName(), ex);
                        ex.printStackTrace();
                    }

                } else {
                    // Si el usuario no seleccionó ningún archivo, limpiar la etiqueta y mostrar un
                    // mensaje
                    lblNewLabel.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");

                    // Registrar en el log que el usuario canceló la selección de archivo
                    logger.log("Selección de archivo cancelada por el usuario.");
                }
            }
        });
        btnSelectFile2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para seleccionar el
                // archivo de empleados
                logger.log("Botón 'Seleccionar Archivo Empleados' presionado.");

                // Mostrar un mensaje de "Cargando..." en la etiqueta correspondiente
                lblNewLabel_1.setText("Cargando...");

                // Configurar el JFileChooser para seleccionar archivos
                JFileChooser fileChooser = new JFileChooser();

                // Establecer un filtro para mostrar solo archivos Excel (.xls y .xlsx)
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));

                // Usar la misma ruta que se utilizó en el último JFileChooser
                fileChooser.setCurrentDirectory(new File(lastPath));

                // Mostrar el diálogo para que el usuario seleccione un archivo
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                // Verificar si el usuario seleccionó un archivo y confirmó la selección
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    // Obtener el archivo seleccionado por el usuario
                    File fileToOpen = fileChooser.getSelectedFile();

                    // Si ya había una tabla cargada, eliminarla antes de cargar la nueva
                    if (!lblNewLabel_1.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel_1.getText());
                    }

                    // Mostrar el nombre del archivo seleccionado en la etiqueta correspondiente
                    lblNewLabel_1.setText(fileToOpen.getName());

                    // Registrar en el log que el usuario seleccionó un archivo de empleados
                    logger.log("Archivo de empleados seleccionado: " + fileToOpen.getName());

                    // Cargar los empleados desde el archivo seleccionado
                    cargarEmpleados(fileToOpen);
                } else {
                    // Si el usuario no seleccionó ningún archivo, limpiar la etiqueta y mostrar un
                    // mensaje
                    lblNewLabel_1.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");

                    // Registrar en el log que el usuario canceló la selección de archivo
                    logger.log("Selección de archivo de empleados cancelada por el usuario.");
                }
            }
        });
        btnSelectFile3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para seleccionar el
                // archivo de horarios
                logger.log("Botón 'Seleccionar Archivo Horarios' presionado.");

                // Mostrar un mensaje de "Cargando..." en la etiqueta correspondiente
                lblNewLabel_2.setText("Cargando...");

                // Configurar el JFileChooser para seleccionar archivos
                JFileChooser fileChooser = new JFileChooser();

                // Establecer un filtro para mostrar solo archivos Excel (.xls y .xlsx)
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));

                // Usar la misma ruta que se utilizó en el último JFileChooser
                fileChooser.setCurrentDirectory(new File(lastPath));

                // Mostrar el diálogo para que el usuario seleccione un archivo
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                // Verificar si el usuario seleccionó un archivo y confirmó la selección
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    // Obtener el archivo seleccionado por el usuario
                    File fileToOpen = fileChooser.getSelectedFile();

                    // Si ya había una tabla cargada, eliminarla antes de cargar la nueva
                    if (!lblNewLabel_2.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel_2.getText());
                    }

                    // Mostrar el nombre del archivo seleccionado en la etiqueta correspondiente
                    lblNewLabel_2.setText(fileToOpen.getName());

                    // Registrar en el log que el usuario seleccionó un archivo de horarios
                    logger.log("Archivo de horarios seleccionado: " + fileToOpen.getName());

                    // Procesar el archivo Excel
                    String hoja = "Hoja1"; // Nombre de la hoja a cargar
                    mostrarTablaDesdeExcel(fileToOpen, hoja); // Mostrar la tabla desde el archivo Excel
                    cargarDatosExtra(fileToOpen); // Cargar los datos adicionales desde el archivo Excel

                    // Actualizar la última ruta seleccionada para futuras operaciones
                    lastPath = fileToOpen.getParent();
                } else {
                    // Si el usuario no seleccionó ningún archivo, limpiar la etiqueta y mostrar un
                    // mensaje
                    lblNewLabel_2.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");

                    // Registrar en el log que el usuario canceló la selección de archivo
                    logger.log("Selección de archivo de horarios cancelada por el usuario.");
                }
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para generar el
                // reporte
                logger.log("Botón 'Generar Reporte' presionado.");

                // Usar las IDs guardadas en la configuración para filtrar las checadas
                String idsFiltro = filtroIdGuardado;
                System.out.println(idsFiltro);
                // Filtrar las checadas si se ingresaron IDs en la configuración
                List<Checadas> checadasFiltradas = checadas; // Inicialmente, usar todas las checadas

                // Actualizar las checadas con la información de los empleados
                actualizarChecadasConEmpleados();

                // Crear una instancia del generador de reportes en PDF
                reporte = new ReportePDF();

                // Generar el reporte con las checadas filtradas, el período y las opciones de
                // configuración
                reporte.generateReport(checadasFiltradas, periodo, incluirEncabezado, incluirNumeroPagina, idsFiltro);

                // Registrar en el log que se generó el reporte
                logger.log("Reporte generado con éxito.");

                // Limpiar el filtro de IDs guardado después de generar el reporte
                filtroIdGuardado = null;
            }
        });
        btnEditarEmpleado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para editar un
                // empleado
                logger.log("Botón 'Editar Empleado' presionado.");

                // Crear un diálogo modal para editar la información del empleado
                JDialog dialog = new JDialog(Vista.this, "Editar Empleado", true);
                logger.log("Diálogo 'Editar Empleado' abierto.");
                // Establecer el contenido del diálogo con el panel de edición de empleado
                dialog.setContentPane(crearPanelEditarEmpleado());

                // Ajustar el tamaño del diálogo al contenido
                dialog.pack();

                // Centrar el diálogo en la ventana principal
                dialog.setLocationRelativeTo(Vista.this);

                // Hacer visible el diálogo
                dialog.setVisible(true);

                // Registrar en el log que se abrió el diálogo de edición de empleado

            }
        });

        btnEditarHorario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Registrar en el log que el usuario hizo clic en el botón para editar un
                // horario
                logger.log("Botón 'Editar Horario' presionado.");

                // Crear un diálogo modal para editar el horario del empleado
                JDialog dialog = new JDialog(Vista.this, "Editar Horario", true);

                // Establecer el contenido del diálogo con el panel de edición de horario
                dialog.setContentPane(crearPanelEditarHorario());

                // Ajustar el tamaño del diálogo al contenido
                dialog.pack();

                // Centrar el diálogo en la ventana principal
                dialog.setLocationRelativeTo(Vista.this);

                // Hacer visible el diálogo
                dialog.setVisible(true);

                // Registrar en el log que se abrió el diálogo de edición de horario
                logger.log("Diálogo 'Editar Horario' abierto.");
            }
        });
    }

    private JPanel crearPanelEditarEmpleado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(500, 250));

        // Panel para los campos de entrada
        JPanel camposPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel lblId = new JLabel("ID:");
        JTextField txtId = new JTextField(15);
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField(15);
        JLabel lblPuesto = new JLabel("Puesto:");
        JTextField txtPuesto = new JTextField(15);
        JLabel lblJornada = new JLabel("Tipo de Jornada:");
        JTextField txtJornada = new JTextField(15);
        JLabel lblCtt = new JLabel("CCT:");
        JTextField txtCctt = new JTextField(15);
        camposPanel.add(lblId);
        camposPanel.add(txtId);
        camposPanel.add(lblNombre);
        camposPanel.add(txtNombre);
        camposPanel.add(lblPuesto);
        camposPanel.add(txtPuesto);
        camposPanel.add(lblJornada);
        camposPanel.add(txtJornada);
        camposPanel.add(lblCtt);
        camposPanel.add(txtCctt);

        // Panel para los botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnGuardar = new JButton("Guardar");

        botonesPanel.add(btnBuscar);
        botonesPanel.add(btnGuardar);

        // Agregar los paneles al panel principal
        panel.add(camposPanel, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        // ActionListeners para los botones
        btnBuscar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (!id.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                Empleado empleado = dbManager.obtenerEmpleadoPorId(id);
                if (empleado != null) {
                    txtNombre.setText(empleado.getNombre());
                    txtPuesto.setText(empleado.getEmpleadoPuesto());
                    txtJornada.setText(empleado.getJornada());
                    txtCctt.setText(empleado.getCct());
                    logger.log("Empleado encontrado con ID: " + id); // Registrar en el log
                } else {
                    JOptionPane.showMessageDialog(panel, "No se encontró un empleado con esa ID.");
                    txtNombre.setText("");
                    txtPuesto.setText("");
                    txtJornada.setText("");
                    txtCctt.setText("");
                    logger.log("No se encontró un empleado con ID: " + id); // Registrar en el log
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Por favor, ingrese una ID.");
                logger.log("Intento de búsqueda sin ID."); // Registrar en el log
            }
        });

        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String puesto = txtPuesto.getText().trim();
            String jornada = txtJornada.getText().trim();
            String cct = txtCctt.getText().trim();

            if (!id.isEmpty() && !nombre.isEmpty() && !puesto.isEmpty() && !jornada.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                Empleado empleado = new Empleado(id, nombre, puesto, jornada, cct);
                dbManager.insertarOActualizarEmpleado(empleado);
                JOptionPane.showMessageDialog(panel, "Empleado guardado con éxito.");
                logger.log("Empleado guardado con ID: " + id); // Registrar en el log

                // Cierra el JDialog que contiene este panel
                Window ventana = SwingUtilities.getWindowAncestor(panel);
                if (ventana instanceof JDialog) {
                    ventana.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Por favor, complete todos los campos.");
                logger.log("Intento de guardar empleado con campos incompletos."); // Registrar en el log
            }
        });

        return panel;
    }

    private JPanel crearPanelEditarHorario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(500, 600));

        JPanel camposPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JLabel lblId = new JLabel("ID:");
        JTextField txtId = new JTextField();

        JTextField[] txtHorarios = new JTextField[14]; // 7 días x 2 horarios
        for (int i = 0; i < txtHorarios.length; i++) {
            txtHorarios[i] = new JTextField();
        }

        camposPanel.add(lblId);
        camposPanel.add(txtId);

        // Declaración de los días (SE USA EN TODA LA FUNCIÓN)
        String[] dias = { "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo" };

        for (int i = 0; i < 7; i++) {
            camposPanel.add(new JLabel(dias[i] + " (Horario 1)"));
            camposPanel.add(txtHorarios[i]);

            camposPanel.add(new JLabel(dias[i] + " (Horario 2)"));
            camposPanel.add(txtHorarios[i + 7]);
        }

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnGuardar = new JButton("Guardar");

        botonesPanel.add(btnBuscar);
        botonesPanel.add(btnGuardar);

        panel.add(camposPanel, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        // Acción para el botón de búsqueda
        btnBuscar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (!id.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                List<EmpleadoDatosExtra> horarios = dbManager.obtenerHorariosPorId(id);

                if (!horarios.isEmpty()) {
                    for (JTextField txtHorario : txtHorarios) {
                        txtHorario.setText(""); // Limpiar antes de asignar nuevos valores
                    }

                    // Mapa para contar horarios por día
                    Map<String, Integer> contadorHorarios = new HashMap<>();

                    for (EmpleadoDatosExtra horario : horarios) {
                        int diaIndex = obtenerIndiceDia(horario.getDiaN());
                        if (diaIndex >= 0 && diaIndex < 7) {
                            String horarioTexto = horario.getHoraEntradaReal() + " - " + horario.getHoraSalidaReal();

                            // Si ya hay un horario en ese día, asignar al segundo campo
                            int count = contadorHorarios.getOrDefault(horario.getDiaN(), 0);
                            if (count == 0) {
                                txtHorarios[diaIndex].setText(horarioTexto);
                            } else {
                                txtHorarios[diaIndex + 7].setText(horarioTexto);
                            }

                            // Aumentar contador de horarios para ese día
                            contadorHorarios.put(horario.getDiaN(), count + 1);
                        }
                    }
                    logger.log("Horarios cargados para el empleado con ID: " + id); // Registrar en el log
                } else {
                    JOptionPane.showMessageDialog(panel, "No se encontraron horarios para el empleado con ID: " + id);
                    logger.log("No se encontraron horarios para el empleado con ID: " + id); // Registrar en el log
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Por favor, ingrese una ID.");
                logger.log("Intento de búsqueda sin ID."); // Registrar en el log
            }
        });

        // Acción para el botón de guardar
        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (!id.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                List<EmpleadoDatosExtra> horariosActualizados = new ArrayList<>();

                for (int i = 0; i < 7; i++) {
                    String horario1 = txtHorarios[i].getText().trim();
                    String horario2 = txtHorarios[i + 7].getText().trim();
                    String dia = dias[i];

                    // Manejar el primer horario
                    if (!horario1.isEmpty()) {
                        String[] partes1 = horario1.split(" - ");
                        if (partes1.length == 2) {
                            horariosActualizados.add(new EmpleadoDatosExtra(id, dia, partes1[0], partes1[1]));
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                    "Formato de horario incorrecto para " + dia + " (Horario 1).");
                            logger.log("Formato de horario incorrecto para " + dia + " (Horario 1)."); // Registrar en
                                                                                                       // el log
                        }
                    }

                    // Manejar el segundo horario
                    if (!horario2.isEmpty()) {
                        String[] partes2 = horario2.split(" - ");
                        if (partes2.length == 2) {
                            horariosActualizados.add(new EmpleadoDatosExtra(id, dia, partes2[0], partes2[1]));
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                    "Formato de horario incorrecto para " + dia + " (Horario 2).");
                            logger.log("Formato de horario incorrecto para " + dia + " (Horario 2)."); // Registrar en
                                                                                                       // el log
                        }
                    }
                }

                // Obtener los horarios actuales de la base de datos
                List<EmpleadoDatosExtra> horariosActuales = dbManager.obtenerHorariosPorId(id);

                // Eliminar horarios que ya no están en la lista de horarios actualizados
                for (EmpleadoDatosExtra horarioActual : horariosActuales) {
                    boolean existeEnActualizados = horariosActualizados.stream()
                            .anyMatch(ha -> ha.getId().equals(horarioActual.getId()) &&
                                    ha.getDiaN().equals(horarioActual.getDiaN()) &&
                                    ha.getHoraEntradaReal().equals(horarioActual.getHoraEntradaReal()));

                    if (!existeEnActualizados) {
                        dbManager.eliminarHorarioPorDia(horarioActual.getId(), horarioActual.getDiaN(),
                                horarioActual.getHoraEntradaReal());
                    }
                }

                // Actualizar o insertar los horarios actualizados
                dbManager.actualizarDatos(horariosActualizados);

                JOptionPane.showMessageDialog(panel, "Horario actualizado correctamente.");
                logger.log("Horario actualizado para el empleado con ID: " + id); // Registrar en el log
                btnBuscar.doClick(); // Recargar los datos actualizados
            } else {
                JOptionPane.showMessageDialog(panel, "Por favor, ingrese una ID.");
                logger.log("Intento de guardar horario sin ID."); // Registrar en el log
            }
        });
        return panel;
    }

    private String obtenerHoraEntradaDeHorario(String id, String dia, int indiceHorario, JTextField[] txtHorarios) {
        BaseDeDatosManager dbManager = new BaseDeDatosManager();
        List<EmpleadoDatosExtra> horarios = dbManager.obtenerHorariosPorId(id);

        for (EmpleadoDatosExtra horario : horarios) {
            if (horario.getDiaN().equalsIgnoreCase(dia)) {
                if (indiceHorario == 1 && txtHorarios[obtenerIndiceDia(dia)].getText().trim().isEmpty()) {
                    return horario.getHoraEntradaReal();
                } else if (indiceHorario == 2 && txtHorarios[obtenerIndiceDia(dia) + 7].getText().trim().isEmpty()) {
                    return horario.getHoraEntradaReal();
                }
            }
        }
        return null;
    }

    private int obtenerIndiceDia(String dia) {
        String[] dias = { "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo" };
        for (int i = 0; i < dias.length; i++) {
            if (dias[i].equalsIgnoreCase(dia)) {
                return i;
            }
        }
        return -1; // Si no se encuentra el día
    }

    private String obtenerNombreDia(int indice) {
        String[] dias = { "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo" };
        if (indice >= 0 && indice < dias.length) {
            return dias[indice]; // Devuelve el nombre del día según el índice
        }
        return "";
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < name.length() - 1) {
            logger.log("obtener extensión del archivo: " + name.substring(lastIndex + 1).toLowerCase());
            return name.substring(lastIndex + 1).toLowerCase(); // Devuelve la extensión en minúsculas
        }
        logger.log("obtener extensión del archivo: null");
        return "";
    }

    private void eliminarTablaCargada(String nombreTab) {
        int tabIndex = -1;

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(nombreTab)) {
                tabIndex = i;
                break;
            }
        }
        if (tabIndex != -1) {
            tabbedPane.remove(tabIndex);
        }
        logger.log("Tabla eliminada de la vista");
    }

    private void mostrarTablaDesdeExcel(File file, String hoja) {
        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            // Obtener la hoja especificada del archivo Excel
            Sheet sheet = workbook.getSheet(hoja);
            if (sheet == null) {
                // Si la hoja no se encuentra, no hacer nada
                logger.log("La hoja '" + hoja + "' no se encontró en el archivo: " + file.getName());
                return;
            }

            // Crear un modelo de tabla para almacenar los datos
            DefaultTableModel tableModel = new DefaultTableModel();

            // Obtener la primera fila (encabezados) y agregar las columnas al modelo
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                tableModel.addColumn(cell.toString());
            }

            // Recorrer las filas de la hoja y agregar los datos al modelo
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getLastCellNum() <= 0) {
                    continue; // Ignorar filas vacías
                }

                // Crear un arreglo para almacenar los datos de la fila
                Object[] rowData = new Object[row.getLastCellNum()];
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        rowData[j] = formatCell(cell, j); // Formatear la celda según su tipo
                    } else {
                        rowData[j] = ""; // Si la celda está vacía, asignar una cadena vacía
                    }
                }
                tableModel.addRow(rowData); // Agregar la fila al modelo
            }

            // Crear una tabla con el modelo de datos
            JTable table = new JTable(tableModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            // Agregar la tabla a un JScrollPane para permitir el desplazamiento
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            // Crear una pestaña con el nombre del archivo y agregar la tabla
            String tabName = file.getName();
            tabbedPane.addTab(tabName, scrollPane);

            // Actualizar el panel de tablas
            panelTablasExcel.removeAll();
            panelTablasExcel.add(tabbedPane);
            panelTablasExcel.revalidate();
            panelTablasExcel.repaint();

            // Registrar en el log que la tabla se cargó correctamente
            logger.log("Tabla cargada desde el archivo: " + file.getName() + ", hoja: " + hoja);

        } catch (IOException e) {
            // Registrar en el log el error de lectura del archivo
            logger.logError("Error al leer el archivo: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            // Registrar en el log el error de formato o datos inválidos
            logger.logError("Formato de archivo incorrecto o datos inválidos: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarTablaDesdeDat(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            DefaultTableModel tableModel = new DefaultTableModel();

            // Configurar encabezados fijos
            String[] encabezados = { "ID", "Fecha", "Hora", "1", "0", "1", "0" };
            for (String encabezado : encabezados) {
                tableModel.addColumn(encabezado);
            }

            // Leer las líneas y agregar filas
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] datosFila = linea.split("\\s+");

                    // Ajustar el tamaño de las filas si faltan columnas
                    if (datosFila.length < encabezados.length) {
                        datosFila = Arrays.copyOf(datosFila, encabezados.length);
                    }

                    tableModel.addRow(datosFila);
                }
            }

            // Configurar la tabla
            JTable table = new JTable(tableModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            // Agregar la tabla al panel/tab
            String tabName = file.getName();
            tabbedPane.addTab(tabName, scrollPane);

            panelTablasExcel.removeAll();
            panelTablasExcel.add(tabbedPane);
            panelTablasExcel.revalidate();
            panelTablasExcel.repaint();

            // Registrar en el log que la tabla se cargó correctamente
            logger.log("Tabla cargada desde el archivo .dat: " + file.getName());

        } catch (IOException e) {
            // Registrar en el log el error de lectura del archivo
            logger.logError("Error al leer el archivo .dat: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error al leer el archivo .dat: " + e.getMessage());
        } catch (Exception e) {
            // Registrar en el log el error de procesamiento del archivo
            logger.logError("Error procesando el archivo .dat: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error procesando el archivo .dat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Object formatCell(Cell cell, int columnIndex) {
        String cellValue = "";

        // Registrar el tipo de celda y el valor original
        logger.log("Formateando celda - Tipo: " + cell.getCellType() + ", Valor original: " + cell.toString());

        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (columnIndex >= 4 && columnIndex <= 7) { // Rango de columnas que contienen horas
                    cellValue = convertirDecimalAHora1(numericValue);
                } else if (numericValue == Math.floor(numericValue)) {
                    cellValue = String.valueOf((int) numericValue); // Evitar mostrar decimales innecesarios
                } else {
                    cellValue = String.valueOf(numericValue); // Mostrar número completo
                }
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }

        // Registrar el valor formateado
        logger.log("Celda formateada - Valor final: " + cellValue);

        return cellValue;
    }

    private String convertirDecimalAHora1(double decimal) {
        java.util.Date date = DateUtil.getJavaDate(decimal);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String horaFormateada = timeFormat.format(date);
        logger.log("Convertido decimal a hora: " + decimal + " -> " + horaFormateada);
        return horaFormateada;
    }

    private void cargarDatosExtra(File file) {
        empleadosDatos = new ArrayList<>();
        logger.log("Cargando datos adicionales desde archivo: " + file.getName());

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            boolean dataLoaded = false;
            String hojaProcesada = "";

            for (Sheet sheet : workbook) {
                if (sheet == null)
                    continue;

                Row headerRow = sheet.getRow(0);
                if (headerRow == null)
                    continue;

                Map<String, Integer> columnMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String header = cell.getStringCellValue().trim().toLowerCase();
                        columnMap.put(header, cell.getColumnIndex());
                    }
                }

                String[] requiredColumns = {
                        "numero_empleado", "dia", "hora_entrada", "hora_salida"
                };
                boolean allColumnsFound = true;
                for (String column : requiredColumns) {
                    if (!columnMap.containsKey(column)) {
                        allColumnsFound = false;
                        break;
                    }
                }

                if (!allColumnsFound) {
                    continue;
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)
                        continue;

                    String id = getCellValue(row, columnMap.get("numero_empleado"));
                    String dia = getDayName(getCellNumericValue(row, columnMap.get("dia")));
                    String horaEntradaReal = getTimeValue(row, columnMap.get("hora_entrada"));
                    String horaSalidaReal = getTimeValue(row, columnMap.get("hora_salida"));

                    if (!id.isEmpty()) {
                        EmpleadoDatosExtra empleado = new EmpleadoDatosExtra(id, dia, horaEntradaReal, horaSalidaReal);
                        empleadosDatos.add(empleado);
                        logger.log("Empleado cargado: " + empleado.toString());
                    }
                }

                hojaProcesada = sheet.getSheetName();
                dataLoaded = true;
                break;
            }

            if (dataLoaded) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                dbManager.actualizarDatos(empleadosDatos);

                lblNewLabel_2.setForeground(new Color(0, 104, 0));
                btnSelectFile3.setEnabled(false);
                logger.log("Datos adicionales cargados desde la hoja: " + hojaProcesada);
                JOptionPane.showMessageDialog(this, "Datos adicionales cargados desde la hoja: " + hojaProcesada);
            } else {
                logger.log("No se encontraron las columnas requeridas en ninguna hoja.");
                JOptionPane.showMessageDialog(this, "No se encontraron las columnas requeridas en ninguna hoja.");
                lblNewLabel_2.setForeground(new Color(104, 4, 0));
            }

        } catch (IOException e) {
            logger.logError("Error al leer el archivo: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel_2.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            logger.logError("Formato de archivo incorrecto o datos inválidos: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel_2.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

    private String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null)
            return "";
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                } else {
                    return String.valueOf(value);
                }
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellType() == CellType.FORMULA) {
                return cell.getCellFormula();
            }
        }
        return "";
    }

    private String getTimeValue(Row row, Integer columnIndex) {
        if (columnIndex == null)
            return "Columna no válida";
        Cell cell = row.getCell(columnIndex);
        if (cell == null)
            return "00:00";

        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                return timeFormat.format(cell.getDateCellValue());
            } else {
                return "00:00";
            }
        } else if (cell.getCellType() == CellType.STRING) {
            String timeValue = cell.getStringCellValue().trim();
            if (timeValue.matches("\\d{1,2}:\\d{2}(:\\d{2})?")) {
                return timeValue.substring(0, 5);
            }
        }
        return "00:00";
    }

    private double getCellNumericValue(Row row, Integer columnIndex) {
        if (columnIndex == null)
            return -1;
        Cell cell = row.getCell(columnIndex);
        if (cell == null)
            return -1;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return -1;
                }
            default:
                return -1;
        }
    }

    private String getDayName(double dayNumber) {
        int day = (int) dayNumber;
        switch (day) {
            case 1:
                return "lunes";
            case 2:
                return "martes";
            case 3:
                return "miércoles";
            case 4:
                return "jueves";
            case 5:
                return "viernes";
            case 6:
                return "sabado";
            case 7:
                return "domingo";
            default:
                return "Día no válido";
        }
    }

    private void cargarEmpleados(File file) {
        BaseDeDatosManager dbManager = new BaseDeDatosManager();
        logger.log("Cargando empleados desde archivo: " + file.getName());

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            boolean dataLoaded = false;
            String hojaProcesada = "";

            // Mapa para agrupar empleados por ID
            Map<String, List<Empleado>> empleadosMap = new HashMap<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null)
                    continue;

                // Mapear columnas
                Map<String, Integer> columnMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    String columnName = cell.toString().trim();
                    if ("EMPLEADO_NO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_NO", cell.getColumnIndex());
                    } else if ("EMPLEADO_NOMBRE_COMPLETO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_NOMBRE_COMPLETO", cell.getColumnIndex());
                    } else if ("EMPLEADO_PUESTO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_PUESTO", cell.getColumnIndex());
                    } else if ("EMPLEADO_CATEGORIA".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_CATEGORIA", cell.getColumnIndex());
                    } else if ("EMPLEADO_TIPO_JORNADA".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_TIPO_JORNADA", cell.getColumnIndex());
                    } else if ("EMPLEADO_CCT_NO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_CCT_NO", cell.getColumnIndex());
                    }
                }

                // Verificar columnas requeridas
                String[] requiredColumns = {
                        "EMPLEADO_NO", "EMPLEADO_NOMBRE_COMPLETO", "EMPLEADO_CCT_NO",
                        "EMPLEADO_PUESTO", "EMPLEADO_CATEGORIA", "EMPLEADO_TIPO_JORNADA"
                };
                boolean allColumnsFound = Arrays.stream(requiredColumns)
                        .allMatch(columnMap::containsKey);
                if (!allColumnsFound)
                    continue;

                // Procesar filas y agrupar por ID
                for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row == null)
                        continue;

                    String id = getCellValue(row, columnMap.get("EMPLEADO_NO"));
                    String nombre = getCellValue(row, columnMap.get("EMPLEADO_NOMBRE_COMPLETO"));
                    String puesto = getCellValue(row, columnMap.get("EMPLEADO_PUESTO"));
                    String categoria = getCellValue(row, columnMap.get("EMPLEADO_CATEGORIA"));
                    String jornada = getCellValue(row, columnMap.get("EMPLEADO_TIPO_JORNADA"));
                    String cct = getCellValue(row, columnMap.get("EMPLEADO_CCT_NO"));

                    if (id.isEmpty())
                        continue;

                    Empleado empleado = new Empleado(id, nombre, puesto, jornada, cct);
                    empleadosMap.computeIfAbsent(id, k -> new ArrayList<>()).add(empleado);
                }

                hojaProcesada = sheet.getSheetName();
                dataLoaded = true;
            }

            // Procesar el mapa de empleados para aplicar las reglas
            if (dataLoaded) {
                for (Map.Entry<String, List<Empleado>> entry : empleadosMap.entrySet()) {
                    List<Empleado> registros = entry.getValue();

                    // Caso 1: Empleado con un solo registro -> GUARDAR SIEMPRE
                    if (registros.size() == 1) {
                        dbManager.insertarOActualizarEmpleado(registros.get(0));
                        logger.log("Empleado cargado (única plaza): " + registros.get(0).toString());
                    }
                    // Caso 2: Empleado con múltiples registros
                    else {
                        // Verificar si es CTT (D_G)
                        boolean esCTT = registros.stream()
                                .anyMatch(e -> e.getCct() != null
                                        && e.getCct().equalsIgnoreCase("D_G"));

                        // Para empleados CTT (D_G)
                        if (esCTT) {
                            // Priorizar ADMINISTRATIVO si existe
                            java.util.Optional<Empleado> plazaAdministrativo = registros.stream()
                                    .filter(e -> e.getEmpleadoPuesto() != null
                                            && e.getEmpleadoPuesto().equalsIgnoreCase("ADMINISTRATIVO"))
                                    .findFirst();

                            if (plazaAdministrativo.isPresent()) {
                                dbManager.insertarOActualizarEmpleado(plazaAdministrativo.get());
                                logger.log("Empleado CTT cargado (priorizado ADMINISTRATIVO): "
                                        + plazaAdministrativo.get().toString());
                            } else {
                                // Si no tiene administrativo, guardar el primero (o aplicar otra lógica
                                // específica)
                                dbManager.insertarOActualizarEmpleado(registros.get(0));
                                logger.log("Empleado CTT cargado (sin ADMINISTRATIVO): "
                                        + registros.get(0).toString());
                            }
                        }
                        // Para empleados no CTT
                        else {
                            // Priorizar ADMINISTRATIVO si existe
                            java.util.Optional<Empleado> plazaAdministrativo = registros.stream()
                                    .filter(e -> e.getEmpleadoPuesto() != null
                                            && e.getEmpleadoPuesto().equalsIgnoreCase("ADMINISTRATIVO"))
                                    .findFirst();

                            if (plazaAdministrativo.isPresent()) {
                                dbManager.insertarOActualizarEmpleado(plazaAdministrativo.get());
                                logger.log("Empleado cargado (múltiples plazas, priorizado ADMINISTRATIVO): "
                                        + plazaAdministrativo.get().toString());
                            } else {
                                // Si no tiene plaza administrativa, guardar TODAS sus plazas
                                for (Empleado emp : registros) {
                                    dbManager.insertarOActualizarEmpleado(emp);
                                    logger.log("Empleado cargado (múltiples plazas, sin ADMINISTRATIVO): "
                                            + emp.toString());
                                }
                            }
                        }
                    }
                }

                mostrarTablaDesdeExcel(file, hojaProcesada);
                lblNewLabel_1.setForeground(new Color(0, 104, 0));
                btnSelectFile2.setEnabled(false);
                logger.log("Empleados cargados desde la hoja: " + hojaProcesada);
                JOptionPane.showMessageDialog(this,
                        "Empleados cargados desde la hoja: " + hojaProcesada);
            } else {
                logger.log("No se encontraron las columnas requeridas.");
                JOptionPane.showMessageDialog(this, "No se encontraron las columnas requeridas.");
                lblNewLabel_1.setForeground(new Color(104, 4, 0));
            }

        } catch (IOException e) {
            logger.logError("Error al leer el archivo: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel_1.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            logger.logError("Error en el formato del archivo: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error en el formato del archivo: " + e.getMessage());
            lblNewLabel_1.setForeground(new Color(104, 4, 0));
        }
    }

    private String convertirDecimalAHora(double valorDecimal) {
        int horas = (int) (valorDecimal * 24);
        int minutos = (int) ((valorDecimal * 24 - horas) * 60);
        String horaFormateada = String.format("%02d:%02d", horas, minutos);
        logger.log("Convertido decimal a hora: " + valorDecimal + " -> " + horaFormateada);
        return horaFormateada;
    }

    public void leerArchivoDAT(File file) {
        logger.log("Leyendo archivo .dat: " + file.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            Map<String, Map<String, List<LocalTime>>> registrosPorEmpleado = new HashMap<>();

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split("\\s+");
                    if (partes.length >= 3) {
                        String id = partes[0];
                        String fecha = partes[1];
                        String horaCompleta = partes[2];

                        String horaStr = horaCompleta.length() >= 5 ? horaCompleta.substring(0, 5) : horaCompleta;
                        LocalTime hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));

                        Map<String, List<LocalTime>> registrosPorFecha = registrosPorEmpleado.computeIfAbsent(id,
                                k -> new HashMap<>());
                        List<LocalTime> horas = registrosPorFecha.computeIfAbsent(fecha, k -> new ArrayList<>());

                        if (!horas.isEmpty()) {
                            LocalTime ultimaHora = horas.get(horas.size() - 1);
                            long diferencia = ChronoUnit.MINUTES.between(ultimaHora, hora);
                            if (diferencia < 2)
                                continue;
                        }

                        horas.add(hora);
                    }
                }
            }

            for (Map.Entry<String, Map<String, List<LocalTime>>> entradaEmpleado : registrosPorEmpleado.entrySet()) {
                String id = entradaEmpleado.getKey();
                Map<String, List<LocalTime>> registrosPorFecha = entradaEmpleado.getValue();

                for (Map.Entry<String, List<LocalTime>> entradaFecha : registrosPorFecha.entrySet()) {
                    String fecha = entradaFecha.getKey();
                    List<LocalTime> horas = entradaFecha.getValue();

                    horas.sort(Comparator.naturalOrder());

                    for (int i = 0; i < horas.size(); i += 2) {
                        String horaEntrada = horas.get(i).toString();
                        String horaSalida = (i + 1 < horas.size()) ? horas.get(i + 1).toString() : "";

                        checadas.add(new Checadas(id, "", "", fecha, horaEntrada, horaSalida, "", "", ""));
                        logger.log("Checada agregada: ID=" + id + ", Fecha=" + fecha + ", HoraEntrada=" + horaEntrada
                                + ", HoraSalida=" + horaSalida);
                    }
                }
            }

            checadas.sort(Comparator.comparing(Checadas::getFecha).thenComparing(Checadas::getHoraEntrada));

            if (!checadas.isEmpty()) {
                String primeraFecha = checadas.get(0).getFecha();
                String ultimaFecha = checadas.get(checadas.size() - 1).getFecha();
                periodo = primeraFecha + " - " + ultimaFecha;
            }

            lblNewLabel.setForeground(new Color(0, 104, 0));
            btnSelectFile.setEnabled(false);
            reporteExcepcionesCorrecto = true;
            logger.log("Datos cargados con éxito desde archivo .dat.");
            JOptionPane.showMessageDialog(this, "¡Datos cargados con éxito desde archivo .dat!");

        } catch (IOException e) {
            reporteExcepcionesCorrecto = false;
            logger.logError("Error leyendo el archivo .dat: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error leyendo el archivo .dat: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            reporteExcepcionesCorrecto = false;
            logger.logError("Error procesando los datos: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error procesando los datos: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        }
    }

    private void cargarChecador(File file) {
        logger.log("Cargando checador desde archivo: " + file.getName());

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Reporte de Excepciones");
            if (sheet == null) {
                logger.log("La hoja 'Reporte de Excepciones' no se encontró.");
                JOptionPane.showMessageDialog(this, "La hoja 'Reporte de Excepciones' no se encontró.");
                lblNewLabel.setForeground(new Color(104, 4, 0));
                return;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<Object> rowData = new ArrayList<>();
                    String id = "", fecha = "", horaEntrada = "", horaSalida = "", horaEntrada2 = "", horaSalida2 = "";

                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = "";

                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    cellValue = cell.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    double numericValue = cell.getNumericCellValue();
                                    if (j >= 4 && j <= 7) {
                                        cellValue = convertirDecimalAHora(numericValue);
                                    } else if (numericValue == Math.floor(numericValue)) {
                                        cellValue = String.valueOf((int) numericValue);
                                    } else {
                                        cellValue = String.valueOf(numericValue);
                                    }
                                    break;
                                case BOOLEAN:
                                    cellValue = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    cellValue = cell.getCellFormula();
                                    break;
                                default:
                                    cellValue = "";
                            }
                        }

                        if (((i == 1 && j == 1) || (i == 1 && j == 2)) && !cellValue.isEmpty()) {
                            periodo = cellValue;
                        }

                        if (i > 4) {
                            switch (j) {
                                case 0:
                                    id = cellValue;
                                    break;
                                case 3:
                                    fecha = cellValue;
                                    break;
                                case 4:
                                    horaEntrada = cellValue;
                                    break;
                                case 5:
                                    horaSalida = cellValue;
                                    break;
                                case 6:
                                    horaEntrada2 = cellValue;
                                    break;
                                case 7:
                                    horaSalida2 = cellValue;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    if (!id.isEmpty()) {
                        Checadas checada = new Checadas(id, "", "", fecha, horaEntrada, horaSalida, horaEntrada2,
                                horaSalida2, "");
                        checadas.add(checada);
                        logger.log("Checada agregada: " + checada.toString());
                    }
                }
            }

            lblNewLabel.setForeground(new Color(0, 104, 0));
            btnSelectFile.setEnabled(false);
            reporteExcepcionesCorrecto = true;
            logger.log("Datos cargados con éxito desde archivo Excel.");
            JOptionPane.showMessageDialog(this, "¡Datos Cargados con éxito!");

        } catch (IOException e) {
            reporteExcepcionesCorrecto = false;
            logger.logError("Error al leer el archivo: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            reporteExcepcionesCorrecto = false;
            logger.logError("Formato de archivo incorrecto o datos inválidos: " + file.getName(), e);
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

    private void actualizarChecadasConEmpleados() {
        BaseDeDatosManager dbManager = new BaseDeDatosManager();
        logger.log("Actualizando checadas con información de empleados.");

        listaEmpleados = dbManager.obtenerEmpleadosNombre();
        for (Checadas checada : checadas) {
            for (Empleado empleado : listaEmpleados) {
                if (checada.getId().equals(empleado.getId())) {
                    checada.setNombre(empleado.getNombre());
                    checada.setEmpleadoPuesto(empleado.getEmpleadoPuesto());
                    checada.setJornada(empleado.getJornada());
                    logger.log("Checada actualizada con empleado: " + checada.toString());
                    break;
                }
            }
        }
    }
}