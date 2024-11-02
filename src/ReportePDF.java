import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ReportePDF {

    public void generateReport(List<Checadas> checadasList) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter pdfs = new FileNameExtensionFilter("Documentos PDF", "pdf");
        chooser.addChoosableFileFilter(pdfs);
        chooser.setFileFilter(pdfs);

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".pdf")) {
                file = new File(file + ".pdf");
            }

            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));
                 Document doc = new Document(pdfDoc, PageSize.A4)) {

                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                for (Checadas checada : checadasList) {
                    Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 2, 2, 2, 2})).useAllAvailableWidth();
                    table.addHeaderCell(new Cell(1, 8).add(new Paragraph("Reporte Checador")
                            .setFont(font)
                            .setFontSize(18)
                            .setFontColor(DeviceGray.BLACK)
                            .setBackgroundColor(new DeviceRgb(180, 180, 255))
                            .setTextAlignment(TextAlignment.CENTER)));

                    table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Nombre").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Categoría").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada 1").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida 1").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada 2").setFont(font).setBold()));
                    table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida 2").setFont(font).setBold()));

                    int daysCount = 0;
                    int totalFaltas = 0;

                    for (int i = 0; i < 10; i++) {  // Limit to 10 days
                        // Suponiendo que en `Checadas` tienes métodos como getFecha(i), getHoraEntrada1(i), etc.
                        String fecha = checada.getFecha(); // Método que obtiene la fecha del día i
                        String horaEntrada1 = checada.getHoraEntrada();
                        String horaSalida1 = checada.getHoraSalida();
                        String horaEntrada2 = checada.getHoraEntrada2();
                        String horaSalida2 = checada.getHoraSalida2();

                        if (fecha == null || fecha.isEmpty()) break; // Si no hay más registros, terminar el bucle

                        // Calcula el tiempo de trabajo (se debe implementar `calcularTiempoTrabajo`)
                        String tiempoTrabajo = calcularTiempoTrabajo(horaEntrada1, horaSalida1);

                        table.addCell(new Cell().add(new Paragraph(String.valueOf(checada.getId())).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(checada.getNombre()).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(checada.getCategoria()).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(fecha).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(horaEntrada1).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(horaSalida1).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(horaEntrada2).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(horaSalida2).setFont(font)));

                        // Contabilizar faltas
                        if (horaEntrada1 == null || horaEntrada1.isEmpty()) {
                            totalFaltas++;
                        }

                        daysCount++;
                    }

                    // Fila final para el total de faltas
                    table.addCell(new Cell(1, 8).add(new Paragraph("Total de Faltas: " + totalFaltas))
                            .setFont(font)
                            .setFontSize(12)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setBackgroundColor(DeviceGray.BLACK));

                    doc.add(table);
                    doc.add(new Paragraph("\n")); // Espacio entre tablas de empleados
                }

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String calcularTiempoTrabajo(String horaEntrada, String horaSalida) {
        // Implementa el cálculo de tiempo de trabajo aquí basado en las horas de entrada y salida
        return "Tiempo calculado"; // Ejemplo
    }
}
