import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        client client = new client();
        client.run();
    }

    private static final int SERVER_PORT = 80;
    private static final String SERVER_IP_ADRESS = "172.20.10.5";

    private static final Boolean WAITING_FOR_CODE = true;
    private static final Boolean WAITING_FOR_CONTEXT = false;
    private static final String GREET_MESSAGE = "Hello Server!";

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean establishedConnection = false;
    private boolean clientState = WAITING_FOR_CONTEXT;

    private ArrayList<String> data = new ArrayList<>();

    public client(){
        try {
            this.socket = new Socket(SERVER_IP_ADRESS, SERVER_PORT);
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            this.establishedConnection = true;
            System.out.println("Established connection!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String str;
        Scanner scanner = new Scanner(System.in);
        try {
            this.dos.writeUTF(GREET_MESSAGE);
            str = this.dis.readUTF();
            while (this.establishedConnection) {
                switch (str) {
                    case "ok":
                        System.out.println("Request executed Sucessfully.");
                        this.clientState = WAITING_FOR_CODE;
                        break;
                    case "bdr":
                        System.out.println("Bad request.");
                        this.clientState = WAITING_FOR_CODE;
                        break;
                    case "prc":
                        System.out.println("Proceed with request:");
                        this.clientState = WAITING_FOR_CONTEXT;
                        break;
                    case "cun":
                        System.out.println("Change user name:");
                        this.clientState = WAITING_FOR_CONTEXT;
                        break;
                    case "ssd":
                        System.out.println("Receiving data from server.");
                        this.clientState = WAITING_FOR_CODE;
                        receiveServerData();
                        System.out.println(this.data.toString());
                        break;
                    default:
                        System.out.println("The server sent:\n" + str);
                        this.clientState = WAITING_FOR_CODE;
                        break;
                }
                if (this.clientState) {
                    System.out.println("Give a SAP code to the server: (help) to get a list of all available codes. (close) to close the connection.");
                }
                str = scanner.nextLine();
                if (str.equals("close")) {
                    scanner.close();
                    this.close();
                    break;
                }
                this.dos.writeUTF(str);
                str = this.dis.readUTF();
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
            System.exit(-1);
            e.printStackTrace();
        }
    }

    private void receiveServerData() throws IOException{
        int max = this.dis.readInt();
        this.data = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            this.data.add(this.dis.readUTF());
        }
    }
}