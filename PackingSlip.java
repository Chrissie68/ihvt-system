import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
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
    private String NAWQuery = "SELECT c.customername, c.DeliveryAddressLine2, c.PostalAddressLine1, c.PostalAddressLine2 FROM orders o  JOIN customers c USING (customerid) WHERE orderid = '";

    public PackingSlip(Object orderId, int doosNummer, List<Object[]> box) {
        // bestandlocatie + naam
        this.filePath = "Pakbonnen/order" + orderId + "-Doos" + doosNummer + ".pdf";

        try {
            // aanmaak pakbonnen map, mocht deze niet bestaan
            File directory = new File("Pakbonnen");
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Pdf wordt aangemaakt op locatie
            Document packingSlip = new Document();
            PdfWriter.getInstance(packingSlip, new FileOutputStream(filePath));
            packingSlip.open();

            // lettertypes
            var titelFont = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
            var textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            var tabelFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            // titel
            Paragraph title = new Paragraph("Order " + orderId + " - Doos " + doosNummer, titelFont);
            packingSlip.add(title);
            packingSlip.add(new Paragraph("\n"));

            // NAW gegevens
            List<Object[]> customerInfo = NAWInfo(orderId);
            for (Object[] customerData : customerInfo) {
                for (Object data : customerData) {
                    Paragraph customerInfoParagraph = new Paragraph(data.toString(), textFont);
                    packingSlip.add(customerInfoParagraph);
                }
            }

            //datum
            Paragraph datum = new Paragraph("Datum: " + currentDate.toString(), textFont);
            packingSlip.add(datum);
            packingSlip.add(new Paragraph("\n"));

            // aanmaak tabel
            var table = new PdfPTable(4);
            Stream.of("Omschrijving", "ItemID:", "Grootte:", "Hoeveelheid:").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setPhrase(new Paragraph(columnTitle, tabelFont));
                table.addCell(header);
            });

            //tabel invulling
            packingSlip.add(new Paragraph("Artikelen:"));
            for (Object[] item : box) {
                for (int i = 1; i < item.length; i++) {
                    tabel.add(new String[]{item[i].toString()});
                }
            }
            for (String[] row : tabel) {
                for (String cell : row) {
                    PdfPCell tableCell = new PdfPCell(new Paragraph(cell, textFont));
                    table.addCell(tableCell);
                }
            }

            //toevoegen en afsluiten
            packingSlip.add(table);
            packingSlip.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    // ophalen NAW gegevens
    private List<Object[]> NAWInfo(Object orderID) {
        List<Object[]> NAWGegevens = new ArrayList<>();
        try {
            DefaultTableModel model = Database.executeSelectQuery(NAWQuery + orderID + "'");
            Object[] customerData = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                customerData[j] = model.getValueAt(0, j);
            }
            NAWGegevens.add(customerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NAWGegevens;
    }
}
