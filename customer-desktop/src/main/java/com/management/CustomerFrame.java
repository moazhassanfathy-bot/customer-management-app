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

public class CustomerFrame extends JFrame {
    private final String BASE_URL = "http://localhost:8080/customers";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtEmail, txtPhone, txtId;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public CustomerFrame() {
        setTitle("Customer Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🌟 إضافة مساحة فارغة (Padding) لراحة العين وتناسق الـ Dark Mode
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());

        // 1. الجدول
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Created At"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. اللوحة السفلية
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

        // 🔥 منع كتابة الحروف في خانة التليفون نهائياً أثناء الكتابة
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

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

        // 🌟 تظبيط الخطوط والألوان الاحترافية للأزرار
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

        // الأحداث
        loadCustomers();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtEmail.setText(tableModel.getValueAt(row, 2).toString());
                txtPhone.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearFields());
    }

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
            JOptionPane.showMessageDialog(this, "فشل الاتصال بالـ Backend API: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🛑 دالة التحقق الذكي (Input Validation)
    private boolean validateInputs(String name, String email, String phone) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "برجاء ملء جميع الخانات!", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "صيغة البريد الإلكتروني غير صحيحة! (مثال: user@email.com)", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (phone.length() < 7 || phone.length() > 15) {
            JOptionPane.showMessageDialog(this, "رقم التليفون غير منطقي! (يجب أن يكون بين 7 إلى 15 رقم)", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        // 🌟 تفعيل الـ Validation هنا
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
                JOptionPane.showMessageDialog(this, "تم إضافة العميل بنجاح!");
                loadCustomers();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "برجاء اختيار عميل لتعديله!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        // 🌟 تفعيل الـ Validation هنا برضه
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
                JOptionPane.showMessageDialog(this, "تم تعديل بيانات العميل!");
                loadCustomers();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteCustomer() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "برجاء اختيار عميل لحذفه!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد من حذف هذا العميل؟", "تأكيد", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + txtId.getText()))
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204 || response.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "تم حذف العميل!");
                    loadCustomers();
                    clearFields();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> new CustomerFrame().setVisible(true));
    }
}