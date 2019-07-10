package bouncingball;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;


public class Panel extends JPanel implements KeyListener, ActionListener {
    
    private int xPlayer = 270, xBall = 220, yBall = 50, count = 0, previousBest;
    private double xDirection = 1, yDirection = 1;
    private boolean gamePlay = false;
    private final javax.swing.Timer timer;
    
    public Panel() throws SQLException, ClassNotFoundException{
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new javax.swing.Timer(1, this);
        timer.start();
        
        final Timer time = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                if(gamePlay)
                    count++;
            }
        };
        time.scheduleAtFixedRate(task, 1000, 1000);
        
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
        previousBest = conn.createStatement().executeQuery("select * from SCORE;").getInt("score");
        conn.close();
    }
    
    public void paint(Graphics g){
        try {
            g.setColor(Color.black);
            g.fillRect(1, 1, 697, 597);
            
            g.setColor(Color.green);
            g.fillRect(0, 0, 3, 597);
            g.fillRect(0, 0, 697, 3);
            g.fillRect(696, 0, 3, 597);
            
            g.fillRect(xPlayer, 550, 200, 25);
            
            g.fillOval(xBall, yBall, 27, 27);
            
            g.setFont(new Font("times",Font.BOLD,30));
            g.drawString("Your time: "+count+"s", 465, 30);
            
            g.setFont(new Font("times",Font.BOLD,15));
            g.drawString("Best time: "+previousBest+"s", 470, 55);
            
            g.setFont(new Font("times",Font.BOLD,15));
            g.drawString("(Move the paddle with <- and ->)", 200, 17);
            
            g.setFont(new Font("times",Font.BOLD,15));
            g.drawString("© Marius Jonikas 2019", 5, 17);
            
            if(!gamePlay){
                g.setFont(new Font("times",Font.BOLD,15));
                g.drawString("(Press space to play)", 300, 280);
            }
            
            if(yBall>580){
                gamePlay = false;
                g.setFont(new Font("times",Font.BOLD,50));
                g.drawString("GAME OVER!", 195, 200);
                
                g.setFont(new Font("times",Font.BOLD,20));
                g.drawString("You lasted "+count+" seconds", 270, 250);
         
                if(count>previousBest){
                    g.setFont(new Font("times",Font.BOLD,70));
                    g.drawString("NEW BEST TIME!!!", 30, 130);
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
                    PreparedStatement prep = conn.prepareStatement("update SCORE set score = ? where score = ?;");
                    prep.setInt(1, count);
                    prep.setInt(2, previousBest);
                    prep.executeUpdate();
                    conn.close();
                }
            }
            g.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE&&!gamePlay){
            if(count>previousBest)
                previousBest = count;
            xBall = 220; yBall = 50; count = 0;
            xDirection = 1; yDirection = 1;
            gamePlay = true;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            if(xPlayer>500)
                xPlayer = 500;
            else
                xPlayer+=100;
        
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
            if(xPlayer<0)
                xPlayer = 0;
            else
                xPlayer-=100;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if(gamePlay){
            if(Math.abs(yDirection)<50)
                yDirection*=1.0001;
            else
                xDirection*=1.00001;
            xBall+=xDirection;
            yBall+=yDirection;
            
            Random r = new Random();
            if(r.nextBoolean())
                xDirection*=1.01;   
            else if(Math.abs(xDirection)>1)
                xDirection/=1.01; 
            
            if(xBall<0)
                xDirection = -xDirection;
            if(xBall>670)
                xDirection = -xDirection;
            if(yBall<0)
                yDirection = -yDirection;
            if(new Rectangle(xBall,yBall,27,27).intersects(new Rectangle(xPlayer,550,200,25)))
                yDirection = -yDirection;
        }
        repaint();
    }
}