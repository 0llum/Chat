package Server;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ServerThread extends Thread {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private Socket socket;
    private String id, localIP, publicIP;
    private PrintStream toClientListPrintStream;
    private PrintStream toClientPrintStream;

    public ServerThread(Socket socket, String id) {
        this.socket = socket;
        this.id = id;
    }

    public void run() {
        InputStream inputStream;
        BufferedReader bufferedReader;

        try {
            inputStream = socket.getInputStream();
            toClientPrintStream = new PrintStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            localIP = socket.getLocalAddress().getHostAddress();
            publicIP = socket.getInetAddress().getHostAddress();

            statusToServerLog("connected");

            URL whatIsMyIP = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));

            String serverIP = in.readLine();

            statusToClient("connected to " + serverIP);
            statusToClientList("connected");
        } catch (IOException e) {
            return;
        }

        String line;

        while (true) {
            try {
                line = bufferedReader.readLine();

                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    statusToServerLog("disconnected");
                    statusToClient("disconnected");
                    statusToClientList("disconnected");
                    Server.clients.remove(socket);
                    socket.close();
                    return;
                }

                messageToServerLog(line);
                messageToClient(line);
                messageToClientList(line);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void statusToClient(String status) {
        toClientPrintStream.println(localIP + " @ " + publicIP);
        toClientPrintStream.println(DATE_FORMAT.format(new Date()) + " --> " + status + "\n");
    }

    private void statusToClientList(String status) throws IOException {
        for (Map.Entry<Socket, String> entry : Server.clients.entrySet()) {
            if (entry.getKey() != socket) {
                toClientListPrintStream = new PrintStream(entry.getKey().getOutputStream());
                toClientListPrintStream.println(localIP + " @ " + publicIP);
                toClientListPrintStream.println(DATE_FORMAT.format(new Date()) + " --> " + status + "\n");
            }
        }
    }

    private void statusToServerLog(String status) {
        System.out.println(localIP + " @ " + publicIP);
        System.out.println(DATE_FORMAT.format(new Date()) + " --> " + status + "\n");
    }

    private void messageToClient(String message) {
        toClientPrintStream.println(localIP + " @ " + publicIP);
        toClientPrintStream.println(DATE_FORMAT.format(new Date()) + ": " + message + "\n");
    }

    private void messageToClientList(String message) throws IOException {
        for (Map.Entry<Socket, String> entry : Server.clients.entrySet()) {
            if (entry.getKey() != socket) {
                toClientListPrintStream = new PrintStream(entry.getKey().getOutputStream());
                toClientListPrintStream.println(localIP + " @ " + publicIP);
                toClientListPrintStream.println(DATE_FORMAT.format(new Date()) + ": " + message + "\n");
            }
        }
    }

    private void messageToServerLog(String message) {
        System.out.println(localIP + " @ " + publicIP);
        System.out.println(DATE_FORMAT.format(new Date()) + ": " + message + "\n");
    }
}
