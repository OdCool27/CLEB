package client.cli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Bookings extends JInternalFrame {
	
	//Components - Tables
    private JTable labTable;
    private DefaultTableModel labTableModel;
    private JTable equipTable;
    private DefaultTableModel equipTableModel;
    
    //Components - Form fields
    private JComboBox<String> typeComboBox;
    private JComboBox<String> labComboBox;
    private JComboBox<String> seatComboBox;
    private JTextField dateTimeField;
    
    //Components - Panels
    private JPanel mainPanel;
    private JLabel title;
    private JSplitPane splitPane;
    private JPanel formPanel;
    private JButton submitBtn;
    
    //Data fields
    private String currentUser;
    
    //Constants - Frame dimensions
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 540;
    private static final int BORDER_SIZE = 16;
    private static final int COMPONENT_SPACING = 14;
    private static final int FORM_INSET = 14;
    private static final int SPLIT_DIVIDER_LOCATION = 380;
    private static final int SPLIT_RESIZE_WEIGHT = 55;
    private static final int FIELD_HEIGHT = 32;
    private static final int BUTTON_HEIGHT = 36;
    private static final int TABLE_ROW_HEIGHT = 26;
    
    //Constants - Column widths
    private static final int LAB_COL_NAME_WIDTH = 200;
    private static final int LAB_COL_CAMPUS_WIDTH = 90;
    private static final int LAB_COL_FREE_WIDTH = 70;
    private static final int LAB_COL_TOTAL_WIDTH = 50;
    private static final int EQUIP_COL_ID_WIDTH = 100;
    private static final int EQUIP_COL_DESC_WIDTH = 160;
    private static final int EQUIP_COL_STATUS_WIDTH = 90;
    
    //Constants - Font sizes
    private static final int TITLE_FONT_SIZE = 15;
    private static final int SECTION_FONT_SIZE = 13;
    private static final int TABLE_FONT_SIZE = 12;
    private static final int FORM_TITLE_FONT_SIZE = 13;
    private static final int LABEL_FONT_SIZE = 11;
    private static final int FIELD_FONT_SIZE = 12;
    private static final int BUTTON_FONT_SIZE = 13;
    
    //Colors
    private static final Color HEADER_COLOR = new Color(26, 39, 68);
    private static final Color FORM_BG = new Color(245, 247, 252);
    private static final Color BORDER_COLOR = new Color(200, 210, 225);
    private static final Color TABLE_HEADER_BG = new Color(255, 119, 92);
    private static final Color TABLE_SELECTION_BG = new Color(210, 225, 245);
    private static final Color TABLE_GRID_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BG = new Color(255, 119, 92);
    private static final Color PLACEHOLDER_FG = Color.GRAY;
    
    //Fonts
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, TITLE_FONT_SIZE);
    private static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, SECTION_FONT_SIZE);
    private static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, TABLE_FONT_SIZE);
    private static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, TABLE_FONT_SIZE);
    private static final Font FORM_TITLE_FONT = new Font("SansSerif", Font.BOLD, FORM_TITLE_FONT_SIZE);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, LABEL_FONT_SIZE);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, FIELD_FONT_SIZE);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, BUTTON_FONT_SIZE);
    
    //ComboBox data
    private static final String[] BOOKING_TYPES = {"Lab Seat", "Equipment"};
    private static final String[] LAB_OPTIONS = {
        "SCIT Software Eng. Lab",
        "SCIT Networking & Systems Lab",
        "SOE Industrial & Mech. Eng. Lab",
        "EQ-OSC-2210 — Oscilloscope",
        "EQ-3DP-0007 — 3D Printer"
    };
    private static final String[] SEAT_OPTIONS = {
        "Seat 1", "Seat 2", "Seat 3", "Seat 4", "Seat 5",
        "Seat 6", "Seat 7", "Seat 8", "Seat 9", "Seat 10"
    };
	
    public Bookings(String username) {
        super("Book a Lab Seat / Equipment", true, true, true, true);
        this.currentUser = username;
        
        this.initializeComponents();
        this.setupMainPanel();
        this.setWindowProperties();
        this.registerListeners();
        this.loadLabData();
        this.loadEquipmentData();
    }
    
    public void initializeComponents() {
        //Main panel
        this.mainPanel = new JPanel(new GridBagLayout());
        this.mainPanel.setBackground(Color.WHITE);
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        
        //Title
        this.title = new JLabel("Lab & Equipment Availability");
        this.title.setFont(TITLE_FONT);
        this.title.setForeground(HEADER_COLOR);
        
        //Split pane
        this.splitPane = buildAvailabilityPanel();
        this.splitPane.setDividerLocation(SPLIT_DIVIDER_LOCATION);
        
        //Form panel
        this.formPanel = buildBookingForm();
    }
    
    public void setupMainPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, COMPONENT_SPACING, 0);
        
        int row = 0;
        
        //Page title
        gbc.gridy = row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(title, gbc);
        
        //Split pane with tables
        gbc.gridy = row++;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(splitPane, gbc);
        
        //Booking form
        gbc.gridy = row;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(formPanel, gbc);
    }
    
    public void setWindowProperties() {
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setContentPane(mainPanel);
        this.setVisible(true);
    }
    
    public void registerListeners() {
        //Add focus listener to date time field
        dateTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateTimeField.getText().equals("DD/MM/YYYY HH:MM")) {
                    dateTimeField.setText("");
                    dateTimeField.setForeground(HEADER_COLOR);
                }
            }
        });
        
        //Submit button listener
        submitBtn.addActionListener(e -> handleSubmitBooking());
    }
    
    // Availability Panel (split pane with two tables)
    private JSplitPane buildAvailabilityPanel() {
        JPanel labPanel = buildLabPanel();
        JPanel equipPanel = buildEquipmentPanel();
        
        //Padding around each side
        labPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        equipPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, labPanel, equipPanel);
        split.setResizeWeight(SPLIT_RESIZE_WEIGHT / 100.0);
        split.setBorder(null);
        split.setBackground(Color.WHITE);
        
        return split;
    }
    
    private JPanel buildLabPanel() {
        JPanel labPanel = new JPanel(new BorderLayout(0, 6));
        labPanel.setBackground(Color.WHITE);
        
        JLabel labTitle = new JLabel("Available Labs");
        labTitle.setFont(SECTION_FONT);
        labTitle.setForeground(HEADER_COLOR);
        labPanel.add(labTitle, BorderLayout.NORTH);
        
        String[] labCols = {"Lab Name", "Campus", "Seats Free", "Total"};
        labTableModel = new DefaultTableModel(labCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        
        labTable = new JTable(labTableModel);
        styleTable(labTable);
        labTable.getColumnModel().getColumn(0).setPreferredWidth(LAB_COL_NAME_WIDTH);
        labTable.getColumnModel().getColumn(1).setPreferredWidth(LAB_COL_CAMPUS_WIDTH);
        labTable.getColumnModel().getColumn(2).setPreferredWidth(LAB_COL_FREE_WIDTH);
        labTable.getColumnModel().getColumn(3).setPreferredWidth(LAB_COL_TOTAL_WIDTH);
        
        labPanel.add(new JScrollPane(labTable), BorderLayout.CENTER);
        
        return labPanel;
    }
    
    private JPanel buildEquipmentPanel() {
        JPanel equipPanel = new JPanel(new BorderLayout(0, 6));
        equipPanel.setBackground(Color.WHITE);
        
        JLabel equipTitle = new JLabel("Equipment Availability");
        equipTitle.setFont(SECTION_FONT);
        equipTitle.setForeground(HEADER_COLOR);
        equipPanel.add(equipTitle, BorderLayout.NORTH);
        
        String[] equipCols = {"Equipment ID", "Description", "Status"};
        equipTableModel = new DefaultTableModel(equipCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        
        equipTable = new JTable(equipTableModel);
        styleTable(equipTable);
        equipTable.getColumnModel().getColumn(0).setPreferredWidth(EQUIP_COL_ID_WIDTH);
        equipTable.getColumnModel().getColumn(1).setPreferredWidth(EQUIP_COL_DESC_WIDTH);
        equipTable.getColumnModel().getColumn(2).setPreferredWidth(EQUIP_COL_STATUS_WIDTH);
        
        equipPanel.add(new JScrollPane(equipTable), BorderLayout.CENTER);
        
        return equipPanel;
    }
    
   
    // Booking Form
    private JPanel buildBookingForm() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(FORM_BG);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(FORM_INSET, FORM_INSET, FORM_INSET, FORM_INSET)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        
        JLabel formTitle = new JLabel("New Booking Request");
        formTitle.setFont(FORM_TITLE_FONT);
        formTitle.setForeground(HEADER_COLOR);
        formContainer.add(formTitle, gbc);
        
        //Form fields row
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 8);
        
        //Booking type
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        this.typeComboBox = new JComboBox<>(BOOKING_TYPES);
        formContainer.add(buildFieldColumn("Booking Type", typeComboBox), gbc);
        
        //Lab selector
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        this.labComboBox = new JComboBox<>(LAB_OPTIONS);
        formContainer.add(buildFieldColumn("Select Lab / Equipment", labComboBox), gbc);
        
        //Seat selector
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        this.seatComboBox = new JComboBox<>(SEAT_OPTIONS);
        formContainer.add(buildFieldColumn("Seat / Item", seatComboBox), gbc);
        
        //Date & time
        gbc.gridx = 3;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(0, 0, 0, 0);
        this.dateTimeField = new JTextField("DD/MM/YYYY HH:MM");
        dateTimeField.setFont(FIELD_FONT);
        dateTimeField.setForeground(PLACEHOLDER_FG);
        dateTimeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        formContainer.add(buildFieldColumn("Date & Time", dateTimeField), gbc);
        
        //Submit button row
        this.submitBtn = new JButton("Submit Booking Request");
        submitBtn.setFont(BUTTON_FONT);
        submitBtn.setBackground(BUTTON_BG);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setPreferredSize(new Dimension(0, BUTTON_HEIGHT));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 0, 0, 0);
        formContainer.add(submitBtn, gbc);
        
        return formContainer;
    }
    
    private JPanel buildFieldColumn(String labelText, JComponent field) {
        JPanel col = new JPanel(new GridBagLayout());
        col.setBackground(FORM_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(Color.GRAY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        col.add(lbl, gbc);
        
        if (field instanceof JComboBox) {
            field.setFont(FIELD_FONT);
            ((JComboBox<?>) field).setBackground(Color.WHITE);
        }
        field.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        col.add(field, gbc);
        
        return col;
    }

    // Event Handlers
    private void handleSubmitBooking() {
        String type = (String) typeComboBox.getSelectedItem();
        String lab = (String) labComboBox.getSelectedItem();
        String seat = (String) seatComboBox.getSelectedItem();
        String dateTime = dateTimeField.getText().trim();
        
        if (dateTime.isEmpty() || dateTime.equals("DD/MM/YYYY HH:MM")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a date and time for your booking.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // TODO: Send booking request to server via socket (Phase 2)
        // Request envelope with UUID correlation ID goes here
        
        JOptionPane.showMessageDialog(this,
                "Booking request submitted!\n\n"
                        + "Type:     " + type + "\n"
                        + "Lab:      " + lab + "\n"
                        + "Seat:     " + seat + "\n"
                        + "DateTime: " + dateTime + "\n\n"
                        + "Awaiting staff approval.",
                "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
    }
    
  
    // Data Loading
    private void loadLabData() {
        // TODO: Replace with server request (Phase 2)
        labTableModel.setRowCount(0);
        labTableModel.addRow(new Object[]{"SCIT Software Eng. Lab", "Papine", 16, 40});
        labTableModel.addRow(new Object[]{"SCIT Networking & Systems Lab", "Papine", 24, 32});
        labTableModel.addRow(new Object[]{"SOE Industrial & Mech. Eng. Lab", "Papine", 5, 24});
    }
    
    private void loadEquipmentData() {
        // TODO: Replace with server request (Phase 2)
        equipTableModel.setRowCount(0);
        equipTableModel.addRow(new Object[]{"EQ-3DP-0007", "3D Printer (metal)", "Available"});
        equipTableModel.addRow(new Object[]{"EQ-NET-1021", "Network Switch (L3)", "Maintenance"});
        equipTableModel.addRow(new Object[]{"EQ-OSC-2210", "Oscilloscope", "Available"});
        equipTableModel.addRow(new Object[]{"EQ-PC-4502", "Desktop Workstation", "Booked"});
    }
    
  
    // Helper
    private void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.getTableHeader().setFont(TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setGridColor(TABLE_GRID_COLOR);
        table.setShowGrid(true);
    }

}
