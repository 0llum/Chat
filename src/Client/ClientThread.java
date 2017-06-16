package Client;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream inputStream;
        BufferedReader bufferedReader;

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                String line = bufferedReader.readLine();

                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                }

                System.out.println(line);

                Platform.runLater(() -> Client.textArea.appendText(line + "\n"));
            } catch (IOException e) {
                Platform.runLater(() -> Client.textArea.appendText("Server Connection lost" + "\n"));
                Client.socket = null;
                e.printStackTrace();
                return;
            }
        }
    }
}