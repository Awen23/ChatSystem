import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientGUI extends JFrame {

    final int port;
    private final DataOutputStream dos;
    private JTextArea messageDisplay;
    private JTextField textInput;
    private JButton disconnect;
    private JLabel info;

    /**
     * Constructor for ClientGUI, sets up design
     *
     * @param port the port the client is on
     * @param dos the data output stream for the client so the GUI can write to it
     */
    public ClientGUI(int port, DataOutputStream dos) {
        super("Chat Client GUI");

        this.port = port;
        this.dos = dos;

        //area where messages will be shown
        messageDisplay = new JTextArea("Welcome!\n",40, 20);
        JPanel chatPanel = new JPanel(new GridLayout(1,1));
        chatPanel.add(new JScrollPane(messageDisplay));
        add(chatPanel, BorderLayout.CENTER);
        messageDisplay.setEditable(false);

        //area at bottom asking you to give your input, then a text box, and also a disconnect button
        JPanel messagePanel = new JPanel();
        info = new JLabel("Type your input here: ");
        textInput = new JTextField("", 10);
        disconnect = new JButton("Disconnect");
        messagePanel.add(info);
        messagePanel.add(textInput);
        messagePanel.add(disconnect);

        add(messagePanel, BorderLayout.SOUTH);

        //for the frame it's all on
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);

        //so button presses can be dealt with
        ButtonTrigger bh = new ButtonTrigger();
        disconnect.addActionListener(bh);

        //so messages being sent can be dealt with
        TextTrigger tt = new TextTrigger();
        textInput.addActionListener(tt);

    }

    /**
     * Writes to the Client GUI
     *
     * @param message message to write
     */
    public void write(String message) {
        messageDisplay.append(message + "\n");
        messageDisplay.setCaretPosition(messageDisplay.getText().length() - 1); //sets the cursor position in the chat field
    }

    /**
     * Disconnects client on disconnect button click
     */
    private class ButtonTrigger implements ActionListener {
        public void actionPerformed(ActionEvent action) {
            if(action.getSource() == disconnect) {
                ChatClient.disconnect();
            }
        }
    }

    /**
     * Writes message from the textInput box onto the output stream upon enter being pressed
     */
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
