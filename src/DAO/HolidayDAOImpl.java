package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Employee;
import Model.Holiday;
import Model.HolidayType;
import View.HolidayView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HolidayDAOImpl implements GeneriqueDAOI<Holiday> {
    private Connection connection;

    // Constructeur avec gestion des exceptions
    public HolidayDAOImpl() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de la connexion à la base de données", e);
        }
    }
    public List<Holiday> findByIdLoggedHoliday(int employeeId) {
        String SQL = "SELECT * FROM holiday WHERE employee_id = ?";
        List<Holiday> holidays = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, employeeId); // Set the employee ID parameter
            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    int id = rset.getInt("id");
                    int idEmployee = rset.getInt("employee_id");
                    HolidayType type = HolidayType.valueOf(rset.getString("type"));
                    String start = rset.getString("start_date");
                    String end = rset.getString("end_date");
                    holidays.add(new Holiday(id, idEmployee, type, start, end));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }
    
    public void modifierEmployeeBalance(Employee employee, int employeeId) {
        String SQL = "UPDATE employee SET holidayBalance = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, employee.getHolidayBalance());
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public List<Employee> afficherEmployee() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employee";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                double salaire = resultSet.getDouble("salaire");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                Model.Role role = Model.Role.valueOf(resultSet.getString("role"));
                Model.Poste poste = Model.Poste.valueOf(resultSet.getString("poste"));
                int holidayBalance = resultSet.getInt("holidayBalance");
                Employee employee = new Employee(id, nom, prenom, salaire, email, phone, role, poste, holidayBalance);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public List<Holiday> afficher() {
        List<Holiday> holidays = new ArrayList<>();
        String SQL = "SELECT id, employee_id, type, start_date, end_date FROM holiday";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int employeeId = rs.getInt("employee_id");
                String type = rs.getString("type");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");

                Holiday holiday = new Holiday();
                holiday.setId(id);
                holiday.setIdEmployee(employeeId);
                holiday.setType(HolidayType.valueOf(type));
                holiday.setStart(startDate.toString());
                holiday.setEnd(endDate.toString());

                holidays.add(holiday);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holidays;
    }

    @Override
    public boolean ajouter(Holiday holiday) {
        String SQL = "INSERT INTO holiday (employee_id, type, start_date, end_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, holiday.getIdEmployee());
            stmt.setString(2, holiday.getType().toString());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(holiday.getStart(), formatter);
            LocalDate endDate = LocalDate.parse(holiday.getEnd(), formatter);

            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));

            stmt.executeUpdate();
            HolidayView.success("Congé ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void modifier(Holiday holiday, int holidayId) {
        String SQL = "UPDATE holiday SET employee_id = ?, type = ?, start_date = ?, end_date = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, holiday.getIdEmployee());
            stmt.setString(2, holiday.getType().toString());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(holiday.getStart(), formatter);
            LocalDate endDate = LocalDate.parse(holiday.getEnd(), formatter);

            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(endDate));
            stmt.setInt(5, holidayId);

            stmt.executeUpdate();
            HolidayView.success("Congé modifié avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int holidayId) {
        String SQL = "DELETE FROM holiday WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, holidayId);
            stmt.executeUpdate();
            HolidayView.success("Congé supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Employee findById(int employeeId) {
        String SQL = "SELECT * FROM employee WHERE id = ?";
        Employee employee = null;
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDouble("salaire"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        Model.Role.valueOf(rs.getString("role")),
                        Model.Poste.valueOf(rs.getString("poste")),
                        rs.getInt("holidayBalance")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public Holiday FindHolidayById(int holidayId) {
        String SQL = "SELECT * FROM holiday WHERE id = ?";
        Holiday holiday = null;
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setInt(1, holidayId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    holiday = new Holiday(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        HolidayType.valueOf(rs.getString("type")),
                        rs.getString("start_date"),
                        rs.getString("end_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holiday;
    }
}
