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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
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
                        String categoria = checadasPorId.get(id).get(0).getCategoria();
                        Paragraph title = new Paragraph(id + "\t" + nombre +"\t"+ categoria)
                                .setFontSize(12)
                                .setBold()
                                .setTextAlignment(TextAlignment.LEFT);
                        document.add(title);

                        Table table = new Table(new float[]{4, 4, 4, 4,4,4,4});
                        table.setWidth(UnitValue.createPercentValue(100));

                        // Encabezado de la tabla
                        table.addHeaderCell(new Cell().add(new Paragraph("Fecha"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Dia"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Estatus"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Estatus"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        table.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado"))
                                .setBackgroundColor(new DeviceRgb(255, 158, 56))
                                .setTextAlignment(TextAlignment.CENTER));
                        
                        Duration totalHorasTrabajadas = Duration.ZERO;
                        int faltas=0;
                        int entrada=0;
                        int salida=0;
                        int mediosRetardos=0;
                        int retardos=0;
                        for (Checadas checada : checadasPorId.get(id)) {	
                            table.addCell(new Cell().add(new Paragraph(checada.getFecha()))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(calcularDiaSemana(checada.getFecha())))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(checada.getHoraEntrada()))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(estatusChequeo(checada.getHoraEntrada())))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(checada.getHoraSalida()))
                                    .setTextAlignment(TextAlignment.CENTER));
                            table.addCell(new Cell().add(new Paragraph(estatusChequeo(checada.getHoraSalida())))
                                    .setTextAlignment(TextAlignment.CENTER));

                            String tiempoTrabajo = calcularTiempoTrabajo(checada.getHoraEntrada(), checada.getHoraSalida());
                            table.addCell(new Cell().add(new Paragraph(tiempoTrabajo))
                                    .setTextAlignment(TextAlignment.CENTER));
                            if(estatusChequeo(checada.getHoraEntrada())=="Medio Retardo"){
                            	mediosRetardos++;
                            }
                            if(estatusChequeo(checada.getHoraEntrada())=="Retardo"){
                            	retardos++;
                            }
                            if(estatusChequeo(checada.getHoraSalida())=="Falta" || estatusChequeo(checada.getHoraEntrada())=="Falta"){
                            	salida++;
                            }
                            if(checada.getHoraEntrada()=="" || checada.getHoraSalida()=="") {
                            	if(checada.getHoraEntrada()=="" && checada.getHoraSalida()=="") {
                            	faltas++;
                            	}else
                            	 if(checada.getHoraEntrada()=="") {
                                 	entrada++;
                                 }else
                            	 if(checada.getHoraSalida()=="") {
                                 	salida++;
                                 }
                            }
                            if(retardos>=5) {
                            	faltas++;
                            }
                            if(mediosRetardos>=10) {
                            	faltas++;
                            }
                            Duration duracion = obtenerDuracion(checada.getHoraEntrada(), checada.getHoraSalida());
                            totalHorasTrabajadas = totalHorasTrabajadas.plus(duracion);
                        }


                        long totalHoras = totalHorasTrabajadas.toHours();
                        long totalMinutos = totalHorasTrabajadas.toMinutes() % 60;

                        Paragraph info = new Paragraph("Total de horas a cubrir: " + checadasPorId.get(id).get(0).getTotal() +
                                "  Horas trabajadas: " + totalHoras + ":" + totalMinutos + "  Faltas días: "+faltas + "  Entrada:"+entrada+"  Salida:"+salida)
                                .setFontSize(10)
                                .setBold()
                                .setTextAlignment(TextAlignment.LEFT);
                        document.add(info);

                        document.add(table);
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
    	public String estatusChequeo(String horaChequeo) { 
    	    if (horaChequeo == null || horaChequeo.isEmpty()) {
    	        return "";
    	    }
    	    String[] partes = horaChequeo.split(":");
    	    //int horas = Integer.parseInt(partes[0]);
    	    int minutos = Integer.parseInt(partes[1]);

    	    if (minutos >= 0 && minutos <= 10) {
    	        return "Tolerancia";
    	    } else if (minutos <= 20) {
    	        return "Medio Retardo";
    	    } else if (minutos <= 30) {
    	        return "Retardo";
    	    } else {
    	        return "Falta";
    	    }
    	}
    	public static String calcularDiaSemana(String fecha) {
    		if (fecha == null || fecha.isEmpty()) {
                return "";
            }
            // Define el formato de la fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Convierte el String a un objeto LocalDate
            LocalDate localDate = LocalDate.parse(fecha, formatter);

            // Obtiene el día de la semana en español
            return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
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