package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserStartFrame extends JFrame implements Serializable {

    private String ipAddress;
    private int port;
    private String username;
    private UserStartFrame.ClientConnectButtonListener clientConnectButtonListener;

    public UserStartFrame() {
        this.init();
    }

    public void init() {
        this.setTitle("Client Start Page");
        this.setSize(320, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(235, 233, 238));
        this.add(panel);


        JLabel addressLabel = new JLabel("Server address: ");
        addressLabel.setBounds(25, 40, 100, 25);
        panel.add(addressLabel);

        JTextField addressText = new JTextField(20);
        addressText.setBounds(125, 40, 165, 25);
        panel.add(addressText);

        JLabel portLabel = new JLabel("Port: ");
        portLabel.setBounds(25, 80, 80, 25);
        panel.add(portLabel);

        JTextField portText = new JTextField(20);
        portText.setBounds(125, 80, 165, 25);
        panel.add(portText);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(25, 120, 80, 25);
        panel.add(usernameLabel);

        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(125, 120, 165, 25);
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
                if (addressText.getText().equals("") || !isIPAddress(addressText.getText())) {
                    JOptionPane.showMessageDialog(panel,
                            "Please input a correct ip address", "Message", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    ipAddress = addressText.getText();
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

                username = usernameText.getText();

                if (username.equals("")) {
                    JOptionPane.showMessageDialog(panel,
                            "Please input a username", "Message", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (clientConnectButtonListener != null) {
                    clientConnectButtonListener.clientConnectButtonClick(ipAddress, port, username);
                }
            }
        });

        this.setVisible(true);
    }

    public void setClientConnectButtonListener(UserStartFrame.ClientConnectButtonListener clientConnectButtonListener) {
        this.clientConnectButtonListener = clientConnectButtonListener;
    }

    public interface ClientConnectButtonListener {
        void clientConnectButtonClick(String ipAddress, Integer port, String username);
    }

    public boolean isIPAddress(String str){
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if (m.matches()) { return true; }
        else { return false; }
    }

}
