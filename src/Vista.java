import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import java.awt.Color;
import java.awt.Font;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class Vista extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private List<Empleado> listaEmpleados;
    private List<Checadas> checadas;
    private ReportePDF reporte;
    public String periodo="";
    public Vista() {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(100, 100, 1280, 720);
	    setLocationRelativeTo(null);
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);

	    listaEmpleados = new ArrayList<>(); // Inicializar la lista de empleados
	    checadas = new ArrayList<>();

        // Crear un botón para seleccionar el archivo Excel
        JButton btnSelectFile = new JButton("Seleccionar Archivo Excel");
        btnSelectFile.setForeground(new Color(255, 255, 255));
        btnSelectFile.setFocusable(false);
        btnSelectFile.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile.setBackground(new Color(0, 64, 0));
        btnSelectFile.setBounds(49, 11, 300, 50);
        contentPane.add(btnSelectFile);

        JButton btnSelectFile2 = new JButton("Seleccionar archivo empleados");
        btnSelectFile2.setForeground(Color.WHITE);
        btnSelectFile2.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile2.setFocusable(false);
        btnSelectFile2.setBackground(new Color(0, 64, 128));
        btnSelectFile2.setBounds(380, 11, 200, 50);
        btnSelectFile2.setEnabled(false);
        contentPane.add(btnSelectFile2);
        
        JButton btnSelectFile3 = new JButton("Seleccionar archivo 3ro");
        btnSelectFile3.setForeground(Color.WHITE);
        btnSelectFile3.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSelectFile3.setFocusable(false);
        btnSelectFile3.setBackground(new Color(100, 64, 128));
        btnSelectFile3.setBounds(590, 11, 200, 50);
        btnSelectFile3.setEnabled(false);
        contentPane.add(btnSelectFile3);

        // Agregar JScrollPane alrededor de la tabla para que siempre se muestren los encabezados
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(49, 87, 1098, 594);
        contentPane.add(scrollPane);
        
        JButton btnSave = new JButton("Generar Reporte");
        btnSave.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSave.setFocusable(false);
        btnSave.setForeground(new Color(255, 255, 255));
        btnSave.setBackground(new Color(0, 128, 192));
        btnSave.setBounds(847, 11, 300, 50);
        btnSave.setEnabled(false);
        contentPane.add(btnSave);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 reporte = new ReportePDF();
            	reporte.generateReport(checadas, periodo);
            }
        });
       
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
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
                    cargarDatosExtra(fileToOpen);
                } else {
                    JOptionPane.showMessageDialog(Vista.this, "No se seleccionó ningún archivo.");
                }
            }
        });
    }
    private void cargarDatosExtra(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Hoja1"); // Cambiar el nombre de la hoja si es necesario
            if (sheet == null) {
                JOptionPane.showMessageDialog(this, "La hoja 'Hoja1' no se encontró.");
                return;
            }

            // Leer cada fila para extraer los datos y actualizar `checadas`
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Obtener el valor de la ID
                    Cell idCell = row.getCell(0); // Suponiendo que "ID" está en la columna 0
                    String id = "";
                    if (idCell != null) {
                        if (idCell.getCellType() == CellType.NUMERIC) {
                            id = String.valueOf((int) idCell.getNumericCellValue()); // Convertir a int y luego a String
                        } else {
                            id = idCell.toString().trim();
                        }
                    }

                    // Obtener otros valores directamente por índice de columna
                    String cctNo = row.getCell(2) != null ? row.getCell(1).toString().trim() : ""; // Cambiar al índice de "cct no"
                    String diaN = row.getCell(3) != null ? row.getCell(2).toString().trim() : ""; // Cambiar al índice de "diaN"
                    String horaEntradaReal = row.getCell(4) != null ? row.getCell(3).toString().trim() : ""; // Cambiar al índice de "horaEntradaReal"
                    String horaSalidaReal = row.getCell(5) != null ? row.getCell(4).toString().trim() : ""; // Cambiar al índice de "horaSalidaReal"
                    String horarioMixto = row.getCell(7) != null ? row.getCell(5).toString().trim() : ""; // Cambiar al índice de "horarioMixto"

                    // Buscar y actualizar el objeto `Checadas` correspondiente
                    for (Checadas checada : checadas) {
                        if (checada.getId().equals(id)) {
                            checada.setCctNo(cctNo);
                            checada.setDiaN(diaN);
                            checada.setHoraEntradaReal(horaEntradaReal);
                            checada.setHoraSalidaReal(horaSalidaReal);
                            checada.setHorarioMixto(horarioMixto);
                            break;
                        }
                    }
                    
                }
                for(Checadas checada:checadas) {
                	System.out.println(checada.toString());
                }
            }

            JOptionPane.showMessageDialog(this, "Datos adicionales cargados y lista de checadas actualizada exitosamente.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
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

            // Leer cada fila para extraer los datos y actualizar `checadas`
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Obtener el valor de la ID
                    Cell idCell = row.getCell(0); // Columna de ID
                    String id = "";
                    if (idCell != null) {
                        if (idCell.getCellType() == CellType.NUMERIC) {
                            id = String.valueOf((int) idCell.getNumericCellValue()); // Convertir a int y luego a String
                        } else {
                            id = idCell.toString().trim();
                        }
                    }

                    // Obtener otros valores directamente por índice de columna
                    String nombreCompleto = row.getCell(5) != null ? row.getCell(5).toString().trim() : "";
                    String puesto = row.getCell(17) != null ? row.getCell(17).toString().trim() : "";
                    String jornada = row.getCell(20) != null ? row.getCell(20).toString().trim() : "";
                    // Buscar y actualizar el objeto `Checadas` correspondiente
                    for (Checadas checada : checadas) {
                        if (checada.getId().equals(id)) {
                            checada.setNombre(nombreCompleto);
                            checada.setEmpleadoPuesto(puesto);
                            checada.setJornada(jornada);
                            break;
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Empleados cargados y lista de checadas actualizada exitosamente.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace();
        }
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
                                    cellValue = String.valueOf(cell.getNumericCellValue());
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
                        if((i==1 && j==2)&& !cellValue.isEmpty()) {
                        	periodo=cellValue;
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

                    Checadas checada = new Checadas(id, "", "", fecha, horaEntrada, horaSalida, horaEntrada2, horaSalida2, "", "", "", "", "", "");
                    checadas.add(checada);
                }
            }
            JOptionPane.showMessageDialog(this, "¡Datos Cargados con exito!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}