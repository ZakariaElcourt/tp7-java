package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DAO.DBConnection;
import DAO.EmployeeDAOImpl;
import Utilities.Utils;
import View.EmployeeView;

public class EmployeeModel {
    private EmployeeDAOImpl dao;
    public EmployeeModel(EmployeeDAOImpl dao) {
        this.dao = dao;
    }
    public void exportData(String filePath, List<Employee> employees) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Id,Nom,Prenom,Email,Salaire,Phone,Role,Poste,HolidayBalance\n");
            for (Employee employee : employees) {
                writer.write(employee.getId() + "," +
                             employee.getNom() + "," +
                             employee.getPrenom() + "," +
                             employee.getEmail() + "," +
                             employee.getSalaire() + "," +
                             employee.getPhone() + "," +
                             employee.getRole() + "," +
                             employee.getPoste() + "," +
                             employee.getHolidayBalance() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkFileExists(File file) {
    if (!file.exists()) {
        throw new IllegalArgumentException("Le fichier n'existe pas : " + file.getPath());
    }
    return true;
}
public boolean checkIsFile(File file) {
    if (!file.isFile()) {
        throw new IllegalArgumentException("Le chemin spécifié n'est pas un fichier : " + file.getPath());
    }
    return true;
}
public boolean checkIsReadable(File file) {
    if (!file.canRead()) {
        throw new IllegalArgumentException("Le fichier n'est pas lisible : " + file.getPath());
    }
    return true;
}
public List<Employee> findAll() throws SQLException {
    List<Employee> employees = new ArrayList<>();
    String query = "SELECT * FROM employee"; // Votre table dans la base de données

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Employee employee = new Employee(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getDouble("salaire"),
                rs.getString("email"),
                rs.getString("phone"),
                Role.valueOf(rs.getString("role")),
                Poste.valueOf(rs.getString("poste")),
                rs.getInt("holidayBalance") // Ajout du solde des congés
            );
            employees.add(employee);
        }
    }
    return employees;
}

    public List<Employee> importData(String filePath) {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Lire la première ligne (les en-têtes)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length == 9) { // Vérifie que toutes les colonnes sont présentes
                    Employee employee = new Employee();
                    employee.setId(Integer.parseInt(fields[0]));
                    employee.setNom(fields[1]);
                    employee.setPrenom(fields[2]);
                    employee.setEmail(fields[3]);
                    employee.setSalaire(Double.parseDouble(fields[4]));
                    employee.setPhone(fields[5]);
                    employee.setRole(Role.valueOf(fields[6]));
                    employee.setPoste(Poste.valueOf(fields[7]));
                    employee.setHolidayBalance(Integer.parseInt(fields[8]));

                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de l'analyse des données du fichier : " + e.getMessage());
        }

        return employees;
    }

    public boolean ajouterEmployee(String nom, String prenom, String salaire, String email, String phone, Role role, Poste poste) {
        double salaireDouble = Utils.parseDouble(salaire);
        if(nom.trim().isEmpty() || prenom.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || salaireDouble == 0) {
            EmployeeView.AjouterFail("Veuillez remplir tous les champs.");
            return false;
        }
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            EmployeeView.AjouterFail("Veuillez entrer une adresse email valide.");
            return false;
        }
        if(!phone.matches("^0\\d{9}$")) {
            EmployeeView.AjouterFail("Le numéro de téléphone doit contenir 10 chiffres");
            return false;
        }
        
        if(salaireDouble < 0 ){
            EmployeeView.AjouterFail("Le salaire doit être un nombre positif");
            return false;
        }
        Employee employee = new Employee(0, nom, prenom, salaireDouble, email, phone, role, poste,25);
        return dao.ajouter(employee);
    }

    public List<Employee> afficherEmployee() {
        return dao.afficher();
    }
    public void supprimerEmployee(int id) {
        if(EmployeeView.SupprimerConfirmation()){
            dao.supprimer(id);
        }
        return;
    }
    public Employee findById(int id) {
        return dao.findById(id);
    }
    public void updateEmployee(Employee employee, int id, String nom, String prenom, String email, double salaire, String phone, Role role, Poste poste) {
        if (nom.trim().isEmpty() && prenom.trim().isEmpty() && email.trim().isEmpty() && phone.trim().isEmpty() && salaire == 0 && role == null && poste == null) {
            EmployeeView.ModifierFail("Veuillez remplir au moins un champ.");
            return;
        }

        // Récupère l'employé existant de la base de données avant de modifier
        Employee existingEmployee = dao.findById(id);
        if (existingEmployee == null) {
            EmployeeView.ModifierFail("L'employé avec cet ID n'existe pas.");
            return;
        }

        // Mise à jour des champs seulement si de nouvelles valeurs sont fournies
        boolean updated = false;  // Flag pour savoir si une mise à jour a été effectuée
        
        // Mise à jour des valeurs dans l'employé existant
        if (!nom.trim().isEmpty()) {
            existingEmployee.setNom(nom);
            updated = true;
        }
        if (!prenom.trim().isEmpty()) {
            existingEmployee.setPrenom(prenom);
            updated = true;
        }
        if (!email.trim().isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                EmployeeView.ModifierFail("Veuillez entrer une adresse email valide.");
                return;
            }
            existingEmployee.setEmail(email);
            updated = true;
        }
        if (salaire != 0) {
            if (salaire < 0) {
                EmployeeView.ModifierFail("Le salaire doit être un nombre positif");
                return;
            }
            existingEmployee.setSalaire(salaire);
            updated = true;
        }
        if (!phone.isEmpty()) {
            if (!phone.matches("^0\\d{9}$")) {
                EmployeeView.ModifierFail("Le numéro de téléphone doit contenir 10 chiffres");
                return;
            }
            existingEmployee.setPhone(phone);
            updated = true;
        }
        if (role != null) {
            existingEmployee.setRole(role);
            updated = true;
        }
        if (poste != null) {
            existingEmployee.setPoste(poste);
            updated = true;
        }

        if (!updated) {
            EmployeeView.ModifierFail("Aucune modification n'a été apportée.");
            return;
        }

        // Si des modifications ont été effectuées, on appelle le DAO pour mettre à jour l'employé dans la base de données
        dao.modifier(existingEmployee, id);
    }
    
}