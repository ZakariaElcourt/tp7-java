package DAO;

import Model.Holiday;
import Model.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAOImpl implements GenericDAO<Holiday> {

    // Constants for SQL queries
    private static final String INSERT_HOLIDAY_SQL = "INSERT INTO Holiday (employeeId, startDate, endDate, type) VALUES (?, ?, ?, ?)";
    private static final String DELETE_HOLIDAY_SQL = "DELETE FROM Holiday WHERE id = ?";
    private static final String SELECT_ALL_HOLIDAY_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM Holiday h JOIN Employee e ON h.employeeId = e.id";
    private static final String SELECT_HOLIDAY_BY_ID_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM Holiday h JOIN Employee e ON h.employeeId = e.id WHERE h.id = ?";
    private static final String SELECT_EMPLOYEE_ID_BY_NAME_SQL = "SELECT id FROM Employee WHERE CONCAT(nom, ' ', prenom) = ?";
    private static final String SELECT_ALL_EMPLOYEE_NAMES_SQL = "SELECT CONCAT(nom, ' ', prenom) AS fullName FROM Employee";

    // Method to add a holiday
    @Override
    public void add(Holiday holiday) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_HOLIDAY_SQL)) {
            // Retrieve the employee ID based on the employee name
            int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
            if (employeeId == -1) {
                System.out.println("Error: Employee not found.");
                return;
            }
            stmt.setInt(1, employeeId);
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name()); // Store the enum as a string
            stmt.executeUpdate();
            System.out.println("Holiday added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding holiday: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to delete a holiday by ID
    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_HOLIDAY_SQL)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Holiday deleted successfully.");
            } else {
                System.out.println("No holiday found with this ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting holiday: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to list all holidays
    @Override
    public List<Holiday> listAll() {
        List<Holiday> holidays = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_HOLIDAY_SQL); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Holiday holiday = new Holiday(
                        rs.getInt("id"),
                        rs.getString("employeeName"),  // Concatenated employee name (nom + prenom)
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))  // Convert string back to enum
                );
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            System.err.println("Error listing holidays: " + e.getMessage());
            e.printStackTrace();
        }
        return holidays;
    }

    // Method to find a holiday by ID
    @Override
    public Holiday findById(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_HOLIDAY_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Holiday(
                        rs.getInt("id"),
                        rs.getString("employeeName"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding holiday by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Method to update a holiday by ID
    @Override
    public void update(Holiday holiday, int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE Holiday SET employeeId = ?, startDate = ?, endDate = ?, type = ? WHERE id = ?")) {
            int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
            if (employeeId == -1) {
                System.out.println("Error: Employee not found.");
                return;
            }
            stmt.setInt(1, employeeId);
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());  // Store enum as string
            stmt.setInt(5, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Holiday updated successfully.");
            } else {
                System.out.println("No holiday found with this ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating holiday: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to retrieve employee ID by full name (concatenated nom + prenom)
    public int getEmployeeIdByName(String employeeName) { // changer 'private' en 'public'
    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_ID_BY_NAME_SQL)) {
        stmt.setString(1, employeeName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving employee ID by name: " + e.getMessage());
        e.printStackTrace();
    }
    return -1;  // Return -1 if the employee is not found
}

    // Method to retrieve all employee names for the combo box
    public List<String> getAllEmployeeNames() {
        List<String> employeeNames = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EMPLOYEE_NAMES_SQL); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String fullName = rs.getString("fullName");
                employeeNames.add(fullName);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all employee names: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Employee Names: " + employeeNames);  // Debugging output
        return employeeNames;
    }
    

    // Method to retrieve all holiday types (Enum)
    public List<Type> getAllHolidayTypes() {
        List<Type> holidayTypes = new ArrayList<>();
        for (Type type : Type.values()) {
            holidayTypes.add(type);
        }
        return holidayTypes;
    }
}
