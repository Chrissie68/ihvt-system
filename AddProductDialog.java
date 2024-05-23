import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class AddProductDialog extends JDialog implements ActionListener {
    private Object order;
    private JTextField artklNrInvoer = new JTextField("");
    private JTextField hvlheidInvoer = new JTextField("");
    private Boolean checkDone = false;
    private Object stockName, packageID, taxRate;

    public AddProductDialog(JFrame frame, Boolean modal, Object order) {
        super(frame, modal);
        setTitle("Toevoegen product");
        setLayout(new GridLayout(1, 4));
        this.order = order;
        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(this);


        JLabel artikelnummer = new JLabel("Artikelnummer:");
        add(artikelnummer);
        add(artklNrInvoer);
        JLabel hoeveelheid = new JLabel("Hoeveelheid:");
        add(hoeveelheid);
        add(hvlheidInvoer);
        add(confirm);
        setSize(800, 100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            //Getting input from GUI
            int quantity = Integer.parseInt(hvlheidInvoer.getText());
            System.out.println("Quantity is:" + quantity);
            ;
            int stockItemId = Integer.parseInt(artklNrInvoer.getText());
            System.out.println("Artikelnr is: " + stockItemId);

            //Check if stockItemID is valid
            if (Database.stockItemIdValid(stockItemId)) {
                //Getting information from database to add to INSERT
                Object[] informationForInsert = Database.informationForInsertOrderLine(stockItemId);
                assert informationForInsert != null;
                stockName = informationForInsert[0];
                packageID = informationForInsert[1];
                taxRate = informationForInsert[2];

                //Getting the current time
                DateTimeFormatter dateWithHrs = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                //Building the INSERT query with the information gotten above
                String query = "INSERT INTO orderlines(OrderID, StockItemID, Description, PackageTypeID, Quantity, TaxRate, PickedQuantity, LastEditedBy, LastEditedWhen) VALUES('" + order.toString() + "', '" + stockItemId + "', '" + stockName + "' , '" + packageID + "', '" + quantity + "', '" + taxRate + "' , 0, 3, '" + dateWithHrs.format(now) + "')";
                //Check if there is enough stock
                if (Database.enoughStockCheck(quantity, stockItemId)) {
                    Database.executeChangeQuery(query);
                    checkDone = true;
                    dispose();
                }
                else {
                    //In case there is not enough stock
                    JOptionPane.showMessageDialog(null, "De voorraad strekt niet");
                }
            }
            else {
                //In case the stockItemID isnt valid
                JOptionPane.showMessageDialog(null, "Voer een geldig artikelnummer in");
            }
        }
        catch(NumberFormatException n){
                //In case the input isnt a number
                JOptionPane.showMessageDialog(null, "Voer een geldig getal in");
        }
    }

    public Boolean getCheckDone() {
        return checkDone;
    }
}
