import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
    private ArrayList<String> fileData = new ArrayList<>();
    private ArrayList<String> transmitData = new ArrayList<>();

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
            System.out.println("Server client state:" + this.state + ", sending: " + str);
            this.dos.writeUTF(str);
            if (this.state == SAP.ssd) {
                sendServerData();
                this.state = SAP.await;
            }
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
                    case "utl":
                        this.getIntParam(in);
                        return this.utl(in);
                    case "atl":
                        this.state = SAP.atl;
                        this.getIntParam(in);
                        return "prc";
                    case "rtl":
                        this.getIntParam(in);
                        return this.rtl();
                    case "stl":
                        return this.stl();
                    case "etl":
                        return this.etl();
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
                return this.otl(in);
            case atl:
                return this.atl(in);
            case ctl:
                return this.ctl(in);
            case mun:
                return this.mun(in);
            case ocu:
                return this.ocu(in);
            case moc:
                return this.moc(in);
            case uoc:
                return this.uoc(in);
            default:
                return "bdr";
        }
    }

    private String otl(String in){
        this.file = new File(server.FILE_PATH + in + ".txt");
        if (this.file.exists()) {
            this.fileData = server.openFile(this.file);
            moveDataToTransmit(this.intParam);
            this.state = SAP.ssd;
            return "ssd";
        }
        this.state = SAP.await;
        return "bdr";
    }

    private String utl(String in){
        if (this.file != null && this.file.exists()) {
            this.fileData = server.openFile(this.file);
            moveDataToTransmit(this.intParam);
            this.state = SAP.ssd;
            return "ssd";
        }
        this.state = SAP.await;
        return "bdr";
    }

    private String atl(String in){
        System.out.println("Adding: " + in);
        if (this.intParam == 0) {
            this.fileData.add(in + "\n");
        } else if(this.intParam > 0){
            this.fileData.add(this.intParam - 1, in + "\n");
        }else{
            this.fileData.add(this.fileData.size() + this.intParam - 1, in + "\n");
        }
        this.state = SAP.await;
        System.out.println("File data: " + this.fileData.toString());
        return "ok";
    }

    private String rtl(){
        if (this.intParam == 0) {
            this.fileData.remove(this.fileData.size() - 1);
        }else if(this.intParam > 0){
            this.fileData.remove(this.intParam - 1);
        }else{
            this.fileData.remove(this.fileData.size() + this.intParam - 1);
        }
        this.state = SAP.await;
        System.out.println("File data: " + this.fileData.toString());
        return "ok";
    }

    private String stl(){
        this.state = SAP.await;
        if(server.writeFile(this.file, this.fileData)){
            return "ok";
        }
        return "bdr";
    }

    private String etl(){
        this.fileData.clear();
        this.file = null;
        this.state = SAP.await;
        return "ok";
    }

    private String ctl(String in){
        this.state = SAP.await;
        if(server.createFile(in)){
            return "ok";
        }
        return "bdr";
    }

    private String mun(String in){
        this.state = SAP.await;
        if (this.file != null) {
            return "bdr";
        }
        
        return "bdr";
    }

    private String ocu(String in){
        this.state = SAP.await;
        return "bdr";
    }

    private String moc(String in){
        this.state = SAP.await;
        return "bdr";
    }

    private String uoc(String in){
        this.state = SAP.await;
        return "bdr";
    }

    private void getIntParam(String str){
        if (str.length() < 5) {
            this.intParam = 0;
            return;
        }
        if (str.charAt(3) == ':') {
            this.intParam = Integer.parseInt(str.substring(4));
        }else{
            this.intParam = 0;
        }
    }

    private void getStringParam(String str){
        if (str.length() < 5) {
            this.stringParam = "";
            return;
        }
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

    private void moveDataToTransmit(int param){
        if (param == 0) {
            this.transmitData = this.fileData;
        }else if (param < 0) {
            this.transmitData.clear();
            for (int i = this.fileData.size() + param; i < this.fileData.size(); i++) {
                this.transmitData.add(this.fileData.get(i));
            }
        }else{
            this.transmitData.clear();
            for (int i = 0; i < param; i++) {
                this.transmitData.add(this.fileData.get(i));
            }
        }
    }

    private void sendServerData() throws IOException{
        this.dos.writeInt(this.transmitData.size());
        for (int i = 0; i < this.transmitData.size(); i++) {
            this.dos.writeUTF(this.transmitData.get(i));
        }
    }

    private static final String HELP = "help,   //Returns a text with all the available commands.\ngreet,  //The begining state of the server. Expects a client name.\nawait,  //The default state of the server. When it is on await it expects to receive an SAP code.\nok,     //A signal given by the server to proceed with the request.\nprc,    //Proceed with the client request.\nbdr,    //Bad Request.\ncun,    //Change User Name.\notl,    //Open text log. Optional parameters <+int>, <-int>, 0(default). ex. \"otl:-10\" the client will receive the 10 last lines of the log file if they exist. Follows the directory of the text file if given the ok by the server.\natl,    //Add to Text Log.\nrtl,    //Remove from Text Log. Follows void.\nstl,    //Store Text Log. Follows void.\nctl,    //Create Text Log. Follows void.\nmun,    //Message User with Name. Mandatory parameter <String> the user name of the message recepient. Follows the message of the user.\nocu,    //Open Conversation with User. Mandatory parameter <String> the user name of the message recepient. Follows void.\nmoc,    //Message Open Conversation. Follows the message of the user.\nuoc,    //Update Open Conversation. Follows void.";
}
