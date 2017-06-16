package Client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    public static TextArea textArea;

    public static Socket socket;
    private PrintStream printStream;
    private String username, serverIP, message;
    private int serverPort;
    private TextField textFieldUsername, textFieldServerIP, textFieldServerPort, textField;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setTitle("Chat");

        textFieldUsername = new TextField();
        textFieldUsername.setMinSize(100, 20);
        textFieldUsername.setPromptText("Username");
        textFieldUsername.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onConnectToServerClicked();
            }
        });

        textFieldServerIP = new TextField();
        textFieldServerIP.setMinSize(200, 20);
        textFieldServerIP.setPromptText("Server IP");
        textFieldServerIP.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onConnectToServerClicked();
            }
        });

        textFieldServerPort = new TextField();
        textFieldServerPort.setMinSize(50, 20);
        textFieldServerPort.setPromptText("Server Port");
        textFieldServerPort.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                onConnectToServerClicked();
            }
        });

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(event -> onConnectToServerClicked());
        connectButton.setFocusTraversable(false);

        Button disconnectButton = new Button("Disonnect");
        disconnectButton.setOnAction(event -> disconnectFromServer());
        disconnectButton.setFocusTraversable(false);

        textArea = new TextArea();
        textArea.setMinSize(400, 600);
        textArea.setFocusTraversable(false);
        textArea.setEditable(false);

        textField = new TextField();
        textField.setMinSize(400, 20);
        textField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                message = textField.getText();
                printStream.println(message);
                textField.clear();
            }
        });

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> textField.clear());

        HBox serverAddressFields = new HBox();
        serverAddressFields.getChildren().add(textFieldUsername);
        serverAddressFields.getChildren().add(textFieldServerIP);
        serverAddressFields.getChildren().add(textFieldServerPort);
        serverAddressFields.getChildren().add(connectButton);
        serverAddressFields.getChildren().add(disconnectButton);

        HBox sendFields = new HBox();
        sendFields.getChildren().add(textField);
        sendFields.getChildren().add(sendButton);

        VBox layout = new VBox();
        layout.getChildren().add(serverAddressFields);
        layout.getChildren().add(textArea);
        layout.getChildren().add(sendFields);

        root.getChildren().add(layout);
        stage.setScene(scene);
        stage.show();

        //connectToServer(SERVER_IP, PORT);
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
        super.stop();
    }

    private void onConnectToServerClicked() {
        if (textFieldUsername.getText().isEmpty()) {
            return;
        }
        if (textFieldServerIP.getText().isEmpty()) {
            return;
        }

        if (textFieldServerPort.getText().isEmpty()) {
            return;
        }

        username = textFieldUsername.getText();
        serverIP = textFieldServerIP.getText();
        serverPort = Integer.parseInt(textFieldServerPort.getText());
        connectToServer(serverIP, serverPort);
    }

    private void connectToServer(String serverIP, int port) {
        if (socket != null && socket.isConnected()) {
            textArea.appendText("Already connected\n\n");
            return;
        }

        textArea.appendText("Connecting to Server...\n\n");

        try {
            socket = new Socket(serverIP, port);
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ClientThread(socket).start();
        printStream.println(username);
        textField.requestFocus();
    }

    private void disconnectFromServer() {
        if (socket == null || !socket.isConnected()) {
            textArea.appendText("Not connected\n\n");
            return;
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
