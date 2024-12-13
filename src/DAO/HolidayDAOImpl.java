package DAO;

import Model.Holiday;
import Model.Type; // Ensure you're importing the correct Type enum

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAOImpl implements GenericDAO<Holiday> {

    @Override
    public void add(Holiday holiday) {
        String sql = "INSERT INTO Holiday (employeeId, startDate, endDate, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, holiday.getEmployeeId());
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());  // Storing enum name
            stmt.executeUpdate();
            System.out.println("Holiday ajouté avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Holiday WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Holiday supprimé avec succès.");
            } else {
                System.out.println("Aucun Holiday trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Holiday> listAll() {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM Holiday";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Holiday holiday = new Holiday(
                        rs.getInt("id"),
                        rs.getInt("employeeId"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))  // Convert string back to enum
                );
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }

    @Override
    public Holiday findById(int id) {
        String sql = "SELECT * FROM Holiday WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Holiday(
                        rs.getInt("id"),
                        rs.getInt("employeeId"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))  // Convert string back to enum
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Holiday holiday, int id) {
        String sql = "UPDATE Holiday SET employeeId = ?, startDate = ?, endDate = ?, type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, holiday.getEmployeeId());
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());  // Storing enum name
            stmt.setInt(5, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Holiday mis à jour avec succès.");
            } else {
                System.out.println("Aucun Holiday trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
