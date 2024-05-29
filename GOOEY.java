import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.fazecast.jSerialComm.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;

public class GOOEY extends JFrame implements ActionListener {
    private JButton ControlPanelButton, StockCheckButton, addOrderButton;
    private JLabel databaseNotWorking;
    private JTable orderShow;
    SerialPort Arduino;
    private Object orderID;
    private JFrame thisFrame;


    public GOOEY() {
        if(Database.databaseCheck()){
            //Basic stuff
            this.setTitle("Warehouserobot");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setLayout(new BorderLayout());
            thisFrame = this;
            StockCheckButton = new JButton("Bekijk voorraad");
            StockCheckButton.addActionListener(this);
            addOrderButton = new JButton("Voeg order toe");
            addOrderButton.addActionListener(this);
            JButton infoButton = new JButton("?");
            infoButton.addActionListener(this);
            JPanel buttonContainer = new JPanel();
            buttonContainer.setLayout(new FlowLayout());
            buttonContainer.add(StockCheckButton);
            buttonContainer.add(addOrderButton);
            buttonContainer.add(infoButton);
            add(buttonContainer, BorderLayout.NORTH);

            //TEST VOOR TSP
            int[][] locaties = {{0, 0}, {2, 5}, {3, 4}, {4, 2}, {2, 8} ,{0, 0}};
            TSPAlgorithm.addLocationsGetResults(locaties);


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
            add(rasterPanel, BorderLayout.CENTER);

            //Adding button for manual operation
            ControlPanelButton = new JButton("Handmatig bedienen");
            ControlPanelButton.addActionListener(this);
            add(ControlPanelButton, BorderLayout.SOUTH);

            try {
                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC");
                orderShow = new JTable(model);
                orderShow.getTableHeader().setReorderingAllowed(false);
                JScrollPane scrollPane = new JScrollPane(orderShow);
                add(scrollPane, BorderLayout.EAST);
            } catch (SQLException e) {
                e.printStackTrace();
            }



            //Code werkt niet.
//            Arduino = SerialPort.getCommPort("COM8");
//            Arduino.setComPortParameters(9600, 8, 1, 0);
//
//            if (Arduino.openPort()) {
//                System.out.println("poort open");
//            } else {
//                System.out.println("kan poort niet openen");
//                return;
//            }
//
//            try { Thread.sleep(2000); } catch (Exception edrie) { edrie.printStackTrace(); }
//            ControlPanelDialog controlPanel = new ControlPanelDialog(this, true/*, Arduino*/);
//            Arduino.closePort();

            //Double click added so information can be extracted from JTable
            orderShow.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    Point point = mouseEvent.getPoint();
                    int row = orderShow.rowAtPoint(point);
                    if (mouseEvent.getClickCount() == 2 && row != -1) {
                        orderID = orderShow.getValueAt(row, 0);
                        System.out.println("Double clicked on: "+ orderID);
                        OrderDialog orderDialog = new OrderDialog(thisFrame, true, orderID);
                    }
                }
            });

            orderShow.setDefaultEditor(Object.class, null);
            this.setMinimumSize(new Dimension(800, 600));
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
        else{
            this.setTitle("Warehouserobot");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setLayout(new GridLayout());
            databaseNotWorking = new JLabel("Database not working");
            add(databaseNotWorking);
            this.setMinimumSize(new Dimension(800, 600));
            this.setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ControlPanelButton) {
            ControlPanelDialog controlPanelDialog = new ControlPanelDialog(this, true);
        }
        if(e.getSource() == StockCheckButton){
            StockcheckDialog stockcheckDialog = new StockcheckDialog(this, true);
        }
        if(e.getSource() == addOrderButton){
            Database.addOrder();
            OrderDialog orderDialog = new OrderDialog(thisFrame, true, Objects.requireNonNull(Database.lastOrderID()));
        }
    }
}


