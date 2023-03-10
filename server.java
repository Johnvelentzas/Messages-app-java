import java.io.*;
import java.net.*;
//import java.util.ArrayList;

public class server{
    public static void main(String[] args) {
        server server = new server();
        server.run();
    }

    private ServerSocket server;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean establishedConnection = false;

    //private ArrayList<String[]> messages = new ArrayList<>();

    public server(){
        try {
            this.server = new ServerSocket(6666);
            System.out.println("Server Created Succesfully!");
            this.socket = this.server.accept();
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            this.establishedConnection = true;
            System.out.println("Established connection with: " + this.socket.getInetAddress());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run(){
        String str;
        try {
            while (this.establishedConnection) {
                System.out.println("Waiting to read.");
                str = this.dis.readUTF();
                System.out.println("Read : " + str);
                if (str == "close") {
                    this.close();
                    break;
                }
                //messages.add(str.split("\n"));
                this.dos.writeUTF("Read message: " + str);
            }
        } catch (IOException e) {
            this.establishedConnection = false;
            this.close();
        }
    }

    public void close(){
        try {
            this.establishedConnection = false;
            this.dis.close();
            this.dos.close();
            this.server.close();
            this.socket.close();
            System.out.println("Server closed succesfully!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}