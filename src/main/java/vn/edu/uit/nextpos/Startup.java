package vn.edu.uit.nextpos;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import vn.edu.uit.nextpos.config.AppPaths;
import vn.edu.uit.nextpos.util.SQLSeeder;

/**
 * Infrastructure bootstrap before any Swing UI is shown.
 */
public final class Startup {

    private Startup() {
    }

    public static void run() {
        AppPaths.ensureDirectoriesExist();

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            setGlobalFont(new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLSeeder.runIfEnabled();
    }

    private static void setGlobalFont(Font font) {
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }
}
