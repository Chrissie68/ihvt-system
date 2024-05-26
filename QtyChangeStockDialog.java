import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QtyChangeStockDialog extends JDialog implements ActionListener {

    JTextField input;
    JButton confirm;
    //    Object order;
    JTable tabel;
    int inputNumber;
    Boolean doneCheck = false;
    Object rowData;
    public QtyChangeStockDialog(JFrame frame, Boolean modal, Object rowData){
        super(frame, modal);
        setTitle("Aanpassen hoeveelheid");
        setLayout(new GridLayout(1, 1));
//        this.order = order;
        this.rowData = rowData;
        input = new JTextField("");
        confirm = new JButton("Confirm");
        confirm.addActionListener(this);



        add(input);
        add(confirm);
        setSize(300,100);
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == confirm){
            try{
                int inputNumber = Integer.parseInt(input.getText());
//                int row = tabel.getSelectedRow();
                Database.executeChangeQuery("UPDATE stockitemholdings SET QuantityOnHand = '" + inputNumber + "' WHERE StockItemID = '" + rowData + "'");
                System.out.println("Het is gelukt");
                doneCheck = true;
                this.inputNumber = inputNumber;
                dispose();
            }
            catch(Exception a){
                a.printStackTrace();
            }
        }
    }
}
