package vn.edu.uit.nextpos;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import vn.edu.uit.nextpos.util.Session;
import vn.edu.uit.nextpos.view.LoginFrame;
import vn.edu.uit.nextpos.view.MainFrame;

/**
 * JVM entry point for NextPOS.
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        Startup.run();
        SwingUtilities.invokeLater(Application::launchUi);
    }

    private static void launchUi() {
        Session.autoLoginFromFile();

        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Bạn cần đăng nhập trước khi sử dụng hệ thống.");
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setExtendedState(JFrame.NORMAL);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        } else {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setExtendedState(JFrame.NORMAL);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        }
    }
}
