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
import modelos.Empleado;
import modelos.EmpleadoDatosExtra;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
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
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReportePDF {
    private static String ultimaRuta = System.getProperty("user.home");
    private LoggerSAPI logger;
    int tamañoTabla = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static LocalDate fechaInicio = null;
    private static LocalDate fechaFin = null;
    private Map<String, List<Checadas>> checadasPorId;

    public void generateReport(List<Checadas> checadasList, String periodo, boolean incluirEncabezado,
            boolean incluirNumeroPagina, String idString) {

        logger = LoggerSAPI.getInstance();
        logger.log("Iniciando generacion de reporte");

        String periodoReporte = periodo;
        String[] planteles = {
                "Seleccione un plantel", "DIRECCION GENERAL", "CONTRALORÍA Y JURÍDICO", "CASETA_VIG",
                "EMSAD01", "EMSAD03", "EMSAD06", "EMSAD07", "EMSAD08", "EMSAD09", "EMSAD10", "EMSAD11", "EMSAD12",
                "EMSAD13", "EMSAD14", "EMSAD16", "CECYT01", "CECYT02", "CECYT03", "CECYT04", "CECYT05",
                "CECYT06", "CECYT07", "CECYT08", "CECYT09", "CECYT10", "CECYT11"
        };

        JComboBox<String> plantelComboBox = new JComboBox<>(planteles);
        JTextField nuevoPlantelField = new JTextField(20);
        JButton agregarButton = new JButton("Agregar");

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nuevoPlantel = nuevoPlantelField.getText().trim();
                logger.log("Nuevo plantel ingresado: " + nuevoPlantel);

                if (!nuevoPlantel.isEmpty()) {
                    plantelComboBox.addItem(nuevoPlantel);
                    plantelComboBox.setSelectedItem(nuevoPlantel);
                    nuevoPlantelField.setText("");
                    logger.log("Plantel agregado y seleccionado: " + nuevoPlantel);
                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar vacío.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    logger.log("Error: intento de agregar un plantel vacío");
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
            logger.log("Ícono cargado correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage());
            customIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
            logger.log("Error al cargar el ícono, se usará el ícono por defecto");
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

        logger.log("Mostrando diálogo de selección de plantel");

        int option = JOptionPane.showConfirmDialog(
                null, panel, "Seleccione el plantel", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, customIcon);

        if (option != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(
                    null, "No se seleccionó ningún plantel. Operación cancelada.",
                    "Operación cancelada", JOptionPane.WARNING_MESSAGE);
            logger.log("Operación cancelada por el usuario");
            return;
        }

        String plantelSeleccionado = (String) plantelComboBox.getSelectedItem();
        logger.log("Plantel seleccionado: " + plantelSeleccionado);

        if ("Seleccione un plantel".equals(plantelSeleccionado)) {
            JOptionPane.showMessageDialog(
                    null, "Debe seleccionar un plantel antes de continuar.",
                    "Selección inválida", JOptionPane.ERROR_MESSAGE);
            logger.log("Error: no se seleccionó un plantel válido");
            return;
        }

        JPanel fechaPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel fechaInicioLabel = new JLabel("Fecha de inicio (yyyy-MM-dd):");
        JTextField fechaInicioField = new JTextField(10);
        JLabel fechaFinLabel = new JLabel("Fecha de fin (yyyy-MM-dd):");
        JTextField fechaFinField = new JTextField(10);
        JLabel cctLabel = new JLabel("CCT:");
        String[] cctList = {
                "C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10", "C11", "D_G", "E01", "E03", "E06",
                "E07", "E08", "E09", "E10", "E11", "E12", "E13", "E14", "E16",
        };

        JComboBox<String> cctComboBox = new JComboBox<>(cctList);
        // Establecer valores predeterminados si las fechas son null
        if (fechaInicio != null) {
            fechaInicioField.setText(fechaInicio.format(formatter));
        }

        if (fechaFin != null) {
            fechaFinField.setText(fechaFin.format(formatter));
        }

        fechaPanel.add(fechaInicioLabel);
        fechaPanel.add(fechaInicioField);
        fechaPanel.add(fechaFinLabel);
        fechaPanel.add(fechaFinField);
        fechaPanel.add(cctLabel);
        fechaPanel.add(cctComboBox);

        logger.log("Mostrando diálogo de ingreso de rango de fechas");

        int result = JOptionPane.showConfirmDialog(
                null,
                fechaPanel,
                "Ingrese el rango de fechas",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.CANCEL_OPTION) {
            logger.log("Usuario canceló la selección de fechas");
            return;
        }

        logger.log("Diálogo de fechas cerrado con opción: " + result);

        List<String> dias = null;
        if (result == JOptionPane.OK_OPTION) {
            String fechaInicioStr = fechaInicioField.getText().trim();
            String fechaFinStr = fechaFinField.getText().trim();

            logger.log("Fechas ingresadas: Inicio - " + fechaInicioStr + ", Fin - " + fechaFinStr);

            try {
                fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
                fechaFin = LocalDate.parse(fechaFinStr, formatter);

                if (fechaInicio.isAfter(fechaFin)) {
                    JOptionPane.showMessageDialog(null,
                            "Error: La fecha de inicio no puede ser posterior a la fecha de fin.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    logger.log("Error: la fecha de inicio es posterior a la fecha de fin");
                    return;
                }

                JOptionPane.showMessageDialog(null, "Fecha de inicio: " + fechaInicio + "\nFecha de fin: " + fechaFin);
                periodoReporte = fechaInicio + " - " + fechaFin;
                dias = obtenerDiasEntreFechas(fechaInicio, fechaFin);
                logger.log("Fechas validadas y período generado: " + periodoReporte);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error: Las fechas ingresadas no son válidas. Asegúrese de usar el formato yyyy-MM-dd.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                logger.log("Error al parsear las fechas: " + e.getMessage());
                return;
            }
        }

        // Obtener el CCT seleccionado
        String cctSeleccionado = (String) cctComboBox.getSelectedItem();
        logger.log("CCT seleccionado: " + cctSeleccionado);

        BaseDeDatosManager dbManager = new BaseDeDatosManager();

        // Obtener todos los empleados del CCT seleccionado
        List<Empleado> empleadosCCT = dbManager.obtenerEmpleadosPorCCT(cctSeleccionado);
        logger.log("Número de empleados encontrados para CCT " + cctSeleccionado + ": " + empleadosCCT.size());

        // Crear un mapa de empleados para fácil acceso
        Map<String, Empleado> mapaEmpleados = empleadosCCT.stream()
                .collect(Collectors.toMap(Empleado::getId, empleado -> empleado));

        // Obtener horarios para estos empleados
        List<EmpleadoDatosExtra> empleadosDatos = dbManager.obtenerTodosLosHorarios();
        logger.log("Datos de horarios obtenidos para CCT " + cctSeleccionado);

        // Filtrar checadas para el rango de fechas y el CCT seleccionado
        checadasPorId = checadasList.stream()
                .filter(checada -> {
                    LocalDate fechaChecada = LocalDate.parse(checada.getFecha(), formatter);
                    return (fechaChecada.isEqual(fechaInicio) || fechaChecada.isAfter(fechaInicio)) &&
                            (fechaChecada.isEqual(fechaFin) || fechaChecada.isBefore(fechaFin)) &&
                            mapaEmpleados.containsKey(checada.getId());
                })
                .collect(Collectors.groupingBy(Checadas::getId));
        logger.log("Checadas filtradas para CCT " + cctSeleccionado + ": " + checadasPorId.size()
                + " empleados con registros");

        // Generar checadas vacías para empleados sin registros
        for (Empleado empleado : empleadosCCT) {
            String id = empleado.getId();
            if (!checadasPorId.containsKey(id)) {
                List<Checadas> checadasGeneradas = new ArrayList<>();
                for (String fecha : dias) {
                    Checadas checada = new Checadas();
                    checada.setId(id);
                    checada.setNombre(empleado.getNombre());
                    checada.setEmpleadoPuesto(empleado.getEmpleadoPuesto());
                    checada.setJornada(empleado.getJornada());
                    checada.setFecha(fecha);
                    checada.setHoraEntrada("00:00");
                    checada.setHoraSalida("00:00");
                    checadasGeneradas.add(checada);
                }
                checadasPorId.put(id, checadasGeneradas);
                logger.log("Generadas checadas vacías para empleado ID: " + id);
            }
        }
        logger.log("Total de empleados procesados (con y sin checadas): " + checadasPorId.size());

        // Crear índice de horarios por empleado y día
        Map<String, Map<String, List<EmpleadoDatosExtra>>> empleadoIndex = new HashMap<>();
        for (EmpleadoDatosExtra empleado : empleadosDatos) {
            String diaEmpleado = empleado.getDiaN().toLowerCase();
            empleadoIndex
                    .computeIfAbsent(empleado.getId(), k -> new HashMap<>())
                    .computeIfAbsent(diaEmpleado, k -> new ArrayList<>())
                    .add(empleado);
        }
        logger.log("Índice de horarios creado");

        JFileChooser fileChooser = new JFileChooser(ultimaRuta);
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        fileChooser.setSelectedFile(
                new File(periodoReporte.replace(" ", "") + "_" + cctSeleccionado.replace(" ", "") + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ultimaRuta = file.getParent();
            String filePath = file.getAbsolutePath();

            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            logger.log("Archivo seleccionado para guardar: " + filePath);

            try {
                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.LETTER);
                Document document = new Document(pdfDoc);
                logger.log("Documento PDF creado");

                final String periodoFinal = periodoReporte;
                if (incluirEncabezado || incluirNumeroPagina) {
                    pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
                        @Override
                        public void handleEvent(com.itextpdf.kernel.events.Event event) {
                            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                            PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
                            Rectangle pageSize = docEvent.getPage().getPageSize();
                            if (incluirNumeroPagina) {
                                agregarNumeroPagina(pdfDoc, plantelSeleccionado);
                                logger.log("Número de página agregado");
                            }
                            try {
                                if (incluirEncabezado) {
                                    dibujarEncabezado(canvas, pageSize, periodoFinal);
                                    logger.log("Encabezado agregado");
                                }
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                                logger.log("Error al dibujar el encabezado: " + e.getMessage());
                            }
                            canvas.release();
                        }
                    });
                }

                List<String> idsOrdenados = checadasPorId.keySet().stream()
                        .sorted((id1, id2) -> {
                            String nombre1 = checadasPorId.get(id1).get(0).getNombre();
                            String nombre2 = checadasPorId.get(id2).get(0).getNombre();
                            return nombre1.compareToIgnoreCase(nombre2);
                        })
                        .collect(Collectors.toList());

                logger.log("Checadas ordenadas alfabéticamente por nombre de empleado");

                boolean primeraVezEnPagina = true;

                // Iterar sobre los IDs ordenados
                for (String id : idsOrdenados) {
                    logger.log("Procesando empleado ID: " + id);
                    String nombre = checadasPorId.get(id).get(0).getNombre();
                    String categoria = checadasPorId.get(id).get(0).getEmpleadoPuesto();
                    Paragraph title = new Paragraph(id + "\t" + nombre + "\t" + categoria)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setMultipliedLeading(0.6f)
                            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE));
                    logger.log("Empleado: " + nombre + " " + categoria);
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

                    // Agrupar checadas por fecha
                    Map<String, List<Checadas>> checadasPorFecha = checadasPorId.get(id).stream()
                            .collect(Collectors.groupingBy(Checadas::getFecha));
                    logger.log("Lista de checadas agrupada por fecha");
                    // Iterar sobre todas las fechas en el rango
                    for (String fecha : dias) {
                        logger.log("Procesando fecha: " + fecha + " para el empleado ID: " + id);
                        List<Checadas> checadasDelDia = checadasPorFecha.getOrDefault(fecha, new ArrayList<>());
                        String diaSemana = calcularDiaSemana(fecha).toLowerCase();

                        // Verificar si el empleado tenía un horario para este día
                        boolean tieneHorario = empleadoIndex.containsKey(id) &&
                                empleadoIndex.get(id) != null &&
                                empleadoIndex.get(id).containsKey(diaSemana);

                        // Si no hay checadas ni horarios, continuar con la siguiente fecha
                        if (!tieneHorario && checadasDelDia.isEmpty()) {
                            logger.log("No hay checadas ni horarios para la fecha: " + fecha + " para el empleado ID: "
                                    + id);
                            continue;
                        }

                        // Obtener todos los horarios disponibles para este día
                        List<EmpleadoDatosExtra> horariosDisponibles = tieneHorario
                                ? new ArrayList<>(empleadoIndex.get(id).get(diaSemana))
                                : new ArrayList<>();

                        // Si no hay checadas, mostrar todos los horarios
                        if (checadasDelDia.isEmpty()) {
                            logger.log("No hay checadas registradas para la fecha: " + fecha + " para el empleado ID: "
                                    + id);
                            for (EmpleadoDatosExtra horario : horariosDisponibles) {

                                String horaEntradaReal = horario.getHoraEntradaReal();
                                String horaSalidaReal = horario.getHoraSalidaReal();

                                Duration duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
                                totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);
                                logger.log("Falta registrada para el empleado ID: " + id + " en la fecha: " + fecha);

                                // Mostrar el horario en la tabla
                                table.addCell(new Cell().add(new Paragraph(fecha))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(diaSemana))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(horaEntradaReal + " - 00:00"))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph("Falta"))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(horaSalidaReal + " - 00:00"))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph("Falta"))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph("00:00"))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                tamañoTabla++;
                            }
                        } else {
                            // Si hay checadas, procesarlas junto con los horarios
                            for (Checadas checada : checadasDelDia) {
                                String horaEntradaReal = "00:00";
                                String horaSalidaReal = "00:00";
                                Duration duracionACubrir = Duration.ZERO;
                                logger.log("Procesando checada para empleado: " + checada.toString());

                                if (tieneHorario && !horariosDisponibles.isEmpty()) {
                                    EmpleadoDatosExtra empleadoData = horariosDisponibles.remove(0);
                                    horaEntradaReal = empleadoData.getHoraEntradaReal();
                                    horaSalidaReal = empleadoData.getHoraSalidaReal();
                                    duracionACubrir = obtenerDuracion(horaEntradaReal, horaSalidaReal);
                                    totalHorasACubrir = totalHorasACubrir.plus(duracionACubrir);
                                    logger.log("Horario asignado. Hora entrada real: " + horaEntradaReal
                                            + ", Hora salida real: " + horaSalidaReal);
                                } else {
                                    logger.log("No se encontró horario disponible para el empleado.");
                                }

                                String horaEntrada = (checada.getHoraEntrada() != null
                                        && !checada.getHoraEntrada().isEmpty()) ? checada.getHoraEntrada() : "00:00";
                                String horaSalida = (checada.getHoraSalida() != null
                                        && !checada.getHoraSalida().isEmpty()) ? checada.getHoraSalida() : "00:00";

                                logger.log("Hora entrada registrada (antes de ajustes): " + horaEntrada);
                                logger.log("Hora salida registrada (antes de ajustes): " + horaSalida);

                                // Validación e intercambio de horas según el horario programado
                                if (horaEntradaReal.equals("00:00") && !horaSalidaReal.equals("00:00")
                                        && !horaEntrada.equals("00:00")) {
                                    logger.log(
                                            "Caso 1: Hora entrada programada es 00:00, pero hora salida no. Asignando checada a la salida.");
                                    // La checada se considera como salida
                                    horaSalida = horaEntrada; // Asignar la hora de la checada a la salida
                                    horaEntrada = "00:00"; // La entrada se mantiene en 00:00
                                } else if (horaSalidaReal.equals("00:00") && !horaEntradaReal.equals("00:00")
                                        && !horaSalida.equals("00:00")) {
                                    logger.log(
                                            "Caso 2: Hora salida programada es 00:00, pero hora entrada no. Asignando checada a la entrada.");
                                    // La checada se considera como entrada
                                    horaEntrada = horaSalida; // Asignar la hora de la checada a la entrada
                                    horaSalida = "00:00"; // La salida se mantiene en 00:00
                                } else {
                                    logger.log("Caso 3: No se requiere intercambio. Horas programadas: Entrada = "
                                            + horaEntradaReal + ", Salida = " + horaSalidaReal);
                                }

                                logger.log("Hora entrada registrada (después de ajustes): " + horaEntrada);
                                logger.log("Hora salida registrada (después de ajustes): " + horaSalida);

                                // Asignar el estatus de entrada y salida
                                String estatusEntrada = tieneHorario ? estatusChequeo(horaEntrada, horaEntradaReal)
                                        : "---";
                                String estatusSalida = tieneHorario ? estatusSalida(horaSalida, horaSalidaReal) : "---";

                                logger.log("Estatus de entrada: " + estatusEntrada);
                                logger.log("Estatus de salida: " + estatusSalida);

                                String tiempoTrabajo = calcularTiempoTrabajo(horaEntrada, horaSalida);
                                Duration duracionTrabajada = obtenerDuracion(horaEntrada, horaSalida);
                                logger.log("Tiempo trabajado: " + tiempoTrabajo);

                                if (!horaEntrada.equals("00:00") && !horaSalida.equals("00:00")) {
                                    totalHorasTrabajadas = totalHorasTrabajadas.plus(duracionTrabajada);
                                    logger.log("Horas trabajadas sumadas. Total horas trabajadas: "
                                            + totalHorasTrabajadas.toHours() + " horas.");
                                }

                                // Contar retardos y faltas solo si el empleado tenía un horario para ese día
                                if (tieneHorario) {
                                    if ("Medio retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
                                        retardos++;
                                        logger.log("Medio retardo registrado en la entrada.");
                                    }
                                    if ("Medio retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
                                        retardos++;
                                        logger.log("Medio retardo registrado en la salida.");
                                    }

                                    if ("Retardo".equals(estatusChequeo(horaEntrada, horaEntradaReal))) {
                                        retardos++;
                                        logger.log("Retardo registrado en la entrada.");
                                    }
                                    if ("Retardo".equals(estatusChequeo(horaSalida, horaSalidaReal))) {
                                        retardos++;
                                        logger.log("Retardo registrado en la salida.");
                                    }
                                    if (mediosRetardos >= 10) {
                                        faltas++;
                                        logger.log("Falta registrada debido a 10 medios retardos.");
                                    }

                                    // Contar falta solo si el empleado tenía un horario para ese día
                                    if ((horaEntrada.equals("00:00") && horaSalida.equals("00:00")) || (retardos >= 5)
                                            ||
                                            ("Falta".equals(estatusChequeo(horaSalida, horaSalidaReal))
                                                    && "Falta".equals(estatusChequeo(horaEntrada, horaEntradaReal)))) {
                                        faltas++;
                                        logger.log("Falta registrada debido a entradas o salidas faltantes.");
                                    } else {
                                        if (horaEntrada.equals("00:00")) {
                                            entradaFaltante++;
                                            logger.log("Entrada faltante registrada.");
                                        }
                                        if (horaSalida.equals("00:00")) {
                                            salidaFaltante++;
                                            logger.log("Salida faltante registrada.");
                                        }
                                    }
                                }

                                // Agregar la fila a la tabla
                                table.addCell(new Cell().add(new Paragraph(checada.getFecha()))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(diaSemana))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(horaEntradaReal + " - " + horaEntrada))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(estatusEntrada))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(horaSalidaReal + " - " + horaSalida))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(estatusSalida))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                table.addCell(new Cell().add(new Paragraph(tiempoTrabajo))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontSize(7).setBorder(Border.NO_BORDER)
                                        .setPadding(2).setMargin(0));

                                tamañoTabla++;
                            }
                        }
                    }

                    long totalHoras = totalHorasACubrir.toHours();
                    long totalMinutos = totalHorasACubrir.toMinutes() % 60;
                    long horasTrabajadas = totalHorasTrabajadas.toHours();
                    long minutosTrabajados = totalHorasTrabajadas.toMinutes() % 60;

                    // Crear la tabla con 5 columnas
                    Table info = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 1, 1, 1 }))
                            .useAllAvailableWidth()
                            .setBorder(Border.NO_BORDER);

                    // Agregar cada dato en su celda sin bordes y en negrita
                    info.addCell(new Cell().add(new Paragraph("Total horas: " + totalHoras + ":" +
                            (totalMinutos < 10 ? "0" + totalMinutos : totalMinutos)).setFont(boldFont))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Horas trabajadas: " + horasTrabajadas + ":" +
                            (minutosTrabajados < 10 ? "0" + minutosTrabajados : minutosTrabajados)).setFont(boldFont))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Faltas días: " + faltas).setFont(boldFont))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Entrada faltante: " + entradaFaltante).setFont(boldFont))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    info.addCell(new Cell().add(new Paragraph("Salida faltante: " + salidaFaltante).setFont(boldFont))
                            .setFontSize(7).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

                    // Agregar fila vacía con borde inferior delgado
                    info.addCell(new Cell(1, 5) // 1 fila, 5 columnas unidas
                            .setBackgroundColor(new DeviceRgb(0, 0, 0)) // Fondo negro
                            .setBorderBottom(new SolidBorder(0.05f)));

                    if (primeraVezEnPagina && incluirEncabezado) {
                        document.add(new Paragraph(" ").setMarginTop(35));
                        primeraVezEnPagina = false;
                    }

                    // Calcular espacio disponible en la página actual
                    float remainingHeight = calcularEspacioDisponible(document) - 20;
                    logger.log("Espacio disponible: " + remainingHeight);
                    float estimatedContentHeight = 120 + (tamañoTabla * 11);
                    logger.log("Espacio estimado para id:" + id + " espacio estimado: " + estimatedContentHeight);
                    float alturaFila = 10; // Estimación de la altura de cada fila
                    int maxFilas = calcularMaxFilas(document);
                    if (maxFilas > 39) {
                        maxFilas = 40;
                    }
                    if (incluirEncabezado) {
                        logger.log("Incluir encabezado: " + incluirEncabezado);
                        if (remainingHeight < 35) {
                            logger.log("Espacio menor a 35, nueva hoja y espacio para encabezado");
                            document.add(new AreaBreak());
                            document.add(new Paragraph(" ").setMarginTop(35));
                            remainingHeight = calcularEspacioDisponible(document) - 20;
                        }
                        if (remainingHeight < estimatedContentHeight || tamañoTabla > 40) {
                            logger.log("Espacio disponible es menor a espacio estimado");
                            document.add(title);

                            // Dividir la tabla usando maxFilas calculado
                            List<Table> tablasDivididas = dividirTabla(table, maxFilas);
                            logger.log("Tablas divididas del empleado: " + id);
                            int i = 0;
                            for (Table tablaParcial : tablasDivididas) {
                                if (i == 1) {
                                    logger.log("Nueva hoja para segunda tabla");
                                    document.add(new AreaBreak());
                                    document.add(new Paragraph(" ").setMarginTop(35));
                                }
                                if (tablaParcial.getNumberOfRows() > 0) {
                                    logger.log("Tabla parcial agregada");
                                    document.add(tablaParcial);
                                }
                                i++;
                            }
                            logger.log("Informacion del empleado agregada el reporte");
                            document.add(info);
                            document.add(new Paragraph(" ").setMarginTop(7));
                        } else {
                            logger.log("contenido del empleado agregado title, table y info");
                            document.add(title);
                            document.add(table);
                            document.add(info);
                            document.add(new Paragraph(" ").setMarginTop(7));
                        }
                    } else {
                        logger.log("contenido del empleado agregado title, table y info");
                        document.add(title);
                        document.add(table);
                        document.add(info);
                        document.add(new Paragraph(" ").setMarginTop(7));
                    }
                }

                logger.log("Documento cerrado");
                document.close();

                System.out.println("PDF generado en: " + filePath);

                logger.log("PDF generado en: " + filePath);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(new File(filePath));

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo abrir el documento", "",
                                JOptionPane.ERROR_MESSAGE);

                        logger.log("No se pudo abrir el documento");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int calcularMaxFilas(Document document) {
        float espacioDisponible = calcularEspacioDisponible(document);
        float espacioFijo = 35;
        float espacioRestante = espacioDisponible - espacioFijo;
        float alturaFila = 8 + 5;
        return (int) (espacioRestante / alturaFila);
    }

    private void agregarNumeroPagina(PdfDocument pdfDoc, String plantel) {
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler() {
            @Override
            public void handleEvent(com.itextpdf.kernel.events.Event event) {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
                Rectangle pageSize = docEvent.getPage().getPageSize();

                PdfFont font = null;
                try {
                    font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                float fontSize = 8;
                float plantelWidth = font.getWidth(plantel, fontSize);

                float marginLeft = plantelWidth;
                float xPageNumber = pageSize.getRight() - 75 - marginLeft;
                float yPageNumber = pageSize.getBottom() + 30;

                String pageNumber = plantel + " - Página " + docEvent.getDocument().getPageNumber(docEvent.getPage());

                canvas.beginText();
                canvas.setFontAndSize(font, fontSize);
                canvas.moveText(xPageNumber, yPageNumber);
                canvas.showText(pageNumber);
                canvas.endText();

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
        float textWidthEncabezado = boldFont.getWidth(encabezado, 10);
        float x = (pageSize.getWidth() - textWidthEncabezado) / 2;
        canvas.setFillColor(new DeviceRgb(0, 0, 0));
        canvas.beginText();
        canvas.setFontAndSize(boldFont, 10);
        canvas.moveText(x, y - 35);
        canvas.showText(encabezado);
        canvas.endText();
    }

    private Table crearTabla() throws java.io.IOException {
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 1, 1, 1, 1, 1.2f }))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER);

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        DeviceRgb backgroundColor = new DeviceRgb(57, 166, 100);
        DeviceRgb textColor = new DeviceRgb(255, 255, 255);

        String[] headers = { "Fecha", "Día", "Hora Entrada", "Estatus", "Hora Salida", "Estatus", "Tiempo Trabajado" };

        for (String header : headers) {
            tabla.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont).setFontColor(textColor))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(8)
                    .setBackgroundColor(backgroundColor)
                    .setBorder(Border.NO_BORDER));
        }

        return tabla;
    }

    public static List<String> obtenerDiasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> dias = new ArrayList<>();
        LocalDate fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dias.add(fechaActual.format(formatter));
            fechaActual = fechaActual.plusDays(1);
        }

        return dias;
    }

    private List<Table> dividirTabla(Table tablaOriginal, int maxFilasPorTabla) throws Exception {
        int numColumnas = tablaOriginal.getNumberOfColumns();
        List<Table> tablasDivididas = new ArrayList<>();
        Table tablaMax = crearTabla();
        Table tablaResto = crearTabla();

        int filaActual = 0;
        boolean maxTablaLlena = false;

        for (IElement elemento : tablaOriginal.getChildren()) {
            if (elemento instanceof Cell) {
                Cell celda = (Cell) elemento;

                if (!maxTablaLlena) {
                    tablaMax.addCell(celda);
                    filaActual++;

                    if (filaActual >= maxFilasPorTabla * numColumnas) {
                        maxTablaLlena = true;
                    }
                } else {
                    tablaResto.addCell(celda);
                }
            }
        }

        tablasDivididas.add(tablaMax);

        if (tablaResto.getNumberOfRows() > 40) {
            List<Table> tablasExtra = dividirTabla(tablaResto, 40);
            tablasDivididas.addAll(tablasExtra);
        } else if (!tablaResto.isEmpty()) {
            tablasDivididas.add(tablaResto);
        }

        return tablasDivididas;
    }

    private float calcularEspacioDisponible(Document document) {
        LayoutArea currentArea = document.getRenderer().getCurrentArea();
        return currentArea.getBBox().getHeight() - 50;
    }

    public String estatusSalida(String horaSalida, String horaReal) {
        if (horaReal == null || horaReal.equals("00:00") || horaSalida == null) {
            return "";
        }
        if (horaSalida.equals("00:00") || horaSalida.isEmpty()) {
            return "Falta";
        }

        String[] partesSalida = horaSalida.split(":");
        String[] partesReal = horaReal.split(":");

        int horasSalida = Integer.parseInt(partesSalida[0]);
        int minutosSalida = Integer.parseInt(partesSalida[1]);

        int horasReal = Integer.parseInt(partesReal[0]);
        int minutosReal = Integer.parseInt(partesReal[1]);

        int diferenciaEnMinutos = (horasSalida - horasReal) * 60 + (minutosSalida - minutosReal);

        if (diferenciaEnMinutos < 0) {
            return "Salida anticipada";
        } else if (diferenciaEnMinutos <= 30) {
            return "Tolerancia";
        } else {
            return "30 min después";
        }
    }

    public String estatusChequeo(String horaChequeo, String horaReal) {
        if (horaReal == null || horaReal.isEmpty() || horaReal.equals("00:00")) {
            return "";
        }

        if (horaChequeo == null || horaChequeo.isEmpty() || horaChequeo.equals("00:00")) {
            return "Falta";
        }

        if (!horaChequeo.matches("\\d{2}:\\d{2}") || !horaReal.matches("\\d{2}:\\d{2}")) {
            return "Formato de hora incorrecto";
        }

        try {
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
                return "Retardo completo";
            } else {
                return "Falta Entrada";
            }
        } catch (NumberFormatException e) {
            return "Formato de hora incorrecto";
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

            if (salida.isBefore(entrada)) {
                Duration duracionPrimeraParte = Duration.between(entrada, LocalTime.MAX).plus(Duration.ofSeconds(1));
                Duration duracionSegundaParte = Duration.between(LocalTime.MIN, salida);
                Duration duracionTotal = duracionPrimeraParte.plus(duracionSegundaParte);

                long horas = duracionTotal.toHours();
                long minutos = duracionTotal.toMinutes() % 60;
                return horas + ":" + (minutos < 10 ? "0" + minutos : minutos);
            } else {
                Duration duracion = Duration.between(entrada, salida);

                if (duracion.isNegative()) {
                    return "Hora de salida antes de la entrada";
                }

                long horas = duracion.toHours();
                long minutos = duracion.toMinutes() % 60;
                return horas + ":" + (minutos < 10 ? "0" + minutos : minutos);
            }

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