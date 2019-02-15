import javax.swing.*;
import java.io.*;
import java.net.*;

public class MultiCastChat implements Runnable {
    private DatagramSocket serverSocket = null;
    private JTextField input;           // Campo donde el usuario ingresa el texto.
    private JButton send;               // Botón para enviar el mensaje.
    private JTextArea output;           // Despliegue de los mensajes recibidos.
    private JPanel MultiCastChatView;   // Panel que contiene los elementos.

    MultiCastChat() {
        new Thread(this).start();
        try {
            serverSocket = new DatagramSocket(4445);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        send.addActionListener(e -> sendMessage()); // Fijamos el método para enviar mensajes al botón.
    }

    @Override
    public void run() {
        try {
            MulticastSocket clientSocket = new MulticastSocket(4446);
            InetAddress address = InetAddress.getByName("230.0.0.1");
            clientSocket.joinGroup(address);
            DatagramPacket packet;
            while (true) {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                clientSocket.receive(packet);
                String incomingMessage = new String(packet.getData(), 0, packet.getLength());
                output.setText(output.getText() + incomingMessage + "\n");
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private void sendMessage() {
        try {
            if (input.getText().length() != 0) {  // Si el campo de entrada contiene algo.
                byte[] buf;
                String message = input.getText();
                buf = message.getBytes();
                InetAddress group = InetAddress.getByName("230.0.0.1");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                serverSocket.send(packet);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        input.setText("");      // Reiniciamos el campo independientemente de lo que pase.
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MultiCastChat");
        frame.setContentPane(new MultiCastChat().MultiCastChatView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
