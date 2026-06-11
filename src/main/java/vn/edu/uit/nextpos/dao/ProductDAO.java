package vn.edu.uit.nextpos.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.uit.nextpos.models.Product;
import vn.edu.uit.nextpos.util.DatabaseConnection;
import com.google.gson.Gson;
import vn.edu.uit.nextpos.models.AuditLog;
import vn.edu.uit.nextpos.dao.AuditLogDAO;

/**
 * ProductDAO dùng để thao tác với bảng products và các nghiệp vụ liên quan đến
 * hóa đơn trong hệ thống POS.
 *
 * Cung cấp các phương thức để: - Lấy danh sách sản phẩm - Thêm, sửa, xóa sản
 * phẩm - Tìm kiếm sản phẩm theo barcode
 *
 * Sử dụng kết nối từ lớp DatabaseConnection để thao tác với database.
 *
 * @author 04dkh
 */
public class ProductDAO {

    /**
     * Lấy danh sách toàn bộ sản phẩm từ database.
     *
     * @return danh sách sản phẩm
     */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_path")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Tìm kiếm sản phẩm theo ID.
     *
     * @param id mã định danh sản phẩm
     * @return đối tượng Product nếu tìm thấy, null nếu không có
     */
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("barcode"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("image_path")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm sản phẩm mới vào database.
     *
     * @param p đối tượng sản phẩm cần thêm
     */
    public void insertProduct(Product p) {
        insertProduct(p, 1); // Mặc định Admin
    }

    public void insertProduct(Product p, int employeeId) {
        String sql = "INSERT INTO products (name, price, quantity, image_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getName());
            stmt.setDouble(2, p.getPrice());
            stmt.setInt(3, p.getQuantity());
            stmt.setString(4, p.getImagePath());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }

            AuditLogDAO auditDAO = new AuditLogDAO(conn);
            Gson gson = new Gson();
            AuditLog log = new AuditLog(employeeId, "INSERT", "products", p.getId(), null, gson.toJson(p));
            auditDAO.insertAuditLog(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật thông tin sản phẩm theo ID.
     *
     * @param p đối tượng sản phẩm cần cập nhật
     */
    public void updateProduct(Product p) {
        updateProduct(p, 1); // Mặc định Admin
    }

    public void updateProduct(Product p, int employeeId) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ?, image_path = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            Product oldProduct = getProductById(p.getId());

            stmt.setString(1, p.getName());
            stmt.setDouble(2, p.getPrice());
            stmt.setInt(3, p.getQuantity());
            stmt.setString(4, p.getImagePath());
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();

            AuditLogDAO auditDAO = new AuditLogDAO(conn);
            Gson gson = new Gson();
            AuditLog log = new AuditLog(employeeId, "UPDATE", "products", p.getId(), gson.toJson(oldProduct), gson.toJson(p));
            auditDAO.insertAuditLog(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa sản phẩm theo ID.
     *
     * @param id mã sản phẩm cần xóa
     */
    public void deleteProduct(int id) {
        deleteProduct(id, 1); // Mặc định Admin
    }

    public void deleteProduct(int id, int employeeId) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            Product oldProduct = getProductById(id);

            stmt.setInt(1, id);
            stmt.executeUpdate();

            AuditLogDAO auditDAO = new AuditLogDAO(conn);
            Gson gson = new Gson();
            AuditLog log = new AuditLog(employeeId, "DELETE", "products", id, gson.toJson(oldProduct), null);
            auditDAO.insertAuditLog(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tìm kiếm sản phẩm theo barcode.
     *
     * @param barcode mã vạch sản phẩm
     * @return sản phẩm tương ứng hoặc null nếu không tìm thấy
     */
    public Product getProductByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("barcode"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("image_path")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
