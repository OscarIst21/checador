import com.itextpdf.io.exceptions.IOException;
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
import java.awt.Desktop;
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
                        String nombre = checadasPorId.get(id).get(0).getNombre();
                        String jornada = checadasPorId.get(id).get(0).getJornada();
                        Paragraph title = new Paragraph(id + " Empleado: " + nombre + " Jornada: " + jornada)
                                .setFontSize(12)
                                .setBold()
                                .setTextAlignment(TextAlignment.LEFT);
                        document.add(title);

                        Table table = new Table(new float[]{4, 4, 4, 4});
                        table.setWidth(UnitValue.createPercentValue(100));

                        // Encabezado de la tabla
                        table.addHeaderCell(new Cell().add(new Paragraph("Fecha"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));

                        Duration totalHorasTrabajadas = Duration.ZERO;

                        for (Checadas checada : checadasPorId.get(id)) {
                            table.addCell(new Cell().add(new Paragraph(checada.getFecha()))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(checada.getHoraEntrada()))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(checada.getHoraSalida()))
                                    .setTextAlignment(TextAlignment.CENTER));

                            String tiempoTrabajo = calcularTiempoTrabajo(checada.getHoraEntrada(), checada.getHoraSalida());
                            table.addCell(new Cell().add(new Paragraph(tiempoTrabajo))
                                    .setTextAlignment(TextAlignment.CENTER));

                            Duration duracion = obtenerDuracion(checada.getHoraEntrada(), checada.getHoraSalida());
                            totalHorasTrabajadas = totalHorasTrabajadas.plus(duracion);
                        }

                        document.add(table);

                        long totalHoras = totalHorasTrabajadas.toHours();
                        long totalMinutos = totalHorasTrabajadas.toMinutes() % 60;

                        Paragraph info = new Paragraph("Total de horas a cubrir: " + checadasPorId.get(id).get(0).getTotal() +
                                " Horas trabajadas: " + totalHoras + ":" + totalMinutos)
                                .setFontSize(10)
                                .setBold()
                                .setTextAlignment(TextAlignment.LEFT);
                        document.add(info);

                        document.add(new Paragraph("\n"));
                    }

                    document.close();
                    System.out.println("PDF generado en: " + filePath);

                    // Abrir el archivo PDF automáticamente si es compatible con el sistema
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(new File(filePath));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "No se pudo abrir el documento", "", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    private String calcularTiempoTrabajo(String horaEntrada, String horaSalida) {
        if (horaEntrada == null || horaSalida == null || horaEntrada.isEmpty() || horaSalida.isEmpty()) {
            return "";
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime entrada = LocalTime.parse(horaEntrada, formatter);
            LocalTime salida = LocalTime.parse(horaSalida, formatter);
            Duration duracion = Duration.between(entrada, salida);
            
            if (duracion.isNegative()) {
                return "Hora de salida antes de la entrada";
            }
            
            long horas = duracion.toHours();
            long minutos = duracion.toMinutes() % 60;
            return horas + ":" + (minutos < 10 ? "0" + minutos : minutos);
            
        } catch (DateTimeParseException e) {
            System.out.println("Error en el formato de hora: " + e.getMessage());
            return "Formato de hora incorrecto";
        }
    }
    
    private Duration obtenerDuracion(String horaEntrada, String horaSalida) {
        if (horaEntrada == null || horaSalida == null || horaEntrada.isEmpty() || horaSalida.isEmpty()) {
            return Duration.ZERO;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime entrada = LocalTime.parse(horaEntrada, formatter);
            LocalTime salida = LocalTime.parse(horaSalida, formatter);
            return Duration.between(entrada, salida).isNegative() ? Duration.ZERO : Duration.between(entrada, salida);
        } catch (DateTimeParseException e) {
            System.out.println("Error en el formato de hora: " + e.getMessage());
            return Duration.ZERO;
        }
    }
}