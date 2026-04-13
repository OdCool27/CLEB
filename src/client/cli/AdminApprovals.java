package cli;

import controller.ClientController;
import dto.EmployeeDTO;
import dto.EquipmentReservationDTO;
import dto.LabSeatReservationDTO;
import dto.UserDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
import javax.swing.table.TableCellRenderer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdminApprovals extends JInternalFrame {
    private static final Logger logger = LogManager.getLogger(AdminApprovals.class);

    private JTable queue;
    private DefaultTableModel tableModel;
    private JLabel pendingCountLabel;
    private final ClientController clientController;
    private final UserDTO currentUser;
    private final Runnable reservationUpdateListener;

    public AdminApprovals(UserDTO currentUser, ClientController clientController) {
        super("Pending Reservations - Approval Queue", true, true, true, true);
        logger.info("Initializing AdminApprovals GUI...");
        this.currentUser = currentUser;
        this.clientController = clientController;
        this.reservationUpdateListener = this::refreshQueueSilently;
        setSize(820, 480);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        mainPanel.add(buildHeaderRow(), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 12, 0);
        mainPanel.add(buildTablePanel(), gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buildActionPanel(), gbc);

        setContentPane(mainPanel);
        setVisible(true);
        clientController.addReservationUpdateListener(reservationUpdateListener);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                clientController.removeReservationUpdateListener(reservationUpdateListener);
            }
        });

        loadPendingReservations();
    }

    private JPanel buildHeaderRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;

        JLabel title = new JLabel("Approval Queue");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(26, 39, 68));
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(title, gbc);

        pendingCountLabel = new JLabel("0 Pending", SwingConstants.CENTER);
        pendingCountLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        pendingCountLabel.setForeground(new Color(133, 79, 11));
        pendingCountLabel.setBackground(new Color(250, 238, 218));
        pendingCountLabel.setOpaque(true);
        pendingCountLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 8, 0, 8);
        panel.add(pendingCountLabel, gbc);

        JButton refreshBtn = new JButton("Refresh Queue");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(255, 119, 92));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setPreferredSize(new Dimension(130, 30));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshQueue());

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(refreshBtn, gbc);

        return panel;
    }

    private JPanel buildTablePanel() {
        String[] columns = {"Booking ID", "Student", "Lab / Equipment", "Date & Time", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return String.class;
            }
        };

        queue = new JTable(tableModel);
        queue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        queue.setRowHeight(34);
        queue.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        queue.getTableHeader().setBackground(new Color(255, 119, 92));
        queue.setSelectionBackground(new Color(210, 225, 245));
        queue.setGridColor(new Color(230, 230, 230));
        queue.setShowGrid(true);

        queue.getColumnModel().getColumn(0).setPreferredWidth(70);
        queue.getColumnModel().getColumn(1).setPreferredWidth(120);
        queue.getColumnModel().getColumn(2).setPreferredWidth(220);
        queue.getColumnModel().getColumn(3).setPreferredWidth(140);
        queue.getColumnModel().getColumn(4).setPreferredWidth(90);
        queue.getColumnModel().getColumn(5).setPreferredWidth(100);
        queue.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(queue);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 10);

        JLabel hint = new JLabel("Select a row then choose an action");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(hint, gbc);

        JButton approveBtn = buildActionButton("Approve", new Color(59, 109, 17), new Color(234, 243, 222));
        approveBtn.addActionListener(e -> handleAction("Approved"));
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(approveBtn, gbc);

        JButton rejectBtn = buildActionButton("Reject", new Color(163, 45, 45), new Color(252, 235, 235));
        rejectBtn.addActionListener(e -> handleAction("Rejected"));
        gbc.gridx = 2;
        panel.add(rejectBtn, gbc);

        JButton cancelBtn = buildActionButton("Cancel Booking", new Color(90, 90, 90), new Color(240, 240, 240));
        cancelBtn.addActionListener(e -> handleAction("Cancelled"));
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(cancelBtn, gbc);

        return panel;
    }

    private JButton buildActionButton(String label, Color fg, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleAction(String action) {
        int selectedRow = queue.getSelectedRow();
        if (selectedRow == -1) {
            logger.warn("User attempted action '{}' without selecting a row", action);
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        String student = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        if (!"Pending".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "This reservation has already been " + currentStatus.toLowerCase() + ".",
                    "Already Actioned", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action.toLowerCase().replace("ed", "") + " booking " + bookingId + " for " + student + "?",
                "Confirm Action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int reservationId = Integer.parseInt(bookingId.split("-")[1]);
        boolean isEquipment = bookingId.startsWith("EQ");
        String requestAction;

        if ("Approved".equals(action)) {
            requestAction = isEquipment ? "APPROVE_EQUIPMENT_RESERVATION" : "APPROVE_LAB_SEAT_RESERVATION";
        } else if ("Rejected".equals(action)) {
            requestAction = isEquipment ? "REJECT_EQUIPMENT_RESERVATION" : "REJECT_LAB_SEAT_RESERVATION";
        } else {
            requestAction = isEquipment ? "CANCEL_EQUIPMENT_RESERVATION" : "CANCEL_LAB_SEAT_RESERVATION";
        }

        RequestEnvelope<String[]> envelope = new RequestEnvelope<>(
                UUID.randomUUID(),
                requestAction,
                new String[]{String.valueOf(reservationId), getApproverId()}
        );
        clientController.sendRequest(envelope);
        ResponseEnvelope<Boolean> response = clientController.receiveResponse();

        if (response != null && "SUCCESS".equals(response.getStatus())) {
            tableModel.setValueAt(action, selectedRow, 4);
            updatePendingCount();
            JOptionPane.showMessageDialog(this,
                    "Booking " + bookingId + " has been " + action.toLowerCase() + ".",
                    "Action Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    response != null ? response.getMessage() : "Failed to update reservation status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshQueue() {
        logger.info("Refreshing approval queue...");
        loadPendingReservations();
    }

    private void refreshQueueSilently() {
        if (isDisplayable()) {
            loadPendingReservations();
        }
    }

    private void loadPendingReservations() {
        logger.debug("Loading pending reservations data...");
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        RequestEnvelope<Void> equipmentRequest = new RequestEnvelope<>(UUID.randomUUID(), "GET_ALL_EQUIPMENT_RESERVATIONS", null);
        clientController.sendRequest(equipmentRequest);
        ResponseEnvelope<List<EquipmentReservationDTO>> equipmentResponse = clientController.receiveResponse();
        if (equipmentResponse != null && equipmentResponse.getPayload() != null) {
            for (EquipmentReservationDTO reservation : equipmentResponse.getPayload()) {
                String resourceName = reservation.getReservedItem() != null ? reservation.getReservedItem().getDescription() : "Equipment";
                tableModel.addRow(new Object[]{
                        "EQ-" + reservation.getReservationID(),
                        reservation.getStudentID(),
                        resourceName,
                        reservation.getDateTime().format(formatter),
                        formatStatus(reservation.getApprovalStatus().name()),
                        ""
                });
            }
        }

        RequestEnvelope<Void> labRequest = new RequestEnvelope<>(UUID.randomUUID(), "GET_ALL_LAB_SEAT_RESERVATIONS", null);
        clientController.sendRequest(labRequest);
        ResponseEnvelope<List<LabSeatReservationDTO>> labResponse = clientController.receiveResponse();
        if (labResponse != null && labResponse.getPayload() != null) {
            for (LabSeatReservationDTO reservation : labResponse.getPayload()) {
                String resourceName = "Lab Seat";
                if (reservation.getReservedSeat() != null && reservation.getReservedSeat().getSeatLocation() != null) {
                    resourceName = getLabDisplayName(reservation.getReservedSeat().getSeatLocation()) + " - Seat " + reservation.getReservedSeat().getSeatID();
                }
                tableModel.addRow(new Object[]{
                        "LS-" + reservation.getReservationID(),
                        reservation.getStudentID(),
                        resourceName,
                        reservation.getDateTime().format(formatter),
                        formatStatus(reservation.getApprovalStatus().name()),
                        ""
                });
            }
        }

        updatePendingCount();
    }

    private String formatStatus(String rawStatus) {
        String lower = rawStatus.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private String getLabDisplayName(dto.LabDTO lab) {
        return lab != null ? lab.getName() : "";
    }

    private void updatePendingCount() {
        int count = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("Pending".equals(tableModel.getValueAt(i, 4))) {
                count++;
            }
        }
        pendingCountLabel.setText(count + " Pending");
    }

    private String getApproverId() {
        if (currentUser instanceof EmployeeDTO employeeDTO) {
            return employeeDTO.getEmpID();
        }
        return String.valueOf(currentUser.getUserID());
    }

    static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = value == null ? "" : value.toString();
            setText(status);

            switch (status) {
                case "Pending" -> {
                    setBackground(new Color(250, 238, 218));
                    setForeground(new Color(133, 79, 11));
                }
                case "Approved" -> {
                    setBackground(new Color(234, 243, 222));
                    setForeground(new Color(59, 109, 17));
                }
                case "Rejected" -> {
                    setBackground(new Color(252, 235, 235));
                    setForeground(new Color(163, 45, 45));
                }
                case "Cancelled" -> {
                    setBackground(new Color(240, 240, 240));
                    setForeground(new Color(90, 90, 90));
                }
                case "Complete" -> {
                    setBackground(new Color(219, 239, 255));
                    setForeground(new Color(18, 84, 136));
                }
                default -> {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
            }

            if (isSelected) {
                setBackground(new Color(210, 225, 245));
                setForeground(new Color(26, 39, 68));
            }

            return this;
        }
    }
}
