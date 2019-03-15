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
    static Scanner scn = new Scanner(System.in);
    public static boolean isGUI = false;
    public static ClientGUI gui;

    /**
     *
     */
    public static void disconnect() {
        System.out.println("Disconnecting client");
        System.exit(0);

    }

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String args[])
    {
        String ip = "localhost";
        int port = 14001;
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
            Socket s = new Socket(ip, port);




        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        if(isGUI) {
            try {
                gui = new ClientGUI(port, dos);
            } catch(HeadlessException e) {
                System.out.println("Error: Could not display GUI. " + e.getMessage());
            }
        }

        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public synchronized void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();
                    if(!msg.equals("EXIT")) {
                        try {
                            // write on the output stream
                            dos.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        disconnect();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            public synchronized void run() {
                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        if(isGUI) {
                            gui.write(msg);
                        } else {
                            System.out.println(msg);
                        }
                    } catch(SocketException | EOFException s) {
                        System.out.println("Server Connection Lost");
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

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
