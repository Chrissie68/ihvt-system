import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// BELANGRIJK!!!!!!!!!      Zet in: "file" -> "project structure" -> "libraries" -> "+" -> "from maven..." ->   com.itextpdf.maven:itextdoc:2.0.0   -> "ok"
public class PackingSlip {

    private ArrayList<String[]> tabel = new ArrayList<>();
    private String filePath;
    private LocalDate currentDate = LocalDate.now();

    public PackingSlip(int doosNummer, List<Object[]> box) {
        this.filePath = "Pakbonnen/order" + "-Onbekend+Doos-" + doosNummer +".pdf";
        createPdf(box, doosNummer);
    }

    private void createPdf(List<Object[]> box, int doosnummer) {
        try {
            // aanmaak pakbonnen map, mocht deze niet bestaan
            File directory = new File("Pakbonnen");
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Pdf wordt aangemaakt op locatie
            Document pakbon = new Document();
            PdfWriter.getInstance(pakbon, new FileOutputStream(filePath));
            pakbon.open();

            // lettertypes
            var titelFont = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
            var textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            var tabelFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            // titel
            Paragraph title = new Paragraph("Order test - Doos " + doosnummer, titelFont);

            // aanmaak tabel
            var table = new PdfPTable(4);
            Stream.of("Name", "ItemID:", "Size:", "Quantity:").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setPhrase(new Paragraph(columnTitle, tabelFont));
                table.addCell(header);
            });

            //tabel invulling
            for (Object[] item : box) {
                tabel.add(new String[]{ item[1].toString()});
                tabel.add(new String[]{ item[2].toString()});
                tabel.add(new String[]{ item[3].toString()});
                tabel.add(new String[]{ item[4].toString()});
            }
            for (String[] row : tabel) {
                for (String cell : row) {
                    PdfPCell tableCell = new PdfPCell(new Paragraph(cell, textFont));
                    table.addCell(tableCell);
                }
            }

            //datum
            Paragraph date = new Paragraph("Date: " + currentDate.toString(), textFont);

            //toevoegen en afsluiten
            pakbon.add(title);
            pakbon.add(new Paragraph("\n"));
            pakbon.add(table);
            pakbon.add(date);
            pakbon.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}