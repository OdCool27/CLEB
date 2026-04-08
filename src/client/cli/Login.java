package client.cli;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import server.util.InputValidator;

import javax.swing.JPanel;
import javax.swing.JSeparator;

public class Login extends JFrame {
	
	//Email
	private JLabel emailLabel;
	private JTextField emailField;
	
	//Password
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	
	//Roles
	private JComboBox<String> roleComboBox;
	
	private final String[] role= {"Student", "Admin", "Technician"};
	
	//Buttons
	private JButton loginButton;
	private JButton clearButton;
	
	private JLabel statusLabel;
	
	private GridBagConstraints gbc;

	
	public Login() {
		setTitle("Campus Lab & Equiqment Booking");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		setSize(400, 500);
		
		initComponents();
		pack();
		
	}
	
	 private void initComponents() {
		 
	        //Outer panel with padding
	        JPanel mainPanel = new JPanel(new GridBagLayout());
	        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
	        mainPanel.setBackground(Color.WHITE);
	 
	        gbc = new GridBagConstraints();
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.insets = new Insets(6, 0, 6, 0); //top, left, bottom, right spacing
	        gbc.weightx = 1.0;
	 
	        int row = 0;
	 
	        //Title
	        JLabel logoLabel = new JLabel("CLEB", SwingConstants.CENTER);
	        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
	        logoLabel.setForeground(new Color(255, 119, 92));
	 
	        gbc.gridx = 0; 
	        gbc.gridy = row;
	        gbc.gridwidth = 2;
	        gbc.insets = new Insets(0, 0, 4, 0);
	        mainPanel.add(logoLabel, gbc);
	        row++;
	 
	        JLabel subtitleLabel = new JLabel("C-ampus  L-ab  E-quiment  B-ooking", SwingConstants.CENTER);
	        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
	        subtitleLabel.setForeground(Color.DARK_GRAY);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 20, 0);
	        mainPanel.add(subtitleLabel, gbc);
	        row++;
	 
	        //Separator
	        JSeparator separator = new JSeparator();
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 16, 0);
	        separator.setForeground(new Color(255, 119, 92));
	        mainPanel.add(separator, gbc);
	        row++;
	 
	        //Reset gridwidth back to 1 for label + field rows
	        gbc.gridwidth = 1;
	        gbc.insets    = new Insets(6, 0, 2, 0);
	 
	        //Email label
	        emailLabel = new JLabel("Email Address");
	        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
	        emailLabel.setForeground(Color.DARK_GRAY);
	 
	        gbc.gridx = 0; 
	        gbc.gridy = row;
	        gbc.gridwidth = 2;
	        mainPanel.add(emailLabel, gbc);
	        row++;
	 
	        //Email field
	        emailField = new JTextField(22);
	        emailField.setFont(new Font("SansSerif", Font.PLAIN, 13));
	        emailField.setPreferredSize(new Dimension(300, 34));
	        emailField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(new Color(200, 200, 200)),
	                BorderFactory.createEmptyBorder(4, 8, 4, 8)
	        ));
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 10, 0);
	        mainPanel.add(emailField, gbc);
	        row++;
	 
	        //Password label
	        passwordLabel = new JLabel("Password");
	        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
	        passwordLabel.setForeground(Color.DARK_GRAY);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(6, 0, 2, 0);
	        mainPanel.add(passwordLabel, gbc);
	        row++;
	 
	        //Password field
	        passwordField = new JPasswordField(22);
	        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
	        passwordField.setPreferredSize(new Dimension(300, 34));
	        passwordField.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(new Color(200, 200, 200)),
	                BorderFactory.createEmptyBorder(4, 8, 4, 8)
	        ));
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 10, 0);
	        mainPanel.add(passwordField, gbc);
	        row++;
	 
	        //Sign is as Role label
	        JLabel roleLabel = new JLabel("Sign in as");
	        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
	        roleLabel.setForeground(Color.DARK_GRAY);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(6, 0, 2, 0);
	        mainPanel.add(roleLabel, gbc);
	        row++;
	 
	        //Role combo box
	        roleComboBox = new JComboBox<>(role);
	        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
	        roleComboBox.setPreferredSize(new Dimension(300, 34));
	        roleComboBox.setBackground(Color.WHITE);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 20, 0);
	        mainPanel.add(roleComboBox, gbc);
	        row++;
	 
	        //Buttons (Login, Clear side by side)
	        JPanel buttonPanel = new JPanel(new GridBagLayout());
	        buttonPanel.setBackground(Color.WHITE);
	 
	        GridBagConstraints btnGbc = new GridBagConstraints();
	        btnGbc.fill = GridBagConstraints.HORIZONTAL;
	        btnGbc.weightx = 1.0;
	        btnGbc.insets = new Insets(0, 0, 0, 6);
	 
	        //Sign-In Button
	        loginButton = new JButton("Sign In");
	        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
	        loginButton.setBackground(new Color(255, 119, 92));
	        loginButton.setForeground(Color.WHITE);
	        loginButton.setFocusPainted(false);
	        loginButton.setBorderPainted(false);
	        loginButton.setPreferredSize(new Dimension(0, 36));
	        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	 
	        btnGbc.gridx = 0; 
	        btnGbc.gridy = 0;
	        buttonPanel.add(loginButton, btnGbc);
	 
	        //Clear Button
	        clearButton = new JButton("Clear");
	        clearButton.setFont(new Font("SansSerif", Font.BOLD, 13));
	        clearButton.setBackground(new Color(197, 202, 233));
	        clearButton.setForeground(Color.DARK_GRAY);
	        clearButton.setFocusPainted(false);
	        clearButton.setPreferredSize(new Dimension(0, 36));
	        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	 
	        btnGbc.gridx = 1;
	        btnGbc.insets = new Insets(0, 6, 0, 0);
	        buttonPanel.add(clearButton, btnGbc);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 12, 0);
	        mainPanel.add(buttonPanel, gbc);
	        row++;
	 
	        //Separator
	        separator = new JSeparator();
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 4, 0);
	        separator.setForeground(new Color(255, 119, 92));
	        mainPanel.add(separator, gbc);
	        row++;
	        
	        //Status label (shows validation / error messages)
	        statusLabel = new JLabel(" ", SwingConstants.CENTER);
	        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
	        statusLabel.setForeground(Color.RED);
	 
	        gbc.gridy = row;
	        gbc.insets = new Insets(0, 0, 4, 0);
	        mainPanel.add(statusLabel, gbc);
	        row++;
	 
	        //Separator
	        separator = new JSeparator();
	        gbc.gridy = row;
	        gbc.insets = new Insets(4, 0, 4, 0);
	        separator.setForeground(new Color(255, 119, 92));
	        mainPanel.add(separator, gbc);
	        row++;
	        
	        
	        //Add listeners 
	        loginButton.addActionListener(e -> handleLogin());
	        clearButton.addActionListener(e -> handleClear());
	 
	        /* Allow pressing Enter on password field to trigger login
	        passwordField.addKeyListener(new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent e) {
	                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	                    handleLogin();
	                }
	            }
	        });*/
	        
	 
	        add(mainPanel);
	    }
	 
	//Event Handlers
	 private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
 
        //Validate email
        if (!InputValidator.validateEmail(email)) {
            setStatus("Please enter a valid email address.", Color.RED);
            emailField.requestFocus();
            return;
        }
 
        //Validate password
        if (!InputValidator.validatePassword(password)) {
            setStatus("Please enter a valid password.", Color.RED);
            passwordField.requestFocus();
            return;
        }
 
        //If both pass, proceed
        // TODO: Send login request to server via socket (Phase 2)
        setStatus("Signing in as " + role + "...", new Color(26, 39, 68));
 

        dispose(); //Close the login window
        SwingUtilities.invokeLater(() -> new MainDashboard(email, role));
    }
 
    private void handleClear() {
        emailField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        statusLabel.setText(" ");
        emailField.requestFocus();
    }
 
    // Helper
    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
	 

	public static void main(String[] args) {
		//new Login();
		SwingUtilities.invokeLater(Login::new);
	}
}
