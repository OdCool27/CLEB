package client.cli;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JInternalFrame{
	 // Components
    private JLabel activeCountLabel;
    private JLabel pendingCountLabel;
    private JLabel completedCountLabel;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JPanel statsPanel;
    private JLabel tableTitle;
    private JPanel tablePanel;
    private JButton refreshBtn;
    private JPanel btnRow;
    private JScrollPane scrollPane;
    
    // Data fields
    private String currentUser;
    private String currentRole;
    
    // Constants
    private static final int FRAME_WIDTH = 750;
    private static final int FRAME_HEIGHT = 520;
    private static final int BORDER_SIZE = 16;
    private static final int STATS_INSET_BOTTOM = 16;
    private static final int TABLE_INSET_BOTTOM = 10;
    private static final int CARD_PADDING_TOP = 12;
    private static final int CARD_PADDING_BOTTOM = 12;
    private static final int CARD_PADDING_LEFT = 16;
    private static final int CARD_PADDING_RIGHT = 16;
    private static final int CARD_INSET_RIGHT = 10;
    private static final int STATS_LABEL_FONT_SIZE = 26;
    private static final int STATS_TEXT_FONT_SIZE = 11;
    private static final int TABLE_ROW_HEIGHT = 26;
    private static final int TABLE_FONT_SIZE = 12;
    private static final int COLUMN_ID_WIDTH = 80;
    private static final int COLUMN_RESOURCE_WIDTH = 280;
    private static final int COLUMN_DATETIME_WIDTH = 160;
    private static final int COLUMN_STATUS_WIDTH = 100;
    
    // Colors
    private static final Color HEADER_COLOR = new Color(26, 39, 68);
    private static final Color CARD_ACTIVE_BG = new Color(165, 214, 167);
    private static final Color CARD_PENDING_BG = new Color(255, 245, 157);
    private static final Color CARD_COMPLETED_BG = new Color(129, 212, 250);
    private static final Color CARD_BORDER_COLOR = Color.black;
    private static final Color TABLE_HEADER_BG = new Color(255, 119, 92);
    private static final Color TABLE_SELECTION_BG = new Color(210, 225, 245);
    private static final Color TABLE_GRID_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BG = new Color(255, 119, 92);
    private static final Color WELCOME_FG = new Color(26, 39, 68);
    private static final Color VALUE_LABEL_FG = new Color(26, 39, 68);
    private static final Color CARD_LABEL_FG = Color.DARK_GRAY;
    
    // Fonts
    private static final Font WELCOME_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font TABLE_TITLE_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font VALUE_FONT = new Font("SansSerif", Font.BOLD, STATS_LABEL_FONT_SIZE);
    private static final Font CARD_LABEL_FONT = new Font("SansSerif", Font.PLAIN, STATS_TEXT_FONT_SIZE);
    private static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, TABLE_FONT_SIZE);
    private static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, TABLE_FONT_SIZE);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, TABLE_FONT_SIZE);
    
    public Dashboard(String username, String role) {
        super("My Reservations - Dashboard", true, true, true, true);
        this.currentUser = username;
        this.currentRole = role;
        
        this.initializeComponents();
        this.setupWelcomeLabel();
        this.setupStatsPanel();
        this.setupTableTitle();
        this.setupTablePanel();
        this.setupRefreshButton();
        this.setupMainPanel();
        this.setWindowProperties();
        this.loadSampleData();
    }
    
    public void initializeComponents() {
        // Main panel
        this.mainPanel = new JPanel(new GridBagLayout());
        this.mainPanel.setBackground(Color.WHITE);
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        
        // Labels
        this.activeCountLabel = new JLabel("0", SwingConstants.CENTER);
        this.pendingCountLabel = new JLabel("0", SwingConstants.CENTER);
        this.completedCountLabel = new JLabel("0", SwingConstants.CENTER);
        
        this.welcomeLabel = new JLabel();
        this.tableTitle = new JLabel("Recent Reservations");
        
        // Stats panel
        this.statsPanel = new JPanel(new GridBagLayout());
        this.statsPanel.setBackground(Color.WHITE);
        
        // Table components
        String[] columns = {"Booking ID", "Lab / Equipment", "Date & Time", "Status"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        
        this.reservationsTable = new JTable(tableModel);
        this.scrollPane = new JScrollPane(reservationsTable);
        this.tablePanel = new JPanel(new BorderLayout());
        this.tablePanel.setBackground(Color.WHITE);
        
        // Button
        this.refreshBtn = new JButton("Refresh");
        this.refreshBtn.setPreferredSize(new Dimension(120, 36));
        this.refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        this.btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.btnRow.setBackground(Color.WHITE);
    }
    
    public void setupWelcomeLabel() {
        welcomeLabel.setText("Welcome back, " + currentUser);
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(WELCOME_FG);
    }
    
    public void setupStatsPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, CARD_INSET_RIGHT);
        
        gbc.gridx = 0;
        statsPanel.add(buildStatCard("Active Bookings", activeCountLabel, CARD_ACTIVE_BG), gbc);
        gbc.gridx = 1;
        statsPanel.add(buildStatCard("Pending Approval", pendingCountLabel, CARD_PENDING_BG), gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        statsPanel.add(buildStatCard("Completed", completedCountLabel, CARD_COMPLETED_BG), gbc);
    }
    
    public void setupTableTitle() {
        tableTitle.setFont(TABLE_TITLE_FONT);
        tableTitle.setForeground(HEADER_COLOR);
    }
    
    public void setupTablePanel() {
        reservationsTable.setFont(TABLE_FONT);
        reservationsTable.setRowHeight(TABLE_ROW_HEIGHT);
        reservationsTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        reservationsTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        reservationsTable.setSelectionBackground(TABLE_SELECTION_BG);
        reservationsTable.setGridColor(TABLE_GRID_COLOR);
        reservationsTable.setShowGrid(true);
        
        //Column widths
        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(COLUMN_ID_WIDTH);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(COLUMN_RESOURCE_WIDTH);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(COLUMN_DATETIME_WIDTH);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(COLUMN_STATUS_WIDTH);
        
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER_COLOR));
        
        scrollPane.setPreferredSize(new Dimension(0, 200)); 
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setupRefreshButton() {
        refreshBtn.setFont(BUTTON_FONT);
        refreshBtn.setBackground(BUTTON_BG);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setHorizontalAlignment(SwingConstants.CENTER);
        refreshBtn.setPreferredSize(new Dimension(120, 34));
        
        btnRow.add(refreshBtn);
    }
    
    public void setupMainPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        
        int row = 0;
        
        //Welcome header
        gbc.gridy = row++;
        mainPanel.add(welcomeLabel, gbc);
        
        //Stats panel
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, STATS_INSET_BOTTOM, 0);
        mainPanel.add(statsPanel, gbc);
        
        //Table title
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 6, 0);
        mainPanel.add(tableTitle, gbc);
        
        //Table panel
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, TABLE_INSET_BOTTOM, 0);
        mainPanel.add(tablePanel, gbc);
        
        //Button row
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(btnRow, gbc);
    }
    
    public void setWindowProperties() {
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setFrameIcon(null);
        this.setContentPane(mainPanel);
        this.setVisible(true);
    }
    
    public void registerListeners() {
        this.refreshBtn.addActionListener(e -> refreshTable());
    }
    
    //Business methods
    private JPanel buildStatCard(String label, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR),
                BorderFactory.createEmptyBorder(CARD_PADDING_TOP, CARD_PADDING_LEFT, 
                                               CARD_PADDING_BOTTOM, CARD_PADDING_RIGHT)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        valueLabel.setFont(VALUE_FONT);
        valueLabel.setForeground(VALUE_LABEL_FG);
        gbc.gridy = 0;
        card.add(valueLabel, gbc);
        
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(CARD_LABEL_FONT);
        lbl.setForeground(CARD_LABEL_FG);
        gbc.gridy = 1;
        card.add(lbl, gbc);
        
        return card;
    }
    
    private void loadSampleData() {
        tableModel.setRowCount(0);
        
        // TODO: Replace with actual server request (Phase 2)
        Object[][] sampleData = {
            {"R-1041", "SCIT Software Eng. Lab — Seat 12",  "25 Mar 2026, 10:00 AM", "Approved"},
            {"R-1038", "EQ-OSC-2210 — Oscilloscope",        "24 Mar 2026, 02:00 PM", "Pending"},
            {"R-1029", "SCIT Networking Lab — Seat 5",       "20 Mar 2026, 09:00 AM", "Completed"},
            {"R-1020", "EQ-3DP-0007 — 3D Printer (metal)",  "15 Mar 2026, 01:00 PM", "Completed"},
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
        
        updateStatCards();
    }
    
    private void updateStatCards() {
        int active = 0, pending = 0, completed = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String status = (String) tableModel.getValueAt(i, 3);
            switch (status) {
                case "Approved" -> active++;
                case "Pending" -> pending++;
                case "Completed" -> completed++;
            }
        }
        activeCountLabel.setText(String.valueOf(active));
        pendingCountLabel.setText(String.valueOf(pending));
        completedCountLabel.setText(String.valueOf(completed));
    }
    
    /**
     * Called by the server push listener when a reservation status changes.
     * Requirement #4 — real-time updates hook.
     */
    public void refreshTable() {
        // TODO: Request updated reservation list from server (Phase 2)
        loadSampleData();
        JOptionPane.showMessageDialog(this,
                "Reservations refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
}
