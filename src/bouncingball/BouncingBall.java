package bouncingball;

import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class BouncingBall{
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try{
            UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[1].getClassName());
        }catch(Exception e){}
        
        JFrame frame = new JFrame();
        frame.setBounds(10,10,705,600);
        frame.add(new Panel());
        frame.setTitle("Bounce while you can!");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    }    
}
