import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        client client = new client();
        client.run();
    }

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean establishedConnection = false;

    public client(){
        try {
            this.socket = new Socket("192.168.1.14", 6666);
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            this.establishedConnection = true;
            System.out.println("Established connection!");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run(){
        String str;
        Scanner scanner = new Scanner(System.in);
        try {
            while (this.establishedConnection) {
                System.out.println("Write a message to the server:");
                str = scanner.nextLine();
                if (str == "close") {
                    scanner.close();
                    this.close();
                    break;
                }
                this.dos.writeUTF(str);
                str = this.dis.readUTF();
                System.out.println("The server sent: " + str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.establishedConnection = false;
            this.close();
        }
    }

    public void close(){
        try {
            this.establishedConnection = false;
            this.dis.close();
            this.dos.close();
            this.socket.close();
            System.out.println("Client closed succesfully!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
