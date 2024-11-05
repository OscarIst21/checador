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
        btnSelectFile2.setBounds(380, 11, 300, 50);
        btnSelectFile2.setEnabled(false);
        contentPane.add(btnSelectFile2);

         DefaultTableModel model = new DefaultTableModel();
        table = new JTable(model);

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
            	reporte.generateReport(checadas);
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
                    String estado = row.getCell(24) != null ? row.getCell(24).toString().trim() : "";
                    String jornada = row.getCell(20) != null ? row.getCell(20).toString().trim() : "";
                    String total=row.getCell(29) != null ? row.getCell(29).toString().trim() : "";
                    Empleado empleado = new Empleado(id, nombre, categoria, estado, jornada,total);
                    listaEmpleados.add(empleado);
                }
            }

            for (Checadas checada : checadas) {
                for (Empleado empleado : listaEmpleados) {
                    if (checada.getId().equals(empleado.getId())) {
                        checada.setNombre(empleado.getNombre());
                        checada.setCategoria(empleado.getCategoria());
                        checada.setEstado(empleado.getEstado());
                        checada.setJornada(empleado.getJornada());
                        checada.setTotal(empleado.getTotal());
                        
                        break;
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

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); 
            model.setColumnCount(0); 

            // Leer la primera fila como encabezados
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    model.addColumn(headerRow.getCell(j).toString()); // Agrega los encabezados dinámicamente
                }
                periodo=headerRow.getCell(2).getStringCellValue();
                
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<Object> rowData = new ArrayList<>();
                    String id = "", nombre = "", departamento = "", fecha = "", horaEntrada = "", horaSalida = "", horaEntrada2 = "", horaSalida2 = "", retardo = "", salida = "", falta = "", total = "", notas = "";

                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = ""; // Variable para almacenar el valor de la celda

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

                        rowData.add(cellValue); 

                        if (i > 4) {
                            switch (j) {
                                case 0: id = cellValue; break;
                                case 1: nombre = cellValue; break;
                                case 2: departamento = cellValue; break;
                                case 3: fecha = cellValue; break;
                                case 4: horaEntrada = cellValue; break;
                                case 5: horaSalida = cellValue; break;
                                case 6: horaEntrada2 = cellValue; break;
                                case 7: horaSalida2 = cellValue; break;
                                case 8: retardo = cellValue; break;
                                case 9: salida = cellValue; break;
                                case 10: falta = cellValue; break;
                                case 11: total = cellValue; break;
                                case 12: notas = cellValue; break;
                            }
                        }
                    }

                    Checadas checada = new Checadas(id, nombre, departamento, fecha, horaEntrada, horaSalida, horaEntrada2, horaSalida2, retardo, salida, falta, "", notas, "", "", "");
                    checadas.add(checada);
                    model.addRow(rowData.toArray()); 
                }
            }
            System.out.println(periodo);
            JOptionPane.showMessageDialog(this, "Datos cargados exitosamente en la tabla desde 'Reporte estadístico'.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para obtener más detalles
        }
    }



}