import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import org.apache.poi.hpsf.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import javax.swing.ImageIcon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.SwingConstants;

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
    
    public Vista() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1280, 720);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBackground(Color.white);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        listaEmpleados = new ArrayList<>(); 
        checadas = new ArrayList<>();
        empleadosDatos = new ArrayList<>();

        JButton btnSelectFile = new JButton("Seleccionar Archivo Excel Checadas");
        btnSelectFile.setForeground(new Color(255, 255, 255));
        btnSelectFile.setFocusable(false);
        btnSelectFile.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile.setBackground(new Color(0, 64, 0));
        btnSelectFile.setBounds(68, 226, 300, 50);
        contentPane.add(btnSelectFile);

        JButton btnSelectFile2 = new JButton("Seleccionar archivo Excel empleados");
        btnSelectFile2.setForeground(Color.WHITE);
        btnSelectFile2.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile2.setFocusable(false);
        btnSelectFile2.setBackground(new Color(0, 64, 0));
        btnSelectFile2.setBounds(68, 309, 300, 50);
        btnSelectFile2.setEnabled(false);
        contentPane.add(btnSelectFile2);

        JButton btnSelectFile3 = new JButton("Seleccionar archivo Excel extra(Empleados)");
        btnSelectFile3.setForeground(Color.WHITE);
        btnSelectFile3.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile3.setFocusable(false);
        btnSelectFile3.setBackground(new Color(0, 64, 0));
        btnSelectFile3.setBounds(70, 398, 300, 50);
        btnSelectFile3.setEnabled(false);
        contentPane.add(btnSelectFile3);

        JLabel headerGreen = new JLabel("");
        headerGreen.setOpaque(true);
        headerGreen.setBounds(new Rectangle(594, 87, 611, 38));
        headerGreen.setBackground(new Color(54, 165, 85));
        contentPane.add(headerGreen);

        JButton btnSave = new JButton("Generar Reporte");
        btnSave.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSave.setFocusable(false);
        btnSave.setForeground(new Color(255, 255, 255));
        btnSave.setBackground(new Color(0, 104, 0));
        btnSave.setBounds(70, 488, 300, 50);
        btnSave.setEnabled(false);
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
        encabezado.setBackground(new Color(242, 106, 29));
        encabezado.setBounds(0, 0, 1280, 73);
        contentPane.add(encabezado);

        JLabel footer = new JLabel("");
        ImageIcon iconFooter = new ImageIcon(Vista.class.getResource("/img/footer.png"));
        Image footerImage = iconFooter.getImage(); // Obtiene la imagen original

        // Dimensiones deseadas
        int footerWidth = 1280;
        int footerHeight = 170;

        // Crear una nueva imagen con el tamaño deseado
        BufferedImage resizedFooterImage = new BufferedImage(footerWidth, footerHeight, BufferedImage.TYPE_INT_ARGB);

        // Usar Graphics2D para aplicar el escalado bicúbico
        Graphics2D g2dFooter = resizedFooterImage.createGraphics();
        g2dFooter.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); // Escalado bicúbico
        g2dFooter.drawImage(footerImage, 0, 0, footerWidth, footerHeight, null);
        g2dFooter.dispose(); // Liberar recursos gráficos

        // Asignar la imagen escalada al JLabel del footer
        footer.setIcon(new ImageIcon(resizedFooterImage));
        footer.setBounds(0, 520, footerWidth, footerHeight);
        contentPane.add(footer);


        JPanel panelTablasExcel = new JPanel();
        panelTablasExcel.setBounds(594, 124, 611, 502);
        contentPane.add(panelTablasExcel);
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(373, 226, 170, 50);
        contentPane.add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1.setBounds(373, 309, 170, 50);
        contentPane.add(lblNewLabel_1);
        
        JLabel lblNewLabel_2 = new JLabel("");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_2.setBounds(373, 398, 170, 50);
        contentPane.add(lblNewLabel_2);

        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    lblNewLabel.setText(fileToOpen.getName());
                    lblNewLabel.setForeground(new Color(0, 104, 0));
                    cargarChecador(fileToOpen);
                    btnSelectFile2.setEnabled(true);
                    btnSave.setEnabled(true);
                    btnSelectFile3.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });

        btnSelectFile2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    lblNewLabel_1.setText(fileToOpen.getName());
                    lblNewLabel_1.setForeground(new Color(0, 104, 0));
                    cargarEmpleados(fileToOpen);
                } else {
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });

        btnSelectFile3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    lblNewLabel_2.setText(fileToOpen.getName());
                    lblNewLabel_2.setForeground(new Color(0, 254, 0));
                    cargarDatosExtra(fileToOpen);
                } else {
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

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    private void cargarDatosExtra(File file) {
    	empleadosDatos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Hoja1");
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "La hoja 'Hoja1' no se encontró.");
                return;
            }

            // Iterar sobre cada fila de la hoja para obtener los datos de checadas
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

                    // Procesar horaEntradaReal en formato de 24 horas
                    String horaEntradaReal = "";
                    if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                        java.util.Date dateValue = row.getCell(4).getDateCellValue();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        horaEntradaReal = timeFormat.format(dateValue);
                    }

                    // Procesar horaSalidaReal en formato de 24 horas
                    String horaSalidaReal = "";
                    if (row.getCell(5) != null && row.getCell(5).getCellType() == CellType.NUMERIC) {
                        java.util.Date dateValue = row.getCell(5).getDateCellValue();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        horaSalidaReal = timeFormat.format(dateValue);
                    }
                    // Obtener horarioMixto sin cambios
                    String horarioMixto = row.getCell(7) != null ? row.getCell(7).toString().trim() : "";
                    if(!id.isEmpty()) {
                        EmpleadoDatosExtra empleado1 = new EmpleadoDatosExtra(id,cctNo,diaN,horaEntradaReal,horaSalidaReal, horarioMixto);
                        empleadosDatos.add(empleado1);
                        System.out.println(empleado1.toString());
                    }
                    
                }
            }
            
            JOptionPane.showMessageDialog(this, "Datos adicionales cargados y lista de checadas actualizada exitosamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace();
        }
    }



    
    private void cargarEmpleados(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Hoja1");
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "La hoja 'Hoja1' no se encontró.");
                return;
            }

            // Iterar sobre cada fila de la hoja para obtener ID, nombre, categoría, estado y jornada
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell idCell = row.getCell(0); // EMPLEADO_NO
                    Cell nombreCell = row.getCell(5); // EMPLEADO_NOMBRE_COMPLETO

                    if ((idCell == null || idCell.toString().trim().isEmpty()) &&
                            (nombreCell == null || nombreCell.toString().trim().isEmpty())) {
                        continue;
                    }

                    String id = "";
                    if (idCell != null) {
                        if (idCell.getCellType() == CellType.NUMERIC) {
                            id = String.valueOf((int) idCell.getNumericCellValue()); // Convertir a int y luego a String
                        } else {
                            id = idCell.toString().trim();
                        }
                    }

                    String nombre = nombreCell != null ? nombreCell.toString().trim() : "";
                    String categoria = row.getCell(17) != null ? row.getCell(17).toString().trim() : "";
                    String jornada = row.getCell(20) != null ? row.getCell(20).toString().trim() : "";
                    if(!id.isEmpty()) {
                    Empleado empleado = new Empleado(id, nombre, categoria, jornada);
                    listaEmpleados.add(empleado);
                    //System.out.println(empleado.toString());
                    }
                }
            }
            actualizarChecadasConEmpleados();

            

            JOptionPane.showMessageDialog(this, "Empleados cargados y lista de checadas actualizada exitosamente.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private String convertirDecimalAHora(double valorDecimal) {
        int horas = (int) (valorDecimal * 24); // Calcular la hora
        int minutos = (int) ((valorDecimal * 24 - horas) * 60); // Calcular los minutos
        return String.format("%02d:%02d", horas, minutos);
    }

    private void cargarChecador(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Reporte de Excepciones");
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "La hoja 'Reporte de Excepciones' no se encontró.");
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
                                    if (j >= 4 && j <= 7) { // Columnas de hora
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

                        if ((i == 1 && j == 2) && !cellValue.isEmpty()) {
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
                        //System.out.println(checada.toString());
                    }
                }
            }
            System.out.println(periodo);
            JOptionPane.showMessageDialog(this, "¡Datos Cargados con éxito!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
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