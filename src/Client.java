import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 3000));
            System.out.println("Connected to " + client.getInetAddress());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            out.writeUTF("Hello server");

            DataInputStream in = new DataInputStream(client.getInputStream());
            System.out.println(in.readUTF());
            Scanner scan = new Scanner(System.in);

            System.out.println(in.readUTF());
            out.writeUTF(scan.nextLine());
            System.out.println(in.readUTF());
            out.writeUTF(scan.nextLine());
            while(true){
                String s = scan.nextLine();
                if(s.equals("q")) {
                    out.writeUTF("END");
                }
                else if(s.equals("quit")) {
                    out.writeUTF("END");
                    break;
                }
                out.writeUTF(s);
                if(s.equals("GET")) {
                    while(true) {
                        String message = in.readUTF();
                        if(message.equals("END"))
                            break;
                        System.out.println(message);
                    }
                }
            }
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}