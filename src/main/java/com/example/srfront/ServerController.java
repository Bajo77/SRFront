package com.example.srfront;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerController {
    String IP_Server;
    Integer Port;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(formatter);
    @FXML
    private TextField textIPServera;
    @FXML
    private TextField textPort;
    @FXML
    public TextArea textOutcome;
    private ServerImp serverImp;

    public ServerController() throws RemoteException {
    }

    @FXML
    protected void onUruchomButtonClick() throws RemoteException {



        try {
            IP_Server = textIPServera.getText();
            Port = Integer.parseInt(textPort.getText());
            System.setProperty("java.rmi.server.hostname", IP_Server);

            ServerImp obj = new ServerImp(this );

            LocateRegistry.createRegistry(Port);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("TyranElection", obj);

            textOutcome.appendText("- " + formattedDateTime + " - Serwer RMI gotowy\n");

        } catch (Exception e) {

            System.err.println("Serwer wyjątek: " + e);

        }
    }

}
