package client.cli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class MainDashboard extends JFrame{
	
	// Components
    private JDesktopPane desktopPane;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu bookingsMenu;
    private JMenu adminMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private JMenuItem logoutItem;
    private JMenuItem exitItem;
    private JMenuItem dashboardItem;
    private JMenuItem bookItem;
    private JMenuItem approvalItem;
    private JMenuItem cascadeItem;
    private JMenuItem closeAllItem;
    private JMenuItem aboutItem;
    private JLabel userLabel;
    
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
    
    public MainDashboard(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
        
        this.initializeComponents();
        this.addMenuItemsToMenus();
        this.addMenusToMenuBar();
        this.addUserLabelToMenuBar();
        this.addComponentsToWindow();
        this.setWindowProperties();
        this.registerListeners();
        this.openDashboard();
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
        
        this.adminMenu = new JMenu("Admin");
        this.adminMenu.setForeground(MENU_FG);
        this.adminMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        this.adminMenu.setVisible(currentRole.equals("Admin") || currentRole.equals("Technician"));
        
        this.viewMenu = new JMenu("View");
        this.viewMenu.setForeground(MENU_FG);
        this.viewMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.helpMenu = new JMenu("Help");
        this.helpMenu.setForeground(MENU_FG);
        this.helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        //Menu items
        this.logoutItem = new JMenuItem("Logout");
        this.logoutItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.exitItem = new JMenuItem("Exit");
        this.exitItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.dashboardItem = new JMenuItem("My Reservations (Dashboard)");
        this.dashboardItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.bookItem = new JMenuItem("Book a Lab Seat / Equipment");
        this.bookItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        this.approvalItem = new JMenuItem("Approval Queue");
        this.approvalItem.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
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
        fileMenu.add(logoutItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);
        
        //Bookings menu items
        bookingsMenu.add(dashboardItem);
        bookingsMenu.add(bookItem);
        
        //Admin menu items
        adminMenu.add(approvalItem);
        
        //View menu items
        viewMenu.add(cascadeItem);
        viewMenu.add(closeAllItem);
        
        //Help menu items
        helpMenu.add(aboutItem);
    }
    
    public void addMenusToMenuBar() {
        menuBar.add(fileMenu);
        menuBar.add(bookingsMenu);
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
    
    public void registerListeners() {
        this.logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        this.exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        this.dashboardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDashboard();
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
    public void openDashboard() {
        openInternalFrame(new Dashboard(currentUser, currentRole), 
                         DASHBOARD_OFFSET_X, DASHBOARD_OFFSET_Y);
    }
    
    
    public void openBookingFrame() {
        openInternalFrame(new Bookings(currentUser), 
                         BOOKING_OFFSET_X, BOOKING_OFFSET_Y);
    }
    
    public void openAdminFrame() {
        if (currentRole.equals("Admin") || currentRole.equals("Technician")) {
            openInternalFrame(new AdminApprovals(), 
                             ADMIN_OFFSET_X, ADMIN_OFFSET_Y);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Access denied. Admin or Technician role required.",
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
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(Login::new);
        }
    }
    
    /* Testing purposes
	public static void main(String[] args) {
		new MainDashboard("Tajh", "Student");
	}*/
}
