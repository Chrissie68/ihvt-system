import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderVisualizationDialog extends JDialog implements ActionListener {
    int DoosNummer = 1;
    JButton StartenOrder;
    private Boolean doneCheck = false;

    public OrderVisualizationDialog(Object order, JFrame frame, boolean modal, List<List<Object[]>> boxes) {
        super(frame, modal);
        setTitle("Order visualizatie");
        setLayout(new BorderLayout());

        JPanel boxesPanel = new JPanel();
        boxesPanel.setLayout(new GridLayout(boxes.size(), 1));

        for (List<Object[]> box : boxes) {
            int currentDoosNummer = DoosNummer;
            JPanel boxPanel = new JPanel();
            boxPanel.setLayout(new BorderLayout());
            boxPanel.setBorder(BorderFactory.createTitledBorder("Doos " + DoosNummer));
            DoosNummer++;

            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

            for (Object[] item : box) {
                JLabel itemLabel = new JLabel("Name: " + item[1] + ", Item ID: " + item[2] + ", Size: " + item[3] + ", Quantity: " + item[4]);
                itemsPanel.add(itemLabel);
                itemsPanel.add(Box.createVerticalStrut(5));
            }

            // aanmaken printknop per doos
            JButton printKnop = new JButton("Print");
            printKnop.addActionListener(new ActionListener() {
                @Override

                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == printKnop) {
                        PackingSlipDialog packingSlipDialog = new PackingSlipDialog(frame, true, order, currentDoosNummer, box);
                    }
//                    PackingSlip PackingSlip = new PackingSlip(order, currentDoosNummer, box);
//                    System.out.println("Bon aangemaakt voor Doos " + currentDoosNummer);
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(printKnop);

            boxPanel.add(itemsPanel, BorderLayout.CENTER);
            boxPanel.add(buttonPanel, BorderLayout.SOUTH);

            boxesPanel.add(boxPanel);
        }

        add(boxesPanel, BorderLayout.CENTER);

        StartenOrder = new JButton("Starten Order");
        StartenOrder.addActionListener(this);
        JPanel startOrderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startOrderPanel.add(StartenOrder);
        add(startOrderPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == StartenOrder){
            doneCheck = true;
        }
    }

    public Boolean getDoneCheck() {
        return doneCheck;
    }
}