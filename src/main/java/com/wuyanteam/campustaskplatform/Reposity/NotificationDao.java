package com.wuyanteam.campustaskplatform.Reposity;

import com.wuyanteam.campustaskplatform.utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationDao {

    public void deleteNotificationById(int id) {
        String sql = "DELETE FROM notification WHERE notification_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deleted " + rowsAffected + " row(s).");
            } else {
                System.out.println("No rows were deleted.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}