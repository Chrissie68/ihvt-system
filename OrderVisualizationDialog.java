import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderVisualizationDialog extends JDialog {
    int DoosNummer = 1;
    JButton PrintKnop;
    JButton StartenOrder;

    public OrderVisualizationDialog(JFrame frame, boolean modal, List<List<Object[]>> boxes) {
        super(frame, modal);
        setTitle("Order visualizatie");
        setLayout(new BorderLayout());

        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(new GridLayout(boxes.size(), 1));

        for (List<Object[]> box : boxes) {
            JPanel boxPanel = new JPanel();
            boxPanel.setLayout(new BorderLayout());
            boxPanel.setBorder(BorderFactory.createTitledBorder("Doos " + DoosNummer));
            DoosNummer++;

            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

            for (Object[] item : box) {
                JLabel itemLabel = new JLabel("Item ID: " + item[1] + ", Size: " + item[2]);
                itemsPanel.add(itemLabel);
                itemsPanel.add(Box.createVerticalStrut(5));
            }

            PrintKnop = new JButton("Print");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(PrintKnop);

            boxPanel.add(itemsPanel, BorderLayout.CENTER);
            boxPanel.add(buttonPanel, BorderLayout.SOUTH);

            boxesPanel.add(boxPanel);
        }

        add(boxesPanel, BorderLayout.CENTER);

        StartenOrder = new JButton("Starten Order");
        JPanel startOrderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startOrderPanel.add(StartenOrder);
        add(startOrderPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(frame);
        setVisible(true);
    }
}
