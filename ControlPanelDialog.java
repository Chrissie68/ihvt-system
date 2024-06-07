import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.fazecast.jSerialComm.*;

public class ControlPanelDialog extends JDialog implements ActionListener {
    private final JButton left, right, up, down, fork, stil;
    private SerialPort Arduino;
    public ControlPanelDialog(JFrame frame, boolean modal ,SerialPort Arduino){
        super(frame, modal);
        this.Arduino = Arduino;
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
        this.fork = new JButton("fork");
        fork.addActionListener(this);
        add(fork);
        this.stil = new JButton("stil");
        stil.addActionListener(this);
        add(stil);

        pack();
        setLocationRelativeTo(frame);


        setVisible(true);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = "";
        if (e.getSource() == left) {
            message = "left";
        } else if (e.getSource() == right) {
            message = "right";
        } else if (e.getSource() == up) {
            message = "up";
        } else if (e.getSource() == down) {
            message = "down";
        } else if (e.getSource() == fork){
            message = "fork";
        } else if (e.getSource() == stil){
            message = "stil";
        }
        byte[] messageBytes = message.getBytes();
        Arduino.writeBytes(messageBytes, messageBytes.length);
    }
}

