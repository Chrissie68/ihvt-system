import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.util.Arrays;

public class ArduinoConnectie {
    private String status = "bewegen";
    private GOOEY gooey;
    int xCoordinaten, yCoordinaten;
    private SerialPort Arduino;
    private StringBuilder messageBuffer = new StringBuilder();

    public ArduinoConnectie(String portNaam, int baudRate, GOOEY gooey) throws InterruptedException {
        this.gooey = gooey;
        Arduino = SerialPort.getCommPort(portNaam);
        Arduino.setComPortParameters(baudRate, 8, 1, 0);
        Arduino.setComPortTimeouts(Arduino.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!openPort()) {
            return;
        }
        addSerialPortEventListener();
        Thread.sleep(2000);
    }

    public boolean openPort() {
        if (Arduino.openPort()) {
            System.out.println("connectie met arduino gelukt");
            return true;
        } else {
            System.out.println("Connectie gefaald");
            return false;
        }
    }

    public void stuurBericht(String message) throws IOException {
        Arduino.getOutputStream().write(message.getBytes());
        Arduino.getOutputStream().flush();
    }


    public void addSerialPortEventListener() {
        Arduino.addDataListener(new SerialPortDataListener() {

            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    byte[] newData = new byte[Arduino.bytesAvailable()];
                    Arduino.readBytes(newData, newData.length);
                    String receivedMessage = new String(newData);
                    processData(receivedMessage);
                }
            }
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });
    }

    private void processData(String data) {
        messageBuffer.append(data);
        int index;
        while ((index = messageBuffer.indexOf("\n")) != -1) { // Assuming commands end with newline character
            String Commando = messageBuffer.substring(0, index).trim();
            messageBuffer.delete(0, index + 1);
            processcommando(Commando);
        }
    }
    public void processcommando(String data) {

        System.out.println("Received: " + data);
        if(data.equals("fork")){
            System.out.println("niggers");
            status = "fork";
        } else if (data.equals("bewegen")) {
            status = "bewegen";
        }
        if (data.startsWith("COORD")) {
            String[] parts = data.split(",");
            if (parts.length == 3) {
                try {
                    int xCoordinaten = Integer.parseInt(parts[1]);
                    int yCoordinaten = Integer.parseInt(parts[2]);

                    xCoordinaten = (int) Math.round(xCoordinaten / 7.222222222222);
                    yCoordinaten = (int) Math.round(yCoordinaten / -5.5);

                    gooey.addRedDotLabel(xCoordinaten, yCoordinaten, status);
                    System.out.println("Coordinates received: x=" + xCoordinaten + ", y=" + yCoordinaten);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing coordinates: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid COORD format: " + Arrays.toString(parts));
            }
        } else {
            System.out.println("Unexpected data format: " + data);
        }
    }


}
