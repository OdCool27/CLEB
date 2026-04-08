package client.cli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

public class AdminApprovals extends JInternalFrame{
	
    private JTable            queue;
    private DefaultTableModel tableModel;
    private JLabel            pendingCountLabel;

    public AdminApprovals() {
    	super("Pending Reservations — Approval Queue", true, true, true, true);
    	 
        setSize(820, 480);
 
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
 
        int row = 0;
 
        //Header row
        JPanel headerRow = buildHeaderRow();
        gbc.gridy  = row++;
        gbc.insets = new Insets(0, 0, 14, 0);
        mainPanel.add(headerRow, gbc);
 
        //Queue JTable
        JPanel tablePanel = buildTablePanel();
        gbc.gridy   = row++;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets  = new Insets(0, 0, 12, 0);
        mainPanel.add(tablePanel, gbc);
 
        //Action buttons
        JPanel actionPanel = buildActionPanel();
        gbc.gridy   = row;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets  = new Insets(0, 0, 0, 0);
        mainPanel.add(actionPanel, gbc);
 
        setContentPane(mainPanel);
        setVisible(true);
 
        loadPendingReservations();
    }
 
    //Header Row
    private JPanel buildHeaderRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
 
        JLabel title = new JLabel("Approval Queue");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(26, 39, 68));
        gbc.gridx   = 0;
        gbc.weightx = 1.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        panel.add(title, gbc);
 
        //Pending count badge
        pendingCountLabel = new JLabel("3 Pending", SwingConstants.CENTER);
        pendingCountLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        pendingCountLabel.setForeground(new Color(133, 79, 11));
        pendingCountLabel.setBackground(new Color(250, 238, 218));
        pendingCountLabel.setOpaque(true);
        pendingCountLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
 
        gbc.gridx   = 1;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.insets  = new Insets(0, 8, 0, 8);
        panel.add(pendingCountLabel, gbc);
 
        //Refresh button
        JButton refreshBtn = new JButton("Refresh Queue");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(255, 119, 92));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setPreferredSize(new Dimension(130, 30));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshQueue());
 
        gbc.gridx  = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(refreshBtn, gbc);
 
        return panel;
    }
 

    //Table Panel
    private JPanel buildTablePanel() {
        String[] columns = {"Booking ID", "Student", "Lab / Equipment", "Date & Time", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };
 
        queue = new JTable(tableModel);
        queue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        queue.setRowHeight(34);
        queue.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        queue.getTableHeader().setBackground(new Color(255, 119, 92));
        queue.setSelectionBackground(new Color(210, 225, 245));
        queue.setGridColor(new Color(230, 230, 230));
        queue.setShowGrid(true);
 
        //Column widths
        queue.getColumnModel().getColumn(0).setPreferredWidth(70);
        queue.getColumnModel().getColumn(1).setPreferredWidth(120);
        queue.getColumnModel().getColumn(2).setPreferredWidth(220);
        queue.getColumnModel().getColumn(3).setPreferredWidth(140);
        queue.getColumnModel().getColumn(4).setPreferredWidth(90);
        queue.getColumnModel().getColumn(5).setPreferredWidth(100);
 
        //Status column color renderer
        queue.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
 
        JScrollPane scrollPane = new JScrollPane(queue);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    //Action Buttons Panel
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy  = 0;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 10);
 
        JLabel hint = new JLabel("Select a row then choose an action");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 14));
        hint.setForeground(Color.DARK_GRAY);
        gbc.gridx   = 0;
        gbc.weightx = 1.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        panel.add(hint, gbc);
 
        //Approve
        JButton approveBtn = buildActionButton("Approve", new Color(59, 109, 17), new Color(234, 243, 222));
        approveBtn.addActionListener(e -> handleAction("Approved"));
        gbc.gridx   = 1;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        panel.add(approveBtn, gbc);
 
        //Reject
        JButton rejectBtn = buildActionButton("Reject", new Color(163, 45, 45), new Color(252, 235, 235));
        rejectBtn.addActionListener(e -> handleAction("Rejected"));
        gbc.gridx  = 2;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel.add(rejectBtn, gbc);
 
        //Cancel
        JButton cancelBtn = buildActionButton("Cancel Booking", new Color(90, 90, 90), new Color(240, 240, 240));
        cancelBtn.addActionListener(e -> handleAction("Cancelled"));
        gbc.gridx  = 3;
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

    //Event Handlers
    private void handleAction(String action) {
        int selectedRow = queue.getSelectedRow();
 
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        String student   = (String) tableModel.getValueAt(selectedRow, 1);
        String current   = (String) tableModel.getValueAt(selectedRow, 4);
 
        if (!current.equals("Pending")) {
            JOptionPane.showMessageDialog(this,
                    "This reservation has already been " + current.toLowerCase() + ".",
                    "Already Actioned", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action.toLowerCase().replace("d","") + " booking "
                + bookingId + " for " + student + "?",
                "Confirm Action", JOptionPane.YES_NO_OPTION);
 
        if (confirm == JOptionPane.YES_OPTION) {
            //Update the status column in the table
            tableModel.setValueAt(action, selectedRow, 4);
            updatePendingCount();
 
            // TODO: Send status update to server (Phase 2)
            // Server will then push update to all connected clients (Requirement #4)
 
            JOptionPane.showMessageDialog(this,
                    "Booking " + bookingId + " has been " + action.toLowerCase() + ".\n"
                    + "All connected clients will be notified.",
                    "Action Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
 
    private void refreshQueue() {
        // TODO: Request fresh queue from server (Phase 2)
        loadPendingReservations();
    }
 
    //Data Loading
    private void loadPendingReservations() {
        tableModel.setRowCount(0);
 
        // TODO: Replace with server request (Phase 2)
        Object[][] data = {
            {"R-1038", "Tajhbe Brown",   "EQ-OSC-2210 — Oscilloscope",            "24 Mar 2026, 02:00 PM", "Pending",  ""},
            {"R-1039", "Marcus Reid",    "SCIT Networking & Systems Lab — Seat 8", "26 Mar 2026, 09:00 AM", "Pending",  ""},
            {"R-1040", "Shania Clarke",  "EQ-3DP-0007 — 3D Printer (metal)",       "27 Mar 2026, 01:00 PM", "Pending",  ""},
            {"R-1035", "Devroy Grant",   "SCIT Software Eng. Lab — Seat 3",        "22 Mar 2026, 11:00 AM", "Approved", ""},
            {"R-1030", "Kezia Thompson", "EQ-NET-1021 — Network Switch (L3)",      "18 Mar 2026, 10:00 AM", "Rejected", ""},
        };
 
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
 
        updatePendingCount();
    }
 
    private void updatePendingCount() {
        int count = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("Pending".equals(tableModel.getValueAt(i, 4))) count++;
        }
        pendingCountLabel.setText(count + " Pending");
    }
 
 
    //Status Cell Renderer - colors the Status column by value
    static class StatusCellRenderer extends JLabel implements TableCellRenderer {
 
        public StatusCellRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setHorizontalAlignment(SwingConstants.CENTER);
        }
 
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
 
            String status = value == null ? "" : value.toString();
            setText(status);
 
            switch (status) {
                case "Pending"   -> { setBackground(new Color(250, 238, 218)); setForeground(new Color(133, 79, 11)); }
                case "Approved"  -> { setBackground(new Color(234, 243, 222)); setForeground(new Color(59, 109, 17)); }
                case "Rejected"  -> { setBackground(new Color(252, 235, 235)); setForeground(new Color(163, 45, 45)); }
                case "Cancelled" -> { setBackground(new Color(240, 240, 240)); setForeground(new Color(90, 90, 90)); }
                default          -> { setBackground(Color.WHITE);              setForeground(Color.BLACK); }
            }
 
            if (isSelected) {
                setBackground(new Color(210, 225, 245));
                setForeground(new Color(26, 39, 68));
            }
 
            return this;
        }
    }
}
