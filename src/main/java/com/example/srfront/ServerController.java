package com.example.srfront;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerController {
    String IP_Server;
    Integer Port;
    @FXML
    private TextField textIPServera;
    @FXML
    private TextField textPort;
    @FXML
    private TextArea textOutcome;

    @FXML
    protected void openNewWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("outcome-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Wyniki");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onUruchomButtonClick() {
        try {
            IP_Server = textIPServera.getText();
            Port = Integer.parseInt(textPort.getText());
            System.setProperty("java.rmi.server.hostname", IP_Server);

            ServerImp obj = new ServerImp();

            LocateRegistry.createRegistry(Port);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("TyranElection", obj);

            textOutcome.appendText("Serwer RMI gotowy\n");

            openNewWindow();

        } catch (Exception e) {

            System.err.println("Serwer wyjątek: " + e);

        }
    }

}
