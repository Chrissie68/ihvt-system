import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class GOOEY extends JFrame implements ActionListener {
    private int xCoordinaat = 0, yCoordinaat = 0;
    private JButton ControlPanelButton, StockCheckButton, addOrderButton, RemoveOrderButton;
    private JLabel databaseNotWorking;
    private JTable orderShow;
    private ArduinoConnectie Arduino;
    private Object orderID;
    private JFrame thisFrame;
    private JLabel redDotLabel;
    private JPanel rasterPanel;

    public GOOEY() {
        if (Database.databaseCheck()) {
            // Basic stuff
            try {
                Arduino = new ArduinoConnectie("com7", 9600, this);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.setTitle("Warehouserobot");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setLayout(new BorderLayout());
            thisFrame = this;
            StockCheckButton = new JButton("Bekijk voorraad");
            StockCheckButton.addActionListener(this);
            addOrderButton = new JButton("Voeg order toe");
            addOrderButton.addActionListener(this);
            RemoveOrderButton = new JButton("Verwijder Order");
            RemoveOrderButton.addActionListener(this);
            JButton infoButton = new JButton("?");
            infoButton.addActionListener(this);
            JPanel buttonContainer = new JPanel();
            buttonContainer.setLayout(new FlowLayout());
            buttonContainer.add(StockCheckButton);
            buttonContainer.add(addOrderButton);
            buttonContainer.add(RemoveOrderButton);
            buttonContainer.add(infoButton);
            add(buttonContainer, BorderLayout.NORTH);

            // Tests voor het toevoegen van productlocaties vanuit het BPP
            int[] producten = {2, 23, 4, 25, 3, 6};
            System.out.println(Arrays.deepToString(BPPtoTSPTransform.locationTransform(producten)));
            TSPAlgorithm.addLocationsGetResults(BPPtoTSPTransform.locationTransform(producten));

            // Define layout of storage
            rasterPanel = new JPanel(null); // Set layout to null for manual positioning
            addRasterLabels();

            // Adding the storage layout to the GUI
            add(rasterPanel, BorderLayout.CENTER);

            // Adding button for manual operation
            ControlPanelButton = new JButton("Handmatig bedienen");
            ControlPanelButton.addActionListener(this);
            add(ControlPanelButton, BorderLayout.SOUTH);

            try {
                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 5");
                orderShow = new JTable(model);
                orderShow.getTableHeader().setReorderingAllowed(false);
                JScrollPane scrollPane = new JScrollPane(orderShow);
                add(scrollPane, BorderLayout.EAST);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Double click added so information can be extracted from JTable
            orderShow.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    Point point = mouseEvent.getPoint();
                    int row = orderShow.rowAtPoint(point);
                    if (mouseEvent.getClickCount() == 2 && row != -1) {
                        orderID = orderShow.getValueAt(row, 0);
                        System.out.println("Double clicked on: " + orderID);
                        OrderDialog orderDialog = new OrderDialog(thisFrame, true, orderID);
                    }
                }
            });

            orderShow.setDefaultEditor(Object.class, null);
            this.setMinimumSize(new Dimension(1000, 600));
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);

            // Add red dot label at initial position (0, 0)
            addRedDotLabel(xCoordinaat, yCoordinaat);
        } else {
            this.setTitle("Warehouserobot");
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setLayout(new GridLayout());
            databaseNotWorking = new JLabel("Database not working");
            add(databaseNotWorking);
            this.setMinimumSize(new Dimension(800, 600));
            this.setVisible(true);
        }
    }

    private void addRasterLabels() {
        for (char rowLabel = 'A'; rowLabel <= 'E'; rowLabel++) {
            for (int j = 1; j <= 5; j++) {
                JLabel label = new JLabel(String.format("%c%d", rowLabel, j), SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 100));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                int x = (j - 1) * 100; // Calculate x-coordinate
                int y = (rowLabel - 'A') * 100; // Calculate y-coordinate
                label.setBounds(x, y, 100, 100); // Set position

                // Set cursor to hand pointer when hovering over the label
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Add mouse listener to each label for mouse click event only
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String labelText = label.getText();
                        CustomDialog customDialog = new CustomDialog(thisFrame, "" + labelText, labelText, Arduino);
                    }
                });

                rasterPanel.add(label);
            }
        }
    }

    public void addRedDotLabel(int x, int y) {
        if (redDotLabel == null) {
            // Create a red dot label if it doesn't exist
            redDotLabel = new JLabel("\u2022", SwingConstants.CENTER); // Red dot character
            redDotLabel.setForeground(Color.RED);
            redDotLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Adjust font size to make the dot bigger
            rasterPanel.add(redDotLabel);
        }
        // Set position of red dot label
        redDotLabel.setBounds(x, y, 75, 925); // Set position based on coordinates

        // Repaint the panel
        rasterPanel.revalidate();
        rasterPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ControlPanelButton) {
            ControlPanelDialog controlPanelDialog = new ControlPanelDialog(this, true, Arduino);
        }
        if (e.getSource() == StockCheckButton) {
            StockcheckDialog stockcheckDialog = new StockcheckDialog(this, true);
        }
        if (e.getSource() == addOrderButton) {
            Database.addOrder();
            try {
                DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 5");
                orderShow.setModel(model);
                OrderDialog orderDialog = new OrderDialog(thisFrame, true, Objects.requireNonNull(Database.lastOrderID()));
            } catch (SQLException a) {
                a.printStackTrace();
            }
        }
        if (e.getSource() == RemoveOrderButton) {
            int selectedRow = orderShow.getSelectedRow();
            if (selectedRow != -1) {
                int orderID = (int) orderShow.getValueAt(selectedRow, 0);
                Database.removeOrder(orderID);
                try {
                    DefaultTableModel model = Database.executeSelectQuery("SELECT OrderID, CustomerID FROM orders ORDER BY OrderID DESC LIMIT 5");
                    orderShow.setModel(model);
                } catch (SQLException a) {
                    a.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecteer een order om te verwijderen.");
            }
        }
    }
}
