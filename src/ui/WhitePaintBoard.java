package ui;

import remote.IWhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class WhitePaintBoard extends JFrame implements Serializable {

    protected JPanel mainPanel = new JPanel();
    protected JPanel leftPanel = new JPanel();
    protected PaintBoard paintBoard = new PaintBoard();
//    protected JPanel rightPanel = new JPanel();
    protected ButtonGroup paintTools = new ButtonGroup();
    protected UserListBoard userListBoard;
    IWhiteBoard server;
    protected String username;

    public WhitePaintBoard (String username){
        userListBoard = new UserListBoard(username);
        init();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    server.exit(username);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
//                e.getWindow().dispose();
            }
        });
    }

    public void setServer(IWhiteBoard server) {
        this.server = server;
        paintBoard.setServer(server);
        userListBoard.setServer(server);
    }

    public void setUsername(String username){
        this.username = username;
    }


    public void init(){
        // set the parameter
        this.setTitle("WhiteBoard");
        this.setSize(1400,850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        initMenuBar();

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(235,233,238));
        this.add(mainPanel);

        leftPanel.setPreferredSize(new Dimension(90,0));
        leftPanel.setLayout(null);
        leftPanel.setBackground(new Color(235,233,238));
        mainPanel.add(leftPanel,BorderLayout.WEST);

        String[] tool = {"freedraw","line","rectangle","circle","erase","text"};
        JButton[] toolButton = new JButton[tool.length];
        for (int j=0; j<tool.length;j++){
            toolButton[j] = new JButton();
            toolButton[j].setBounds(10+40*(j%2),60+50*(j/2),30,30);
            toolButton[j].setIcon(new ImageIcon("./src/icon/"+(j+1)+".gif"));
            paintTools.add(toolButton[j]);
            leftPanel.add(toolButton[j]);

            int Tj = j;
            toolButton[j].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    paintBoard.setType(tool[Tj]);
                }
            });
        }

        // set choose color button
        Color[] color = {Color.BLACK, Color.GRAY, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.WHITE};
        JButton[] colorButton = new JButton[color.length];
        for (int i=0; i<color.length; i++) {
            colorButton[i] = new JButton();
            colorButton[i].setBackground(color[i]);
            colorButton[i].setBounds(30,240+(40)*i,30,30);
            leftPanel.add(colorButton[i]);

            int Ci = i;
            colorButton[Ci].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    paintBoard.setColor(color[Ci]);
                }
            });
        }

        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(10,660,70,20);
        leftPanel.add(comboBox);
        comboBox.setFont(new Font("Apple Chancery", Font.PLAIN, 13));
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"Tiny", "Small", "Medium", "Large", "Huge"}));
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s=(String)comboBox.getSelectedItem();
                Stroke selectStroke = new BasicStroke(1.0f);
                if(s.contentEquals("Tiny")) {
                    selectStroke = new BasicStroke(1.0f);
                }
                else if(s.contentEquals("Small")) {
                    selectStroke = new BasicStroke(3.0f);
                }
                else if(s.contentEquals("Medium")) {
                    selectStroke = new BasicStroke(8.0f);
                }
                else if(s.contentEquals("Large")) {
                    selectStroke = new BasicStroke(12.0f);
                }
                else if(s.contentEquals("Huge")) {
                    selectStroke = new BasicStroke(20.0f);
                }
                paintBoard.setStroke(selectStroke);
            }
        });

        paintBoard.setBackground(Color.WHITE);
        mainPanel.add(paintBoard,BorderLayout.CENTER);

        userListBoard.setPreferredSize(new Dimension(350,200));
        userListBoard.setLayout(new BorderLayout());
        mainPanel.add(userListBoard,BorderLayout.EAST);

        this.setVisible(true);

    }

    public abstract void initMenuBar();

    public PaintBoard getPaintBoard(){ return this.paintBoard; }

    public UserListBoard getUserListBoard() { return this.userListBoard; }
}
