import java.io.*;
import java.net.*;
import java.util.Scanner;

public class serverClient extends Thread{
    
    private Socket client;
    private DataInputStream dis;
    private DataOutputStream dos;

    private InetAddress inetAddress;
    private String clientName = "Unknown";

    private boolean establishedConnection = false;
    private SAP state = SAP.greet;

    private int intParam = 0;
    private String stringParam = "";

    private File file;
    private Scanner scanner;

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
            System.out.println("Waiting for Client: " + this.clientName);
            str = this.dis.readUTF();
            System.out.println("Server received: " + str + " from Client: " + this.clientName);
            if (str == "close") {
                this.close();
            }
            str = this.process(str);
            System.out.println("Server client state is: " + this.state);
            this.dos.writeUTF(str);
        }
        } catch (Exception e) {
            System.out.println("Connection with Client:" + this.clientName + " failed.");
            this.close();
        }
    }

    private String process(String in){
        switch (this.state) {
            case await:
                switch (in.substring(0, 3)) {
                    case "cun":
                        this.state = SAP.cun;
                        return "prc";
                    case "otl":
                        this.state = SAP.otl;
                        this.getIntParam(in);
                        return "prc";
                    case "atl":
                        this.state = SAP.atl;
                        return "prc";
                    case "rtl":
                        this.state = SAP.rtl;
                        return "prc";
                    case "stl":
                        this.state = SAP.stl;
                        return "prc";
                    case "ctl":
                        this.state = SAP.ctl;
                        return "prc";
                    case "mun":
                        this.state = SAP.mun;
                        this.getStringParam(in);
                        return "prc";
                    case "ocu":
                        this.state = SAP.ocu;
                        this.getStringParam(in);
                        return "prc";
                    case "moc":
                        this.state = SAP.moc;
                        return "prc";
                    case "uoc":
                        this.state = SAP.uoc;
                        return "prc";
                    default:
                        return "bdr";
                }
            case greet:
                this.state = SAP.cun;
                return "cun";
            case cun:
                this.clientName = in;
                this.state = SAP.await;
                return "ok";
            case otl:
                this.state = SAP.await;
                return "ok";
            case atl:
                this.state = SAP.await;
                return "ok";
            case rtl:
                this.state = SAP.await;
                return "ok";
            case stl:
                this.state = SAP.await;
                return "ok";
            case ctl:
                this.state = SAP.await;
                return "ok";
            case mun:
                this.state = SAP.await;
                return "ok";
            case ocu:
                this.state = SAP.await;
                return "ok";
            case moc:
                this.state = SAP.await;
                return "ok";
            case uoc:
                this.state = SAP.await;
                return "ok";
            default:
                return "bdr";
        }
    }

    private void getIntParam(String str){
        if (str.charAt(3) == ':') {
            this.intParam = Integer.parseInt(str.substring(4));
        }else{
            this.intParam = 0;
        }
    }

    private void getStringParam(String str){
        if (str.charAt(3) == ':') {
            this.stringParam = str.substring(4);
        }else{
            this.stringParam = "";
        }
    }


    public void close(){
        try {
            this.establishedConnection = false;
            this.dis.close();
            this.dos.close();
            this.client.close();
            System.out.println("Closed connection with Client: " + this.clientName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public InetAddress getInetAddress(){
        return this.inetAddress;
    }

    public String getClientName(){
        return this.clientName;
    }
}
