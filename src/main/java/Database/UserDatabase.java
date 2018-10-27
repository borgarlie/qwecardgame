package Database;

import API.authentication.Roles;
import Pojos.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Database.DatabaseUtils.getConnection;

public class UserDatabase {

    // TODO: This should only be available for ADMIN role
    public static List<User> getAll() {
        String sql = "SELECT * FROM users";
        ArrayList<User> users = new ArrayList<>();
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User user = buildUserFromResultSet(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static User buildUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(rs.getString("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .username(rs.getString("username"))
                .role(Roles.valueOf(rs.getString("role")))
                .build();
    }

    public static Optional<User> get(String id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next()) {
                User user = buildUserFromResultSet(rs);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /*
        Returns the id of the created user
     */
    public static String create(User user) {
        String sql = "INSERT INTO users(user_id, email, name, username, role) VALUES(?, ?, ?, ?, ?)";
        Connection conn = DatabaseUtils.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, user.getRole().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // TODO: Should handle error somehow
            // Just throw SQL exceptions ?
            return null;
        }
        return user.getUserId();
    }

    public static boolean updateUsername(User user) {
        String sql = "UPDATE users SET username = ? WHERE user_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

}
