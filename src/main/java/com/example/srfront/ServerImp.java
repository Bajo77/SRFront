package com.example.srfront;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerImp extends UnicastRemoteObject implements TyranElection {
    private ServerController serverController;

    public ServerImp(ServerController serverController) throws RemoteException {
        super();
        this.serverController = serverController;
    }
    private final Map<String, Map<String, Integer>> nodes = new HashMap<>();
    private volatile int leaderId = -1;
    private volatile String leaderName = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(formatter);

    private void clear(){
        try {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Błąd podczas czyszczenia ekranu: " + e.getMessage());
        }
    }

    private void printNodes(){
        //serverController.textOutcome.clear();

        nodes.forEach((key, value) -> {
            serverController.textOutcome.appendText("- " + formattedDateTime + " - Węzeł: " + key + "\n");
            value.forEach((nodeKey, nodeId) -> serverController.textOutcome.appendText("                                             - " + nodeKey + ": " + nodeId + "\n"));
        });
    }

    @Override
    public void registerNode(String name, int id) throws RemoteException {

        synchronized (nodes){

            nodes.putIfAbsent(name, new HashMap<>());

            nodes.get(name).put(name, id);

            for (Map.Entry<String, Map<String, Integer>> entry : nodes.entrySet()) {
                entry.getValue().put(name, id);
                nodes.get(name).put(entry.getKey(), entry.getValue().get(entry.getKey()));
            }
            printNodes();
        }
    }

    @Override
    public void startElection() throws RemoteException {
        synchronized (nodes) {

            leaderId = -1;
            leaderName = null;

            for (Map.Entry<String, Map<String, Integer>> nodeEntry : nodes.entrySet()) {
                for (Map.Entry<String, Integer> idEntry : nodeEntry.getValue().entrySet()) {
                    if (idEntry.getValue() > leaderId) {
                        leaderId = idEntry.getValue();
                        leaderName = idEntry.getKey();
                    }
                }
            }
            serverController.textOutcome.appendText("- " + formattedDateTime + " - Wybrany lider to " + leaderName + " z ID: " + leaderId + "\n");
        }
    }

    @Override
    public int getNodesCount() throws RemoteException {
        return this.nodes.size();
    }

    @Override
    public void unregisterNode(String name) throws RemoteException {
        synchronized (nodes) {
            nodes.remove(name);
            nodes.forEach((key, value) -> value.remove(name));

            if (leaderName.equals(name) && nodes.size() > 1) {
                printNodes();
                serverController.textOutcome.appendText("- " + formattedDateTime + " - Usunięto węzeł: " + name + ", który był obcenym liderem" + "\n");
                serverController.textOutcome.appendText("- " + formattedDateTime + " - Wybieranie nowego lidera!" + "\n");
                startElection();
            }
        }
    }
}
