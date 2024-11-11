import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import modelos.Checadas;
import modelos.EmpleadoDatosExtra;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportePDF {

	public void generateReport(List<Checadas> checadasList, String periodo, List<EmpleadoDatosExtra> empleadosDatos) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Guardar Reporte PDF");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

	    int userSelection = fileChooser.showSaveDialog(null);

	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File file = fileChooser.getSelectedFile();
	        String filePath = file.getAbsolutePath();

	        if (!filePath.endsWith(".pdf")) {
	            filePath += ".pdf";
	        }

	        try {
	            PdfWriter writer = new PdfWriter(filePath);
	            PdfDocument pdfDoc = new PdfDocument(writer);
	            Document document = new Document(pdfDoc);

	            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
	                @Override
	                public void handleEvent(com.itextpdf.kernel.events.Event event) {
	                    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	                    PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
	                    Rectangle pageSize = docEvent.getPage().getPageSize();
	                    float y = pageSize.getTop() - 20;

	                    try {
	                        var font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	                        String encabezado = "REPORTE DETALLADO DE REGISTRO ENTRADA Y SALIDA EN EL PERIODO: " + periodo;
	                        float textWidth = font.getWidth(encabezado, 10);
	                        float x = (pageSize.getWidth() - textWidth) / 2;
	                        canvas.beginText();
	                        canvas.setFontAndSize(font, 10);
	                        canvas.moveText(x, y);
	                        canvas.showText(encabezado);
	                        canvas.endText();
	                    } catch (java.io.IOException e) {
	                        e.printStackTrace();
	                    }
	                    canvas.release();
	                }
	            });

	            Map<String, Map<String, EmpleadoDatosExtra>> empleadoIndex = new HashMap<>();
	            for (EmpleadoDatosExtra empleado : empleadosDatos) {
	                String diaEmpleado = empleado.getDiaN().toLowerCase();
	                empleadoIndex
	                    .computeIfAbsent(empleado.getId(), k -> new HashMap<>())
	                    .put(diaEmpleado, empleado);
	            }

	            Map<String, List<Checadas>> checadasPorId = checadasList.stream()
	                    .collect(Collectors.groupingBy(Checadas::getId));

	            for (String id : checadasPorId.keySet()) {
	                String nombre = checadasPorId.get(id).get(0).getNombre();
	                String categoria = checadasPorId.get(id).get(0).getEmpleadoPuesto();
	                Paragraph title = new Paragraph(id + "\t" + nombre + "\t" + categoria)
	                        .setFontSize(12)
	                        .setBold()
	                        .setTextAlignment(TextAlignment.LEFT);
	                document.add(title);
	                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1, 1, 1}))
	                        .useAllAvailableWidth();
	                table.addHeaderCell(new Cell().add(new Paragraph("Fecha"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Día"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Estatus"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Estatus"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));
	                table.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado"))
	                        .setBackgroundColor(new DeviceRgb(255, 158, 56))
	                        .setTextAlignment(TextAlignment.CENTER)
	                        .setFontSize(10));

	                Duration totalHorasACubrir = Duration.ZERO;
	                Duration totalHorasTrabajadas = Duration.ZERO;
	                int retardos=0;
	                int mediosRetardos=0;
	                int faltas = 0;
	                int entradaFaltante = 0;
	                int salidaFaltante = 0;
	                for (Checadas checada : checadasPorId.get(id)) {
	                    String fecha = checada.getFecha() != null ? checada.getFecha() : "Sin Fecha";
	                    String diaSemana = calcularDiaSemana(fecha).toLowerCase();

	                    String horaEntradaReal = "00:00";
	                    String horaSalidaReal = "00:00";
	                    Duration duracionACubrir = Duration.ZERO;

	                    if (empleadoIndex.containsKey(id) && empleadoIndex.get(id).containsKey(diaSemana)) {
	                        EmpleadoDatosExtra empleadoData = empleadoIndex.get(id).get(diaSemana);
	                        horaEntradaReal = empleadoData.getHoraEntradaReal();
	                        horaSalidaReal = empleadoData.getHoraSalidaReal();
	                        duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
	                        totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);
	                    }

	                    String horaEntrada = (checada.getHoraEntrada() != null && !checada.getHoraEntrada().isEmpty()) ? checada.getHoraEntrada() : "00:00";
	                    String horaSalida = (checada.getHoraSalida() != null && !checada.getHoraSalida().isEmpty()) ? checada.getHoraSalida() : "00:00";
	                    String estatusEntrada = estatusChequeo(horaEntrada, horaEntradaReal);
	                    String estatusSalida = estatusChequeo(horaSalida, horaSalidaReal);
	                    String tiempoTrabajo = calcularTiempoTrabajo(horaEntrada, horaSalida);
	                    Duration duracionTrabajada = obtenerDuracion(horaEntrada, horaSalida);

	                    if (!horaEntrada.equals("00:00") && !horaSalida.equals("00:00")) {
	                        totalHorasTrabajadas = totalHorasTrabajadas.plus(duracionTrabajada);
	                    }
	                    if("Medio retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
	                    	retardos++;
	                    }
	                    if("Medio retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
	                    	retardos++;
	                    }
	                    
	                    if("Retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
	                    	retardos++;
	                    }
	                    if("Retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
	                    	retardos++;
	                    }
	                    if(mediosRetardos>=10) {
	                    	faltas++;
	                    }
	                    
	                    
	                    if ((horaEntrada.equals("00:00") && horaSalida.equals("00:00")) || (retardos>=5)) {
	                        faltas++;
	                    } else {
	                        if (horaEntrada.equals("00:00")) {
	                            entradaFaltante++;
	                        }
	                        if (horaSalida.equals("00:00")) {
	                            salidaFaltante++;
	                        }
	                    }

	                    table.addCell(new Cell().add(new Paragraph(fecha))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8)); 

	                    table.addCell(new Cell().add(new Paragraph(diaSemana))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(horaEntrada + " - " + horaEntradaReal))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8)); 

	                    table.addCell(new Cell().add(new Paragraph(estatusEntrada))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));  

	                    table.addCell(new Cell().add(new Paragraph(horaSalida + " - " + horaSalidaReal))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));  

	                    table.addCell(new Cell().add(new Paragraph(estatusSalida))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));  

	                    table.addCell(new Cell().add(new Paragraph(tiempoTrabajo))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8)); 
	                }

	                long totalHoras = totalHorasACubrir.toHours();
	                long totalMinutos = totalHorasACubrir.toMinutes() % 60;
	                long horasTrabajadas = totalHorasTrabajadas.toHours();
	                long minutosTrabajados = totalHorasTrabajadas.toMinutes() % 60;

	                Paragraph info = new Paragraph("Total de horas a cubrir: " + totalHoras + ":" + (totalMinutos < 10 ? "0" + totalMinutos : totalMinutos) +
	                        "  Horas trabajadas: " + horasTrabajadas + ":" + (minutosTrabajados < 10 ? "0" + minutosTrabajados : minutosTrabajados) +
	                        "  Faltas días: " + faltas +
	                        "  Entrada faltante: " + entradaFaltante +
	                        "  Salida faltante: " + salidaFaltante)
	                        .setFontSize(10)
	                        .setBold()
	                        .setTextAlignment(TextAlignment.LEFT);

	                document.add(info);
	                document.add(table);
	                document.add(new Paragraph("\n"));
	            }

	            document.close();
	            System.out.println("PDF generado en: " + filePath);

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


    	public String estatusChequeo(String horaChequeo, String horaReal) {
    		if (horaChequeo.equals("00:00") || horaChequeo.isEmpty()) {
    			return "Falta";
    		}
    		if (horaReal == null || horaReal.equals("00:00")) {
    	        return "";
    	    }
    	    
    	    // Separamos las horas y los minutos de las dos horas
    	    String[] partesChequeo = horaChequeo.split(":");
    	    String[] partesReal = horaReal.split(":");

    	    int horasChequeo = Integer.parseInt(partesChequeo[0]);
    	    int minutosChequeo = Integer.parseInt(partesChequeo[1]);

    	    int horasReal = Integer.parseInt(partesReal[0]);
    	    int minutosReal = Integer.parseInt(partesReal[1]);

    	    int diferenciaEnMinutos = (horasChequeo - horasReal) * 60 + (minutosChequeo - minutosReal);

    	     if (diferenciaEnMinutos <= 10) {
    	        return "Tolerancia";
    	    } else if (diferenciaEnMinutos <= 20) {
    	        return "Medio Retardo";
    	    } else if (diferenciaEnMinutos <= 30) {
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
    		if (horaEntrada.equals("00:00") || horaEntrada==null || horaSalida == null || horaSalida.equals("00:00")) {
    	        return "";
    	    }
    	    
    	    try {
    	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    	        if (horaEntrada.matches("\\d+\\.\\d+")) {
    	            horaEntrada = convertirDecimalAHora(Double.parseDouble(horaEntrada));
    	        }
    	        if (horaSalida.matches("\\d+\\.\\d+")) {
    	            horaSalida = convertirDecimalAHora(Double.parseDouble(horaSalida));
    	        }

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

    	        if (horaEntrada.matches("\\d+\\.\\d+")) {
    	            horaEntrada = convertirDecimalAHora(Double.parseDouble(horaEntrada));
    	        }
    	        if (horaSalida.matches("\\d+\\.\\d+")) {
    	            horaSalida = convertirDecimalAHora(Double.parseDouble(horaSalida));
    	        }

    	        LocalTime entrada = LocalTime.parse(horaEntrada, formatter);
    	        LocalTime salida = LocalTime.parse(horaSalida, formatter);
    	        return Duration.between(entrada, salida).isNegative() ? Duration.ZERO : Duration.between(entrada, salida);
    	        
    	    } catch (DateTimeParseException e) {
    	        System.out.println("Error en el formato de hora: " + e.getMessage());
    	        return Duration.ZERO;
    	    }
    	}
    	private String convertirDecimalAHora(double valorDecimal) {
    	    int horas = (int) (valorDecimal * 24); 
    	    int minutos = (int) ((valorDecimal * 24 - horas) * 60); 
    	    return String.format("%02d:%02d", horas, minutos);
    	}

    
}