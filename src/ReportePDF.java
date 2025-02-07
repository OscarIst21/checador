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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    int tamañoTabla = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate fechaInicio = LocalDate.parse("2025-01-15", formatter);
    LocalDate fechaFin = LocalDate.parse("2025-01-31", formatter);
    public void generateReport(List<Checadas> checadasList, String periodo) {
    	String periodoReporte=periodo;
        String[] planteles = {
            "Seleccione un plantel", "Dirección General", "Contraloria y Juridico", "Caseta de vigilancia", 
            "Emsad01", "Emsad03", "Emsad06", "Emsad07", "Emsad09", "Emsad10", "Emsad11", "Emsad12", 
            "Emsad13", "Emsad14", "Emsad16", "Cecyt01", "Cecyt02", "Cecyt03", "Cecyt04", "Cecyt05", 
            "Cecyt06", "Cecyt07", "Cecyt08", "Cecyt09", "Cecyt10", "Cecyt11"
        };

        JComboBox<String> plantelComboBox = new JComboBox<>(planteles);
        JTextField nuevoPlantelField = new JTextField(20);
        JButton agregarButton = new JButton("Agregar");

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nuevoPlantel = nuevoPlantelField.getText().trim();
                if (!nuevoPlantel.isEmpty()) {
                    plantelComboBox.addItem(nuevoPlantel);
                    plantelComboBox.setSelectedItem(nuevoPlantel);
                    nuevoPlantelField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ImageIcon customIcon = null;

        try (InputStream imageStream = getClass().getResourceAsStream("/img/iconReporte.png")) {
            if (imageStream == null) {
                throw new Exception("No se encontró la imagen: /img/iconReporte.png");
            }
            byte[] imageBytes = imageStream.readAllBytes();
            customIcon = new ImageIcon(imageBytes);
        } catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage());
            customIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("¿A qué plantel corresponde el reporte?");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(plantelComboBox, BorderLayout.CENTER);

        JPanel agregarPanel = new JPanel(new BorderLayout(5, 5));
        agregarPanel.add(nuevoPlantelField, BorderLayout.CENTER);
        agregarPanel.add(agregarButton, BorderLayout.EAST);

        panel.add(agregarPanel, BorderLayout.SOUTH);
       
        int option = JOptionPane.showConfirmDialog(
            null, panel, "Seleccione el plantel", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE, customIcon
        );

        if (option != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(
                null, "No se seleccionó ningún plantel. Operación cancelada.",
                "Operación cancelada", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String plantelSeleccionado = (String) plantelComboBox.getSelectedItem();
        if ("Seleccione un plantel".equals(plantelSeleccionado)) {
            JOptionPane.showMessageDialog(
                null, "Debe seleccionar un plantel antes de continuar.",
                "Selección inválida", JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        String fechaInicioStr = JOptionPane.showInputDialog("Ingrese la fecha de inicio (yyyy-MM-dd):");
        String fechaFinStr = JOptionPane.showInputDialog("Ingrese la fecha de fin (yyyy-MM-dd):");
        List<String> dias=null;

        try {
            // Parsear las fechas de inicio y fin
             fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
             fechaFin = LocalDate.parse(fechaFinStr, formatter);

            // Aquí puedes realizar cualquier operación con las fechas
            JOptionPane.showMessageDialog(null, "Fecha de inicio: " + fechaInicio + "\nFecha de fin: " + fechaFin);
             periodoReporte=fechaInicio+" - "+fechaFin;
             dias= obtenerDiasEntreFechas(fechaInicio, fechaFin);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: Las fechas ingresadas no son válidas. Asegúrese de usar el formato yyyy-MM-dd.");
            return;
        }
        BaseDeDatosManager dbManager = new BaseDeDatosManager();
        List<EmpleadoDatosExtra> empleadosDatos = dbManager.obtenerEmpleados();

        JFileChooser fileChooser = new JFileChooser(ultimaRuta);
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        fileChooser.setSelectedFile(new File(periodoReporte + "_" + plantelSeleccionado + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ultimaRuta = file.getParent();
            String filePath = file.getAbsolutePath();

            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.LETTER);
                Document document = new Document(pdfDoc);
                
                final String periodoFinal = periodoReporte;
                pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
                    @Override
                    public void handleEvent(com.itextpdf.kernel.events.Event event) {
                        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                        PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
                        Rectangle pageSize = docEvent.getPage().getPageSize();
                        agregarNumeroPagina(pdfDoc);
                        try {
                            dibujarEncabezado(canvas, pageSize, periodoFinal);
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
             // Filtrar las checadas dentro del rango de fechas
             Map<String, List<Checadas>> checadasPorId = checadasList.stream()
                 .filter(checada -> {
                     // Convertir el String a LocalDate usando el formato
                     LocalDate fechaChecada = LocalDate.parse(checada.getFecha(), formatter);
                     return (fechaChecada.isEqual(fechaInicio) || fechaChecada.isAfter(fechaInicio)) && 
                            (fechaChecada.isEqual(fechaFin) || fechaChecada.isBefore(fechaFin));
                 })
                 .collect(Collectors.groupingBy(Checadas::getId));

             boolean primeraVezEnPagina = true;

             for (String id : checadasPorId.keySet()) {
            	    String nombre = checadasPorId.get(id).get(0).getNombre();
            	    String categoria = checadasPorId.get(id).get(0).getEmpleadoPuesto();
            	    Paragraph title = new Paragraph(id + "\t" + nombre + "\t" + categoria)
            	        .setFontSize(8)
            	        .setTextAlignment(TextAlignment.LEFT)
            	        .setMultipliedLeading(0.6f);

            	    PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            	    Table table = crearTabla();

            	    Duration totalHorasACubrir = Duration.ZERO;
            	    Duration totalHorasTrabajadas = Duration.ZERO;
            	    int retardos = 0;
            	    int mediosRetardos = 0;
            	    int faltas = 0;
            	    int entradaFaltante = 0;
            	    int salidaFaltante = 0;
            	    tamañoTabla = 0;

            	    Map<String, Map<String, List<EmpleadoDatosExtra>>> horariosDescartados = new HashMap<>();


            	    // Crear un mapa de checadas por fecha para facilitar la búsqueda
            	    Map<String, Checadas> checadasPorFecha = checadasPorId.get(id).stream()
            	        .collect(Collectors.toMap(Checadas::getFecha, checada -> checada));

            	    // Iterar sobre todas las fechas en el rango
            	    for (String fecha : dias) {
            	        Checadas checada = checadasPorFecha.get(fecha);
            	        if (checada == null) {
            	            // Si no hay checada para esta fecha, crear una checada vacía
            	            checada = new Checadas();
            	            checada.setFecha(fecha);
            	            checada.setHoraEntrada("00:00");
            	            checada.setHoraSalida("00:00");
            	        }

            	        String diaSemana = calcularDiaSemana(checada.getFecha()).toLowerCase();

            	        // Verificar si el empleado tenía un horario para este día
            	        boolean tieneHorario = empleadoIndex.containsKey(id) && 
            	                               empleadoIndex.get(id) != null && 
            	                               empleadoIndex.get(id).containsKey(diaSemana);

            	        String horaEntradaReal = "00:00";
            	        String horaSalidaReal = "00:00";
            	        Duration duracionACubrir = Duration.ZERO;

            	        if (tieneHorario) {
            	            // Obtener la lista de horarios disponibles para este día
            	            List<EmpleadoDatosExtra> horariosDisponibles = empleadoIndex.get(id).get(diaSemana);
            	            List<EmpleadoDatosExtra> descartados = horariosDescartados
            	                .computeIfAbsent(id, k -> new HashMap<>())
            	                .computeIfAbsent(diaSemana, k -> new ArrayList<>());

            	            if (!horariosDisponibles.isEmpty()) {
            	                EmpleadoDatosExtra empleadoData = horariosDisponibles.remove(0);
            	                horaEntradaReal = empleadoData.getHoraEntradaReal();
            	                horaSalidaReal = empleadoData.getHoraSalidaReal();
            	                duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
            	                totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);
            	                descartados.add(empleadoData);
            	            } else if (!descartados.isEmpty()) {
            	                horariosDisponibles.addAll(descartados);
            	                descartados.clear();
            	                EmpleadoDatosExtra empleadoData = horariosDisponibles.remove(0);
            	                horaEntradaReal = empleadoData.getHoraEntradaReal();
            	                horaSalidaReal = empleadoData.getHoraSalidaReal();
            	                duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
            	                totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);
            	                descartados.add(empleadoData);
            	            }
            	        }

            	        String horaEntrada = (checada.getHoraEntrada() != null && !checada.getHoraEntrada().isEmpty()) ? checada.getHoraEntrada() : "00:00";
            	        String horaSalida = (checada.getHoraSalida() != null && !checada.getHoraSalida().isEmpty()) ? checada.getHoraSalida() : "00:00";

            	        // Asignar el estatus de entrada y salida
            	        String estatusEntrada = tieneHorario ? estatusChequeo(horaEntrada, horaEntradaReal) : "---";
            	        String estatusSalida = tieneHorario ? estatusSalida(horaSalida, horaSalidaReal) : "---";

            	        String tiempoTrabajo = calcularTiempoTrabajo(horaEntrada, horaSalida);
            	        Duration duracionTrabajada = obtenerDuracion(horaEntrada, horaSalida);

            	        if (!horaEntrada.equals("00:00") && !horaSalida.equals("00:00")) {
            	            totalHorasTrabajadas = totalHorasTrabajadas.plus(duracionTrabajada);
            	        }

            	        // Contar retardos y faltas solo si el empleado tenía un horario para ese día
            	        if (tieneHorario) {
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

            	            // Contar falta solo si el empleado tenía un horario para ese día
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
            	        }

            	        // Agregar la fila a la tabla
            	        table.addCell(new Cell().add(new Paragraph(checada.getFecha()).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(diaSemana).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(horaEntradaReal + " - " + horaEntrada).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(estatusEntrada).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(horaSalidaReal + " - " + horaSalida).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(estatusSalida).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        table.addCell(new Cell().add(new Paragraph(tiempoTrabajo).setFont(boldFont))
            	                .setTextAlignment(TextAlignment.LEFT)
            	                .setFontSize(6).setBorder(Border.NO_BORDER)
            	                .setPadding(2) // Reduce el espacio interno de la celda
            	                .setMargin(0)); // Elimina el espacio externo

            	        tamañoTabla++;
            	    }

                    long totalHoras = totalHorasACubrir.toHours();
                    long totalMinutos = totalHorasACubrir.toMinutes() % 60;
                    long horasTrabajadas = totalHorasTrabajadas.toHours();
                    long minutosTrabajados = totalHorasTrabajadas.toMinutes() % 60;

                 // Crear la tabla con 5 columnas
                    Table info = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1}))
                            .useAllAvailableWidth()
                            .setBorder(Border.NO_BORDER); 

                    // Agregar cada dato en su celda sin bordes
                    info.addCell(new Cell().add(new Paragraph("Total horas: " + totalHoras + ":" + 
                            (totalMinutos < 10 ? "0" + totalMinutos : totalMinutos)))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Horas trabajadas: " + horasTrabajadas + ":" + 
                            (minutosTrabajados < 10 ? "0" + minutosTrabajados : minutosTrabajados)))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Faltas días: " + faltas))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Entrada faltante: " + entradaFaltante))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Salida faltante: " + salidaFaltante))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    // Agregar fila vacía con borde inferior delgado
                    info.addCell(new Cell(1, 5) // 1 fila, 5 columnas unidas
                    		.setBorderBottom(new SolidBorder(0.2f)));


                    if (primeraVezEnPagina) {
                        document.add(new Paragraph(" ").setMarginTop(35));
                        primeraVezEnPagina = false;
                    }

                    // Calcular espacio disponible en la página actual
                    float remainingHeight = calcularEspacioDisponible(document);
                    float estimatedContentHeight = 120 + (tamañoTabla * 10);
                    float alturaFila = 10; // Estimación de la altura de cada fila
                    int maxFilas = calcularMaxFilas(document);

                    if (remainingHeight < estimatedContentHeight) {
                        document.add(title);

                        // Dividir la tabla usando maxFilas calculado
                        List<Table> tablasDivididas = dividirTabla(table, maxFilas);
                        
                        int i = 0;
                        for (Table tablaParcial : tablasDivididas) {
                            if (i == 1) {
                                document.add(new AreaBreak());
                                document.add(new Paragraph(" ").setMarginTop(35));
                            }
                            document.add(tablaParcial);
                            i++;
                        }
                        document.add(info);
                    } else {
                        document.add(title);
                        document.add(table);
                        document.add(info);
                    }
                    
             	}
                agregarNumeroPagina(pdfDoc);
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
    private int calcularMaxFilas(Document document) {
        float espacioDisponible = calcularEspacioDisponible(document); // Espacio disponible en la página
        float espacioFijo = 30; // Ajusta según lo que ocupan otros elementos (title, info, márgenes, etc.)

        float espacioRestante = espacioDisponible - espacioFijo;
        float alturaFila = 7 + 4; // Tamaño de fuente 7 + margen de seguridad 4

        return (int) (espacioRestante / alturaFila); // Número máximo de filas que caben
    }

    private void agregarNumeroPagina(PdfDocument pdfDoc) {
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler() {
            @Override
            public void handleEvent(com.itextpdf.kernel.events.Event event) {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
                Rectangle pageSize = docEvent.getPage().getPageSize();

                float xPageNumber = pageSize.getRight() - 60; // Posición X del número de página
                float yPageNumber = pageSize.getBottom() + 30; // Posición Y del número de página

                String pageNumber = "Página " + docEvent.getDocument().getPageNumber(docEvent.getPage());
                try {
                    PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                    canvas.beginText();
                    canvas.setFontAndSize(font, 8); // Tamaño de la fuente
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
    private void dibujarEncabezado(PdfCanvas canvas, Rectangle pageSize, String periodo) throws java.io.IOException {
        float marginTop = 20;
        float y = pageSize.getTop() - 30;

        InputStream imageStream = getClass().getResourceAsStream("/img/logoPagina.png");
        if (imageStream == null) {
            throw new FileNotFoundException("No se encontró la imagen: /img/logoPagina.png");
        }
        ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
        float targetWidth = 113;
        float targetHeight = 32;
        float imageX = 40;
        float imageY = pageSize.getTop() - targetHeight - marginTop;
        canvas.addImageFittedIntoRectangle(imageData, new Rectangle(imageX, imageY, targetWidth, targetHeight), false);

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        canvas.beginText();
        canvas.setFontAndSize(boldFont, 10);
        canvas.setFillColor(new DeviceRgb(57, 166, 100));
        String text = "Procesado en Dirección General - Departamento de Informática";
        float textWidth = boldFont.getWidth(text, 10);
        float textX = pageSize.getRight() - textWidth - 35;
        float textY = imageY + 14;
        canvas.moveText(textX, textY);
        canvas.showText(text);
        canvas.endText();

        String encabezado = "REPORTE DETALLADO DE REGISTRO ENTRADA Y SALIDA EN EL PERIODO: " + periodo;
        float textWidthEncabezado = boldFont.getWidth(encabezado, 11);
        float x = (pageSize.getWidth() - textWidthEncabezado) / 2;
        canvas.setFillColor(new DeviceRgb(0, 0, 0));
        canvas.beginText();
        canvas.setFontAndSize(boldFont, 11);
        canvas.moveText(x, y - 35);
        canvas.showText(encabezado);
        canvas.endText();
    }
    private Table crearTabla() throws java.io.IOException {
        // Crear la tabla con 7 columnas y sin bordes
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1, 1, 1.2f}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER);  // Sin bordes

        // Usamos la fuente Helvetica Negrita directamente
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // Encabezado con texto alineado a la izquierda y sin bordes
        tabla.addHeaderCell(new Cell().add(new Paragraph("Fecha").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)  // Alineado a la izquierda
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Día").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Estatus").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Hora Salida").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Estatus").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        tabla.addHeaderCell(new Cell().add(new Paragraph("Tiempo Trabajado").setFont(boldFont))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(7)
                .setBorder(Border.NO_BORDER));  // Sin bordes en la celda

        return tabla;
    }

    public static List<String> obtenerDiasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> dias = new ArrayList<>();
        LocalDate fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            // Formato de la fecha (puedes modificar el formato a tu preferencia)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dias.add(fechaActual.format(formatter));
            fechaActual = fechaActual.plusDays(1);
        }

        return dias;
    }

    private List<Table> dividirTabla(Table tablaOriginal, int maxFilasPorTabla) throws Exception {
        int numColumnas = tablaOriginal.getNumberOfColumns();
        DeviceRgb headerColor = new DeviceRgb(244, 124, 0);
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        List<Table> tablasDivididas = new ArrayList<>();
        Table tablaActual = crearTabla();
        int filaActual = 0;

        for (IElement elemento : tablaOriginal.getChildren()) {
            if (elemento instanceof Cell) {
                Cell celda = (Cell) elemento;
                tablaActual.addCell(celda);
                filaActual++;

                if (filaActual >= maxFilasPorTabla * numColumnas) {
                    tablasDivididas.add(tablaActual);
                    tablaActual = crearTabla();
                    filaActual = 0;
                }
            }
        }

        if (!tablaActual.isEmpty()) {
            tablasDivididas.add(tablaActual);
        }

        return tablasDivididas;
    }


    private float calcularEspacioDisponible(Document document) {
        // Obtener la altura disponible en la página actual
        LayoutArea currentArea = document.getRenderer().getCurrentArea();
        return currentArea.getBBox().getHeight() - 50; // Ajusta el margen inferior
    }

    private void agregarTablaConControl(Document document, Table table, Paragraph title, Paragraph info, float estimatedContentHeight) throws IOException {
        float remainingHeight = calcularEspacioDisponible(document);

        // Verificar si hay suficiente espacio para la tabla
        if (remainingHeight < estimatedContentHeight) {
            document.add(new AreaBreak()); // Agregar un salto de página
        }

        // Agregar el título, la tabla y la información
        document.add(title);
        document.add(table);
        document.add(info);

        // Agregar una línea separadora
        Paragraph line = new Paragraph("____________________________________________________")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(6);
        document.add(line);
    }

    public String estatusSalida(String horaSalida, String horaReal) {
        if (horaSalida.equals("00:00") || horaSalida.isEmpty()) {
            return "Falta";
        }
        if (horaReal == null || horaReal.equals("00:00")) {
            return "";
        }

        String[] partesSalida = horaSalida.split(":");
        String[] partesReal = horaReal.split(":");

        int horasSalida = Integer.parseInt(partesSalida[0]);
        int minutosSalida = Integer.parseInt(partesSalida[1]);

        int horasReal = Integer.parseInt(partesReal[0]);
        int minutosReal = Integer.parseInt(partesReal[1]);

        int diferenciaEnMinutos = (horasSalida - horasReal) * 60 + (minutosSalida - minutosReal);

        if (diferenciaEnMinutos < 0) {
            return "Salida anticipada"; // Si la salida es antes de la hora real
        } else if (diferenciaEnMinutos <= 30) {
            return "Tolerancia"; // Si la salida es menos de 30 minutos después de la hora real
        } else {
            return "30 min después"; // Si la salida es más de 30 minutos después de la hora real
        }
    }

    public String estatusChequeo(String horaChequeo, String horaReal) {
        if (horaChequeo.equals("00:00") || horaChequeo.isEmpty()) {
            return "Falta";
        }
        if (horaReal == null || horaReal.equals("00:00")) {
            return "";
        }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(fecha, formatter);
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    private String calcularTiempoTrabajo(String horaEntrada, String horaSalida) {
        if (horaEntrada.equals("00:00") || horaEntrada == null || horaSalida == null || horaSalida.equals("00:00")) {
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