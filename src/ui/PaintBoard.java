package ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import remote.IWhiteBoard;
import shape.Shapes;


public class PaintBoard extends Canvas implements Serializable {
    //paint-type
    private String type = "freedraw";
    //axis
    private int x,y;
    //color
    private Color selectColor = Color.BLACK;
    private Stroke selectStroke = new BasicStroke(1.0f);
    private IWhiteBoard server;
    private BufferedImage image;

    //Points record the trace of freeDraw and erase
    private static ArrayList<Point> points = new ArrayList<Point>();
    //shapeList record all shapes in whiteboard
    private static ArrayList<Shapes> shapeList = new ArrayList<Shapes>();

    //setters
    public void setServer(IWhiteBoard server) {
        this.server = server;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setStroke(Stroke stroke) {
        this.selectStroke = stroke;
    }
    public void setColor(Color color){
        this.selectColor = color;
    }

    // for function to create a new paint board
    public void clear() {
        shapeList = new ArrayList<Shapes>();
        image = null;
    }
    //save the image
    public BufferedImage save() {
        Dimension imageSize = this.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,imageSize.height,BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = image.createGraphics();//draw the image
        this.paint(graphics);
        graphics.dispose();
        return image;
    }
    //load the image from other resource
    public void load(BufferedImage image) {
        clear();
        repaint();
        this.image = image;
    }

    //This paint method is to keep shapes on graphic
    public void paint(Graphics g){
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);
        if(image != null) {
            g.drawImage(image, 0, 0, this);
        }
        for (int i = 0; i < shapeList.size(); i++) {
            if (shapeList.get(i) == null) {break;}
            shapeList.get(i).rePaint(g);
        }
    }
    //draw a image
    public void draw(int x,int y,int x1,int y1,String type) {
        Graphics2D g = (Graphics2D)getGraphics();
        g.setColor(selectColor);
        g.setStroke(selectStroke);
        if(type.equals("line")) {
            shapeList.add(new Shapes(g,x,y,x1,y1,type,selectColor,selectStroke));
            g.drawLine(x, y, x1, y1);
        }
        else {
            int height = Math.abs(y1 - y);
            int width = Math.abs(x1 - x);
            if(type.equals("rectangle")) {
                shapeList.add(new Shapes(g,x,y,x1,y1,type,selectColor,selectStroke));
                g.drawRect(Math.min(x, x1),Math.min(y, y1), width, height);
            }
            if(type.equals("oval")) {
                shapeList.add(new Shapes(g,x,y,x1,y1,type,selectColor,selectStroke));
                g.drawOval(Math.min(x, x1),Math.min(y, y1), width, height);
            }
            if(type.equals("freedraw")) {
                ArrayList<Point> s = new ArrayList<>();
                s.addAll(points);
                shapeList.add(new Shapes(g,s,type,selectColor,selectStroke));
            }
            if(type.equals("erase")) {
                ArrayList<Point> s = new ArrayList<>();
                s.addAll(points);
                shapeList.add(new Shapes(g,s,type,Color.white,selectStroke));
            }
            if(type.equals("circle")) {
                shapeList.add(new Shapes(g,x,y,x1,y1,type,selectColor,selectStroke));
                int round = Math.max(width, height);
                g.drawOval(Math.min(x, x1),Math.min(y, y1), round,round);
            }
            points.clear();
        }
    }
    //synchronize the board with other users
    public void synchronize() {
        try {
            BufferedImage image = save();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image,"png", out);
            byte[] b = out.toByteArray();
            server.draw(b);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "the manager has left the room", "error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PaintBoard() {
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                //press,get start position
                x = e.getX();
                y = e.getY();
                if(server!=null&&type.equals("text")){
                    Graphics2D g = (Graphics2D)getGraphics();
                    String input;
                    input = JOptionPane.showInputDialog(
                            "Please input the text you want!");
                    if(input != null) {
                        g.setColor(selectColor);
                        g.drawString(input,x,y);
                        shapeList.add(new Shapes(g,x,y,input,type,selectColor));
                        synchronize();
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(server != null) {
                    int x1 = e.getX();
                    int y1 = e.getY();
                    String s = (x + " " + y + " " + x1 + " " + y1 + " " + type);
                    draw(x, y, x1, y1, type);
                    synchronize();
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(server != null) {
                    int x2 = e.getX();
                    int y2 = e.getY();
                    int x3;
                    int y3;
                    Graphics2D g = (Graphics2D)getGraphics();
                    g.setColor(selectColor);
                    g.setStroke(selectStroke);
                    //freeDraw and erase
                    if(type.equals("freedraw")){
                        if(points.size()!=0){
                            x3=points.get(points.size()-1).x;
                            y3=points.get(points.size()-1).y;}
                        else{
                            x3=x;
                            y3=y;
                        }
                        g.drawLine(x3,y3,x2,y2);
                        points.add(new Point(x2,y2));
                    }
                    else if(type.equals("erase")){
                        if(points.size()!=0){
                            x3=points.get(points.size()-1).x;
                            y3=points.get(points.size()-1).y;}
                        else{
                            x3=x;
                            y3=y;
                        }
                        Color c = new Color(selectColor.getRGB());
                        g.setColor(Color.WHITE);
                        g.drawLine(x3,y3,x2,y2);
                        points.add(new Point(x2,y2));
                        g.setColor(c);
                    }
                    //Other shapes
                    else {
                        if (type.equals("line")) {
                            g.drawLine(x, y, x2, y2);
                        }
                        else {
                            int height = Math.abs(y2 - y);
                            int width = Math.abs(x2 - x);
                            if (type.equals("rectangle")) {
                                g.drawRect(Math.min(x, x2), Math.min(y, y2), width, height);
                            }
                            if (type.equals("oval")) {
                                g.drawOval(Math.min(x, x2), Math.min(y, y2), width, height);
                            }if (type.equals("circle")) {
                                int round = Math.max(width, height);
                                g.drawOval(Math.min(x, x2),Math.min(y, y2), round,round);
                            }
                        }
                        repaint();
                    }
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }
}
