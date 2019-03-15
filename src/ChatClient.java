import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ChatClient {
    private static Scanner scn = new Scanner(System.in);
    private static boolean isGUI = false;
    private static ClientGUI gui;
    private static boolean running = true;

    /**
     * Disconnects client by stopping the thread loops and exiting
     */
    public static void disconnect() {
        running = false;
        System.out.println("Disconnecting client");
        System.exit(0);

    }

    /**
     * Connects to server and then starts sending and receiving threads
     *
     * @param args arguments passed in
     */
    public static void main(String args[])
    {
        //default values
        String ip = "localhost";
        int port = 14001;

        //loops through all arguments, if there's a cca or ccp then it'll take the next value as the relevant one
        //also looks for gui parameter, otherwise it prints that it's invalid
        if(args.length > 0) {
            boolean cca = false;
            boolean ccp = false;
            for(String arg: args) {
                if(arg.equals("-cca")) {
                    cca = true;
                } else if(arg.equals("-ccp")) {
                    ccp = true;
                } else if(cca) {
                    ip = arg;
                    cca = false;
                } else if(ccp) {
                    port = parseInt(arg);
                    ccp = false;
                } else if(arg.equals("-gui")) {
                    isGUI = true;
                } else {
                    System.out.println("Invalid parameter entered: " + arg);
                }
            }
        }

        try {
            //setting up resources
            Socket s = new Socket(ip, port);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());


            //if it's a GUI then creating the GUI object
            if(isGUI) {
                try {
                    gui = new ClientGUI(port, dos);
                } catch(HeadlessException e) {
                    //printing an error if GUI can't be displayed
                    System.out.println("Error: Could not display GUI. " + e.getMessage());
                    System.exit(0);
                }
            }

            /*
             * sendMessage thread for reading messages from this client and sending them to all other clients
             */
            Thread sendMessage = new Thread(new Runnable()
            {
                @Override
                public synchronized void run() {
                    while (running) {
                        String msg = scn.nextLine();
                        if(!msg.equals("EXIT")) {
                            try {
                                //writing onto the output stream
                                dos.writeUTF(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //if the message was EXIT
                            System.exit(0);
                        }
                    }
                }
            });

            /*
             * Thread for reading message from the input stream and displaying them
             */
            Thread readMessage = new Thread(new Runnable() {
                public synchronized void run() {
                    while (running) {
                        try {
                            //reading in
                            String msg = dis.readUTF();

                            //writing them onto either the GUI or console
                            if(isGUI) {
                                gui.write(msg);
                            } else {
                                System.out.println(msg);
                            }
                        } catch(SocketException | EOFException s) {
                            //gotten when the server has been closed
                            System.out.println("Server Connection Lost");
                            disconnect();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            break;
                        }
                    }
                }
            });

            //don't need to send messages if using the GUI as the text box handles it, but otherwise starting threads
            if(!isGUI) {
                sendMessage.start();
            }

            readMessage.start();


        } catch(ConnectException c) {
            System.out.print("Cannot find server");
        } catch(IOException e) {
            System.out.print(e.getMessage());
        }
    }
}
