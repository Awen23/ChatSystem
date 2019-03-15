import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientGUI extends JFrame {

    final int port;
    final DataOutputStream dos;
    JTextArea messageDisplay;
    JTextField textInput;
    JButton disconnect;
    JLabel info;

    public ClientGUI(int port, DataOutputStream dos) {
        super("Chat Client");

        this.port = port;
        this.dos = dos;

        messageDisplay = new JTextArea("Server started on " + port + "\n", 40, 20);
        JPanel chatPanel = new JPanel(new GridLayout(1,1));
        chatPanel.add(new JScrollPane(messageDisplay));
        add(chatPanel, BorderLayout.CENTER);
        messageDisplay.setEditable(false);

        JPanel messagePanel = new JPanel();
        info = new JLabel("Type your input here: ");
        textInput = new JTextField("", 10);
        disconnect = new JButton("Disconnect");

        messagePanel.add(info);
        messagePanel.add(textInput);
        messagePanel.add(disconnect);

        add(messagePanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);

        ButtonTrigger bh = new ButtonTrigger();
        disconnect.addActionListener(bh);

        TextTrigger tt = new TextTrigger();
        textInput.addActionListener(tt);

    }

    public void write(String message) {
        messageDisplay.append(message + "\n");
        messageDisplay.setCaretPosition(messageDisplay.getText().length() - 1); // Sets cursor position in chat field
    }

    private class ButtonTrigger implements ActionListener {
        public void actionPerformed(ActionEvent action) {
            if(action.getSource() == disconnect) {
                ChatClient.disconnect();
            }
        }
    }

    private class TextTrigger implements ActionListener {
        public void actionPerformed(ActionEvent action) {
            if(action.getSource() == textInput) {
                try {
                    dos.writeUTF(textInput.getText());
                    textInput.setText("");
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
