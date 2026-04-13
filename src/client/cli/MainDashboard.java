package cli;

import controller.ClientController;
import dto.EmployeeDTO;
import dto.StudentDTO;
import dto.UserDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import util.InputValidator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainDashboard extends JFrame{
	private static final Logger logger = LogManager.getLogger(MainDashboard.class);
	
	// Components
    private JDesktopPane desktopPane;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu bookingsMenu;
    private JMenu operationsMenu;
    private JMenu adminMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private JMenuItem logoutItem;
    private JMenuItem exitItem;
    private JMenuItem changePasswordItem;
    private JMenuItem dashboardItem;
    private JMenuItem bookItem;
    private JMenuItem approvalItem;
    private JMenuItem inventoryItem;
    private JMenuItem userManagementItem;
    private JMenuItem cascadeItem;
    private JMenuItem closeAllItem;
    private JMenuItem aboutItem;
    private JLabel userLabel;
    private UserDTO userDTO;
    private final ClientController clientController;
    
    //Data fields
    private String currentUser;
    private String currentRole;
    
    //Constants
    private static final int DEFAULT_WIDTH = 1100;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 600;
    private static final int DASHBOARD_OFFSET_X = 20;
    private static final int DASHBOARD_OFFSET_Y = 20;
    private static final int BOOKING_OFFSET_X = 60;
    private static final int BOOKING_OFFSET_Y = 60;
    private static final int ADMIN_OFFSET_X = 80;
    private static final int ADMIN_OFFSET_Y = 80;
    private static final int CASCADE_OFFSET = 30;
    
    //Colors
    private static final Color MENU_BAR_BG = new Color(255, 119, 92);
    private static final Color MENU_FG = new Color(255, 255, 255);
    private static final Color DESKTOP_BG = new Color(255, 255, 255);
    private static final Color USER_LABEL_FG = new Color(255, 255, 255);
    
    public MainDashboard(UserDTO userDTO, ClientController clientController) {
        logger.info("Initializing MainDashboard for user: {} with role: {}", userDTO.getFirstName(), userDTO.getRole());
        this.userDTO = userDTO;
        this.clientController = clientController;
        this.currentUser = userDTO.getFirstName();
        this.currentRole = userDTO.getRole().name();
        
        this.initializeComponents();
        this.addMenuItemsToMenus();
        this.addMenusToMenuBar();
        this.addUserLabelToMenuBar();
        this.addComponentsToWindow();
        this.setWindowProperties();
        this.registerListeners(userDTO);
        this.openDashboard(userDTO);
        //this.openBookingFrame();
    }
    
    public void initializeComponents() {
        //Desktop pane
        this.desktopPane = new JDesktopPane();
        this.desktopPane.setBackground(DESKTOP_BG);
        
        //Menu bar
        this.menuBar = new JMenuBar();
        this.menuBar.setBackground(MENU_BAR_BG);
        this.menuBar.setBorder(BorderFactory.createEmptyBorder());
        
        //Menus
        this.fileMenu = new JMenu("File");
        this.fileMenu.setForeground(MENU_FG);
        this.fileMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.bookingsMenu = new JMenu("Bookings");
        this.bookingsMenu.setForeground(MENU_FG);
        this.bookingsMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.operationsMenu = new JMenu("Operations");
        this.operationsMenu.setForeground(MENU_FG);
        this.operationsMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        this.operationsMenu.setVisible(isTechnician() || isAdministrator());

        this.adminMenu = new JMenu("Admin");
        this.adminMenu.setForeground(MENU_FG);
        this.adminMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        this.adminMenu.setVisible(isAdministrator());
        
        this.viewMenu = new JMenu("View");
        this.viewMenu.setForeground(MENU_FG);
        this.viewMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.helpMenu = new JMenu("Help");
        this.helpMenu.setForeground(MENU_FG);
        this.helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        //Menu items
        this.logoutItem = new JMenuItem("Logout");
        this.logoutItem.setFont(new Font("SansSerif", Font.PLAIN, 13));

        this.changePasswordItem = new JMenuItem("Change Password");
        this.changePasswordItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.exitItem = new JMenuItem("Exit");
        this.exitItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.dashboardItem = new JMenuItem("My Reservations (Dashboard)");
        this.dashboardItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.bookItem = new JMenuItem("Book a Lab Seat / Equipment");
        this.bookItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.approvalItem = new JMenuItem("Approval Queue");
        this.approvalItem.setFont(new Font("SansSerif", Font.PLAIN, 13));

        this.inventoryItem = new JMenuItem("Inventory & Resources");
        this.inventoryItem.setFont(new Font("SansSerif", Font.PLAIN, 13));

        this.userManagementItem = new JMenuItem("User Management");
        this.userManagementItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.cascadeItem = new JMenuItem("Cascade Windows");
        this.cascadeItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.closeAllItem = new JMenuItem("Close All Windows");
        this.closeAllItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.aboutItem = new JMenuItem("About CLEB");
        this.aboutItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        //User label
        this.userLabel = new JLabel();
        this.userLabel.setForeground(USER_LABEL_FG);
        this.userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
    
    public void addMenuItemsToMenus() {
        //File menu items
        fileMenu.add(changePasswordItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(logoutItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);
        
        //Bookings menu items
        bookingsMenu.add(dashboardItem);
        if (canCreateReservation()) {
            bookingsMenu.add(bookItem);
        }
        
        //Operations menu items
        operationsMenu.add(inventoryItem);
        if (isTechnician() || isAdministrator()) {
            operationsMenu.add(approvalItem);
        }

        //Admin menu items
        if (isAdministrator()) {
            adminMenu.add(userManagementItem);
        }
        
        //View menu items
        viewMenu.add(cascadeItem);
        viewMenu.add(closeAllItem);
        
        //Help menu items
        helpMenu.add(aboutItem);
    }
    
    public void addMenusToMenuBar() {
        menuBar.add(fileMenu);
        menuBar.add(bookingsMenu);
        menuBar.add(operationsMenu);
        menuBar.add(adminMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
    }
    
    public void addUserLabelToMenuBar() {
        menuBar.add(Box.createHorizontalGlue());
        userLabel.setText(currentUser + "  |  " + currentRole + "   ");
        menuBar.add(userLabel);
    }
    
    public void addComponentsToWindow() {
        this.add(desktopPane, BorderLayout.CENTER);
    }
    
    public void setWindowProperties() {
        this.setTitle("CLEB - Campus Lab & Equipment Booking  [" + currentRole + "]");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.setLocationRelativeTo(null);
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }
    
    public void registerListeners(UserDTO userDTO) {
        this.logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        this.changePasswordItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangePassword();
            }
        });
        
        this.exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.shutdown();
                System.exit(0);
            }
        });
        
        this.dashboardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDashboard(userDTO);
            }
        });
        
        
        this.bookItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookingFrame();
            }
        });
        
        this.approvalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminFrame();
            }
        });

        this.inventoryItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInventoryFrame();
            }
        });

        this.userManagementItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserManagementFrame();
            }
        });
        
        this.cascadeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cascadeWindows();
            }
        });
        
        this.closeAllItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAllWindows();
            }
        });
        
        this.aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainDashboard.this,
                		"Campus Lab & Equipment Booking System (CLEB)\n" +
                			    "University of Technology, Jamaica\n" +
                			    "Faculty of Engineering & Computing (FENC)\n" +
                			    "\n" +
                			    "CLEB allows students and staff to book and manage\n" +
                			    "laboratory seats and equipment across FENC at UTech, Ja.\n" +
                			    "\n" +
                			    "Supported Labs:\n" +
                			    "  - SCIT Software Engineering Lab (40 seats)\n" +
                			    "  - SCIT Networking & Systems Lab (32 seats)\n" +
                			    "  - SOE Industrial & Mechanical Engineering Lab (24 seats)\n" +
                			    "\n" +
                			    "User Roles:\n" +
                			    "  - Student    \nCreate and view reservations\n" +
                			    "  - Technician \nReview and manage equipment bookings\n" +
                			    "  - Admin      \nFull system access and approval control\n" +
                			    "\n",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    // Business methods
    public void openDashboard(UserDTO userDTO) {
        logger.debug("Opening Dashboard internal frame");
        openInternalFrame(new Dashboard(userDTO, clientController),
                         DASHBOARD_OFFSET_X, DASHBOARD_OFFSET_Y);
    }
    
    
    public void openBookingFrame() {
        logger.debug("Opening Bookings internal frame");
        openInternalFrame(new Bookings(userDTO, clientController),
                         BOOKING_OFFSET_X, BOOKING_OFFSET_Y);
    }
    
    public void openAdminFrame() {
        if (isTechnician() || isAdministrator()) {
            logger.debug("Opening AdminApprovals internal frame");
            openInternalFrame(new AdminApprovals(userDTO, clientController), 
                             ADMIN_OFFSET_X, ADMIN_OFFSET_Y);
        } else {
            logger.warn("Unauthorized access attempt to Admin Frame by user: {} with role: {}", currentUser, currentRole);
            JOptionPane.showMessageDialog(this,
                    "Access denied. Admin or Technician role required.",
                    "Access Control", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void openInventoryFrame() {
        if (isTechnician() || isAdministrator()) {
            logger.debug("Opening InventoryManagement internal frame");
            openInternalFrame(new InventoryManagement(userDTO, clientController),
                    BOOKING_OFFSET_X + 30, BOOKING_OFFSET_Y + 30);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Access denied. Technician or Administrator role required.",
                    "Access Control", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void openUserManagementFrame() {
        if (isAdministrator()) {
            logger.debug("Opening UserManagement internal frame");
            openInternalFrame(new UserManagement(userDTO, clientController),
                    ADMIN_OFFSET_X + 30, ADMIN_OFFSET_Y + 30);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Access denied. Administrator role required.",
                    "Access Control", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void openInternalFrame(JInternalFrame frame, int x, int y) {
        frame.setLocation(x, y);
        desktopPane.add(frame);
        frame.toFront();
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
    
    private void cascadeWindows() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        int offset = 0;
        for (JInternalFrame frame : frames) {
            frame.setLocation(offset, offset);
            frame.toFront();
            offset += CASCADE_OFFSET;
        }
    }
    
    private void closeAllWindows() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
    }
    
    private void handleLogout() {
        logger.info("User {} initiated logout", currentUser);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Logout Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            logger.info("User {} logged out", currentUser);
            clientController.shutdown();
            this.dispose();
            SwingUtilities.invokeLater(Login::new);
        } else {
            logger.debug("Logout cancelled by user {}", currentUser);
        }
    }

    private void handleChangePassword() {
        JPasswordField newPasswordField = new JPasswordField(18);
        JPasswordField confirmPasswordField = new JPasswordField(18);
        JPanel panel = new JPanel(new GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(4, 4, 4, 4);
        panel.add(new JLabel("New Password"), gbc);
        gbc.gridy = 1;
        panel.add(newPasswordField, gbc);
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password"), gbc);
        gbc.gridy = 3;
        panel.add(confirmPasswordField, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!InputValidator.validatePassword(newPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Password must be 8-64 characters and include uppercase, lowercase, a number, and a special character.",
                    "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match.",
                    "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RequestEnvelope<UserDTO> request = new RequestEnvelope<>(
                java.util.UUID.randomUUID(),
                "UPDATE_USER",
                buildUserUpdatePayload(newPassword)
        );
        clientController.sendRequest(request);
        ResponseEnvelope<Boolean> response = clientController.receiveResponse();

        if (response != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
            JOptionPane.showMessageDialog(this, "Password updated successfully.", "Password Changed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    response != null ? response.getMessage() : "Failed to update password.",
                    "Password Change Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private UserDTO buildUserUpdatePayload(String password) {
        if (userDTO instanceof StudentDTO studentDTO) {
            return new StudentDTO(
                    studentDTO.getUserID(),
                    studentDTO.getFirstName(),
                    studentDTO.getLastName(),
                    studentDTO.getEmail(),
                    studentDTO.getRole(),
                    studentDTO.isActive(),
                    studentDTO.getLastUpdated(),
                    password,
                    studentDTO.getStudentID(),
                    studentDTO.getFaculty(),
                    studentDTO.getSchool()
            );
        }

        EmployeeDTO employeeDTO = (EmployeeDTO) userDTO;
        return new EmployeeDTO(
                employeeDTO.getUserID(),
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getEmail(),
                employeeDTO.getRole(),
                employeeDTO.isActive(),
                employeeDTO.getLastUpdated(),
                password,
                employeeDTO.getEmpID(),
                employeeDTO.getJobTitle()
        );
    }

    private boolean canCreateReservation() {
        return "STUDENT".equalsIgnoreCase(currentRole) || isAdministrator();
    }

    private boolean isTechnician() {
        return "TECHNICIAN".equalsIgnoreCase(currentRole);
    }

    private boolean isAdministrator() {
        return "ADMINISTRATOR".equalsIgnoreCase(currentRole);
    }
    
    /* Testing purposes
	public static void main(String[] args) {
		new MainDashboard("Tajh", "Student");
	}*/
}

