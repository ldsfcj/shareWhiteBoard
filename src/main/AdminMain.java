package main;

import remote.IClient;
import remote.IWhiteBoard;
import remote.IWhiteBoardImpl;
import ui.AdminStartFrame;
import ui.AdminWhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AdminMain extends UnicastRemoteObject implements IClient,Serializable{

    public AdminWhiteBoard AWB;

    public AdminMain() throws RemoteException {
    }

    public static void main(String[] args) {
        AdminStartFrame adminStartFrame = new AdminStartFrame();

        adminStartFrame.setConnectButtonListener((port, username) -> {
            try {
                IWhiteBoard server = new IWhiteBoardImpl();
                Registry registry = LocateRegistry.createRegistry(port);
                registry.bind("Server",server);
                System.out.println("Server is ready");

               AdminMain adminMain = new AdminMain();
               adminMain.connect(port,username);
               adminStartFrame.dispose();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void connect(int port, String username) {
        try {
            //RMI
            AWB = new AdminWhiteBoard(username);

            String hostName = "127.0.0.1"+":"+port;
            String clientServiceName = "Create";
            String[] details = {username,hostName,clientServiceName};
            String serviceName = "Server";

            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);

            IWhiteBoard server = (IWhiteBoard) Naming.lookup("rmi://" + hostName + "/" + serviceName);

            server.isEmpty(details);
            server.registerListener(details);

            AWB.setServer(server);

        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "connection failed", "error", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
            System.exit(0);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {
        AWB.getUserListBoard().getNoticeBoard().append(message);
    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        AWB.getUserListBoard().getUserList().setListData(currentUsers);
    }

    @Override
    public boolean judge(String str) throws RemoteException {
        int flag = JOptionPane.showConfirmDialog(null,str + " want to join the whiteboard\n" + "apporve or not?","Judge", JOptionPane.YES_NO_OPTION);
        if(flag == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void load(byte[] b) throws RemoteException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            BufferedImage image = ImageIO.read(in);
            AWB.getPaintBoard().load(image);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void reject(String str) throws RemoteException {
        JOptionPane.showMessageDialog(null, str + "the request has been rejected", "error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    @Override
    public void info(String str) throws RemoteException {
        Thread t = new Thread(()->{
            JOptionPane.showMessageDialog(null, str, "Information", JOptionPane.INFORMATION_MESSAGE);
        });
        t.start();
    }
}
