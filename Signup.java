package electricity.billing.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Signup extends JFrame implements ActionListener {

    JButton create, back;
    Choice accountType;
    JTextField meter, username, name;
    JPasswordField password;

    Signup() {
        setBounds(450, 150, 700, 400);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(30, 30, 650, 300);
        panel.setBorder(new TitledBorder(new LineBorder(new Color(173, 216, 230), 2), "Create-Account",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(172, 216, 230)));
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        JLabel heading = new JLabel("Create Account As");
        heading.setBounds(100, 50, 140, 20);
        heading.setForeground(Color.GRAY);
        heading.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(heading);

        accountType = new Choice();
        accountType.add("Admin");
        accountType.add("Customer");
        accountType.setBounds(260, 50, 150, 20);
        panel.add(accountType);

        JLabel lblmeter = new JLabel("Meter Number");
        lblmeter.setBounds(100, 90, 140, 20);
        lblmeter.setForeground(Color.GRAY);
        lblmeter.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblmeter.setVisible(false);
        panel.add(lblmeter);

        meter = new JTextField();
        meter.setBounds(260, 90, 150, 20);
        meter.setVisible(false);
        panel.add(meter);

        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(100, 130, 140, 20);
        lblusername.setForeground(Color.GRAY);
        lblusername.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblusername);

        username = new JTextField();
        username.setBounds(260, 130, 150, 20);
        panel.add(username);

        JLabel lblname = new JLabel("Name");
        lblname.setBounds(100, 170, 140, 20);
        lblname.setForeground(Color.GRAY);
        lblname.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblname);

        name = new JTextField();
        name.setBounds(260, 170, 150, 20);
        panel.add(name);

        meter.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent fe) {
                if (!meter.getText().isEmpty()) {
                    try {
                        Conn c = new Conn();
                        if (c.getConnection() == null) {
                            JOptionPane.showMessageDialog(null, "Database connection failed.");
                            return;
                        }

                        String query = "SELECT name FROM login WHERE meter_no = ?";
                        try (PreparedStatement stmt = c.getConnection().prepareStatement(query)) {
                            stmt.setString(1, meter.getText());
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    name.setText(rs.getString("name"));
                                } else {
                                    JOptionPane.showMessageDialog(null, "Meter number not found.");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(100, 210, 140, 20);
        lblpassword.setForeground(Color.GRAY);
        lblpassword.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblpassword);

        password = new JPasswordField();
        password.setBounds(260, 210, 150, 20);
        panel.add(password);

        accountType.addItemListener(ae -> {
            boolean isCustomer = accountType.getSelectedItem().equals("Customer");
            lblmeter.setVisible(isCustomer);
            meter.setVisible(isCustomer);
            name.setEditable(!isCustomer);
            if (!isCustomer) meter.setText("");
        });

        create = new JButton("Create");
        create.setBackground(Color.BLACK);
        create.setForeground(Color.WHITE);
        create.setBounds(140, 260, 120, 25);
        create.addActionListener(this);
        panel.add(create);

        back = new JButton("Back");
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setBounds(300, 260, 120, 25);
        back.addActionListener(this);
        panel.add(back);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == create) {
            String atype = accountType.getSelectedItem();
            String susername = username.getText();
            String sname = name.getText();
            String spassword = new String(password.getPassword());
            String smeter = meter.getText();

            if (atype.equals("Customer") && smeter.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Meter number is required for customers.");
                return;
            }
            if (susername.isEmpty() || spassword.isEmpty() || (atype.equals("Admin") && sname.isEmpty())) {
                JOptionPane.showMessageDialog(null, "Please fill all required fields.");
                return;
            }

            try {
                Conn c = new Conn();
                if (c.getConnection() == null) {
                    JOptionPane.showMessageDialog(null, "Database connection failed.");
                    return;
                }

                // If customer, check if meter number exists
                if (atype.equals("Customer")) {
                    String checkQuery = "SELECT * FROM login WHERE meter_no = ?";
                    try (PreparedStatement checkStmt = c.getConnection().prepareStatement(checkQuery)) {
                        checkStmt.setString(1, smeter);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(null, "Meter number already exists.");
                                return;
                            }
                        }
                    }
                } else {
                    smeter = null; // Admin users should not have meter_no
                }

                // Insert new user
                String query = "INSERT INTO login (username, name, password, user, meter_no) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = c.getConnection().prepareStatement(query)) {
                    stmt.setString(1, susername);
                    stmt.setString(2, sname);
                    stmt.setString(3, spassword);
                    stmt.setString(4, atype);
                    stmt.setString(5, smeter);
                    stmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Account Created Successfully");
                setVisible(false);
                new Login();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (ae.getSource() == back) {
            setVisible(false);
            new Login();
        }
    }

    public static void main(String[] args) {
        new Signup();
    }
}
