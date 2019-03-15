import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static java.lang.Integer.parseInt;

public class ChatServer {

    static Vector<ClientHandler> activeClients = new Vector<>();
    private static boolean running = true;
    //i will be used for numbering clients
    private static int i = 0;
    private static ServerSocket ss;
    private static boolean isGUI = false;
    private static ServerGUI gui;
    private static ClientHandler ch;

    /**
     * Removes a client from the active vector then disconnects them on the client handler
     *
     * @param ch ClientHandler to remove
     */
    public static synchronized void removeClient(ClientHandler ch) {
        activeClients.remove(ch);
    }

    /**
     * Tries to start the server up
     *
     * @param port the port for the server to start up
     * @return whether the server was started successfully or not
     */
    private static synchronized boolean startServer(int port) {
            try {
                ss = new ServerSocket(port);
            } catch(IOException e) {
                return false;
            }

            return true;

        }

    /**
     * Disconnects all active clients, then closes the server socket and turns running to false to stop the listening thread
     */
    public static void disconnect() {
        running = false;
        System.exit(0);
    }

    /**
     * Thread to listen for an EXIT input in the server, then disconnects when it's received
     */
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Thread listening = new Thread(new Runnable() {
        @Override
        public void run() {
            while (running) {

                try {
                    //reading in then checking if it's exit
                    String msg = br.readLine();
                    if (msg.equals("EXIT")) {
                        disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    /**
     * Writes the message onto the gui or system.out
     *
     * @param message message to be printed
     */
    public static void write(String message) {
        if(isGUI) {
            gui.write(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Starts server then gets into loop for accepting clients
     *
     * @param args any arguments passed to the client
     */
    public static void main(String[] args) {
        try {
            //default value
            int port = 14001;
            //looping through all the arguments, if there's a -csp it sets it to true so that the next argument will be taken as port
            //also checks for -gui
            if (args.length > 0) {
                boolean csp = false;
                for (String arg : args) {
                    if (arg.equals("-csp")) {
                        csp = true;
                    } else if (csp) {
                        port = parseInt(arg);
                        csp = false;
                    } else if (arg.equals("-gui")) {
                        isGUI = true;
                    } else {
                        System.out.println("Invalid parameter entered: " + arg);
                    }
                }
            }
            if (!startServer(port)) {
                System.out.println("Server failed to start, try another port");
            } else {
                ChatServer cs = new ChatServer();
                if (isGUI) {
                    try {
                        gui = new ServerGUI(port, cs);
                    } catch (HeadlessException e) {
                        //printing an error if GUI can't be displayed
                        System.out.println("Error: Could not display GUI. " + e.getMessage());
                        System.exit(0);
                    }
                }

                System.out.println("New server started on port " + port);
                Socket s;

                //starts thread for listening for EXIT command
                listening.start();

                while (running) {
                    s = ss.accept();
                    //starting data input and output for the client, then creating right client handler then starting thread
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    //creating new client handler and starting the thread
                    ch = new ClientHandler(dis, dos, i);
                    Thread t = new Thread(ch);
                    activeClients.add(ch);
                    t.start();

                    //informs everyone they've connected
                    ch.sendToAll("Client " + i + " has connected!");

                    //moves onto the next number for the next client
                    i++;

                }
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}