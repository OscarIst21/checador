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

    public Vista() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1280, 720);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        listaEmpleados = new ArrayList<>(); // Inicializar la lista

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
        contentPane.add(btnSelectFile2);

        // Configuración de la tabla con encabezados directamente en el modelo
        String[] columnHeaders = {"ID", "Nombre","Departamento", "Fecha", "Horario de Entrada", "Horario de Salida", "Retardos(Min)", "Falta(Min)","Total"};
        DefaultTableModel model = new DefaultTableModel(columnHeaders, 0);
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
        contentPane.add(btnSave);
        
       
        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (.xls, .xlsx)", "xls", "xlsx"));
                int userSelection = fileChooser.showOpenDialog(Vista.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    cargarChecador(fileToOpen);
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
            model.setRowCount(0); // Limpiar tabla antes de cargar nuevos datos
            model.setColumnCount(0); // Limpiar columnas para definirlas dinámicamente

            // Leer la primera fila como encabezados
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    model.addColumn(headerRow.getCell(j).toString()); // Agrega los encabezados dinámicamente
                }
            }

            // Leer el contenido de cada fila
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<Object> rowData = new ArrayList<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        if (row.getCell(j) != null) {
                            switch (row.getCell(j).getCellType()) {
                                case STRING:
                                    rowData.add(row.getCell(j).getStringCellValue());
                                    break;
                                case NUMERIC:
                                    rowData.add(row.getCell(j).getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    rowData.add(row.getCell(j).getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    rowData.add(row.getCell(j).getCellFormula());
                                    break;
                                default:
                                    rowData.add(""); // En caso de celdas vacías u otros tipos
                            }
                        } else {
                            rowData.add(""); // Para celdas nulas
                        }
                    }
                    model.addRow(rowData.toArray()); // Agregar la fila completa a la tabla
                }
            }

            JOptionPane.showMessageDialog(this, "Datos cargados exitosamente en la tabla desde 'Reporte estadístico'.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de archivo incorrecto o datos inválidos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para obtener más detalles
        }
    }


}