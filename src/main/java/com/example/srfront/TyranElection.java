package com.example.srfront;

import java.rmi.Remote;
import java.rmi.RemoteException;
public interface TyranElection extends Remote {
    void registerNode(String name, int id) throws RemoteException;
    void  unregisterNode(String name) throws RemoteException;
    void startElection() throws RemoteException;
    int getNodesCount() throws RemoteException;
}
