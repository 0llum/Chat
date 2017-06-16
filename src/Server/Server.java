package Server;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    public static final int PORT = 1342;
    public static Map<Socket, String> clients;
    public static int clientCount;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        clients = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        URL whatIsMyIP = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));

        String serverIP = in.readLine();

        System.out.println(dateFormat.format(new Date()) + " Server running @ " + serverIP + "\n");

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientCount = 0;

        //printClients();

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }

            clientCount++;
            clients.put(socket, socket.getInetAddress().getHostAddress());
            new ServerThread(socket, socket.getInetAddress().getHostAddress()).start();
        }
    }

    private static void printClients() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(clients.values());
            }
        }, 0, 5000);
    }
}
