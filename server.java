import java.io.*;
import java.net.*;

import java.util.ArrayList;

public class server{
    public static void main(String[] args) {
        server server = new server();
        server.run();
    }

    public static final int SERVER_PORT = 80;
    public static final String FILE_PATH = "files\\";

    private ServerSocket server;
    private ArrayList<serverClient> clients = new ArrayList<>();

    public server(){
        try {
            this.server = new ServerSocket(SERVER_PORT);
            System.out.println("Server Created Succesfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        serverClient client;
        try {
            while (true) {
                client = new serverClient(this.server.accept());
                this.clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            this.close();
        }
    }

    public static synchronized String message(String str){
        return "test";
    }

    public void close(){
        try {
            for (serverClient client : this.clients) {
                client.close();
            }
            this.clients.clear();
            this.server.close();
            System.out.println("Server closed succesfully!");
        } catch (IOException e) {
            System.exit(-1);
            e.printStackTrace();
        }
    }
}