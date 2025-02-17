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

import javax.swing.SwingConstants;
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
    public String periodo="";
    public String checadasExcel="";
    public String empleadosExcel="";
    public String datosExtraExcel="";
    JLabel lblNewLabel=new JLabel("");
    JLabel lblNewLabel_1=new JLabel("");
    JLabel lblNewLabel_2=new JLabel("");
    JLabel cargando = new JLabel("");
    JPanel panelTablasExcel=new JPanel();
    JButton btnSelectFile= new JButton();
    JButton btnSelectFile2= new JButton();
    JButton btnSelectFile3= new JButton();
    public boolean reporteExcepcionesCorrecto= false;
    private String lastPath = System.getProperty("user.home");
    private boolean incluirEncabezado = true; // Por defecto, incluir encabezado
    private boolean incluirNumeroPagina = true; 
    private JTabbedPane tabbedPane = new JTabbedPane();
    private String filtroIdGuardado = ""; // Variable para almacenar las IDs ingresadas

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
        
        JLabel lblNewLabel_4 = new JLabel("Colegio de Estudios Científicos y Tecnológicos del estado de Baja California Sur");
        lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_4.setFont(new Font("Arial Narrow", Font.BOLD, 24));
        lblNewLabel_4.setBounds(0, 48, 1266, 50);
        contentPane.add(lblNewLabel_4);
        
        JLabel lblNewLabel_3 = new JLabel("Versiòn 1.5  19/02/25");
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
        encabezado.setBackground(new Color(244, 124, 0 ));
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

            	reporteExcepcionesCorrecto=false;
                // Mensaje de confirmación
                JOptionPane.showMessageDialog(Vista.this, "Todos los datos y configuraciones han sido reseteados.");
            }
        });
        btnConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear un panel para las opciones de configuración
                JPanel configPanel = new JPanel(new GridLayout(4, 1)); // 4 filas, 1 columna

                // Crear botones de tipo "apagador" (JCheckBox)
                JCheckBox encabezadoCheckBox = new JCheckBox("Incluir encabezado", incluirEncabezado);
                JCheckBox numeroPaginaCheckBox = new JCheckBox("Incluir número de página", incluirNumeroPagina);

                // Crear un campo de texto para filtrar por ID
                JLabel filtroIdLabel = new JLabel("Filtrar por ID (separados por coma):");
                JTextField filtroIdField = new JTextField(20);
                filtroIdField.setText(filtroIdGuardado); // Restaurar el valor guardado

                // Agregar los componentes al panel
                configPanel.add(encabezadoCheckBox);
                configPanel.add(numeroPaginaCheckBox);
                configPanel.add(filtroIdLabel);
                configPanel.add(filtroIdField);

                // Mostrar el JOptionPane con las opciones
                int result = JOptionPane.showConfirmDialog(
                    Vista.this, // Ventana padre
                    configPanel, // Panel con las opciones
                    "Configuración de Reporte", // Título del diálogo
                    JOptionPane.OK_CANCEL_OPTION, // Botones OK y Cancelar
                    JOptionPane.PLAIN_MESSAGE // Sin ícono
                );

                // Si el usuario hace clic en "OK", guardar las configuraciones
                if (result == JOptionPane.OK_OPTION) {
                    incluirEncabezado = encabezadoCheckBox.isSelected();
                    incluirNumeroPagina = numeroPaginaCheckBox.isSelected();

                    // Guardar el filtro ingresado
                    filtroIdGuardado = filtroIdField.getText().trim();
                }
            }
        });

        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Configurar el JFileChooser con la última ruta
                JFileChooser fileChooser = new JFileChooser(lastPath);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos permitidos (.xls, .xlsx, .dat)", "xls", "xlsx", "dat"));
                lblNewLabel.setText("Cargando...");
                
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    lastPath = fileToOpen.getParent(); // Actualizar la última ruta

                    if (!lblNewLabel.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel.getText());
                    }
                    lblNewLabel.setText(fileToOpen.getName());

                    // Obtener la extensión del archivo seleccionado
                    String extension = getFileExtension(fileToOpen);

                    try {
                        if (extension.equals("xls") || extension.equals("xlsx")) {
                            String hoja = "Reporte de Excepciones";
                            mostrarTablaDesdeExcel(fileToOpen, hoja);
                            cargarChecador(fileToOpen);
                        } else if (extension.equals("dat")) {
                            mostrarTablaDesdeDat(fileToOpen);
                            leerArchivoDAT(fileToOpen); 
                        } else {
                            JOptionPane.showMessageDialog(Vista.this, "Formato de archivo no soportado.");
                            lblNewLabel.setText("");
                        }

                        // Habilitar botones si el archivo fue procesado correctamente
                        if (reporteExcepcionesCorrecto) {
                            btnSelectFile2.setEnabled(true);
                            btnSelectFile3.setEnabled(true);
                            btnSave.setEnabled(true);
                        }

                    } catch (Exception ex) {
                        reporteExcepcionesCorrecto = false;
                        JOptionPane.showMessageDialog(Vista.this, "Error al procesar el archivo: " + ex.getMessage());
                        lblNewLabel.setForeground(new Color(104, 4, 0));
                        ex.printStackTrace();
                    }

                } else {
                    lblNewLabel.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });
        btnSelectFile2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblNewLabel_1.setText("Cargando...");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));

                // Usa la misma ruta del primer JFileChooser
                fileChooser.setCurrentDirectory(new File(lastPath));

                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();

                    if (!lblNewLabel_1.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel_1.getText());
                    }
                    lblNewLabel_1.setText(fileToOpen.getName());

                    // Cargar empleados con el archivo seleccionado
                    cargarEmpleados(fileToOpen);
                } else {
                    lblNewLabel_1.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });
        btnSelectFile3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblNewLabel_2.setText("Cargando...");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));

                // Usa la misma ruta que en los otros JFileChooser
                fileChooser.setCurrentDirectory(new File(lastPath));

                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();

                    if (!lblNewLabel_2.getText().equals("Cargando...")) {
                        eliminarTablaCargada(lblNewLabel_2.getText());
                    }
                    lblNewLabel_2.setText(fileToOpen.getName());

                    // Procesa el archivo Excel
                    String hoja = "Hoja1";
                    mostrarTablaDesdeExcel(fileToOpen, hoja);
                    cargarDatosExtra(fileToOpen);

                    // Actualiza la última ruta seleccionada
                    lastPath = fileToOpen.getParent();
                } else {
                    lblNewLabel_2.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Usar las IDs guardadas en la configuración
                String idsFiltro = filtroIdGuardado;
                
                // Filtrar las checadas si se ingresaron IDs
                List<Checadas> checadasFiltradas = checadas;
                if (idsFiltro != null && !idsFiltro.trim().isEmpty()) {
                    String[] idsArray = idsFiltro.split(",");
                    for (int i = 0; i < idsArray.length; i++) {
                        idsArray[i] = idsArray[i].trim();
                    }
                    checadasFiltradas = checadas.stream()
                        .filter(checada -> Arrays.asList(idsArray).contains(checada.getId()))
                        .collect(Collectors.toList());
                }

                // Actualizar las checadas con los empleados y generar el reporte
                actualizarChecadasConEmpleados();
                reporte = new ReportePDF();
                reporte.generateReport(checadasFiltradas, periodo, incluirEncabezado, incluirNumeroPagina);
                filtroIdGuardado=null;
            }
        });
        btnEditarEmpleado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(Vista.this, "Editar Empleado", true);
                dialog.setContentPane(crearPanelEditarEmpleado());
                dialog.pack();
                dialog.setLocationRelativeTo(Vista.this);
                dialog.setVisible(true);
            }
        });

        btnEditarHorario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(Vista.this, "Editar Horario", true);
                dialog.setContentPane(crearPanelEditarHorario());
                dialog.pack();
                dialog.setLocationRelativeTo(Vista.this);
                dialog.setVisible(true);
            }
        });
    }
    private JPanel crearPanelEditarEmpleado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // BorderLayout con márgenes
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Márgenes externos

        // Panel para los campos de entrada
        JPanel camposPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // 4 filas, 2 columnas, con márgenes
        JLabel lblId = new JLabel("ID:");
        JTextField txtId = new JTextField();
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        JLabel lblPuesto = new JLabel("Puesto:");
        JTextField txtPuesto = new JTextField();
        JLabel lblJornada = new JLabel("Tipo de Jornada:");
        JTextField txtJornada = new JTextField();

        camposPanel.add(lblId);
        camposPanel.add(txtId);
        camposPanel.add(lblNombre);
        camposPanel.add(txtNombre);
        camposPanel.add(lblPuesto);
        camposPanel.add(txtPuesto);
        camposPanel.add(lblJornada);
        camposPanel.add(txtJornada);

        // Panel para los botones
        JPanel botonesPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER, 10, 10)); // Botones centrados
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
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró un empleado con esa ID.");
                    txtNombre.setText("");
                    txtPuesto.setText("");
                    txtJornada.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una ID.");
            }
        });

        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String puesto = txtPuesto.getText().trim();
            String jornada = txtJornada.getText().trim();

            if (!id.isEmpty() && !nombre.isEmpty() && !puesto.isEmpty() && !jornada.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                Empleado empleado = new Empleado(id, nombre, puesto, jornada);
                dbManager.insertarOActualizarEmpleado(empleado);
                JOptionPane.showMessageDialog(this, "Empleado guardado con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            }
        });

        return panel;
    }

    private JPanel crearPanelEditarHorario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // BorderLayout con márgenes
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Márgenes externos

        // Panel para los campos de entrada
        JPanel camposPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // GridLayout dinámico
        JLabel lblId = new JLabel("ID:");
        JTextField txtId = new JTextField();
        JCheckBox chkMultiplesHorarios = new JCheckBox("¿Tiene más de un horario por día?");
        JTextField[] txtHorarios = new JTextField[14]; // 7 días + 7 días adicionales si hay múltiples horarios

        camposPanel.add(lblId);
        camposPanel.add(txtId);
        camposPanel.add(chkMultiplesHorarios);
        camposPanel.add(new JLabel()); // Espacio en blanco

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (int i = 0; i < 7; i++) {
            camposPanel.add(new JLabel(dias[i]));
            txtHorarios[i] = new JTextField();
            camposPanel.add(txtHorarios[i]);

            if (chkMultiplesHorarios.isSelected()) {
                camposPanel.add(new JLabel(dias[i] + " (Segundo horario)"));
                txtHorarios[i + 7] = new JTextField();
                camposPanel.add(txtHorarios[i + 7]);
            }
        }

        // Panel para los botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Botones centrados
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
                List<EmpleadoDatosExtra> horarios = dbManager.obtenerHorariosPorId(id);

                if (!horarios.isEmpty()) {
                    for (EmpleadoDatosExtra horario : horarios) {
                        int diaIndex = obtenerIndiceDia(horario.getDiaN());
                        if (diaIndex != -1) {
                            txtHorarios[diaIndex].setText(horario.getHoraEntradaReal() + " - " + horario.getHoraSalidaReal());
                            if (chkMultiplesHorarios.isSelected() && diaIndex + 7 < txtHorarios.length) {
                                txtHorarios[diaIndex + 7].setText(horario.getHoraEntradaReal() + " - " + horario.getHoraSalidaReal());
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontraron horarios para el empleado con ID: " + id);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una ID.");
            }
        });

        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (!id.isEmpty()) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                List<EmpleadoDatosExtra> horarios = new ArrayList<>();

                for (int i = 0; i < 7; i++) {
                    String horario = txtHorarios[i].getText().trim();
                    if (!horario.isEmpty()) {
                        String[] partes = horario.split(" - ");
                        if (partes.length == 2) {
                            EmpleadoDatosExtra horarioEntrada = new EmpleadoDatosExtra();
                            horarioEntrada.setId(id);
                            horarioEntrada.setDiaN(dias[i]);
                            horarioEntrada.setHoraEntradaReal(partes[0]);
                            horarioEntrada.setHoraSalidaReal(partes[1]);
                            horarios.add(horarioEntrada);
                        }
                    }

                    if (chkMultiplesHorarios.isSelected() && i + 7 < txtHorarios.length) {
                        String horario2 = txtHorarios[i + 7].getText().trim();
                        if (!horario2.isEmpty()) {
                            String[] partes = horario2.split(" - ");
                            if (partes.length == 2) {
                                EmpleadoDatosExtra horarioSalida = new EmpleadoDatosExtra();
                                horarioSalida.setId(id);
                                horarioSalida.setDiaN(dias[i]);
                                horarioSalida.setHoraEntradaReal(partes[0]);
                                horarioSalida.setHoraSalidaReal(partes[1]);
                                horarios.add(horarioSalida);
                            }
                        }
                    }
                }

                dbManager.insertarOActualizarHorarios(horarios);
                JOptionPane.showMessageDialog(this, "Horarios guardados con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una ID.");
            }
        });

        return panel;
    }
    private int obtenerIndiceDia(String dia) {
        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        for (int i = 0; i < dias.length; i++) {
            if (dias[i].equalsIgnoreCase(dia)) {
                return i; // Devuelve el índice del día en el arreglo
            }
        }
        return -1; // Si no se encuentra el día
    }

    private String obtenerNombreDia(int indice) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        if (indice >= 0 && indice < dias.length) {
            return dias[indice]; // Devuelve el nombre del día según el índice
        }
        return "";
    }
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < name.length() - 1) {
            return name.substring(lastIndex + 1).toLowerCase(); // Devuelve la extensión en minúsculas
        }
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
    }
  
    private void mostrarTablaDesdeExcel(File file, String hoja) {

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(hoja); 
            if (sheet == null) {
               // JOptionPane.showMessageDialog(this, "La hoja seleccionada no se encontró.");
                return;
            }

            DefaultTableModel tableModel = new DefaultTableModel();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                tableModel.addColumn(cell.toString());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getLastCellNum() <= 0) {
                    continue; // Ignorar filas vacías
                }

                Object[] rowData = new Object[row.getLastCellNum()];
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        rowData[j] = formatCell(cell, j); // Formatear la celda según su tipo
                    } else {
                        rowData[j] = ""; // Si la celda está vacía, asignar una cadena vacía
                    }
                }
                tableModel.addRow(rowData);
            }

            JTable table = new JTable(tableModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            String tabName = file.getName();
            tabbedPane.addTab(tabName, scrollPane);

            panelTablasExcel.removeAll();
            panelTablasExcel.add(tabbedPane);
            panelTablasExcel.revalidate();
            panelTablasExcel.repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarTablaDesdeDat(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            DefaultTableModel tableModel = new DefaultTableModel();

            // Configurar encabezados fijos
            String[] encabezados = {"ID", "Fecha", "Hora", "1", "0", "1", "0"};
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

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo .dat: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error procesando el archivo .dat: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Object formatCell(Cell cell, int columnIndex) {
        String cellValue = "";

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
        
        return cellValue;
    }

    private String convertirDecimalAHora1(double decimal) {
        java.util.Date date = DateUtil.getJavaDate(decimal);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(date);
    }
    private void cargarDatosExtra(File file) {
        empleadosDatos = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            boolean dataLoaded = false;
            String hojaProcesada = "";

            // Iterar sobre todas las hojas del archivo Excel
            for (Sheet sheet : workbook) {
                if (sheet == null) continue;

                // Mapear columnas dinámicamente según los nombres en la primera fila
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) continue;

                Map<String, Integer> columnMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String header = cell.getStringCellValue().trim().toLowerCase();
                        columnMap.put(header, cell.getColumnIndex());
                    }
                }

                // Verificar si todas las columnas requeridas están presentes
                String[] requiredColumns = {
                    "numero_empleado", "cct", "cct_no", "dia", "hora_entrada", "hora_salida", 
                    "hora_salida_dia_siguiente", "horario_mixto", "anio", "periodo_inicio", "periodo_termino"
                };
                boolean allColumnsFound = true;
                for (String column : requiredColumns) {
                    if (!columnMap.containsKey(column)) {
                        allColumnsFound = false;
                        break;
                    }
                }

                if (!allColumnsFound) {
                    continue; // Pasar a la siguiente hoja si faltan columnas
                }

                // Procesar las filas de la hoja
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String id = getCellValue(row, columnMap.get("numero_empleado"));
                    String cct = getCellValue(row, columnMap.get("cct"));
                    String cctNo = getCellValue(row, columnMap.get("cct_no"));
                    String dia = getDayName(getCellNumericValue(row, columnMap.get("dia")));
                    String horaEntradaReal = getTimeValue(row, columnMap.get("hora_entrada"));
                    System.out.println("Hora: "+horaEntradaReal);
                    String horaSalidaReal = getTimeValue(row, columnMap.get("hora_salida"));
                    String horaSalidaDiaSiguiente = getCellValue(row, columnMap.get("hora_salida_dia_siguiente"));
                    String horarioMixto = getCellValue(row, columnMap.get("horario_mixto"));
                    String anio = getCellValue(row, columnMap.get("anio"));
                    String periodoInicio = getCellValue(row, columnMap.get("periodo_inicio"));
                    String periodoTermino = getCellValue(row, columnMap.get("periodo_termino"));

                    if (!id.isEmpty()) {
                        EmpleadoDatosExtra empleado = new EmpleadoDatosExtra(id, cct, cctNo, dia, horaEntradaReal, horaSalidaReal,
                                horaSalidaDiaSiguiente, horarioMixto, anio, periodoInicio, periodoTermino);
                        empleadosDatos.add(empleado);
                    }
                }

                hojaProcesada = sheet.getSheetName(); // Guardar el nombre de la hoja procesada
                dataLoaded = true;
                break; // Detener la búsqueda después de encontrar una hoja válida
            }

            if (dataLoaded) {
                BaseDeDatosManager dbManager = new BaseDeDatosManager();
                dbManager.actualizarDatos(empleadosDatos);

                lblNewLabel_2.setForeground(new Color(0, 104, 0));
                btnSelectFile3.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Datos adicionales cargados desde la hoja: " + hojaProcesada);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron las columnas requeridas en ninguna hoja.");
                lblNewLabel_2.setForeground(new Color(104, 4, 0));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel_2.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel_2.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

    private String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null) return "";
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim(); // Si ya es texto, solo lo devolvemos.
            } else if (cell.getCellType() == CellType.NUMERIC) {
                // Si el número es entero, lo devolvemos como String sin decimales
                double value = cell.getNumericCellValue();
                if (value == (long) value) {  // Verificamos si el valor es un número entero
                    return String.valueOf((long) value);
                } else {
                    return String.valueOf(value); // Si es decimal, lo dejamos como está
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
        if (columnIndex == null) return "Columna no válida"; 
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "Celda vacía"; 

        //System.out.println("Tipo de celda: " + cell.getCellType());
        //System.out.println("Valor crudo de la celda: " + cell.toString());

        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // Formatear para solo horas y minutos
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                return timeFormat.format(cell.getDateCellValue());
            } else {
                //System.out.println("El valor numérico no es una fecha/hora reconocida.");
            }
        } else if (cell.getCellType() == CellType.STRING) {
            String timeValue = cell.getStringCellValue().trim();
            //System.out.println("Contenido de la celda como texto: " + timeValue);
            // Extraer horas y minutos del texto en formato hh:mm:ss
            if (timeValue.matches("\\d{1,2}:\\d{2}:\\d{2}")) { 
                return timeValue.substring(0, 5); // Tomar solo "hh:mm"
            }
        }

        return "Valor no reconocido"; 
    }


    private double getCellNumericValue(Row row, Integer columnIndex) {
        if (columnIndex == null) return -1; // Columna no válida
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return -1; // Celda vacía
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue(); // Retornar el valor numérico directamente
            case STRING:
                try {
                    // Intentar convertir texto numérico a un número
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return -1; // No se pudo convertir el texto a número
                }
            default:
                return -1; // Tipo de celda no compatible
        }
    }


    private String getDayName(double dayNumber) {
        int day = (int) dayNumber;
        switch (day) {
            case 1: return "lunes";
            case 2: return "martes";
            case 3: return "miércoles";
            case 4: return "jueves";
            case 5: return "viernes";
            case 6: return "sabado";
            case 7: return "domingo";
            default: return "Día no válido";
        }
    }

    private void cargarEmpleados(File file) {
    	BaseDeDatosManager dbManager = new BaseDeDatosManager(); 

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            boolean dataLoaded = false;
            String hojaProcesada = "";

            // Iterar por todas las hojas del archivo
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                // Buscar las columnas necesarias en la primera fila
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    continue; // Saltar hojas vacías o sin encabezados
                }

                Map<String, Integer> columnMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    String columnName = cell.toString().trim();
                    if ("EMPLEADO_NO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_NO", cell.getColumnIndex());
                    } else if ("EMPLEADO_NOMBRE_COMPLETO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_NOMBRE_COMPLETO", cell.getColumnIndex());
                    } else if ("EMPLEADO_PUESTO".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_PUESTO", cell.getColumnIndex());
                    } else if ("EMPLEADO_TIPO_JORNADA".equalsIgnoreCase(columnName)) {
                        columnMap.put("EMPLEADO_TIPO_JORNADA", cell.getColumnIndex());
                    }
                }

                // Verificar si se encontraron todas las columnas necesarias
                String[] requiredColumns = {"EMPLEADO_NO", "EMPLEADO_NOMBRE_COMPLETO", "EMPLEADO_PUESTO", "EMPLEADO_TIPO_JORNADA"};
                boolean allColumnsFound = true;
                for (String col : requiredColumns) {
                    if (!columnMap.containsKey(col)) {
                        allColumnsFound = false;
                        break;
                    }
                }

                if (!allColumnsFound) {
                    continue; // Pasar a la siguiente hoja si faltan columnas
                }

                // Si se encuentran todas las columnas, procesar los datos
                for (int j = 1; j <= sheet.getLastRowNum()+1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null) {
                        String id = getCellValue(row, columnMap.get("EMPLEADO_NO"));
                        String nombre = getCellValue(row, columnMap.get("EMPLEADO_NOMBRE_COMPLETO"));
                        String categoria = getCellValue(row, columnMap.get("EMPLEADO_PUESTO"));
                        String jornada = getCellValue(row, columnMap.get("EMPLEADO_TIPO_JORNADA"));

                        if (!id.isEmpty()) {
                            Empleado empleado = new Empleado(id, nombre, categoria, jornada);

                            dbManager.insertarOActualizarEmpleado(empleado);
                        }
                    }
                }

                hojaProcesada = sheet.getSheetName(); // Guardar el nombre de la hoja procesada
                dataLoaded = true;

                // Llamar a mostrarTablaDesdeExcel con la hoja encontrada
                mostrarTablaDesdeExcel(file, hojaProcesada);

                break; // Detener la búsqueda después de encontrar una hoja válida
            }

            if (dataLoaded) {

                lblNewLabel_1.setForeground(new Color(0, 104, 0));
                btnSelectFile2.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Empleados cargados desde la hoja: " + hojaProcesada + " y lista de checadas actualizada.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron las columnas requeridas en ninguna hoja.");
                lblNewLabel_1.setForeground(new Color(104, 4, 0));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel_1.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel_1.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

  


    // Método auxiliar para obtener el valor de una celda como String
    private String convertirDecimalAHora(double valorDecimal) {
        int horas = (int) (valorDecimal * 24);
        int minutos = (int) ((valorDecimal * 24 - horas) * 60); 
        return String.format("%02d:%02d", horas, minutos);
    }
    public void leerArchivoDAT(File file) {
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

                        // Recortar la hora a "HH:mm" e ignorar los segundos
                        String horaStr = horaCompleta.length() >= 5 ? horaCompleta.substring(0, 5) : horaCompleta;
                        LocalTime hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));

                        // Validar intervalos para evitar registros en poco tiempo
                        Map<String, List<LocalTime>> registrosPorFecha = registrosPorEmpleado.computeIfAbsent(id, k -> new HashMap<>());
                        List<LocalTime> horas = registrosPorFecha.computeIfAbsent(fecha, k -> new ArrayList<>());

                        // Validar si la diferencia con la última hora registrada es menor a 40 minutos
                        if (!horas.isEmpty()) {
                            LocalTime ultimaHora = horas.get(horas.size() - 1);
                            long diferencia = ChronoUnit.MINUTES.between(ultimaHora, hora);
                            if (diferencia < 40) {
                                continue; // Omitir checada si está dentro del intervalo
                            }
                        }

                        // Agregar la hora si pasa la validación
                        horas.add(hora);
                    }
                }
            }

            // Procesar registros agrupados
            for (Map.Entry<String, Map<String, List<LocalTime>>> entradaEmpleado : registrosPorEmpleado.entrySet()) {
                String id = entradaEmpleado.getKey();
                Map<String, List<LocalTime>> registrosPorFecha = entradaEmpleado.getValue();

                for (Map.Entry<String, List<LocalTime>> entradaFecha : registrosPorFecha.entrySet()) {
                    String fecha = entradaFecha.getKey();
                    List<LocalTime> horas = entradaFecha.getValue();

                    // Ordenar las horas
                    horas.sort(Comparator.naturalOrder());

                    // Emparejar entradas y salidas
                    for (int i = 0; i < horas.size(); i += 2) {
                        String horaEntrada = horas.get(i).toString();
                        String horaSalida = (i + 1 < horas.size()) ? horas.get(i + 1).toString() : ""; // Caso sin salida

                        // Almacenar en la lista de checadas
                        checadas.add(new Checadas(id, "", "", fecha, horaEntrada, horaSalida, "", "", ""));
                        Checadas checada = new Checadas(id, "", "", fecha, horaEntrada, horaSalida, "", "", "");
                        //System.out.println(checada.toString());
                    }
                }
            }

            // Ordenar la lista de checadas
            checadas.sort(Comparator.comparing(Checadas::getFecha).thenComparing(Checadas::getHoraEntrada));

            // Obtener el periodo
            if (!checadas.isEmpty()) {
                String primeraFecha = checadas.get(0).getFecha();
                String ultimaFecha = checadas.get(checadas.size() - 1).getFecha();
                periodo = primeraFecha + " - " + ultimaFecha;
            }

            // Configuración visual
            lblNewLabel.setForeground(new Color(0, 104, 0));
            btnSelectFile.setEnabled(false);
            reporteExcepcionesCorrecto = true;
            JOptionPane.showMessageDialog(this, "¡Datos cargados con éxito desde archivo .dat!");

        } catch (IOException e) {
            reporteExcepcionesCorrecto = false;
            JOptionPane.showMessageDialog(this, "Error leyendo el archivo .dat: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            reporteExcepcionesCorrecto = false;
            JOptionPane.showMessageDialog(this, "Error procesando los datos: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        }
    }




    private void cargarChecador(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Reporte de Excepciones");
            if (sheet == null) {
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

                        if (((i == 1 && j == 1) || (i==1 && j==2))  && !cellValue.isEmpty()) {
                            periodo = cellValue;
                        }

                        if (i > 4) {
                            switch (j) {
                                case 0: id = cellValue; break;
                                case 3: fecha = cellValue; break;
                                case 4: horaEntrada = cellValue; break;
                                case 5: horaSalida = cellValue; break;
                                case 6: horaEntrada2 = cellValue; break;
                                case 7: horaSalida2 = cellValue; break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (!id.isEmpty()) {
                        Checadas checada = new Checadas(id, "", "", fecha, horaEntrada, horaSalida, horaEntrada2, horaSalida2, "");
                        checadas.add(checada);
                    }
                }
            }
            lblNewLabel.setForeground(new Color(0, 104, 0));
            btnSelectFile.setEnabled(false);
            reporteExcepcionesCorrecto=true;
            JOptionPane.showMessageDialog(this, "¡Datos Cargados con éxito!");

        } catch (IOException e) {
        	reporteExcepcionesCorrecto=false;
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
        	reporteExcepcionesCorrecto=false;
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

    private void actualizarChecadasConEmpleados() {
    	BaseDeDatosManager dbManager = new BaseDeDatosManager();

        listaEmpleados =dbManager.obtenerEmpleadosNombre();
        for (Checadas checada : checadas) {
            for (Empleado empleado : listaEmpleados) {
                if (checada.getId().equals(empleado.getId())) {
                    checada.setNombre(empleado.getNombre());
                    checada.setEmpleadoPuesto(empleado.getEmpleadoPuesto());
                    checada.setJornada(empleado.getJornada());
                    break;
                }
            }
        }
    }
}