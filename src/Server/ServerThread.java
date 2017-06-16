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
    private PrintStream toClientListPrintStream;
    private PrintStream toClientPrintStream;

    private String username, line;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream inputStream;
        BufferedReader bufferedReader;

        try {
            inputStream = socket.getInputStream();
            toClientPrintStream = new PrintStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                username = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            statusToServerLog("connected");

            URL whatIsMyIP = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));

            String serverIP = in.readLine();

            statusToClient("connected to " + serverIP);
            statusToClientList("connected");
        } catch (IOException e) {
            return;
        }

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
        toClientPrintStream.println(DATE_FORMAT.format(new Date()) + " - " + username + " --> " + status + "\n");
    }

    private void statusToClientList(String status) throws IOException {
        for (Socket client : Server.clients) {
            if (client != socket) {
                toClientListPrintStream = new PrintStream(client.getOutputStream());
                toClientListPrintStream.println(DATE_FORMAT.format(new Date()) + " - " + username + " --> " + status + "\n");
            }
        }
    }

    private void statusToServerLog(String status) {
        System.out.println(DATE_FORMAT.format(new Date()) + " - " + username + " --> " + status + "\n");
    }

    private void messageToClient(String message) {
        toClientPrintStream.println(DATE_FORMAT.format(new Date()) + " - " + username + ":");
        toClientPrintStream.println(message + "\n");
    }

    private void messageToClientList(String message) throws IOException {
        for (Socket client : Server.clients) {
            if (client != socket) {
                toClientListPrintStream = new PrintStream(client.getOutputStream());
                toClientListPrintStream.println(DATE_FORMAT.format(new Date()) + " - " + username + ":");
                toClientListPrintStream.println(message + "\n");
            }
        }
    }

    private void messageToServerLog(String message) {
        System.out.println(DATE_FORMAT.format(new Date()) + " - " + username + ": " + message + "\n");
    }
}
