

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GOOEY extends JFrame implements ActionListener {

    JButton ControlPanelButton;
    static JLabel resultLabel;


    public GOOEY() {
        this.setTitle("Warehouserobot");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

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

        this.add(rasterPanel, BorderLayout.CENTER);

        this.ControlPanelButton = new JButton("Handmatig bedienen");
        ControlPanelButton.addActionListener(this);
        add(ControlPanelButton, BorderLayout.SOUTH);

        resultLabel = new JLabel("Laatste 5 orders", SwingConstants.CENTER);
        add(resultLabel, BorderLayout.EAST);

        this.setMinimumSize(new Dimension(800, 600));

        Database.loadResults();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ControlPanelButton) {
            ControlPanel controlPanel = new ControlPanel(this, true);
        }
    }
}

