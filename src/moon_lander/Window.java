package moon_lander;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Creates frame and set its properties.
 * 
 * @author www.gametutorial.net
 */

public class Window extends JFrame{
        
    private Window()
    {
        this.setTitle("Moon Lander");
        this.setSize(2000, 1500);
        this.setLocationRelativeTo(null);
        this.setResizable(false);        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        this.setContentPane(new Framework());        
        this.setVisible(true);        
    }

	public static void main(String[] args)
    {
        // Use the event dispatch thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window();
            }
        });
    }
}
