import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class AddProduct extends JDialog implements ActionListener {
    Object order;
    JLabel artikelnummer = new JLabel("Artikelnummer:");
    JTextField artklNrInvoer = new JTextField("");
    JLabel hoeveelheid = new JLabel("Hoeveelheid:");
    JTextField hvlheidInvoer = new JTextField("");
    Boolean checkDone = false;
    JButton confirm;
    public AddProduct(JFrame frame, Boolean modal, Object order){
        super(frame, modal);
        setTitle("Toevoegen product");
        setLayout(new GridLayout(1, 4));
        this.order = order;
        confirm = new JButton("Confirm");
        confirm.addActionListener(this);


        add(artikelnummer);
        add(artklNrInvoer);
        add(hoeveelheid);
        add(hvlheidInvoer);
        add(confirm);
        setSize(800,100);
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            int quantity = Integer.parseInt(hvlheidInvoer.getText());
            System.out.println("Quantity is:" + quantity);;
            int stockItemId = Integer.parseInt(artklNrInvoer.getText());
            System.out.println("Artikelnr is: " + stockItemId);
            String query = "INSERT INTO orderlines(OrderID, StockItemID, Description, PackageTypeID, Quantity, TaxRate, PickedQuantity, LastEditedBy, LastEditedWhen) VALUES('" + order.toString() + "', '" + stockItemId + "', 'Dit is testcode', 4, '" + quantity + "', 15.0, 0, 3, '2024-05-01 12:00:00')";
            if(Database.stockItemIdValid(stockItemId)){
                if(Database.enoughStockCheck(quantity, stockItemId)){
                    Database.executeChangeQuery(query);
                    checkDone = true;
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "De voorraad strekt niet");
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Dit artikelnummer bestaat niet");
            }
        }
        catch(Exception a){
            a.printStackTrace();
        }
    }
}
