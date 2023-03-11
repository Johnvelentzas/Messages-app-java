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
            str = this.dis.readUTF();
            System.out.println("Server received: " + str + " from Client: " + this.clientName);
            str = this.process(str);
            this.dos.writeUTF(str);
        }
        } catch (Exception e) {
            System.out.println("Connection with Client:" + this.clientName + " was terminated.");
            this.close();
        }
    }

    private String process(String in){
        if (in.equals("help")) {
            return HELP;
        }
        switch (this.state) {
            case await:
                if (in.length() < 3) {
                    return "bdr";
                }
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

    private static final String HELP = "help,   //Returns a text with all the available commands.\ngreet,  //The begining state of the server. Expects a client name.\nawait,  //The default state of the server. When it is on await it expects to receive an SAP code.\nok,     //A signal given by the server to proceed with the request.\nprc,    //Proceed with the client request.\nbdr,    //Bad Request.\ncun,    //Change User Name.\notl,    //Open text log. Optional parameters <+int>, <-int>, 0(default). ex. \"otl:-10\" the client will receive the 10 last lines of the log file if they exist. Follows the directory of the text file if given the ok by the server.\natl,    //Add to Text Log.\nrtl,    //Remove from Text Log. Follows void.\nstl,    //Store Text Log. Follows void.\nctl,    //Create Text Log. Follows void.\nmun,    //Message User with Name. Mandatory parameter <String> the user name of the message recepient. Follows the message of the user.\nocu,    //Open Conversation with User. Mandatory parameter <String> the user name of the message recepient. Follows void.\nmoc,    //Message Open Conversation. Follows the message of the user.\nuoc,    //Update Open Conversation. Follows void.";
}
