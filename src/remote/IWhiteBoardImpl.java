package remote;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class IWhiteBoardImpl extends UnicastRemoteObject implements IWhiteBoard, Serializable {

    private ArrayList<user> users;
    private byte[] b;

    public IWhiteBoardImpl() throws RemoteException {
        super();
        users = new ArrayList<>();
    }

    @Override
    public void draw(byte[] b) throws RemoteException {
        this.b = b;
        for (user c : users) {
            try {
                c.getClient().load(b);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public boolean check() throws RemoteException {
//        if(users.size() == 0) {
//            return false;
//        }
//        else {
//            return true;
//        }
//    }

    @Override
    public void registerListener(String[] details) throws RemoteException {
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        //reverse link
        try {
            IClient nextClient = (IClient) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
            users.add(new user(details[0], nextClient));

            if(users.size() > 1) {
                if(judge(users.get(users.size() - 1).getName())) {
                    users.remove(users.size() - 1); // remove before send the message to the client
                    nextClient.reject("the manager does not approved your request\n");
                    return ;
                }
            }
            if(b != null) {//synchronize the board with new user
                nextClient.load(b);
            }
            noticeAll("[Server] : " + details[0] + " has joined the share paint board.\n");
            refreshUserList();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // when someone exit the program, refresh the userList board
    private void refreshUserList() {
        //get the list
        String[] currentUsers = new String[users.size()];
        for(int i = 0; i< currentUsers.length; i++){
            currentUsers[i] = users.get(i).getName();
        }
        //update the list
        for(user c : users){
            try {
                c.getClient().updateUserList(currentUsers);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean judge(String str) throws RemoteException {
        return users.get(0).getClient().judge(str);
    }

    @Override
    public void removeUser(String username) throws RemoteException {
        int index = 0;
        for(int i = 0;i < users.size();i++) {
            if(users.get(i).getName().equals(username)) {
                index = i;
            }
        }
        if(index == 0) {
            users.get(0).getClient().info("Do not have this username");
            //users.get(0).getClient().messageFromServer("Do not have this username");
        }
        else {
            IClient temp = users.get(index).getClient();
            String str = users.get(index).getName();
            users.remove(index);
            users.get(0).getClient().info("remove success!");
            noticeAll("[Server] :" + str + " has been removed!\n");
            refreshUserList();
            Thread t = new Thread(()->{
                try {
                    temp.reject("sorry, you have been kicked out by the manager\n");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    @Override
    public void noticeAll(String msg) throws RemoteException {
        for (user client : users) {
            try {
                client.getClient().messageFromServer(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void isEmpty(String[] details) throws RemoteException {
//
//    }

    @Override
    public boolean isSameName(String[] detail) throws RemoteException{
        for (user c : users){
            if (c.getName().equals(detail[0])){
                return true;
            }
        }
        return false;
    }

    @Override
    public void exit(String username) throws RemoteException {
        int index = 0;
        for(int i = 0;i < users.size();i++) {
            if(users.get(i).getName().equals(username)) {
                index = i;
            }
        }
        users.remove(index);
        noticeAll("[Server] :" + username + " has exited!\n");
        refreshUserList();
    }

    @Override
    public void end() throws RemoteException {
        System.exit(0);
    }
}
