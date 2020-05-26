package main;

import remote.IClient;
import remote.IWhiteBoard;
import remote.IWhiteBoardImpl;
import ui.UserStartFrame;
import ui.UserWhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class UserMain extends UnicastRemoteObject implements IClient, Serializable {

    public UserWhiteBoard UWB;
    public UserMain() throws RemoteException {

    }

    public static void main(String[] args) {
        UserStartFrame userStartFrame = new UserStartFrame();

        userStartFrame.setClientConnectButtonListener((ipAddress, port, username) -> {
            try {
                UserMain userMain= new UserMain();
                userMain.connect(ipAddress,port,username);
                userStartFrame.dispose();
                } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

    }

    public void connect(String ipAddress, int port, String username) {
        try {
            //RMI
            UWB = new UserWhiteBoard(username);

            String hostName = ipAddress+ ":" + port;
            String clientServiceName = "Create";
            String serviceName = "Server";
            String[] details = {username,hostName,clientServiceName};

            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            System.out.println(this);

            ServerSocket s = new ServerSocket(0);
            int localPort = s.getLocalPort();
            s.close();

            IWhiteBoard iwb = new IWhiteBoardImpl();
            Registry registry = LocateRegistry.createRegistry(localPort);
            registry.bind("whiteboard",iwb);
            System.out.println("the port: "+ localPort + " \nserver ready");

            IWhiteBoard server = (IWhiteBoard) Naming.lookup("rmi://" + hostName + "/" + serviceName);

//            if(!server.check()) {
//                JOptionPane.showMessageDialog(null, "empty room, connection failed", "error", JOptionPane.ERROR_MESSAGE);
//                System.exit(0);
//            }

//            server.isSameName(details);
            if (server.isSameName(details)){
                JOptionPane.showMessageDialog(null, "This username has been used!", "error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            server.registerListener(details);

            UWB.setServer(server);

        } catch (RemoteException | NotBoundException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "connection failed", "error", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
            System.exit(0);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageFromServer(String message) throws RemoteException {
        UWB.getUserListBoard().getNoticeBoard().append(message);
    }

    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        UWB.getUserListBoard().getUserList().setListData(currentUsers);
    }

    @Override
    public boolean judge(String str) throws RemoteException {
        return false;
    }

    @Override
    public void load(byte[] b) throws RemoteException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            BufferedImage image = ImageIO.read(in);
            UWB.getPaintBoard().load(image);
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
