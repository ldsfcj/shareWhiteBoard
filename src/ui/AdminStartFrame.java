package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

public class AdminStartFrame extends JFrame implements Serializable {

    private ConnectButtonListener connectButtonListener;
    private String username;
    private int port;

    public AdminStartFrame(){
        this.init();
    }

    public void init(){
        this.setTitle("Admin Start Page");
        this.setSize(320,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(235,233,238));
        this.add(panel);

        JLabel portLabel = new JLabel("Port: ");
        portLabel.setBounds(25, 40, 80, 25);
        panel.add(portLabel);

        JTextField portText = new JTextField(20);
        portText.setBounds(125, 40, 165, 25);
        panel.add(portText);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(25, 100, 80, 25);
        panel.add(usernameLabel);

        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(125, 100, 165, 25);
        panel.add(usernameText);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(25, 170, 100, 25);
        panel.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(185, 170, 100, 25);
        panel.add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameText.getText();

                if (username.equals("")) {
                    JOptionPane.showMessageDialog(panel,
                            "Please input a username", "Message", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (portText.getText().equals("")) {
                    JOptionPane.showMessageDialog(panel,
                            "Please input a number", "Message", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    try {
                        port = Integer.parseInt(portText.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel,
                                "please input a Number", "Message", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (port < 0 || port > 65535 ) {
                    JOptionPane.showMessageDialog(panel,
                            "Please input a valid port number (0 ~ 65535)", "Message", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (connectButtonListener != null) {
                    connectButtonListener.connectButtonClick(port, username);
                }

            }
        });

        this.setVisible(true);
    }

    public void setConnectButtonListener(ConnectButtonListener connectButtonListener) {
        this.connectButtonListener = connectButtonListener;
    }

    public interface ConnectButtonListener {
        void connectButtonClick(int port, String username);
    }

}
