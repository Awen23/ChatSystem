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
     * Constructor for GUI
     *
     * @param port the port the server is on
     * @param cs the chat server object
     */
    public ServerGUI(int port, ChatServer cs) {

        //for top of screen
        super("Chat Server GUI");

        this.port = port;
        this.cs = cs;

        messageDisplay = new JTextArea("Server started on " + port + "\n", 40, 20);
        JPanel chatPanel = new JPanel(new GridLayout(1,1));
        chatPanel.add(new JScrollPane(messageDisplay));
        add(chatPanel, BorderLayout.CENTER);
        messageDisplay.setEditable(false);

        JPanel messagePanel = new JPanel();
        disconnect = new JButton("Disconnect");

        messagePanel.add(disconnect);

        add(messagePanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);

        ButtonTrigger bh = new ButtonTrigger();
        disconnect.addActionListener(bh);




    }

    public void write(String message) {
        messageDisplay.append(message + "\n");
        messageDisplay.setCaretPosition(messageDisplay.getText().length() - 1); // Sets cursor position in chat field
    }

    private class ButtonTrigger implements ActionListener {
        public void actionPerformed(ActionEvent action) {
            if(action.getSource() == disconnect) {
                cs.disconnect();
            }
        }
    }
}
