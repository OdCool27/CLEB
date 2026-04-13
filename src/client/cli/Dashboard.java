package cli;

import controller.ClientController;
import dto.EquipmentReservationDTO;
import dto.LabSeatReservationDTO;
import dto.StudentDTO;
import dto.UserDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class Dashboard extends JInternalFrame{
    ClientController clientController;

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
    private JTextField searchField;
    private JPanel searchPanel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
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
    
    private UserDTO userDTO;
    private final Runnable reservationUpdateListener;

    public Dashboard(UserDTO userDTO, ClientController clientController) {
        super(userDTO instanceof StudentDTO ? "My Reservations - Dashboard" : "Reservation Dashboard", true, true, true, true);
        this.userDTO = userDTO;
        this.currentUser = userDTO.getFirstName();
        this.currentRole = userDTO.getRole().name();
        this.clientController = clientController;
        this.reservationUpdateListener = this::refreshTableSilently;
        
        this.initializeComponents();
        this.setupWelcomeLabel();
        this.setupStatsPanel();
        this.setupTableTitle();
        this.setupTablePanel();
        this.setupSearchPanel();
        this.setupRefreshButton();
        this.setupMainPanel();
        this.setWindowProperties();
        this.registerListeners();
        this.clientController.addReservationUpdateListener(reservationUpdateListener);
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                clientController.removeReservationUpdateListener(reservationUpdateListener);
            }
        });
        this.loadData(userDTO, clientController);
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
        this.searchField = new JTextField();
        this.searchPanel = new JPanel(new BorderLayout(8, 0));
        this.searchPanel.setBackground(Color.WHITE);
        
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
        if (!"STUDENT".equalsIgnoreCase(currentRole)) {
            tableTitle.setText("Reservation Dashboard");
        }
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
        statsPanel.add(buildStatCard("Cancelled", completedCountLabel, CARD_COMPLETED_BG), gbc);
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
        tableSorter = new TableRowSorter<>(tableModel);
        reservationsTable.setRowSorter(tableSorter);
        
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER_COLOR));
        
        scrollPane.setPreferredSize(new Dimension(0, 200)); 
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void setupSearchPanel() {
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setForeground(HEADER_COLOR);
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setToolTipText("Filter by booking ID, resource, date, or status");

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
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

        //Search row
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(searchPanel, gbc);
        
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
        this.searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter(searchField.getText());
            }
        });
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
    
    private void loadData(UserDTO userDTO, ClientController clientController) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        if (userDTO instanceof StudentDTO s) {
            // Fetch Equipment Reservations
            RequestEnvelope<String> requestEquipment = new RequestEnvelope<>(
                    UUID.randomUUID(),
                    "GET_EQUIPMENT_RESERVATIONS_BY_STUDENT",
                    s.getStudentID()
            );
            clientController.sendRequest(requestEquipment);
            ResponseEnvelope<List<EquipmentReservationDTO>> responseEquipment = clientController.receiveResponse();

            if (responseEquipment != null && responseEquipment.getPayload() != null) {
                List<EquipmentReservationDTO> eqList = responseEquipment.getPayload();
                for (EquipmentReservationDTO res : eqList) {
                    tableModel.addRow(new Object[]{
                            "EQ-" + res.getReservationID(),
                            res.getReservedItem() != null ? res.getReservedItem().getDescription() : "Equipment",
                            res.getDateTime().format(formatter),
                            formatStatus(res.getApprovalStatus().name())
                    });
                }
            }

            // Fetch Lab Seat Reservations
            RequestEnvelope<String> requestLab = new RequestEnvelope<>(
                    UUID.randomUUID(),
                    "GET_LAB_SEAT_RESERVATIONS_BY_STUDENT",
                    s.getStudentID()
            );
            clientController.sendRequest(requestLab);
            ResponseEnvelope<List<LabSeatReservationDTO>> responseLab = clientController.receiveResponse();

            if (responseLab != null && responseLab.getPayload() != null) {
                List<LabSeatReservationDTO> lsList = responseLab.getPayload();
                for (LabSeatReservationDTO res : lsList) {
                    tableModel.addRow(new Object[]{
                            "LS-" + res.getReservationID(),
                            res.getReservedSeat() != null && res.getReservedSeat().getSeatLocation() != null
                                    ? buildLabSeatDisplay(res)
                                    : "Lab Seat",
                            res.getDateTime().format(formatter),
                            formatStatus(res.getApprovalStatus().name())
                    });
                }
            }
        }
        else {
            RequestEnvelope<Void> requestEquipment = new RequestEnvelope<>(
                    UUID.randomUUID(),
                    "GET_ALL_EQUIPMENT_RESERVATIONS",
                    null
            );
            clientController.sendRequest(requestEquipment);
            ResponseEnvelope<List<EquipmentReservationDTO>> responseEquipment = clientController.receiveResponse();

            if (responseEquipment != null && responseEquipment.getPayload() != null) {
                for (EquipmentReservationDTO res : responseEquipment.getPayload()) {
                    tableModel.addRow(new Object[]{
                            "EQ-" + res.getReservationID(),
                            res.getReservedItem() != null ? res.getReservedItem().getDescription() : "Equipment",
                            res.getDateTime().format(formatter),
                            formatStatus(res.getApprovalStatus().name())
                    });
                }
            }

            RequestEnvelope<Void> requestLab = new RequestEnvelope<>(
                    UUID.randomUUID(),
                    "GET_ALL_LAB_SEAT_RESERVATIONS",
                    null
            );
            clientController.sendRequest(requestLab);
            ResponseEnvelope<List<LabSeatReservationDTO>> responseLab = clientController.receiveResponse();

            if (responseLab != null && responseLab.getPayload() != null) {
                for (LabSeatReservationDTO res : responseLab.getPayload()) {
                    tableModel.addRow(new Object[]{
                            "LS-" + res.getReservationID(),
                            res.getReservedSeat() != null && res.getReservedSeat().getSeatLocation() != null
                                    ? buildLabSeatDisplay(res)
                                    : "Lab Seat",
                            res.getDateTime().format(formatter),
                            formatStatus(res.getApprovalStatus().name())
                    });
                }
            }
        }
        
        updateStatCards();
    }
    
    private void updateStatCards() {
        int active = 0, pending = 0, cancelled = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String status = (String) tableModel.getValueAt(i, 3);
            switch (status) {
                case "Approved" -> active++;
                case "Pending" -> pending++;
                case "Cancelled" -> cancelled++;
            }
        }
        activeCountLabel.setText(String.valueOf(active));
        pendingCountLabel.setText(String.valueOf(pending));
        completedCountLabel.setText(String.valueOf(cancelled));
    }

    private String formatStatus(String rawStatus) {
        String lower = rawStatus.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private void applyFilter(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            tableSorter.setRowFilter(null);
            return;
        }
        tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText.trim())));
    }

    private String getLabDisplayName(dto.LabDTO lab) {
        if (lab == null) {
            return "";
        }
        if (lab.getName() != null && !lab.getName().isBlank()) {
            return lab.getName();
        }
        return lab.getLabID() != null ? lab.getLabID() : "";
    }

    private String buildLabSeatDisplay(LabSeatReservationDTO reservation) {
        String labDisplay = getLabDisplayName(reservation.getReservedSeat().getSeatLocation());
        String seatDisplay = reservation.getReservedSeat().getSeatCode();
        if (seatDisplay == null || seatDisplay.isBlank()) {
            seatDisplay = "Seat " + reservation.getReservedSeat().getSeatID();
        }
        return labDisplay + " - " + seatDisplay;
    }
    
    /**
     * Called by the server push listener when a reservation status changes.
     * Requirement #4 — real-time updates hook.
     */
    public void refreshTable() {
        loadData(userDTO, clientController);
        JOptionPane.showMessageDialog(this,
                "Reservations refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTableSilently() {
        if (isDisplayable()) {
            loadData(userDTO, clientController);
        }
    }
}

