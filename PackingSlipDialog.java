import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PackingSlipDialog extends JDialog implements ActionListener {
    private JButton confirm, cancel;
    private JLabel text;
    private Object order;
    private int currentDoosNummer;
    private List<Object[]> box;

    public PackingSlipDialog(JFrame frame, Boolean modal, Object order, int doosNummer, List<Object[]> box){
        super(frame, modal);
        setTitle("Pakbon bevestiging");
        setLayout(new FlowLayout());

        // info
        this.order = order;
        this.currentDoosNummer = doosNummer;
        this.box = box;

        // aanmaken knoppen/tekst
        this.text = new JLabel("Weet je zeker dat je een pakbon wilt maken voor order: " + order + ", doos " + currentDoosNummer + "?");
        this.confirm = new JButton("Bevestigen");
        this.confirm.addActionListener(this);
        this.cancel = new JButton("Annuleren");
        this.cancel.addActionListener(this);

        add(text);
        add(confirm);
        add(cancel);

        setSize(450,100);
        setMinimumSize(new Dimension(450, 100));
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirm) {
            PackingSlip packingSlip = new PackingSlip(order, currentDoosNummer, box);

            // verandering voor knoppen en text
            getContentPane().remove(confirm);
            getContentPane().remove(cancel);
            text.setText("Bon succesvol aangemaakt voor order: " + order + ", doos " + currentDoosNummer + "!");

            revalidate();
            repaint();
            System.out.println("Bon aangemaakt voor Doos " + currentDoosNummer);

        } else if (e.getSource() == cancel) {
            dispose();
        }
    }
}
