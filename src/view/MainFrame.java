package view;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.*;


public class MainFrame extends JFrame {
    private JComponent component;

    public MainFrame() {
        final String look = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(look);
                } 
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        setTitle("FLASK 0.12");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }

    public void closeWindow() {
        WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
    }

    public void setPanel(final JComponent component) {
        this.getContentPane().removeAll();
        this.component = component;
        Thread controller = new setThread();
        controller.start();
        
    }
    
    private class setThread extends Thread {
        @Override
        public void run() {
            SwingUtilities.invokeLater(new setInvoke());
        }
    }

    private class setInvoke implements Runnable {
        @Override
        public void run() {
            getContentPane().add(component);
            invalidate();
            validate();
        }
    }
    
    

}
