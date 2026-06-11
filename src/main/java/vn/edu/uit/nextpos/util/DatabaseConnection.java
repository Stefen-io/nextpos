package vn.edu.uit.nextpos.util;

import vn.edu.uit.nextpos.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection cung cấp phương thức kết nối tới cơ sở dữ liệu MySQL.
 *
 * Dùng trong toàn hệ thống POS để lấy đối tượng {@link Connection} cho truy vấn
 * SQL.
 *
 * Cấu hình kết nối (host, user, password) được cấu hình sẵn cho XAMPP và MySQL
 * 8 trở lên.
 *
 * Đảm bảo driver MySQL đã được thêm vào classpath
 * (mysql-connector-j-x.x.xx.jar).
 *
 * Ví dụ sử dụng:
 * <pre>
 *     try (Connection conn = DatabaseConnection.getConnection()) {
 *         // Thực hiện truy vấn
 *     }
 * </pre>
 *
 * @author 04dkh
 */
public class DatabaseConnection {

    /**
     * Tạo và trả về kết nối đến cơ sở dữ liệu.
     *
     * @return đối tượng {@link Connection} nếu kết nối thành công
     * @throws SQLException nếu driver không tìm thấy hoặc kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }
        return DriverManager.getConnection(
                AppConfig.getDbUrl(),
                AppConfig.getDbUser(),
                AppConfig.getDbPassword()
        );
    }
}
