package com.example.srfront;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class  Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Podaj adres ip serwera: ");
            String ip = scanner.nextLine();
            System.setProperty("java.rmi.server.hostname", ip);

            ServerImp obj = new ServerImp();

            System.out.print("Podaj port RMI: ");
            int port = scanner.nextInt();

            LocateRegistry.createRegistry(port);
            Registry registry = LocateRegistry.getRegistry();



            registry.bind("TyranElection", obj);

            System.out.println("Serwer RMI gotowy");

        } catch (Exception e) {

            System.err.println("Serwer wyjątek: " + e);

        }
    }
}