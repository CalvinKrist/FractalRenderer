package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

import javax.swing.*;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import util.RoundedButton;

public class AdminLauncher {
	
	private static JFrame loginFrame;
	
	public static void main(String[] args) {
		
		loginFrame = new JFrame("");
		JPanel loginPanel = new JPanel();
		
		initializeLoginPanel(loginPanel);
		
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setContentPane(loginPanel);
		loginFrame.pack();
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setResizable(false);
		loginFrame.setVisible(true);
		
	}
	
	private static void initializeLoginPanel(JPanel p) {
		p.setLayout(new BorderLayout());
		p.setBackground(Color.white);
		
		JTextField username = new JTextField(14);
		username.setFont(new Font("Arial", 12, 12));
		username.setBackground(new Color(244, 244, 244));
		JPasswordField password = new JPasswordField(14);
		password.setBackground(new Color(244, 244, 244));
		password.setFont(new Font("Arial", 12, 12));
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setFont(new Font("Arial", 12, 14));
		JLabel passwordLabel = new JLabel("Password:");	
		passwordLabel.setFont(new Font("Arial", 12, 14));
		
		JPanel container2 = new JPanel();
		container2.setBackground(Color.white);
		container2.add(usernameLabel);
		container2.add(username);
		p.add(container2, BorderLayout.NORTH);
		
		JPanel container1 = new JPanel();
		container1.setBackground(Color.white);
		container1 .add(passwordLabel);
		container1 .add(password);
		p.add(container1, BorderLayout.CENTER);
		
		RoundedButton login = new RoundedButton("Login");
		login.setFont(new Font("Arial", Font.BOLD, 24));
		login.setBackground(new Color(144, 167, 204));
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = username.getText();
				username.setText("");
				String pass = password.getText();
				password.setText("");
				
				DbxClientV2 client = new DbxClientV2(new DbxRequestConfig("dropbox", "en_US"), pass);
				if(login(client, user, pass)) {
					loginFrame.dispose();
					JFrame f = new JFrame("Admin Console");
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.setContentPane(new Display());
					f.pack();
					f.setLocationRelativeTo(null);
					f.setResizable(false);
					f.setVisible(true);
				} else {
					
				}
			}
		});
		JPanel container3 = new JPanel();
		container3.setBackground(Color.white);
		container3.setLayout(new BorderLayout());
		container3.add(login, BorderLayout.CENTER);
		JPanel padding1 = new JPanel();
		padding1.setBackground(Color.white);
		JPanel padding2 = new JPanel();
		padding2.setBackground(Color.white);
		JPanel padding3 = new JPanel();
		padding3.setBackground(Color.white);
		JPanel padding4 = new JPanel();
		padding4.setBackground(Color.white);
		container3.add(padding1, BorderLayout.NORTH);
		container3.add(padding2, BorderLayout.SOUTH);
		container3.add(padding3, BorderLayout.EAST);
		container3.add(padding4, BorderLayout.WEST);
		p.add(container3, BorderLayout.SOUTH);
		
	}
	
	private static boolean login(DbxClientV2 client, String username, String pass) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			client.files().download("/loginFile.txt").download(os);
			String aString = new String(os.toByteArray(),"UTF-8");
			return aString.contains(":" + username + ":");
		} catch(Exception e) {
			return false;
		}
	}

}
