import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class QtyChangeStockOrderlineDialog extends JDialog implements ActionListener {

    private JTextField input;
    private final JButton confirm;
    private Boolean doneCheck = false;
    private int rowDataOrderLineID;
    private int rowDataStockItemID;
    public QtyChangeStockOrderlineDialog(JFrame frame, Boolean modal, Object rowDataOrderLineID, Object rowDataStockItemID){
        super(frame, modal);
        setTitle("Aanpassen hoeveelheid");
        setLayout(new GridLayout(1, 1));
        this.rowDataOrderLineID = (int)rowDataOrderLineID;
        this.rowDataStockItemID = (int)rowDataStockItemID;
        input = new JTextField("");
        confirm = new JButton("Confirm");
        confirm.addActionListener(this);
        add(input);
        add(confirm);
        setSize(300,100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == confirm){
            try{
                int inputNumber = Integer.parseInt(input.getText());
                if(Database.enoughStockCheck(inputNumber, rowDataStockItemID)){
                    Database.executeChangeQuery("UPDATE orderlines SET Quantity = '" + inputNumber + "' WHERE OrderLineID = '" + rowDataOrderLineID + "'");
                    doneCheck = true;
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "De voorraad strekt niet");
                }
            }
            catch(NumberFormatException n){
                JOptionPane.showMessageDialog(null, "Voer een geldig getal in");
            }
        }
    }

    public Boolean getDoneCheck() {
        return doneCheck;
    }
}
