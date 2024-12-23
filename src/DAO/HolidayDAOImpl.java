package DAO;

import Model.Holiday;
import Model.Type;
import Model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAOImpl implements GenericDAO<Holiday> {

    // SQL queries
    private static final String INSERT_HOLIDAY_SQL = "INSERT INTO holiday (employeeId, startDate, endDate, type) VALUES (?, ?, ?, ?)";
    private static final String DELETE_HOLIDAY_SQL = "DELETE FROM holiday WHERE id = ?";
    private static final String SELECT_ALL_HOLIDAY_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM holiday h JOIN employe e ON h.employeeId = e.id";
    private static final String SELECT_HOLIDAY_BY_ID_SQL = "SELECT h.id, CONCAT(e.nom, ' ', e.prenom) AS employeeName, h.startDate, h.endDate, h.type FROM holiday h JOIN employe e ON h.employeeId = e.id WHERE h.id = ?";
    private static final String SELECT_EMPLOYEE_ID_BY_NAME_SQL = "SELECT id FROM employe WHERE CONCAT(nom, ' ', prenom) = ?";

    @Override
public void add(Holiday holiday) {
    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_HOLIDAY_SQL)) {
        int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
        if (employeeId == -1) {
            System.out.println("Erreur : Employé introuvable.");
            return;
        }

        // Get employee details to update their leave balance
        Employee employee = getEmployeeById(employeeId);
        int requiredDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.parse(holiday.getStartDate()), 
                java.time.LocalDate.parse(holiday.getEndDate())
        );
        if (employee.getSolde() >= requiredDays) {
            employee.reduceSolde(requiredDays);

            // Mise à jour du solde dans la base de données
            updateEmployeeSolde(employee);

            stmt.setInt(1, employeeId);
            stmt.setString(2, holiday.getStartDate());
            stmt.setString(3, holiday.getEndDate());
            stmt.setString(4, holiday.getType().name());
            stmt.executeUpdate();
            System.out.println("Congé ajouté avec succès.");
        } else {
            System.out.println("Erreur : Solde de congés insuffisant.");
        }

    } catch (SQLException e) {
        System.err.println("Erreur lors de l'ajout du congé : " + e.getMessage());
        e.printStackTrace();
    }
}

public void updateEmployeeSolde(Employee employee) {
    String updateSoldeSql = "UPDATE employe SET solde = ? WHERE id = ?";
    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(updateSoldeSql)) {
        stmt.setInt(1, employee.getSolde());
        stmt.setInt(2, employee.getId());
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Erreur lors de la mise à jour du solde de l'employé : " + e.getMessage());
        e.printStackTrace();
    }
}





public Employee getEmployeeById(int employeeId) {
        // Query to get employee details
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employe WHERE id = ?")) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setNom(rs.getString("nom"));
                employee.setPrenom(rs.getString("prenom"));
                employee.setEmail(rs.getString("email"));
                employee.setPhone(rs.getString("phone"));
                employee.setSalaire(rs.getDouble("salaire"));
                // Retrieve the solde from the database
                employee.setSolde(rs.getInt("solde"));
                return employee;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'employé : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour supprimer un congé par ID
    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_HOLIDAY_SQL)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Congé supprimé avec succès.");
            } else {
                System.out.println("Aucun congé trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du congé : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour lister tous les congés
    @Override
    public List<Holiday> listAll() {
        List<Holiday> holidays = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_HOLIDAY_SQL); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Holiday holiday = new Holiday(
                        rs.getInt("id"),
                        rs.getString("employeeName"),
                        rs.getString("startDate"),
                        rs.getString("endDate"),
                        Type.valueOf(rs.getString("type"))
                );
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des congés : " + e.getMessage());
            e.printStackTrace();
        }
        return holidays;
    }

    // Méthode pour trouver un congé par ID
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
            System.err.println("Erreur lors de la recherche du congé : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour mettre à jour un congé
    @Override
    
    public void update(Holiday holiday, int id) {
    try (Connection conn = DBConnection.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement("UPDATE holiday SET employeeId = ?, startDate = ?, endDate = ?, type = ? WHERE id = ?")) {
         
        // Vérification de l'ID de l'employé
        int employeeId = getEmployeeIdByName(holiday.getEmployeeName());
        if (employeeId == -1) {
            System.out.println("Erreur : Employé introuvable.");
            return;
        }

        // Calcul de la durée du congé avant et après modification
        Holiday existingHoliday = findById(id); // On récupère le congé existant
        Employee employee = getEmployeeById(employeeId); // On récupère l'employé
        int previousDuration = (int) java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.parse(existingHoliday.getStartDate()),
                java.time.LocalDate.parse(existingHoliday.getEndDate())
        );
        int newDuration = (int) java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.parse(holiday.getStartDate()),
                java.time.LocalDate.parse(holiday.getEndDate())
        );

        // Mise à jour du solde en fonction de la modification
        if (newDuration > previousDuration) {
            int additionalDays = newDuration - previousDuration;
            if (employee.getSolde() >= additionalDays) {
                employee.reduceSolde(additionalDays);
                updateEmployeeSolde(employee); // Mise à jour du solde dans la base
            } else {
                System.out.println("Erreur : Solde de congés insuffisant pour la modification.");
                return;
            }
        } else {
            // Si la durée est réduite, on réajuste le solde
            int daysToAdd = previousDuration - newDuration;
            employee.addSolde(daysToAdd); // Ajout au solde
            updateEmployeeSolde(employee); // Mise à jour du solde dans la base
        }

        // Préparer la requête pour la mise à jour
        stmt.setInt(1, employeeId);
        stmt.setString(2, holiday.getStartDate());
        stmt.setString(3, holiday.getEndDate());
        stmt.setString(4, holiday.getType().name());
        stmt.setInt(5, id);
        
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Congé mis à jour avec succès.");
        } else {
            System.out.println("Aucun congé trouvé avec cet ID.");
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors de la mise à jour du congé : " + e.getMessage());
        e.printStackTrace();
    }
}


    public boolean isHolidayOverlapping(int employeeId, String startDate, String endDate) {
        String query = "SELECT * FROM holiday WHERE employeeId = ? AND (" +
                       "(startDate <= ? AND endDate >= ?) OR " +
                       "(startDate >= ? AND startDate <= ?))";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, endDate);  // endDate is the latest date
            stmt.setString(3, startDate);  // startDate is the earliest date
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
    
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // If there is any overlapping holiday, it will return true
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des chevauchements de congé : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    


    // Méthode pour récupérer l'ID de l'employé par nom complet
    public int getEmployeeIdByName(String employeeName) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_ID_BY_NAME_SQL)) {
            stmt.setString(1, employeeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'ID employé : " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Méthode pour récupérer tous les noms des employés
    public List<String> getAllEmployeeNames() {
        List<String> employeeNames = new ArrayList<>();
        String query = "SELECT CONCAT(nom, ' ', prenom) AS fullName FROM employe";
    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                String fullName = rs.getString("fullName");
                System.out.println("Nom récupéré : " + fullName); // Pour debug
                employeeNames.add(fullName);
            }
    
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms des employés : " + e.getMessage());
            e.printStackTrace();
        }
    
        return employeeNames;
    }
    
    
    

    // Méthode pour récupérer les types de congés (Enum)
    public List<Type> getAllHolidayTypes() {
        List<Type> holidayTypes = new ArrayList<>();
        for (Type type : Type.values()) {
            holidayTypes.add(type);
        }
        return holidayTypes;
    }
}