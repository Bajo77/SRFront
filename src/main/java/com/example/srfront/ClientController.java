package com.example.srfront;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class ClientController {
    String IP_Server;
    Integer Port;
    String Nazwa;
    Integer ID;
    Boolean StartElection = false;

    @FXML
    private TextField textIPServera;

    @FXML
    private TextField textPort;
    @FXML
    private TextField textNazwa;

    @FXML
    private TextField textID;
    @FXML
    private Button buttonYES;

    @FXML
    private Button buttonNO;

    @FXML
    protected void onOdpalButtonClick() {
            IP_Server = textIPServera.getText();
            Port = Integer.parseInt(textPort.getText());
            Nazwa = textNazwa.getText();
            ID = Integer.parseInt(textID.getText());


            final TyranElection[] stubContainer = new TyranElection[1];


            try {
                Registry registry = LocateRegistry.getRegistry(IP_Server, Port);
                TyranElection stub = (TyranElection) registry.lookup("TyranElection");
                stubContainer[0] = stub;

                stub.registerNode(Nazwa, ID);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (stubContainer[0] != null) {
                            stubContainer[0].unregisterNode(Nazwa);
                        }
                    } catch (RemoteException e) {
                        System.err.println("Błąd podczas usuwania węzła: " + e.getMessage());
                    }
                }));

                while (true) {
                    try {
                        if (stub.getNodesCount() >= 2) {
                            buttonYES.setDisable(false);
                            buttonNO.setDisable(false);
                            if (StartElection) {
                                System.out.println("Rozpoczęto elekcję.");
                                stub.startElection();
                            }
                        }
                    } catch (RemoteException e) {
                        System.err.println("Wystąpił błąd podczas wykonywania operacji: " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Błąd podczas łączenia z serwerem: " + e);
            }
    }

    @FXML
    protected void onYESButtonClick() {
        StartElection = true;
    }

    @FXML
    protected void onNOButtonClick() {
        StartElection = false;
    }
}