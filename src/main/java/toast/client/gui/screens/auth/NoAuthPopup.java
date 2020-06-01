package toast.client.gui.screens.auth;

import javax.swing.*;
import java.awt.*;

public class NoAuthPopup {
    public static void createWindow(){
        System.out.println("Creating Window");
        JFrame f = new JFrame("Error");

        JLabel textLabel = new JLabel("Error: This account hasn't been authorized!", SwingConstants.CENTER);
        textLabel.setPreferredSize(new Dimension(300, 100));
        f.getContentPane().add(textLabel, BorderLayout.CENTER);

        f.setAlwaysOnTop(true);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.pack();
        f.setVisible(true);
        System.out.println("Window should of been made!");
    }
}
