import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportePDF {

    public void generateReport(List<Checadas> checadasList) {
        // Seleccionar ubicación para guardar el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            // Asegurarse de que el archivo tenga la extensión .pdf
            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Agrupar checadas por ID
                Map<String, List<Checadas>> checadasPorId = checadasList.stream()
                        .collect(Collectors.groupingBy(Checadas::getId));

                for (String id : checadasPorId.keySet()) {
                    // Obtener el nombre del primer registro para este ID
                    String nombre = checadasPorId.get(id).get(0).getNombre();
                    String jornada=checadasPorId.get(id).get(0).getJornada();
                    Paragraph title = new Paragraph(id + " Empleado: " + nombre + " Tipo de jornada: "+ jornada)
                            .setFontSize(12)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT);
                    document.add(title);

                    // Crear una tabla para las checadas de este ID
                    Table table = new Table(new float[]{4, 4, 4, 4, 4});
                    table.setWidth(UnitValue.createPercentValue(100));
                    
                    // Encabezado de la tabla
                    table.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBackgroundColor(new DeviceRgb(255, 158, 56)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada")).setBackgroundColor(new DeviceRgb(255, 158, 56)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida")).setBackgroundColor(new DeviceRgb(255, 158, 56)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBackgroundColor(new DeviceRgb(255, 158, 56)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado")).setBackgroundColor(new DeviceRgb(255, 158, 56)));
                    
                    // Agregar filas para cada checada de este ID
                    for (Checadas checada : checadasPorId.get(id)) {
                        table.addCell(new Cell().add(new Paragraph(checada.getFecha())));
                        table.addCell(new Cell().add(new Paragraph(checada.getHoraEntrada())));
                        table.addCell(new Cell().add(new Paragraph(checada.getHoraSalida())));
                        table.addCell(new Cell().add(new Paragraph(checada.getNombre())));

                        String tiempoTrabajo = calcularTiempoTrabajo(checada.getHoraEntrada(), checada.getHoraSalida());
                        table.addCell(new Cell().add(new Paragraph(tiempoTrabajo)));
                    }
                    Paragraph info= new Paragraph("Total de horas a cubrir: "+ checadasPorId.get(id).get(0).getTotal()) 
                    		.setFontSize(10)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT);
                    document.add(info);
                    // Agregar la tabla al documento
                    document.add(table);
                    document.add(new Paragraph("\n")); // Espacio entre tablas
                }

                document.close();
                System.out.println("PDF generado en: " + filePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        private String calcularTiempoTrabajo(String horaEntrada, String horaSalida) {
            // Validar que las horas de entrada y salida no estén vacías o nulas
            if (horaEntrada == null || horaSalida == null || horaEntrada.isEmpty() || horaSalida.isEmpty()) {
                return "";
            }
            
            try {
                // Definir el formato de hora
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                
                // Convertir las horas de entrada y salida a LocalTime
                LocalTime entrada = LocalTime.parse(horaEntrada, formatter);
                LocalTime salida = LocalTime.parse(horaSalida, formatter);
                
                // Calcular la diferencia entre la hora de entrada y salida
                Duration duracion = Duration.between(entrada, salida);
                
                // Asegurarse de que el resultado sea positivo (en caso de que la salida sea antes de la entrada)
                if (duracion.isNegative()) {
                    return "Hora de salida antes de la entrada";
                }
                
                // Extraer horas y minutos de la duración
                long horas = duracion.toHours();
                long minutos = duracion.toMinutes() % 60;
                
                // Formatear el tiempo trabajado
                return horas + ":" + minutos ;
                
            } catch (DateTimeParseException e) {
                // Manejar el error si el formato de hora es incorrecto
                System.out.println("Error en el formato de hora: " + e.getMessage());
                return "Formato de hora incorrecto";
            }
        }
    

}
