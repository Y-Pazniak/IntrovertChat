import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatClient {
    JTextField inputText;
    JTextArea chatWindow;
    Socket socket;
    PrintWriter pw;
    BufferedReader bfr;

    public static void main(String[] args) {
        new ChatClient().go();
    }

    public void go(){
        JFrame frame = new JFrame("Chat Client");
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));

        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createEmptyBorder(30, 0,0,0));

        chatWindow = new JTextArea(10, 26);
        JScrollPane scrollPane = new JScrollPane(chatWindow);
        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        chatWindow.setWrapStyleWord(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        northPanel.add(scrollPane);
        frame.getContentPane().add(BorderLayout.NORTH, northPanel);
        inputText = new JTextField(13);
        Font fontBigger = new Font("bigger", Font.BOLD, 20);
        inputText.setFont(fontBigger);
        panel.add(inputText);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new sendActionListener());
        panel.add(BorderLayout.CENTER, sendButton);

        setUpNetwork();
        Thread t = new Thread(new InputMessagesReader());
        t.start();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(400,400);
    }

    public void setUpNetwork(){
        try {
            socket = new Socket("127.0.0.1", 6668);
            pw = new PrintWriter(socket.getOutputStream());
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception exc){exc.printStackTrace();}
    }

    public void sendMessage(){
        try {
           pw.println(inputText.getText());
           pw.flush();
        }
        catch (Exception exc){exc.printStackTrace();}
        inputText.setText("");
        inputText.requestFocus();
    }

    public class sendActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }

    public class InputMessagesReader implements Runnable{
        @Override
        public void run() {
            try {
            String message;
                while ((message = bfr.readLine()) != null){
                    chatWindow.append(message + "\n");
                }
            }
            catch (IOException exc){
                System.out.println("Ошибка в чтении данных с сервера");
                exc.printStackTrace();
            }
        }
    }
}
