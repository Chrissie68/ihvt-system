import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class GOOEY extends JFrame implements ActionListener {

    JButton ControlPanelButton;
    static JLabel resultLabel;
    JButton OrderDialogButton;
    JTable orderShow;

    public GOOEY() {
        //Basic stuff
        this.setTitle("Warehouserobot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        //Define layout of storage
        JPanel rasterPanel = new JPanel(new GridLayout(5, 5));

        //Filling the layout with boxes
        char rowLabel = 'A';
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JLabel label = new JLabel(String.format("%c%d", rowLabel, j + 1), SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 100));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                rasterPanel.add(label);
            }
            rowLabel++;
        }
        //Adding the storage layout to the GUI
        this.add(rasterPanel, BorderLayout.CENTER);

        //Adding button for JDialog to add/remove/alter orders
        OrderDialogButton = new JButton("Order aanpassen");
        OrderDialogButton.addActionListener(this);
        OrderDialogButton.setOpaque(true);
        add(OrderDialogButton, BorderLayout.NORTH);

        //Adding button for manual operation
        ControlPanelButton = new JButton("Handmatig bedienen");
        ControlPanelButton.addActionListener(this);
        add(ControlPanelButton, BorderLayout.SOUTH);

        //Adding last 5 orders
//        resultLabel = new JLabel("Laatste 5 orders", SwingConstants.CENTER);
//        add(resultLabel, BorderLayout.EAST);
        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 40");

            //Test
//            DefaultTableModel model = Database.executeQuery( "SELECT OrderLineID, StockItemID, Quantity FROM orderlines WHERE OrderId = '" + 73585 + "'");

            // Create JTable with the model
            orderShow = new JTable(model);

            // Add the table to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(orderShow);

            // Add JScrollPane to the frame
            add(scrollPane, BorderLayout.EAST);
        } catch (SQLException e) {
            e.printStackTrace();
        }



        this.setMinimumSize(new Dimension(800, 600));
//        Database.loadResults();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ControlPanelButton) {
            ControlPanel controlPanel = new ControlPanel(this, true);
        }
        if (e.getSource() == OrderDialogButton){
            try{
                int row = orderShow.getSelectedRow();
                System.out.println(orderShow.getValueAt(row, 0));
                OrderDialog orderDialog = new OrderDialog(this, true, orderShow.getValueAt(row, 0));
            }
            catch(ArrayIndexOutOfBoundsException exception){
                System.out.println("Het probleem is opgelost");
            }
        }
    }
}

