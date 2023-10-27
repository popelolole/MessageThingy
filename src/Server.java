import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Server extends Thread {
    private final Queue<String> messageQueue;
    private final Socket client;
    private final Persistence persistence;

    public Server(Socket client) {
        messageQueue = new LinkedList<>();
        this.client = client;
        persistence = new Persistence();
    }

    public void run(){
        try {
            System.out.println("Got connection from " + client.getRemoteSocketAddress());

            //add login

            DataInputStream in = new DataInputStream(client.getInputStream());

            System.out.println(in.readUTF());

            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            out.writeUTF("This is major tom to ground control...");

            out.writeUTF("Enter username");
            String user = in.readUTF();
            out.writeUTF("Enter username of recipient");
            String recipient = in.readUTF();

            while(true){
                String received = in.readUTF();
                if(received.equals("GET")){
                    Collection<String> receivedMessages = persistence.getMessageQueue(user);
                    if(receivedMessages == null){
                        out.writeUTF("No messages to retrieve.");
                    }
                    else {
                        for (String message : receivedMessages) {
                            out.writeUTF(message);
                        }
                    }
                    out.writeUTF("END");
                }
                else if(received.equals("END")) {
                    System.out.println("END");
                    break;
                }
                else if(received.equals("SEND")) {
                    if(!messageQueue.isEmpty())
                        persistence.addToMessageQueue(recipient, messageQueue);
                }
                else {
                    messageQueue.offer(received);
                }
            }

            for(String message : messageQueue){
                System.out.println(message);
            }

            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(3000)){
            System.out.println("Server started on port 3000.");

            while(true) {
                Socket client = serverSocket.accept();
                Thread t = new Server(client);
                t.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
