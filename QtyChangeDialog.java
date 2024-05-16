import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QtyChangeDialog extends JDialog implements ActionListener {

    JTable ProductsShowChange;
    JScrollPane scrollPaneChange;
    JTextField input;
    JButton confirm;
    Object order;
    JTable tabel;
    int inputNumber;
    Boolean doneCheck = false;
    public QtyChangeDialog(JFrame frame, Boolean modal, Object order, JTable tabel){
        super(frame, modal);
        setTitle("Aanpassen hoeveelheid");
        setLayout(new GridLayout(1, 1));
        this.order = order;
        this.tabel = tabel;
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
                int row = tabel.getSelectedRow();
                Database.executeChangeQuery("UPDATE orderlines SET Quantity = '" + inputNumber + "' WHERE OrderLineID = '" + tabel.getValueAt(row, 0) + "'");
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
