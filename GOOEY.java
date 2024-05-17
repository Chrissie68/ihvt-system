import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.fazecast.jSerialComm.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GOOEY extends JFrame implements ActionListener {

    JButton ControlPanelButton;
    JButton OrderDialogButton;
    JTable orderShow;
    SerialPort Arduino;
    Object rowData;


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

        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 40");
            orderShow = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(orderShow);
            add(scrollPane, BorderLayout.EAST);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Double click added so information can be extracted from JTable
        orderShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JTable orderShow = (JTable)mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = orderShow.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    rowData = orderShow.getValueAt(row, 0);
                    System.out.println("Double clicked on: "+ rowData);
                }
            }
        });

        orderShow.setDefaultEditor(Object.class, null);
        this.setMinimumSize(new Dimension(800, 600));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
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

            Arduino = SerialPort.getCommPort("COM8");
            Arduino.setComPortParameters(9600, 8, 1, 0);

            if (Arduino.openPort()) {
                System.out.println("poort open");
            } else {
                System.out.println("kan poort niet openen");
                return;
            }

            try { Thread.sleep(2000); } catch (Exception edrie) { edrie.printStackTrace(); }
            ControlPanel controlPanel = new ControlPanel(this, true, Arduino);
            Arduino.closePort();
        }
        if(e.getSource() == OrderDialogButton){
            OrderDialog orderDialog = new OrderDialog(this, true, rowData);

        }
    }
}


