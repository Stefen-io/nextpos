package vn.edu.uit.nextpos.view;

import java.awt.*;
import javax.swing.*;
import vn.edu.uit.nextpos.util.IconUtil;

/**
 * Lớp MainFrame là cửa sổ chính của ứng dụng POS. Giao diện bao gồm sidebar
 * menu bên trái và vùng nội dung trung tâm động, thay đổi theo lựa chọn của
 * người dùng.
 */
public class MainFrame extends JFrame {

    /**
     * Panel hiển thị nội dung trung tâm của ứng dụng.
     */
    private JPanel mainContent;

    /**
     * Khởi tạo giao diện chính của ứng dụng POS. Thiết lập title, icon, layout,
     * sidebar menu và vùng nội dung chính.
     */
    public MainFrame() {
        setTitle("POS - Quản lý bán hàng");
        setBounds(100, 100, 1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Đặt icon cửa sổ bằng tiện ích IconUtil
        ImageIcon icon = IconUtil.loadPng("logo.png", 180);
        setIconImage(icon.getImage());

        // Sidebar điều hướng
        SideBarMenu sideBar = new SideBarMenu(this::showContentPanel, () -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setExtendedState(JFrame.NORMAL);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
        add(sideBar, BorderLayout.WEST);

        // Vùng hiển thị nội dung chính
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Chào mừng đến hệ thống POS", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(60, 60, 60));
        mainContent.add(welcomeLabel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Thay đổi nội dung hiển thị ở vùng trung tâm.
     *
     * @param panel JPanel cần hiển thị thay thế nội dung cũ
     */
    public void showContentPanel(JPanel panel) {
        mainContent.removeAll();
        mainContent.add(panel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }
}
