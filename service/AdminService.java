package employeemanagementsystem.service;

import employeemanagementsystem.dao.Database;
import java.sql.*;

public class AdminService {

    public boolean validateAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = Database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}