import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class server{
    public static void main(String[] args) {
        server server = new server();
        server.run();
    }

    public static final int SERVER_PORT = 80;
    public static final String FILE_PATH = "files\\";
    public static final int MAXIMUM_ACESS_ATTEMPTS = 10;

    private ServerSocket server;
    private ArrayList<serverClient> clients = new ArrayList<>();
    public static HashMap<String, Boolean> files = new HashMap<>();

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

    public static synchronized boolean createFile(String fileName){
        File file = new File(FILE_PATH + fileName + ".txt");
        boolean temp = false;
        try {
            temp = file.createNewFile();
            files.put(fileName, false);
        } catch (IOException e) {
            return false;
        }
        return temp;
    }

    public static ArrayList<String> openFile(File file){
        ArrayList<String> data = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
        }
        return data;
    }

    public static boolean accessWriteFile(File file, ArrayList<String> fileData){
        if (accessFile(file)) {
            writeFile(file, fileData);
            openAccessFile(file);
            return true;
        }
        return false;
    }

    public static synchronized boolean writeFile(File file, ArrayList<String> fileData){
        try {
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < fileData.size(); i++) {
                writer.write(fileData.get(i));
            }
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean accessAppendFile(File file, String str){
            if (accessFile(file)) {
            appendFile(file, str);
            openAccessFile(file);
            return true;
        }
        return false;
    }

    private static synchronized boolean appendFile(File file, String str){
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(str);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

/*
    public static boolean accessUpdateFile(File file, ArrayList<String> changes){
        if (accessFile(file)) {
            updateFile(file, changes);
            openAccessFile(file);
            return true;
        }
        return false;
    }

    private static boolean updateFile(File file, ArrayList<String> changes){
        try {
            ArrayList<String> fileData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                for (String change : changes) {
                    if (Integer.parseInt(change.split(":")[0]) == Integer.parseInt(nextLine.split(":")[0])) {
                        nextLine = change;
                    }
                }
                fileData.add(nextLine);
            }
            scanner.close();
            writeFile(file, fileData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private static boolean tryAccessFile(File file){
        Object foo = new Object();
        int i = 0;
        while (true) {
            if (accessFile(file)) {
                return true;
            }
            try {
                foo.wait(100);
                if (i++ == MAXIMUM_ACESS_ATTEMPTS) {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
    }
*/

    private static synchronized boolean accessFile(File file){
        if (files.get(file.getName())) {
            return false;
        }
        files.replace(file.getName(), false, true);
        return true;
    }

    private static synchronized void openAccessFile(File file){
        files.replace(file.getName(), true, false);
    }
}