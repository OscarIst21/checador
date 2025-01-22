import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import modelos.Checadas;
import modelos.EmpleadoDatosExtra;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportePDF {
	 private static String ultimaRuta = System.getProperty("user.home");
	
	public void generateReport(List<Checadas> checadasList, String periodo) {
		
		String[] planteles = {"Seleccione un plantel",
			    "Emsad01", "Emsad03", "Emsad06", "Emsad07", "Emsad09", 
			    "Emsad10", "Emsad11", "Emsad12", "Emsad13", "Emsad14", 
			    "Emsad16", "Cecyt01", "Cecyt02", "Cecyt03", "Cecyt04", 
			    "Cecyt05", "Cecyt06", "Cecyt07", "Cecyt08", "Cecyt09", 
			    "Cecyt10", "Cecyt11"
			};

			JComboBox<String> plantelComboBox = new JComboBox<>(planteles);
			ImageIcon customIcon = null;

			try (InputStream imageStream = getClass().getResourceAsStream("/img/iconReporte.png")) {
			    if (imageStream == null) {
			        throw new Exception("No se encontró la imagen: /img/iconReporte.png");
			    }
			    // Leer los bytes y crear un ImageIcon directamente
			    byte[] imageBytes = imageStream.readAllBytes();
			    customIcon = new ImageIcon(imageBytes);
			} catch (Exception e) {
			    System.err.println("Error al cargar el ícono: " + e.getMessage());
			    customIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
			}


			// Crear el panel con el mensaje y el JComboBox
			JPanel panel = new JPanel(new BorderLayout(5, 5));
			JLabel label = new JLabel("¿A qué plantel corresponde el reporte?");
			label.setFont(new Font("Arial", Font.PLAIN, 14)); // Puedes ajustar el estilo del texto
			panel.add(label, BorderLayout.NORTH);
			panel.add(plantelComboBox, BorderLayout.CENTER);

			// Mostrar el JOptionPane con el panel
			int option = JOptionPane.showConfirmDialog(
			    null,
			    panel,
			    "Seleccione el plantel",
			    JOptionPane.OK_CANCEL_OPTION,
			    JOptionPane.PLAIN_MESSAGE,
			    customIcon // Aquí se usa el ícono cargado
			);

			// Verificar si el usuario seleccionó "OK"
			if (option != JOptionPane.OK_OPTION) {
			    JOptionPane.showMessageDialog(
			        null,
			        "No se seleccionó ningún plantel. Operación cancelada.",
			        "Operación cancelada",
			        JOptionPane.WARNING_MESSAGE
			    );
			    return; // Salir del método si se cancela
			}

			// Obtener el plantel seleccionado
			String plantelSeleccionado = (String) plantelComboBox.getSelectedItem();

			// Verificar si el usuario eligió una opción válida
			if ("Seleccione un plantel".equals(plantelSeleccionado)) {
			    JOptionPane.showMessageDialog(
			        null,
			        "Debe seleccionar un plantel antes de continuar.",
			        "Selección inválida",
			        JOptionPane.ERROR_MESSAGE
			    );
			    return; // Salir del método si la selección no es válida
			}
			 BaseDeDatosManager dbManager = new BaseDeDatosManager();
		        List<EmpleadoDatosExtra> empleadosDatos = dbManager.obtenerEmpleados();

		        JFileChooser fileChooser = new JFileChooser(ultimaRuta); // Usar la última ruta
		        fileChooser.setDialogTitle("Guardar Reporte PDF");
		        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

		        fileChooser.setSelectedFile(new File(periodo + "_" + plantelSeleccionado + ".pdf"));

		        int userSelection = fileChooser.showSaveDialog(null);

		        if (userSelection == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		            ultimaRuta = file.getParent(); // Guardar la última ruta
		            String filePath = file.getAbsolutePath();

		            if (!filePath.endsWith(".pdf")) {
		                filePath += ".pdf";
		            }

	        try {
	        	PdfWriter writer = new PdfWriter(filePath);
	        	PdfDocument pdfDoc = new PdfDocument(writer);
	        	pdfDoc.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.LETTER); 
	        	Document document = new Document(pdfDoc);

	            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
	                @Override
	                public void handleEvent(com.itextpdf.kernel.events.Event event) {
	                    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	                    PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
	                    Rectangle pageSize = docEvent.getPage().getPageSize();
	                    float y = pageSize.getTop() - 30;
	                    float marginTop = 20;
	                    try {
	                        // Carga la imagen desde el recurso solo una vez (cacheada)
	                    	 InputStream imageStream = getClass().getResourceAsStream("/img/logoPagina.png");
	                         if (imageStream == null) {
	                             throw new FileNotFoundException("No se encontró la imagen: /img/logoPagina.png");
	                         }
	                         ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());

	                         // Posiciones y dimensiones de la imagen
	                         float targetWidth = 113;
	                         float targetHeight = 32;
	                         float imageX = 40;
	                         float imageY = pageSize.getTop() - targetHeight - marginTop;

	                         // Dibujar la imagen en el canvas
	                         canvas.addImageFittedIntoRectangle(
	                             imageData,
	                             new Rectangle(imageX, imageY, targetWidth, targetHeight),
	                             false
	                         );

	                        // Agregar el texto después de la imagen
	                        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	                        canvas.beginText();
	                        canvas.setFontAndSize(boldFont, 10);
	                        canvas.setFillColor(new DeviceRgb(244, 124, 0)); // Color naranja

	                        String text = "Procesado en Dirección General - Departamento de Informática";
	                        float textWidth = boldFont.getWidth(text, 10); // Calcula el ancho del texto
	                        float textX = pageSize.getRight() - textWidth - 35; // Alinea el texto a la derecha con un margen
	                        float textY = imageY +14; // Centra el texto verticalmente con respecto a la imagen
	                        canvas.moveText(textX, textY);
	                        canvas.showText(text);
	                        canvas.endText();
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }


	                    try {
	                        var font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	                        String encabezado = "REPORTE DETALLADO DE REGISTRO ENTRADA Y SALIDA EN EL PERIODO: " + periodo;
	                        float textWidth = font.getWidth(encabezado, 11);
	                        float x = (pageSize.getWidth() - textWidth) / 2;

	                        canvas.setFillColor(new DeviceRgb(0, 0, 0)); 
	                        canvas.beginText();
	                        canvas.setFontAndSize(font, 11);
	                        canvas.moveText(x, y - 35); 
	                        canvas.showText(encabezado);
	                        canvas.endText();

	                        y -= 200; 
	                        canvas.beginText();
	                        canvas.setFontAndSize(font, 11);
	                        canvas.moveText(x, y); 
	                        canvas.showText("   "); // Párrafo vacío
	                        canvas.endText();

	                    } catch (java.io.IOException e) {
	                        e.printStackTrace();
	                    }

	                    canvas.release();
	                }
	            });
	            Map<String, Map<String, List<EmpleadoDatosExtra>>> empleadoIndex = new HashMap<>();
	            for (EmpleadoDatosExtra empleado : empleadosDatos) {
	                String diaEmpleado = empleado.getDiaN().toLowerCase();
	                empleadoIndex
	                    .computeIfAbsent(empleado.getId(), k -> new HashMap<>())
	                    .computeIfAbsent(diaEmpleado, k -> new ArrayList<>())
	                    .add(empleado);
	            }

	            Map<String, List<Checadas>> checadasPorId = checadasList.stream()
	                    .collect(Collectors.groupingBy(Checadas::getId));
	            boolean primeraVezEnPagina = true;
	            for (String id : checadasPorId.keySet()) {
	                String nombre = checadasPorId.get(id).get(0).getNombre();
	                String categoria = checadasPorId.get(id).get(0).getEmpleadoPuesto();
	                Paragraph title = new Paragraph(id + "\t" + nombre + "\t" + categoria)
	                        .setFontSize(10)
	                        .setBold()
	                        .setTextAlignment(TextAlignment.LEFT);
	                DeviceRgb headerColor = new DeviceRgb(244, 124, 0); 
	                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

	                Table table = crearTabla(headerColor, boldFont);


	                Duration totalHorasACubrir = Duration.ZERO;
	                Duration totalHorasTrabajadas = Duration.ZERO;
	                int retardos = 0;
	                int mediosRetardos = 0;
	                int faltas = 0;
	                int entradaFaltante = 0;
	                int salidaFaltante = 0;
	                int tamañoTabla = 0;
	             // Lista global de horarios descartados, inicializar fuera del ciclo
	                Map<String, Map<String, List<EmpleadoDatosExtra>>> horariosDescartados = new HashMap<>();

	                for (Checadas checada : checadasPorId.get(id)) {
	                    String fecha = checada.getFecha() != null ? checada.getFecha() : "Sin Fecha";
	                    String diaSemana = calcularDiaSemana(fecha).toLowerCase();

	                    String horaEntradaReal = "00:00";
	                    String horaSalidaReal = "00:00";
	                    Duration duracionACubrir = Duration.ZERO;

	                    // Asegurarse de que las estructuras necesarias existan en horariosDescartados
	                    horariosDescartados.putIfAbsent(id, new HashMap<>());
	                    horariosDescartados.get(id).putIfAbsent(diaSemana, new ArrayList<>());

	                    if (empleadoIndex.containsKey(id) && empleadoIndex.get(id).containsKey(diaSemana)) {
	                        List<EmpleadoDatosExtra> horariosDisponibles = empleadoIndex.get(id).get(diaSemana);
	                        List<EmpleadoDatosExtra> descartados = horariosDescartados.get(id).get(diaSemana);

	                        if (!horariosDisponibles.isEmpty()) {
	                            // Usar el primer horario disponible
	                            EmpleadoDatosExtra empleadoData = horariosDisponibles.remove(0);
	                            horaEntradaReal = empleadoData.getHoraEntradaReal();
	                            horaSalidaReal = empleadoData.getHoraSalidaReal();
	                            duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
	                            totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);

	                            // Mover el horario usado a la lista de descartados
	                            descartados.add(empleadoData);
	                        } else if (!descartados.isEmpty()) {
	                            // Reiniciar los horarios disponibles desde la lista de descartados
	                            horariosDisponibles.addAll(descartados);
	                            descartados.clear(); // Limpiar la lista de descartados después de reiniciar

	                            // Usar el primer horario ahora disponible
	                            EmpleadoDatosExtra empleadoData = horariosDisponibles.remove(0);
	                            horaEntradaReal = empleadoData.getHoraEntradaReal();
	                            horaSalidaReal = empleadoData.getHoraSalidaReal();
	                            duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
	                            totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);

	                            // Mover nuevamente a descartados
	                            descartados.add(empleadoData);
	                        }
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
	                    if ("Medio retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
	                        retardos++;
	                    }
	                    if ("Medio retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
	                        retardos++;
	                    }

	                    if ("Retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
	                        retardos++;
	                    }
	                    if ("Retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
	                        retardos++;
	                    }
	                    if (mediosRetardos >= 10) {
	                        faltas++;
	                    }

	                    if ((horaEntrada.equals("00:00") && horaSalida.equals("00:00")) || (retardos >= 5) || ("Falta".equals(estatusChequeo(horaSalida, horaSalidaReal)) && "Falta".equals(estatusChequeo(horaEntrada, horaEntradaReal)))) {
	                        faltas++;
	                    } else {
	                        if (horaEntrada.equals("00:00")) {
	                            entradaFaltante++;
	                        }
	                        if (horaSalida.equals("00:00")) {
	                            salidaFaltante++;
	                        }
	                    }

	                    table.addCell(new Cell().add(new Paragraph(fecha).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(diaSemana).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    // Aseguramos que la hora de entrada y salida real sean las correctas
	                    table.addCell(new Cell().add(new Paragraph(horaEntradaReal + " - " + horaEntrada).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(estatusEntrada).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(horaSalidaReal + " - " + horaSalida).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(estatusSalida).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));

	                    table.addCell(new Cell().add(new Paragraph(tiempoTrabajo).setFont(boldFont))
	                            .setTextAlignment(TextAlignment.CENTER)
	                            .setFontSize(8));
	                    tamañoTabla++;
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
	                
	                float estimatedContentHeight = 100 + (tamañoTabla * 11);
	                float remainingHeight = document.getRenderer().getCurrentArea().getBBox().getHeight() - 50;
	                if (remainingHeight < estimatedContentHeight) {
	                    document.add(new AreaBreak());
	                    primeraVezEnPagina = true;
	                }
	                if (primeraVezEnPagina) {
	                    document.add(new Paragraph(" ").setMarginTop(20)); // Este margen es solo para el primer bloque de empleados
	                    primeraVezEnPagina = false;  // Después de este bloque, ya no agregamos el margen
	                }
	              
	                if (tamañoTabla > 35) {
	                    // Dividir la tabla con un límite de 25 filas
	                    List<Table> tablasDivididas = dividirTabla(table, 35);

	                    for (int i = 0; i < tablasDivididas.size(); i++) {
	                        Table tabla = tablasDivididas.get(i);
	                        if(i!=0) {
	                            document.add(new Paragraph(" ").setMarginTop(40));
	                        }else {

	                            document.add(new Paragraph(" ").setMarginTop(15));
	                        }
	                        document.add(title);
	                        document.add(info);
	                        document.add(tabla);
	                    }
	                } else {
	                    // Si no necesita dividirse, agregar la tabla como está
	                    document.add(new Paragraph(" ").setMarginTop(10)); // Ajusta el valor del margen según lo que necesites
	                    document.add(title);
	                    document.add(info);
	                    document.add(table);
	                }


	                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler() {
	                    @Override
	                    public void handleEvent(com.itextpdf.kernel.events.Event event) {
	                        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	                        PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
	                        Rectangle pageSize = docEvent.getPage().getPageSize();
	                        
	                        // Número de página
	                        float xPageNumber = pageSize.getRight() - 60;  // Derecha
	                        float yPageNumber = pageSize.getBottom() + 30; // Altura

	                        String pageNumber = "Página " + docEvent.getDocument().getPageNumber(docEvent.getPage());
	                        try {
	                            var font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	                            canvas.beginText();
	                            canvas.setFontAndSize(font, 8);
	                            canvas.moveText(xPageNumber, yPageNumber);
	                            canvas.showText(pageNumber);
	                            canvas.endText();
	                        } catch (java.io.IOException e) {
	                            e.printStackTrace();
	                        }

	                        canvas.release();
	                    }
	                });

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
	private List<Table> dividirTabla(Table tablaOriginal, int maxFilasPorTabla) throws java.io.IOException {
		int numColumnas = tablaOriginal.getNumberOfColumns();
		
	    DeviceRgb headerColor = new DeviceRgb(244, 124, 0);
	    PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

	    // Crear las dos nuevas tablas con encabezados
	    Table primeraTabla = crearTabla(headerColor, boldFont);
	    Table segundaTabla = crearTabla(headerColor, boldFont);
	    int filaActual = 0;

	    // Iterar sobre las celdas de la tabla original
	    for (IElement elemento : tablaOriginal.getChildren()) {
	        if (elemento instanceof Cell) {
	            Cell celda = (Cell) elemento;

	            // Determinar a qué tabla agregar la celda
	            if (filaActual < maxFilasPorTabla * numColumnas) {
	                primeraTabla.addCell(celda);
	            } else {
	                segundaTabla.addCell(celda);
	            }

	            // Incrementar el contador de filas
	            filaActual++;
	        }
	    }

	    // Crear la lista para devolver las tablas
	    List<Table> tablasDivididas = new ArrayList<>();
	    if (!primeraTabla.isEmpty()) {
	        tablasDivididas.add(primeraTabla);
	    }
	    if (!segundaTabla.isEmpty()) {
	        tablasDivididas.add(segundaTabla);
	    }

	    return tablasDivididas;
	}

	private Table crearTabla(DeviceRgb headerColor, PdfFont boldFont) {
	    // Crear una tabla con 7 columnas (ajusta según tu estructura)
	    Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1, 1, 1.2f}))
	            .useAllAvailableWidth();

	    // Agregar los encabezados
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Fecha").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Día").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Estatus").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Hora Salida").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Estatus").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));
	    tabla.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado").setFont(boldFont))
	            .setBackgroundColor(headerColor)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(9));

	    return tabla;
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