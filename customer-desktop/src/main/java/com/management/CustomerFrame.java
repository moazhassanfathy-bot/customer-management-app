package com.management.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Main desktop application frame for managing customers.
 * Provides a GUI to perform CRUD operations by communicating with the Spring Boot REST API.
 */
public class CustomerFrame extends JFrame {
    private final String BASE_URL = "http://localhost:8080/customers";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtEmail, txtPhone, txtId;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    /**
     * Initializes the UI components, sets layouts, applies custom styles,
     * loads initial data, and binds event listeners.
     */
    public CustomerFrame() {
        setTitle("Customer Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Created At"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel layouts
        JPanel panelBottom = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel panelForm = new JPanel(new FlowLayout());

        panelForm.add(new JLabel("ID:"));
        txtId = new JTextField(4);
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel("Name:"));
        txtName = new JTextField(12);
        panelForm.add(txtName);

        panelForm.add(new JLabel("Email:"));
        txtEmail = new JTextField(15);
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Phone:"));
        txtPhone = new JTextField(10);
        panelForm.add(txtPhone);

        // Key listener for validating numeric input on phone field
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        // Buttons setup
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add Customer");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);

        panelBottom.add(panelForm);
        panelBottom.add(panelButtons);
        add(panelBottom, BorderLayout.SOUTH);

        // Custom Font and Styling
        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        txtName.setFont(mainFont);
        txtEmail.setFont(mainFont);
        txtPhone.setFont(mainFont);
        table.setFont(mainFont);
        table.setRowHeight(25);

        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(70, 130, 180));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnClear.setBackground(new Color(105, 105, 105));
        btnClear.setForeground(Color.WHITE);

        // Initial fetch
        loadCustomers();

        // Table selection listener to populate input fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtEmail.setText(tableModel.getValueAt(row, 2).toString());
                txtPhone.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        // Event action listeners
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearFields());
    }

    /**
     * Fetches all customer data from the backend server via HTTP GET request.
     * Populates or refreshes the table grid view with the received JSON response.
     */
    private void loadCustomers() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                tableModel.setRowCount(0);
                Map[] customers = objectMapper.readValue(response.body(), Map[].class);
                for (Map customer : customers) {
                    tableModel.addRow(new Object[]{
                            customer.get("id"),
                            customer.get("name"),
                            customer.get("email"),
                            customer.get("phone"),
                            customer.get("createdAt")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to Backend API: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Performs client-side validation logic on user-provided data.
     * Checks for empty constraints, correct regex email format, and numeric length conditions.
     *
     * @param name  The name input string
     * @param email The email input string
     * @param phone The phone input string
     * @return true if all criteria match validation constraints, false otherwise
     */
    private boolean validateInputs(String name, String email, String phone) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Invalid email address format! (Example: user@email.com)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (phone.length() < 7 || phone.length() > 15) {
            JOptionPane.showMessageDialog(this, "Invalid phone number length! Must be between 7 and 15 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Collects form inputs, runs validations, converts payload to JSON data,
     * and sends an HTTP POST request to append a new customer record.
     */
    private void addCustomer() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, email, phone)) return;

        try {
            Map<String, String> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("phone", phone);
            String jsonBody = objectMapper.writeValueAsString(data);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if a targeted row record is selected and sends an HTTP PUT request
     * to modify fields corresponding to the mapped identifier.
     */
    private void updateCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer row to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, email, phone)) return;

        try {
            Map<String, String> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("phone", phone);
            String jsonBody = objectMapper.writeValueAsString(data);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + txtId.getText()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Displays a warning prompt and sends an HTTP DELETE request targeting the entity identifier.
     * Clears local states and updates UI view upon successful removal code returns.
     */
    private void deleteCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer row to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + txtId.getText()))
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204 || response.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                    clearFields();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Resets input field form components to an empty string state and clear active grid rows selections.
     */
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }

    /**
     * Application entry point. Applies the FlatLaf theme skin framework layout
     * and asynchronously triggers frame visibility state on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme UI.");
        }
        SwingUtilities.invokeLater(() -> new CustomerFrame().setVisible(true));
    }
}