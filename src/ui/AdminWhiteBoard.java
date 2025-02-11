package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

public class AdminWhiteBoard extends WhitePaintBoard implements Serializable {

    private File file;
    private String dirPath = null;

    public AdminWhiteBoard (String username){
        super(username);
    }

    public static void main(String[] args){
        AdminWhiteBoard AWB = new AdminWhiteBoard("jin");
    }

    @Override
    public void initMenuBar(){
        JMenuBar menuBar= new JMenuBar();
        JMenu menu = new JMenu("File");

        JMenuItem m_New = new JMenuItem("New");
        JMenuItem m_Open = new JMenuItem("Open");
        JMenuItem m_Save = new JMenuItem("Save");
        JMenuItem m_SaveAs = new JMenuItem("SaveAs");
        JMenuItem m_Close = new JMenuItem("Close");

        menu.add(m_New);
        menu.addSeparator();
        menu.add(m_Open);
        menu.addSeparator();
        menu.add(m_Save);
        menu.addSeparator();
        menu.add(m_SaveAs);
        menu.addSeparator();
        menu.add(m_Close);

        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File("."));
        PicFileFilter jpgFilter = new PicFileFilter("jpg file",".jpg");
        PicFileFilter pngFilter = new PicFileFilter("png file",".png");
        jfc.addChoosableFileFilter(jpgFilter);
        jfc.addChoosableFileFilter(pngFilter);


        m_New.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chooseExtension;
                String[] options = {"Yes","No"};
                int res = JOptionPane.showOptionDialog(null, "Do you want to save the current file?",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (res == 0) {
                    int choose = jfc.showSaveDialog(null);

                    if (choose == JFileChooser.APPROVE_OPTION) {
                        try {
                            PicFileFilter pFilter = (PicFileFilter) jfc.getFileFilter();
                            chooseExtension = pFilter.getExtension();
                        } catch (Exception e1) {
                            chooseExtension = ".png";
                        }

                        file = jfc.getSelectedFile();
                        File newFile = null;

                        if (file.getAbsolutePath().toUpperCase().endsWith(chooseExtension.toUpperCase())) {
                            newFile = file;
                            dirPath = file.getAbsolutePath();
                        } else {
                            newFile = new File(file.getAbsolutePath() + chooseExtension);
                            dirPath = file.getAbsolutePath() + chooseExtension;
                        }

                        chooseExtension = chooseExtension.substring(1);
                        try {
                            ImageIO.write(paintBoard.save(),chooseExtension,newFile);
                            JOptionPane.showMessageDialog(null, "Save success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                dirPath = null;
                paintBoard.clear();
                paintBoard.repaint();
                paintBoard.synchronize();
            }
        });

        m_Open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chooseExtension;
                String[] options = {"Yes","No"};
                int res = JOptionPane.showOptionDialog(null, "Do you want to save the current file?",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (res == 0) {

                    int choose = jfc.showSaveDialog(null);

                    if (choose == JFileChooser.APPROVE_OPTION) {
                        try {
                            PicFileFilter pFilter = (PicFileFilter) jfc.getFileFilter();
                            chooseExtension = pFilter.getExtension();
                        } catch (Exception e1) {
                            chooseExtension = ".png";
                        }

                        file = jfc.getSelectedFile();
                        File newFile = null;

                        if (file.getAbsolutePath().toUpperCase().endsWith(chooseExtension.toUpperCase())) {
                            newFile = file;
                            dirPath = file.getAbsolutePath();
                        } else {
                            newFile = new File(file.getAbsolutePath() + chooseExtension);
                            dirPath = file.getAbsolutePath() + chooseExtension;
                        }

                        chooseExtension = chooseExtension.substring(1);
                        try {
                            ImageIO.write(paintBoard.save(),chooseExtension,newFile);
                            JOptionPane.showMessageDialog(null, "Save success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                int resOpen = jfc.showOpenDialog(null);
                if (resOpen == JFileChooser.APPROVE_OPTION){
                    dirPath = jfc.getSelectedFile().getAbsolutePath();
                    if (dirPath != null){
                        file = new File(dirPath);
                    } else {
                        return;
                    }
                    try{
                        BufferedImage bufImg = ImageIO.read(file);
                        paintBoard.load(bufImg);
                        paintBoard.synchronize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        m_Save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dirPath == null) {
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        jfc.setCurrentDirectory(new File("."));
                        String str;
                        try {
                            PicFileFilter filter = (PicFileFilter) jfc.getFileFilter();
                            str = filter.getExtension();
                        }
                        catch(Exception e2) {
                            str = ".png";
                        }
                        file = jfc.getSelectedFile();
                        File newFile = null;
                        try {
                            if (file.getAbsolutePath().toUpperCase().endsWith(str.toUpperCase())) {
                                newFile = file;
                                dirPath = file.getAbsolutePath();
                            } else {
                                newFile = new File(file.getAbsolutePath() + str);
                                dirPath = file.getAbsolutePath() + str;
                            }
                            str = str.substring(1);//remove the point
                            ImageIO.write(paintBoard.save(),str, newFile);
                            JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    return;
                }
                else {
                    file = new File(dirPath);
                }
                try {
                    String[] format = dirPath.split("\\.");
                    ImageIO.write(paintBoard.save(), format[format.length - 1],file);
                    JOptionPane.showMessageDialog(null, "save success", "Information", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        m_SaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chooseExtension;
                String[] options = {"Yes","No"};
                int res = JOptionPane.showOptionDialog(null, "Do you want to save the current file?",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (res == 0) {

                    int choose = jfc.showSaveDialog(null);

                    if (choose == JFileChooser.APPROVE_OPTION) {
                        try {
                            PicFileFilter pFilter = (PicFileFilter) jfc.getFileFilter();
                            chooseExtension = pFilter.getExtension();
                        } catch (Exception e1) {
                            chooseExtension = ".png";
                        }

                        file = jfc.getSelectedFile();
                        File newFile = null;

                        if (file.getAbsolutePath().toUpperCase().endsWith(chooseExtension.toUpperCase())) {
                            newFile = file;
                            dirPath = file.getAbsolutePath();
                        } else {
                            newFile = new File(file.getAbsolutePath() + chooseExtension);
                            dirPath = file.getAbsolutePath() + chooseExtension;
                        }

                        chooseExtension = chooseExtension.substring(1);
                        try {
                            ImageIO.write(paintBoard.save(),chooseExtension,newFile);
                            JOptionPane.showMessageDialog(null, "Save success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        m_Close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chooseExtension;
                String[] options = {"Yes","No"};
                int res = JOptionPane.showOptionDialog(null, "Do you want to save this file?",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (res == 0) {
                    int choose = jfc.showSaveDialog(null);

                    if (choose == JFileChooser.APPROVE_OPTION) {
                        try {
                            PicFileFilter pFilter = (PicFileFilter) jfc.getFileFilter();
                            chooseExtension = pFilter.getExtension();
                        } catch (Exception e1) {
                            chooseExtension = ".png";
                        }

                        file = jfc.getSelectedFile();
                        File newFile = null;

                        if (file.getAbsolutePath().toUpperCase().endsWith(chooseExtension.toUpperCase())) {
                            newFile = file;
                            dirPath = file.getAbsolutePath();
                        } else {
                            newFile = new File(file.getAbsolutePath() + chooseExtension);
                            dirPath = file.getAbsolutePath() + chooseExtension;
                        }

                        chooseExtension = chooseExtension.substring(1);
                        try {
                            ImageIO.write(paintBoard.save(),chooseExtension,newFile);
                            JOptionPane.showMessageDialog(null, "Save success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                try {
                    server.end();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
