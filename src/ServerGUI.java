import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame {

    private final int port;
    private final ChatServer cs;
    private JTextArea messageDisplay;
    private JButton disconnect;

    /**
     *
     * Constructor for ServerGUI, sets up design
     *
     * @param port the port the server is on
     * @param cs the chat server object
     */
    ServerGUI(int port, ChatServer cs) {

        //for top of screen
        super("Chat Server GUI");

        this.port = port;
        this.cs = cs;

        //area that displays messages
        messageDisplay = new JTextArea("Server started on " + port + "\n", 40, 20);
        JPanel chatPanel = new JPanel(new GridLayout(1,1));
        chatPanel.add(new JScrollPane(messageDisplay));
        add(chatPanel, BorderLayout.CENTER);
        messageDisplay.setEditable(false);

        //section at bottom with disconnect button on
        JPanel messagePanel = new JPanel();
        disconnect = new JButton("Disconnect");

        messagePanel.add(disconnect);

        add(messagePanel, BorderLayout.SOUTH);

        //for the frame it's all on
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);

        //so button presses can be dealt with
        ButtonTrigger bh = new ButtonTrigger();
        disconnect.addActionListener(bh);

    }

    /**
     * Writes a message onto the messageDisplay
     *
     * @param message the message to display
     */
    public void write(String message) {
        messageDisplay.append(message + "\n");
        messageDisplay.setCaretPosition(messageDisplay.getText().length() - 1); //sets the cursor's position in chat
    }

    /**
     * Disconnects when you click the disconnect button
     */
    private class ButtonTrigger implements ActionListener {
        public void actionPerformed(ActionEvent action) {
            if(action.getSource() == disconnect) {
                cs.disconnect();
            }
        }
    }
}
