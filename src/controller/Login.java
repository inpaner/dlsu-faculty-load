package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import model.DBUtil;
import model.Initializer;
import model.User;

import view.LoginPanel;
import view.Message;
public class Login {
	private LoginPanel panel;
	private String username;
	private String password;
	private final String FILENAME = "dbdetails";
	
	protected Login() {
	    load();
		panel = new LoginPanel();
		panel.addAcceptListener(login());
		panel.addSetupListener(new Save());
		Driver.setPanel(panel);
    }
	
    private ActionListener login() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            User user = new User(panel.username(), panel.password());
            if (user.isValid()) {
                DBUtil.setup(username, password);
                new Initializer();
                new MainMenu();
            }
            else {
                Message.deny("Wrong username or password");
            }
        }            
        };
    }
    
    private class Save implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            username = Message.input("DB username: ", username);
            password = Message.input("DB password: ", password);
            if (username == null)
                username = "";
            if (password == null)
                password = "";
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(FILENAME));
                out.write(username);
                out.newLine();
                out.write(password);
                out.close();
            }
            catch (IOException ex) {
                
            }
            
        }
        
    }
    
    public void load() {
        try {
            
            BufferedReader in = new BufferedReader(new FileReader(FILENAME));
            username = in.readLine();
            password = in.readLine();
            in.close();
        }
        catch (IOException e) {
        }
    }
}
