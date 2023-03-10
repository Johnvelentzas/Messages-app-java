import java.io.*;
import java.net.*;

public class serverClient extends Thread{
    
    private Socket client;
    private DataInputStream dis;
    private DataOutputStream dos;

    private InetAddress inetAddress;

    private boolean establishedConnection = false;

    public serverClient(Socket socket){
        this.client = socket;
        try {
            this.dis = new DataInputStream(this.client.getInputStream());
            this.dos = new DataOutputStream(this.client.getOutputStream());
            this.inetAddress = this.client.getInetAddress();
            System.out.println("Connected with client: " + this.client.getInetAddress());
            this.establishedConnection = true;
        } catch (IOException e) {
            this.close();
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        String str;
        try {
            while (establishedConnection) {
            System.out.println("Waiting for Client: " + this.inetAddress);
            str = this.dis.readUTF();
            System.out.println("Server received: " + str + " from Client: " + this.inetAddress);
            if (str == "close") {
                this.close();
            }
            str = server.message(str);
            this.dos.writeUTF(str);
        }
        } catch (Exception e) {
            System.out.println("Connection with Client:" + this.inetAddress + " failed.");
            this.close();
        }
    }

    public void close(){
        try {
            this.establishedConnection = false;
            this.dis.close();
            this.dos.close();
            this.client.close();
            System.out.println("Closed connection with Client: " + this.inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public InetAddress getInetAddress(){
        return this.inetAddress;
    }
}
