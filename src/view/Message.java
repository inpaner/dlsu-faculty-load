package view;

import javax.swing.JOptionPane;

public class Message {
    public static void confirm(String message) {
        JOptionPane.showMessageDialog(null, 
            message, 
            "Information", 
            JOptionPane.INFORMATION_MESSAGE
            );
    }
    
    public static void deny(String message) {
        JOptionPane.showMessageDialog(null, 
            message,
            "Error", 
            JOptionPane.ERROR_MESSAGE
            );
    }
        
    public static boolean verified(String message) {
        int option = JOptionPane.showConfirmDialog(null, 
                        message,
                        "Verification", 
                        JOptionPane.YES_NO_OPTION
                        );
        return option == JOptionPane.OK_OPTION;
    }
    
    public static String input(String message, String defaultValue) {
        return JOptionPane.showInputDialog(message, defaultValue);
    }
    
}
