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

    JButton ControlPanelButton, StockCheckButton, AddOrderButton, infoButton;
    JTable orderShow;
    SerialPort Arduino;
    Object rowData;
    JPanel buttonContainer;
    JFrame thisFrame;


    public GOOEY() {
        //Basic stuff
        this.setTitle("Warehouserobot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        thisFrame = this;
        StockCheckButton = new JButton("Bekijk voorraad");
        StockCheckButton.addActionListener(this);
        AddOrderButton = new JButton("Voeg order toe");
        AddOrderButton.addActionListener(this);
        infoButton = new JButton("?");
        infoButton.addActionListener(this);
        buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout());
        buttonContainer.add(StockCheckButton);
        buttonContainer.add(AddOrderButton);
        buttonContainer.add(infoButton);
        add(buttonContainer, BorderLayout.NORTH);

        JPanel rasterPanel = new JPanel(new GridLayout(5, 5));

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
        add(rasterPanel, BorderLayout.CENTER);


        ControlPanelButton = new JButton("Handmatig bedienen");
        ControlPanelButton.addActionListener(this);
        add(ControlPanelButton, BorderLayout.SOUTH);

        try {
            DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC Limit 5;");
            orderShow = new JTable(model);
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
//            ControlPanel controlPanel = new ControlPanel(this, true/*, Arduino*/);
//            Arduino.closePort();

        orderShow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int row = orderShow.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    rowData = orderShow.getValueAt(row, 0);
                    System.out.println("Double clicked on: "+ rowData);
                    OrderDialog orderDialog = new OrderDialog(thisFrame, true, rowData);
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
        if(e.getSource() == StockCheckButton){
            Stockcheck stockcheck = new Stockcheck(this, true);
        }
    }
}


