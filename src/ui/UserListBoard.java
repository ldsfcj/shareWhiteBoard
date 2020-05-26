package ui;

import remote.IWhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

public class UserListBoard extends JPanel implements Serializable {

    private String username;
    private JList userList = new JList();
    private String[] usernameList;
    private JScrollPane scrollPane = new JScrollPane(userList);
    private JLabel userList_label = new JLabel("UserList");
    private JLabel noticeBoard_label = new JLabel("NoticeBoard");
    private IWhiteBoard server;
    private JTextArea noticeBoard = new JTextArea();
    private JScrollPane noticeScrollPane = new JScrollPane(noticeBoard);
    JButton kickButton;
    private JList list;

    public UserListBoard(String username){
        this.username = username;
        scrollPane.setBounds(25, 50, 300, 300);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userList_label.setBounds(150,5,50,50);
//        noticeScrollPane.setBounds(25, 480, 300, 300);
//        noticeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticeBoard_label.setBounds(135,450,80,30);
        noticeScrollPane.setBounds(25,480,300,250);
        noticeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        noticeBoard.setEditable(false);

        kickButton = new JButton("Kick Out");
        kickButton.setBounds(128,375,100,25);
        kickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedClient = userList.getSelectedValue().toString();
                try {
                    server.removeUser(selectedClient);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(userList_label);
        add(scrollPane);
        add(kickButton);
        add(noticeBoard_label);
        add(noticeScrollPane);
    }

    public void setServer(IWhiteBoard server) {
        this.server = server;
    }

    public JList getUserList() {
        return userList;
    }

    public JTextArea getNoticeBoard() { return noticeBoard;}

    public JButton getKickButton() { return kickButton;}
}

