import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;


class ClientHandler implements Runnable {

    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final int num;
    private boolean running = true;

    /**
     * Constructor for ClientHandler when a GUI isn't being used
     *
     * @param dis the data input stream set up
     * @param dos the data output stream set up
     * @param num the number client to be printed by each message
     */
    public ClientHandler(DataInputStream dis, DataOutputStream dos, int num) {
        this.dis = dis;
        this.dos = dos;
        this.num = num;
    }

    /**
     * Sends message to all active clients and then the server
     *
     * @param message message to be sent
     */
    public void sendToAll(String message) {
        for (ClientHandler ch : ChatServer.activeClients) {
            try {
                ch.dos.writeUTF(message);
            } catch(IOException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
        ChatServer.write(message);
    }

    /**
     * Run method for threading
     */
    public void run() {
        try {
            String received;
            while (running) {
                try
                {
                    //receives string from input stream, then if it's not exit then print message next to client number to all clients
                    received = dis.readUTF();

                    String MsgToSend = "Client " + num + ": " + received;

                    sendToAll(MsgToSend);

                } catch (EOFException | SocketException e) {
                    //these happen when a client has been closed
                    ChatServer.removeClient(this);
                    sendToAll("Client " + num + " has disconnected");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //closing resources
            this.dis.close();
            this.dos.close();

            }catch(IOException e){
                e.printStackTrace();
            } finally {
                //disconnecting properly before exiting
                running = false;
                ChatServer.removeClient(this);
            }
    }
}
