import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CustomDialog extends JDialog implements ActionListener {
    private JButton cancelButton, moveButton;
    private String blockTitle;

    // Coordinates map
    private static final Map<String, String> coordinatesMap = new HashMap<>();

    static {
        // Initialize the coordinates map
        coordinatesMap.put("A1", "119,-2069");
        coordinatesMap.put("A2", "803,-2069");
        coordinatesMap.put("A3", "1508,-2069");
        coordinatesMap.put("A4", "2211,-2069");
        coordinatesMap.put("A5", "2923,-2069");

        coordinatesMap.put("B1", "119,-1534");
        coordinatesMap.put("B2", "803,-1534");
        coordinatesMap.put("B3", "1508,-1534");
        coordinatesMap.put("B4", "2211,-1534");
        coordinatesMap.put("B5", "2923,-1534");

        coordinatesMap.put("C1", "119,-1029");
        coordinatesMap.put("C2", "803,-1029");
        coordinatesMap.put("C3", "1508,-1029");
        coordinatesMap.put("C4", "2211,-1029");
        coordinatesMap.put("C5", "2923,-1029");

        coordinatesMap.put("D1", "119,-488");
        coordinatesMap.put("D2", "803,-488");
        coordinatesMap.put("D3", "1508,-488");
        coordinatesMap.put("D4", "2211,-488");
        coordinatesMap.put("D5", "2923,-488");

        coordinatesMap.put("E1", "119,0");
        coordinatesMap.put("E2", "803,0");
        coordinatesMap.put("E3", "1508,0");
        coordinatesMap.put("E4", "2211,0");
        coordinatesMap.put("E5", "2923,0");
    }

    public CustomDialog(JFrame parent, String title, String blockTitle) {
        super(parent, title, true);
        this.blockTitle = blockTitle;

        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Beweeg naar: " + blockTitle));

        cancelButton = new JButton("Annuleren");
        moveButton = new JButton("Ja");

        cancelButton.addActionListener(this);
        moveButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(moveButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(messagePanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moveButton) {
            // Retrieve coordinates from the map based on the block title
            String coordinates = coordinatesMap.get(blockTitle);
            if (coordinates != null) {
                System.out.println(coordinates);
            } else {
                System.out.println("Geen coördinaten gevonden voor: " + blockTitle);
            }
        }
        dispose();
    }
}