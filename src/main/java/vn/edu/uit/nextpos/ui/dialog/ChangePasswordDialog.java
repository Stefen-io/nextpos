package vn.edu.uit.nextpos.ui.dialog;

import vn.edu.uit.nextpos.config.AppPaths;
import vn.edu.uit.nextpos.dao.EmployeeDAO;
import vn.edu.uit.nextpos.models.Employee;
import vn.edu.uit.nextpos.util.Session;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Dialog cho phép admin đổi mật khẩu của chính mình.
 */
public class ChangePasswordDialog extends JDialog {

    private final JPasswordField pfCurrent = new JPasswordField(20);
    private final JPasswordField pfNew = new JPasswordField(20);
    private final JPasswordField pfConfirm = new JPasswordField(20);
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public ChangePasswordDialog(Frame owner) {
        super(owner, "Đổi mật khẩu", true);
        setLayout(new BorderLayout(10, 10));
        setMinimumSize(new Dimension(480, 320));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        formPanel.add(createFormRow("Mật khẩu hiện tại:", pfCurrent));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Mật khẩu mới:", pfNew));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFormRow("Xác nhận mật khẩu:", pfConfirm));

        JButton btnSubmit = new JButton("Xác nhận");
        btnSubmit.setPreferredSize(new Dimension(120, 36));
        btnSubmit.addActionListener(e -> handleSubmit());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 0));
        bottomPanel.add(btnSubmit);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setSize(Math.max(getWidth(), 480), Math.max(getHeight(), 340));
        setLocationRelativeTo(owner);
    }

    private JPanel createFormRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setPreferredSize(new Dimension(150, 30));

        field.setPreferredSize(new Dimension(260, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private void handleSubmit() {
        Employee current = Session.getCurrentUser();
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Phiên đăng nhập không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentPassword = new String(pfCurrent.getPassword());
        String newPassword = new String(pfNew.getPassword());
        String confirmPassword = new String(pfConfirm.getPassword());

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Employee verified = EmployeeDAO.checkLogin(current.getUsername(), currentPassword);
            if (verified == null) {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối database.\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee updated = copyEmployee(current);
        updated.setPassword(newPassword);
        employeeDAO.updateEmployee(updated);
        Session.login(updated);

        if (accountFileMatchesUsername(current.getUsername())) {
            Session.saveAccount(current.getUsername(), newPassword);
        }

        JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
        dispose();
    }

    private static Employee copyEmployee(Employee source) {
        Employee copy = new Employee();
        copy.setId(source.getId());
        copy.setName(source.getName());
        copy.setUsername(source.getUsername());
        copy.setPassword(source.getPassword());
        copy.setRole_id(source.getRole_id());
        copy.setPhone(source.getPhone());
        copy.setEmail(source.getEmail());
        return copy;
    }

    private static boolean accountFileMatchesUsername(String username) {
        File accountFile = AppPaths.getSessionFile().toFile();
        if (!accountFile.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(accountFile))) {
            String savedUsername = reader.readLine();
            return username != null && username.equals(savedUsername);
        } catch (IOException e) {
            return false;
        }
    }
}
