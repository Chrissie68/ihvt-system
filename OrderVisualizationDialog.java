import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderVisualizationDialog extends JDialog {
    public OrderVisualizationDialog(JFrame frame, boolean modal, List<List<Object[]>> boxes) {
        super(frame, modal);
        setTitle("Order Execution Visualization");
        setLayout(new GridLayout(boxes.size(), 1));

        for (List<Object[]> box : boxes) {
            JPanel boxPanel = new JPanel(new FlowLayout());
            boxPanel.setBorder(BorderFactory.createTitledBorder("Box"));
            for (Object[] item : box) {
                JLabel itemLabel = new JLabel("Item ID: " + item[1] + ", Size: " + item[2]);
                boxPanel.add(itemLabel);
            }
            add(boxPanel);
        }

        setSize(600, 400);
        setLocationRelativeTo(frame);
        setVisible(true);
    }
}