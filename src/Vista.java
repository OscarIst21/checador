import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
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
    private JTabbedPane tabbedPane = new JTabbedPane();
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
        Image icon1 = Toolkit.getDefaultToolkit().getImage("/img/logoIcon.ico");
        setIconImage(icon1);
        contentPane = new JPanel();
        contentPane.setBackground(Color.white);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        listaEmpleados = new ArrayList<>(); 
        checadas = new ArrayList<>();
        empleadosDatos = new ArrayList<>();
        
        JLabel lblNewLabel_3 = new JLabel("Versiòn 1.0  25/11/24");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setBounds(1125, 654, 131, 29);
        contentPane.add(lblNewLabel_3);
        
        btnSelectFile = new JButton("Seleccionar Archivo Excel Checadas");
        btnSelectFile.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile.setIcon(new ImageIcon(Vista.class.getResource("/img/icon1.png")));
        btnSelectFile.setForeground(new Color(255, 255, 255));
        btnSelectFile.setFocusable(false);
        btnSelectFile.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile.setBackground(new Color(54, 165, 85));
        btnSelectFile.setBounds(68, 244, 300, 50);
        contentPane.add(btnSelectFile);

        btnSelectFile2 = new JButton("Seleccionar archivo Excel Empleados");
        btnSelectFile2.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile2.setIcon(new ImageIcon(Vista.class.getResource("/img/icon2.png")));
        btnSelectFile2.setForeground(Color.WHITE);
        btnSelectFile2.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile2.setFocusable(false);
        btnSelectFile2.setBackground(new Color(54, 165, 85));
        btnSelectFile2.setBounds(68, 324, 300, 50);
        btnSelectFile2.setEnabled(false);
        contentPane.add(btnSelectFile2);

        btnSelectFile3 = new JButton("Seleccionar Excel Horarios(Layout)");
        btnSelectFile3.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelectFile3.setIcon(new ImageIcon(Vista.class.getResource("/img/icon1.png")));
        btnSelectFile3.setForeground(new Color(255, 255, 255));
        btnSelectFile3.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile3.setFocusable(false);
        btnSelectFile3.setBackground(new Color(54, 165, 85));
        btnSelectFile3.setBounds(68, 404, 300, 50);
        btnSelectFile3.setEnabled(false);
        contentPane.add(btnSelectFile3);

        JLabel headerGreen = new JLabel("");
        headerGreen.setOpaque(true);
        headerGreen.setBounds(new Rectangle(594, 87, 611, 38));
        headerGreen.setBackground(new Color(54, 165, 85));
        contentPane.add(headerGreen);

        JButton btnSave = new JButton("          Generar Reporte");
        btnSave.setEnabled(false);
        btnSave.setHorizontalAlignment(SwingConstants.LEFT);
        btnSave.setIcon(new ImageIcon(Vista.class.getResource("/img/icon3.png")));
        btnSave.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnSave.setFocusable(false);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(54, 165, 85));
        btnSave.setBounds(70, 512, 300, 50);
        
        contentPane.add(btnSave);

        JLabel logo = new JLabel("");
        ImageIcon icon = new ImageIcon(Vista.class.getResource("/img/logo2.png"));
        Image image = icon.getImage(); 

        int width = 120;
        int height = 115;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); // Usar el escalado bicúbico
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

        logo.setIcon(new ImageIcon(resizedImage));
        logo.setBounds(20, 10, width, height);
        contentPane.add(logo);


        JLabel encabezado = new JLabel();
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(244, 124, 0 ));
        encabezado.setBounds(0, 0, 1280, 73);
        contentPane.add(encabezado);

        JLabel footer = new JLabel("");
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
        footer.setBounds(0, 528, 1280, 155);


        panelTablasExcel = new JPanel();
        panelTablasExcel.setBounds(594, 124, 610, 500);
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
        btnReiniciar.setIcon(new ImageIcon(Vista.class.getResource("/img/reiniciar.png")));
        btnReiniciar.setHorizontalAlignment(SwingConstants.LEFT);
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnReiniciar.setFocusable(false);
        btnReiniciar.setBackground(new Color(54, 165, 85));
        btnReiniciar.setBounds(68, 145, 300, 50);
        contentPane.add(btnReiniciar);
        
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

                // Eliminar todas las pestañas del JTabbedPane
                tabbedPane.removeAll();

                // Restaurar estados de botones
                btnSelectFile.setEnabled(true);
                btnSelectFile2.setEnabled(false);
                btnSelectFile3.setEnabled(false);
                btnSave.setEnabled(false);

                // Mensaje de confirmación
                JOptionPane.showMessageDialog(Vista.this, "Todos los datos y configuraciones han sido reseteados.");
            }
        });
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                lblNewLabel.setText("Cargando...");
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    
                    if(!lblNewLabel.getText().equals("Cargando...")) {
                    	eliminarTablaCargada(lblNewLabel.getText());
                    }
                    lblNewLabel.setText(fileToOpen.getName());
                    String hoja="Reporte de Excepciones";
                    mostrarTablaDesdeExcel(fileToOpen,hoja);
                    cargarChecador(fileToOpen);
                    btnSelectFile2.setEnabled(true);
                    btnSelectFile3.setEnabled(true);
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
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    
                    if(!lblNewLabel_1.getText().equals("Cargando...")) {
                    	eliminarTablaCargada(lblNewLabel_1.getText());
                    }
                    lblNewLabel_1.setText(fileToOpen.getName());
                    
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
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    
                    if(!lblNewLabel_2.getText().equals("Cargando...")) {
                    	eliminarTablaCargada(lblNewLabel_2.getText());
                    }
                    lblNewLabel_2.setText(fileToOpen.getName());
                    String hoja="Hoja1";
                    mostrarTablaDesdeExcel(fileToOpen,hoja);
                    cargarDatosExtra(fileToOpen);
                    btnSave.setEnabled(true);
                } else {
                    lblNewLabel_2.setText("");
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reporte = new ReportePDF();
                reporte.generateReport(checadas, periodo, empleadosDatos);
            }
        });
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
                JOptionPane.showMessageDialog(this, "La hoja seleccionada no se encontró.");
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

            Sheet sheet = workbook.getSheet("Hoja1");
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "La hoja 'Hoja1' no se encontró.");
                lblNewLabel_2.setForeground(new Color(104, 4, 0));
                return;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell idCell = row.getCell(0); // Suponiendo que "ID" está en la columna 0
                    String id = "";
                    if (idCell != null) {
                        if (idCell.getCellType() == CellType.NUMERIC) {
                            double numericValue = idCell.getNumericCellValue();
                            if (numericValue == (int) numericValue) {
                                id = String.valueOf((int) numericValue);
                            } else {
                                id = String.valueOf(numericValue);
                            }
                        } else {
                            id = idCell.toString().trim();
                        }
                    }
                    String cctNo = row.getCell(2) != null ? row.getCell(2).toString().trim() : "";

                    String diaN = "";
                    if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.NUMERIC) {
                        double numericValue = row.getCell(3).getNumericCellValue();
                        if (numericValue == (int) numericValue) {
                            int dayNumber = (int) numericValue;
                            
                            switch (dayNumber) {
                                case 1:
                                    diaN = "lunes";
                                    break;
                                case 2:
                                    diaN = "martes";
                                    break;
                                case 3:
                                    diaN = "miércoles";
                                    break;
                                case 4:
                                    diaN = "jueves";
                                    break;
                                case 5:
                                    diaN = "viernes";
                                    break;
                                default:
                                    diaN = "Día no válido";
                                    break;
                            }
                        } else {
                            diaN = String.valueOf(numericValue);
                        }
                    }
                    String horaEntradaReal = "";
                    if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                        java.util.Date dateValue = row.getCell(4).getDateCellValue();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        horaEntradaReal = timeFormat.format(dateValue);
                    }

                    String horaSalidaReal = "";
                    if (row.getCell(5) != null && row.getCell(5).getCellType() == CellType.NUMERIC) {
                        java.util.Date dateValue = row.getCell(5).getDateCellValue();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        horaSalidaReal = timeFormat.format(dateValue);
                    }
                    String horarioMixto = row.getCell(7) != null ? row.getCell(7).toString().trim() : "";
                    if(!id.isEmpty()) {
                        EmpleadoDatosExtra empleado1 = new EmpleadoDatosExtra(id,cctNo,diaN,horaEntradaReal,horaSalidaReal, horarioMixto);
                        empleadosDatos.add(empleado1);
                        //System.out.println(empleado1.toString());
                    }
                    
                }
            }
            lblNewLabel_2.setForeground(new Color(0, 104, 0));

            btnSelectFile3.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Datos adicionales cargados y lista de checadas actualizada exitosamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());

            lblNewLabel_2.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }  
    private void cargarEmpleados(File file) {
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
                for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (row != null) {
                        String id = getCellValue(row, columnMap.get("EMPLEADO_NO"));
                        String nombre = getCellValue(row, columnMap.get("EMPLEADO_NOMBRE_COMPLETO"));
                        String categoria = getCellValue(row, columnMap.get("EMPLEADO_PUESTO"));
                        String jornada = getCellValue(row, columnMap.get("EMPLEADO_TIPO_JORNADA"));

                        if (!id.isEmpty()) {
                            Empleado empleado = new Empleado(id, nombre, categoria, jornada);
                            listaEmpleados.add(empleado);
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
                actualizarChecadasConEmpleados();
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
    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return cell.toString().trim();
        }
    }

    private String convertirDecimalAHora(double valorDecimal) {
        int horas = (int) (valorDecimal * 24);
        int minutos = (int) ((valorDecimal * 24 - horas) * 60); 
        return String.format("%02d:%02d", horas, minutos);
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
                            //System.out.println(periodo);
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
            JOptionPane.showMessageDialog(this, "¡Datos Cargados con éxito!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            lblNewLabel.setForeground(new Color(104, 4, 0));
            e.printStackTrace();
        }
    }

    private void actualizarChecadasConEmpleados() {
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