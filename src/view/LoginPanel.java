package view;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends MigPanel{

	private JTextField username;
    private JPasswordField password;
    private JButton accept;
    private JButton setup;
    
    public LoginPanel(){
        initializeComponents();
        addComponents();      
    }
    
    public String username(){
        return username.getText();
    }
    
    public String password(){
        // bad
    	return password.getText();
    }
    
    private void initializeComponents(){
    	username = new JTextField(15);
    	username.requestFocus();
    	password = new JPasswordField(15);
    	
    	username.setText("user");
    	password.setText("password");
    	
    	accept = new JButton("Login");
        setup = new JButton("Setup DB");
    }
    
    private void addComponents(){
    	add(createLabel("Username: "));
        add(username, "wrap");
        add(createLabel("Password: "));
        add(password, "wrap para");
        add(accept, "skip, span, split");
        add(setup);
        
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    } 
    
    public void addSetupListener(ActionListener listener) {
        setup.addActionListener(listener);
    } 
}
