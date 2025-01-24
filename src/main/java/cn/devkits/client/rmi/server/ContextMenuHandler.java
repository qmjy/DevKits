package cn.devkits.client.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ContextMenuHandler extends UnicastRemoteObject implements IContextMenuHandler {
    public ContextMenuHandler() throws RemoteException {
        super();
    }

    @Override
    public boolean execute(String param, String value) {
        return false;
    }
}
