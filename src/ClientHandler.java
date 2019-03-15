import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


class ClientHandler implements Runnable {

    final DataInputStream dis;
    final DataOutputStream dos;
    final int num;
    final boolean isGUI;
    Socket s;
    public boolean running = true;
    public ServerGUI GUI;

    /**
     *
     * @param s
     * @param dis
     * @param dos
     * @param num
     */
    public ClientHandler(Socket s,
                         DataInputStream dis, DataOutputStream dos, int num, ServerGUI gui) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.num = num;
        this.isGUI = true;
        this.GUI = gui;

    }

    public ClientHandler(Socket s,
                         DataInputStream dis, DataOutputStream dos, int num) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.num = num;
        this.isGUI = false;
    }

    public Socket getSocket() {
        return s;
    }

    /**
     *
     * @param message
     */
    public void sendToAll(String message) {
        for (ClientHandler ch : ChatServer.activeClients) {

            try {
                ch.dos.writeUTF(message);
            } catch(IOException e) {
                System.out.println(e.getStackTrace());
                System.exit(0);
            }
        }
        ChatServer.write(message);
    }

    /**
     *
     */
    public void disconnect() {
        running = false;
        sendToAll("Client " + num + " has disconnected");
    }

    /**
     *
     */
    public void run() {
        try {
            String received;
            while (running) {
                try
                {

                    received = dis.readUTF();

                    if(received.equals("EXIT")){
                        this.s.close();
                        break;
                    }

                    String MsgToSend = "Client " + num + ": " + received;

                    sendToAll(MsgToSend);

                } catch (EOFException | SocketException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
            this.dis.close();
            this.dos.close();

            }catch(IOException e){
                e.printStackTrace();
            } finally {
                running = false;
                ChatServer.removeClient(this);
            }
    }
}
