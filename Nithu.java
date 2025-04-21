import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Nithu extends JFrame implements Runnable, ActionListener {

    private JTextField textField;
    private JButton sendButton;
    private JTextArea chatArea;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Thread chatThread;

    public Nithu() {
        setTitle("Thusha Kutty Chat Client");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Chat display area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(this);

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            socket = new Socket("localhost", 8888);
            chatArea.append("Connected to server!\n");
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            chatThread = new Thread(this);
            chatThread.setDaemon(true);
            chatThread.start();

        } catch (Exception e) {
            chatArea.append("Error connecting to server: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = textField.getText().trim();
        if (!msg.isEmpty()) {
            chatArea.append("Nithu: " + msg + "\n");
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
                textField.setText("");
            } catch (Exception ex) {
                chatArea.append("Error sending message.\n");
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = dataInputStream.readUTF();
                chatArea.append("Thusha: " + msg + "\n");
            } catch (Exception e) {
                chatArea.append("Connection closed or lost.\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Nithu();
    }
}
