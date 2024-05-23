

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanelDialog extends JDialog implements ActionListener {
    private final JButton left, right, up, down;
    public ControlPanelDialog(JFrame frame, boolean modal){
        super(frame, modal);
        setPreferredSize(new Dimension(300, 300));
        setLayout(new FlowLayout());
        setTitle("Control Panel");

        this.left = new JButton("left");
        left.addActionListener(this);
        add(left);
        this.right = new JButton("right");
        right.addActionListener(this);
        add(right);
        this.up = new JButton("up");
        up.addActionListener(this);
        add(up);
        this.down = new JButton("down");
        down.addActionListener(this);
        add(down);


        pack();
        setLocationRelativeTo(frame);


        setVisible(true);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == left) {
            System.out.println("left");
        } else if (e.getSource() == right) {
            System.out.println("right");
        } else if (e.getSource() == up) {
            System.out.println("up");
        } else if (e.getSource() == down) {
            System.out.println("down");
        }
    }
}

