package cli;

import controller.ClientController;
import dto.EmployeeDTO;
import dto.EquipmentDTO;
import dto.EquipmentReservationDTO;
import dto.LabDTO;
import dto.LabSeatDTO;
import dto.LabSeatReservationDTO;
import dto.StudentDTO;
import dto.UserDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import model.Reservation;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bookings extends JInternalFrame {
    private static final Logger logger = LogManager.getLogger(Bookings.class);

    private JTable labTable;
    private DefaultTableModel labTableModel;
    private JTable equipTable;
    private DefaultTableModel equipTableModel;

    private JComboBox<String> typeComboBox;
    private JComboBox<String> resourceComboBox;
    private JComboBox<String> seatComboBox;
    private JTextField dateTimeField;

    private JPanel mainPanel;
    private JLabel title;
    private JSplitPane splitPane;
    private JPanel formPanel;
    private JButton submitBtn;

    private final UserDTO currentUser;
    private final ClientController clientController;
    private List<LabDTO> labs = new ArrayList<>();
    private List<EquipmentDTO> equipments = new ArrayList<>();
    private List<LabSeatDTO> availableLabSeats = new ArrayList<>();

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

    private static final int LAB_COL_NAME_WIDTH = 200;
    private static final int LAB_COL_CAMPUS_WIDTH = 90;
    private static final int LAB_COL_FREE_WIDTH = 70;
    private static final int LAB_COL_TOTAL_WIDTH = 50;
    private static final int EQUIP_COL_ID_WIDTH = 100;
    private static final int EQUIP_COL_DESC_WIDTH = 160;
    private static final int EQUIP_COL_STATUS_WIDTH = 90;

    private static final int TITLE_FONT_SIZE = 15;
    private static final int SECTION_FONT_SIZE = 13;
    private static final int TABLE_FONT_SIZE = 12;
    private static final int FORM_TITLE_FONT_SIZE = 13;
    private static final int LABEL_FONT_SIZE = 11;
    private static final int FIELD_FONT_SIZE = 12;
    private static final int BUTTON_FONT_SIZE = 13;

    private static final Color HEADER_COLOR = new Color(26, 39, 68);
    private static final Color FORM_BG = new Color(245, 247, 252);
    private static final Color BORDER_COLOR = new Color(200, 210, 225);
    private static final Color TABLE_HEADER_BG = new Color(255, 119, 92);
    private static final Color TABLE_SELECTION_BG = new Color(210, 225, 245);
    private static final Color TABLE_GRID_COLOR = new Color(230, 230, 230);
    private static final Color BUTTON_BG = new Color(255, 119, 92);
    private static final Color PLACEHOLDER_FG = Color.GRAY;

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, TITLE_FONT_SIZE);
    private static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, SECTION_FONT_SIZE);
    private static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, TABLE_FONT_SIZE);
    private static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, TABLE_FONT_SIZE);
    private static final Font FORM_TITLE_FONT = new Font("SansSerif", Font.BOLD, FORM_TITLE_FONT_SIZE);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, LABEL_FONT_SIZE);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, FIELD_FONT_SIZE);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, BUTTON_FONT_SIZE);

    private static final String[] BOOKING_TYPES = {"Lab Seat", "Equipment"};
    private static final String DATE_TIME_PLACEHOLDER = "DD/MM/YYYY HH:MM";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    public Bookings(UserDTO userDTO, ClientController clientController) {
        super("Book a Lab Seat / Equipment", true, true, true, true);
        logger.info("Initializing Bookings GUI for user: {}", userDTO.getEmail());
        this.currentUser = userDTO;
        this.clientController = clientController;

        initializeComponents();
        setupMainPanel();
        setWindowProperties();
        registerListeners();
        loadLabData();
        loadEquipmentData();
        refreshResourceInputs();
    }

    public void initializeComponents() {
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));

        title = new JLabel("Lab & Equipment Availability");
        title.setFont(TITLE_FONT);
        title.setForeground(HEADER_COLOR);

        splitPane = buildAvailabilityPanel();
        splitPane.setDividerLocation(SPLIT_DIVIDER_LOCATION);

        formPanel = buildBookingForm();
    }

    public void setupMainPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, COMPONENT_SPACING, 0);

        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(splitPane, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(formPanel, gbc);
    }

    public void setWindowProperties() {
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setContentPane(mainPanel);
        setVisible(true);
    }

    public void registerListeners() {
        dateTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateTimeField.getText().equals(DATE_TIME_PLACEHOLDER)) {
                    dateTimeField.setText("");
                    dateTimeField.setForeground(HEADER_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String input = dateTimeField.getText().trim();
                if (input.isEmpty()) {
                    dateTimeField.setText(DATE_TIME_PLACEHOLDER);
                    dateTimeField.setForeground(PLACEHOLDER_FG);
                    return;
                }

                if (!isValidDateTimeFormat(input)) {
                    JOptionPane.showMessageDialog(Bookings.this,
                            "Date must be in the format DD/MM/YYYY HH:MM",
                            "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
                    dateTimeField.requestFocus();
                }
            }
        });

        typeComboBox.addActionListener(e -> refreshResourceInputs());
        resourceComboBox.addActionListener(e -> refreshSeatOptions());
        submitBtn.addActionListener(e -> handleSubmitBooking());
    }

    private JSplitPane buildAvailabilityPanel() {
        JPanel labPanel = buildLabPanel();
        JPanel equipPanel = buildEquipmentPanel();

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

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 8);

        gbc.gridx = 0;
        gbc.weightx = 0.2;
        typeComboBox = new JComboBox<>(BOOKING_TYPES);
        formContainer.add(buildFieldColumn("Booking Type", typeComboBox), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        resourceComboBox = new JComboBox<>();
        formContainer.add(buildFieldColumn("Select Lab / Equipment", resourceComboBox), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        seatComboBox = new JComboBox<>();
        formContainer.add(buildFieldColumn("Seat / Item", seatComboBox), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(0, 0, 0, 0);
        dateTimeField = new JTextField("DD/MM/YYYY HH:MM");
        dateTimeField.setFont(FIELD_FONT);
        dateTimeField.setForeground(PLACEHOLDER_FG);
        dateTimeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        formContainer.add(buildFieldColumn("Date & Time", dateTimeField), gbc);

        submitBtn = new JButton("Submit Booking Request");
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

        if (field instanceof JComboBox<?> comboBox) {
            comboBox.setFont(FIELD_FONT);
            comboBox.setBackground(Color.WHITE);
        }
        field.setPreferredSize(new Dimension(0, FIELD_HEIGHT));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        col.add(field, gbc);

        return col;
    }

    private void handleSubmitBooking() {
        String type = (String) typeComboBox.getSelectedItem();
        String selectedResourceName = (String) resourceComboBox.getSelectedItem();
        String selectedSeatOrItem = (String) seatComboBox.getSelectedItem();
        String dateTimeStr = dateTimeField.getText().trim();

        if (selectedResourceName == null || dateTimeStr.isEmpty() || dateTimeStr.equals(DATE_TIME_PLACEHOLDER)) {
            logger.warn("User {} attempted to submit booking with incomplete data", currentUser.getEmail());
            JOptionPane.showMessageDialog(this,
                    "Please choose a resource and enter a valid date and time.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use DD/MM/YYYY HH:MM", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Reservation:\n" +
                        "Type: " + type + "\n" +
                        "Resource: " + selectedResourceName + "\n" +
                        ("Lab Seat".equals(type) ? "Seat: " + selectedSeatOrItem + "\n" : "") +
                        "Time: " + dateTimeStr,
                "Confirm Booking", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = false;
        String message = "Failed to submit booking request.";
        String studentId = resolveReservationOwnerId();

        if ("Lab Seat".equals(type)) {
            LabDTO selectedLab = findLabByName(selectedResourceName);
            LabSeatDTO selectedSeat = findSeatByCode(selectedSeatOrItem);
            if (selectedLab != null && selectedSeat != null) {
                LabSeatReservationDTO reservation = new LabSeatReservationDTO(
                        0, studentId, dateTime, 2, Reservation.ReservationStatus.PENDING, "", null, selectedSeat
                );
                RequestEnvelope<LabSeatReservationDTO> envelope =
                        new RequestEnvelope<>(UUID.randomUUID(), "CREATE_LAB_SEAT_RESERVATION", reservation);
                clientController.sendRequest(envelope);
                ResponseEnvelope<Boolean> response = clientController.receiveResponse();
                if (response != null && "SUCCESS".equals(response.getStatus())) {
                    success = true;
                    message = response.getMessage();
                }
            }
        } else {
            EquipmentDTO selectedEquipment = findEquipmentByDisplayName(selectedResourceName);
            if (selectedEquipment != null) {
                EquipmentReservationDTO reservation = new EquipmentReservationDTO(
                        0, studentId, dateTime, 2, Reservation.ReservationStatus.PENDING, "", null, selectedEquipment
                );
                RequestEnvelope<EquipmentReservationDTO> envelope =
                        new RequestEnvelope<>(UUID.randomUUID(), "CREATE_EQUIPMENT_RESERVATION", reservation);
                clientController.sendRequest(envelope);
                ResponseEnvelope<Boolean> response = clientController.receiveResponse();
                if (response != null && "SUCCESS".equals(response.getStatus())) {
                    success = true;
                    message = response.getMessage();
                }
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Booking request submitted successfully.\n" + message, "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLabData() {
        logger.debug("Loading lab data...");
        RequestEnvelope<Void> envelope = new RequestEnvelope<>(UUID.randomUUID(), "GET_ALL_LABS", null);
        clientController.sendRequest(envelope);
        ResponseEnvelope<List<LabDTO>> response = clientController.receiveResponse();

        labTableModel.setRowCount(0);
        labs = new ArrayList<>();

        if (response != null && response.getPayload() != null) {
            labs = response.getPayload();
            for (LabDTO lab : labs) {
                String campus = lab.getLocation() != null ? lab.getLocation().getCampus() : "";
                labTableModel.addRow(new Object[]{getLabDisplayName(lab), campus, lab.getNumOfSeats(), lab.getNumOfSeats()});
            }
        }
    }

    private void loadEquipmentData() {
        logger.debug("Loading equipment data...");
        RequestEnvelope<LocalDateTime> envelope = new RequestEnvelope<>(UUID.randomUUID(), "GET_AVAILABLE_EQUIPMENT_AT_TIME", LocalDateTime.now());
        clientController.sendRequest(envelope);
        ResponseEnvelope<List<EquipmentDTO>> response = clientController.receiveResponse();

        equipTableModel.setRowCount(0);
        equipments = new ArrayList<>();

        if (response != null && response.getPayload() != null) {
            equipments = response.getPayload();
            for (EquipmentDTO equipment : equipments) {
                equipTableModel.addRow(new Object[]{equipment.getEquipmentID(), equipment.getDescription(), equipment.getStatus()});
            }
        }
    }

    private void refreshResourceInputs() {
        boolean isLabSeat = "Lab Seat".equals(typeComboBox.getSelectedItem());

        resourceComboBox.removeAllItems();
        if (isLabSeat) {
            for (LabDTO lab : labs) {
                resourceComboBox.addItem(getLabDisplayName(lab));
            }
        } else {
            for (EquipmentDTO equipment : equipments) {
                resourceComboBox.addItem(buildEquipmentDisplayName(equipment));
            }
        }

        seatComboBox.setEnabled(isLabSeat);
        refreshSeatOptions();
    }

    private void refreshSeatOptions() {
        seatComboBox.removeAllItems();
        availableLabSeats = new ArrayList<>();

        if (!"Lab Seat".equals(typeComboBox.getSelectedItem())) {
            return;
        }

        String selectedLabName = (String) resourceComboBox.getSelectedItem();
        LabDTO selectedLab = findLabByName(selectedLabName);
        if (selectedLab == null) {
            return;
        }

        RequestEnvelope<String> request = new RequestEnvelope<>(
                UUID.randomUUID(),
                "GET_SEATS_BY_LAB",
                selectedLab.getLabID()
        );
        clientController.sendRequest(request);
        ResponseEnvelope<List<LabSeatDTO>> response = clientController.receiveResponse();

        if (response != null && response.getPayload() != null) {
            availableLabSeats = response.getPayload();
            for (LabSeatDTO seat : availableLabSeats) {
                seatComboBox.addItem(seat.getSeatCode());
            }
        }
    }

    private LabDTO findLabByName(String labName) {
        for (LabDTO lab : labs) {
            if (getLabDisplayName(lab).equals(labName)) {
                return lab;
            }
        }
        return null;
    }

    private EquipmentDTO findEquipmentByDisplayName(String displayName) {
        for (EquipmentDTO equipment : equipments) {
            if (buildEquipmentDisplayName(equipment).equals(displayName)) {
                return equipment;
            }
        }
        return null;
    }

    private String buildEquipmentDisplayName(EquipmentDTO equipment) {
        return equipment.getEquipmentID() + " - " + equipment.getDescription();
    }

    private LabSeatDTO findSeatByCode(String seatCode) {
        for (LabSeatDTO seat : availableLabSeats) {
            if (seat.getSeatCode().equals(seatCode)) {
                return seat;
            }
        }
        return null;
    }

    private String getLabDisplayName(LabDTO lab) {
        return lab != null ? lab.getName() : "";
    }

    private String resolveReservationOwnerId() {
        if (currentUser instanceof StudentDTO studentDTO) {
            return studentDTO.getStudentID();
        }
        if (currentUser instanceof EmployeeDTO employeeDTO) {
            return employeeDTO.getEmpID();
        }
        return String.valueOf(currentUser.getUserID());
    }

    private boolean isValidDateTimeFormat(String input) {
        try {
            LocalDateTime.parse(input, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

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
