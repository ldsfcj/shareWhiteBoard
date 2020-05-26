package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IWhiteBoard extends Remote {
    public void draw(byte[] b) throws RemoteException;
    public boolean check() throws RemoteException;
    public void registerListener(String[] details)throws RemoteException;
    public boolean judge(String str) throws RemoteException;
    public void removeUser(String username) throws RemoteException;
    public void noticeAll(String msg) throws RemoteException;
    public void isEmpty(String[] details) throws RemoteException;
//    public void isSameName(String[] details) throws RemoteException;
    public boolean isSameName(String[] details) throws RemoteException;
    public void exit(String username) throws RemoteException;
    public void end() throws RemoteException;
    public void printUsers() throws RemoteException;
}
