/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.uit.nextpos.util;

import vn.edu.uit.nextpos.config.AppConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * SQLSeeder dùng để khởi tạo lại cơ sở dữ liệu từ file SQL.
 *
 * Tính năng: - Xoá toàn bộ bảng hiện có (DROP TABLE IF EXISTS) - Đọc và chạy
 * lệnh SQL từ file `.sql` dòng theo dòng
 *
 * Dùng cho mục đích phát triển, testing hoặc khởi tạo hệ thống POS từ đầu.
 *
 * Kích hoạt seeding khi khởi động bằng JVM flag {@code -Dapp.seed-on-startup=true},
 * sau đó gọi {@link #runIfEnabled()} từ điểm vào ứng dụng.
 *
 * ⚠️ CẢNH BÁO: Phương thức này sẽ xoá toàn bộ dữ liệu hiện có trong database.
 *
 * @author 04dkh
 */
public class SQLSeeder {
    private static final String SEED_SQL_RESOURCE = "nextpos/sql/schema.sql";

    public static void runIfEnabled() {
        if (!AppConfig.isSeedOnStartup()) {
            System.out.println("ℹ️ Bỏ qua SQL seeding vì app.seed-on-startup=false");
            return;
        }
        runFromClasspath();
    }

    private static void runFromClasspath() {
        try (InputStream sqlInputStream = SQLSeeder.class.getClassLoader().getResourceAsStream(SEED_SQL_RESOURCE)) {
            if (sqlInputStream == null) {
                throw new IllegalStateException("Không tìm thấy resource SQL: " + SEED_SQL_RESOURCE);
            }
            run(sqlInputStream);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi đọc resource SQL seed:");
            e.printStackTrace();
        }
    }

    private static void run(InputStream sqlInputStream) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            dropAllTables(conn); // 💣 Xoá toàn bộ bảng hiện tại
            runSQLFromFile(conn, sqlInputStream);
            System.out.println("✅ Đã chạy file SQL seed thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi chạy file SQL seed:");
            e.printStackTrace();
        }
    }

    /**
     * Xoá toàn bộ bảng trong cơ sở dữ liệu hiện tại.
     *
     * @param conn kết nối SQL đang sử dụng
     * @throws SQLException nếu có lỗi xảy ra khi thực thi câu lệnh SQL
     */
    private static void dropAllTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES");

        java.util.List<String> tables = new java.util.ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close(); // đóng ResultSet trước khi tiếp tục

        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String table : tables) {
            stmt.executeUpdate("DROP TABLE IF EXISTS `" + table + "`");
        }
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        stmt.close();

        System.out.println("🗑️ Đã xoá toàn bộ bảng");
    }

    /**
     * Đọc nội dung từ file SQL và thực thi từng lệnh (các dòng kết thúc bằng
     * ;).
     *
     * @param conn kết nối SQL đang dùng
     * @param sqlInputStream luồng dữ liệu SQL
     * @throws Exception nếu có lỗi khi đọc hoặc chạy câu lệnh
     */
    private static void runSQLFromFile(Connection conn, InputStream sqlInputStream) throws Exception {
        try (
                Statement stmt = conn.createStatement();
                BufferedReader reader = new BufferedReader(new InputStreamReader(sqlInputStream, StandardCharsets.UTF_8))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) {
                    continue; // bỏ qua dòng comment và dòng trống
                }
                sqlBuilder.append(line);
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    sql = sql.substring(0, sql.length() - 1); // xoá dấu ;
                    stmt.execute(sql);
                    sqlBuilder.setLength(0);
                }
            }
        }
    }
}
