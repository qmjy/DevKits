package cn.devkits.client.rmi.server;

import java.rmi.Remote;

public interface IContextMenuHandler extends Remote {
    boolean execute(String param, String value);
}
