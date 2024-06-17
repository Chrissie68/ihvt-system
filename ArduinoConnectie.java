import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.util.Arrays;

public class ArduinoConnectie {
    private GOOEY gooey;
    int xCoordinaten, yCoordinaten;
    private SerialPort serialPort;
    private StringBuilder messageBuffer = new StringBuilder();

    public ArduinoConnectie(String portNaam, int baudRate, GOOEY gooey) throws InterruptedException {
        this.gooey = gooey;
        serialPort = SerialPort.getCommPort(portNaam);
        serialPort.setComPortParameters(baudRate, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!openPort()) {
            return;
        }
        addSerialPortEventListener();
        Thread.sleep(2000);
    }

    public boolean openPort() {
        if (serialPort.openPort()) {
            System.out.println("connectie met arduino gelukt");
            return true;
        } else {
            System.out.println("Connectie gefaald");
            return false;
        }
    }

    public boolean closePort() {
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
            return true;
        } else {
            System.out.println("Failed to close port :(");
            return false;
        }
    }

    public void sendMessage(String message) throws IOException {
        serialPort.getOutputStream().write(message.getBytes());
        serialPort.getOutputStream().flush();
        System.out.println("Sent message: " + message);
    }


    public void addSerialPortEventListener() {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    serialPort.readBytes(newData, newData.length);
                    String receivedMessage = new String(newData);
                    processIncomingData(receivedMessage);
                }
            }
        });
    }

    private void processIncomingData(String data) {
        messageBuffer.append(data);
        int index;
        while ((index = messageBuffer.indexOf("\n")) != -1) { // Assuming commands end with newline character
            String completeCommand = messageBuffer.substring(0, index).trim();
            messageBuffer.delete(0, index + 1);
            processCommand(completeCommand);
        }
    }
    public void processCommand(String data) {
        System.out.println("Received: " + data);
        if (data.startsWith("COORD")) {
            String[] parts = data.split(",");
            if (parts.length == 3) {
                try {
                    int xCoordinaten = Integer.parseInt(parts[1]);
                    int yCoordinaten = Integer.parseInt(parts[2]);

                    xCoordinaten = (int) Math.round(xCoordinaten / 7.222222222222);
                    yCoordinaten = (int) Math.round(yCoordinaten / -5.5);

                    gooey.addRedDotLabel(xCoordinaten, yCoordinaten);
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
