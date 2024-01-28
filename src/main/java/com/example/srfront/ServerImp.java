package com.example.srfront;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ServerImp extends UnicastRemoteObject implements TyranElection {
    private final Map<String, Map<String, Integer>> nodes = new HashMap<>();
    private volatile int leaderId = -1;
    private volatile String leaderName = null;

    protected ServerImp() throws RemoteException {
        super();
    }

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
        clear();

        nodes.forEach((key, value) -> {
            System.out.println("Węzeł: " + key);
            value.forEach((nodeKey, nodeId) -> System.out.println(" - " + nodeKey + ": " + nodeId));
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

            System.out.println("Wybrany lider to " + leaderName + " z ID: " + leaderId);

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
                System.out.println("Usunięto węzeł: " + name + ", który był obcenym liderem");
                System.out.println("Wybieranie nowego lidera!");
                startElection();
            }
        }
    }

}
