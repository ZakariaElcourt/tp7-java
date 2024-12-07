package Controller;

import DAO.EmployeeDAOI;
import DAO.EmployeeDAOImpl;
import Model.Employee;
import Model.Poste;
import Model.Role;
import View.EmployeeView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeController {
    private final EmployeeView view;
    private final EmployeeDAOI dao;

    public EmployeeController(EmployeeView view) {
        this.view = view;
        this.dao = new EmployeeDAOImpl();

        // Écouteur pour le bouton Ajouter
        view.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        // Écouteur pour le bouton Lister
        view.listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listEmployees();
            }
        });

        // Écouteur pour le bouton Supprimer
        view.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        // Écouteur pour le bouton Modifier
        view.modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyEmployee();
            }
        });
    }

    // Méthode pour ajouter un employé
    private void addEmployee() {
        try {
            String nom = view.nameField.getText();
            String prenom = view.surnameField.getText();
            String email = view.emailField.getText();
            String phone = view.phoneField.getText();
            double salaire = Double.parseDouble(view.salaryField.getText());
            Role role = Role.valueOf(view.roleCombo.getSelectedItem().toString().toUpperCase());
            Poste poste = Poste.valueOf(view.posteCombo.getSelectedItem().toString().toUpperCase());

            Employee employee = new Employee(nom, prenom, email, phone, salaire, role, poste);
            dao.add(employee);
            JOptionPane.showMessageDialog(view, "Employé ajouté avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    // Méthode pour afficher la liste des employés
    private void listEmployees() {
        List<Employee> employees = dao.listAll();
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Salaire", "Rôle", "Poste"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Employee emp : employees) {
            Object[] row = {emp.getId(), emp.getNom(), emp.getPrenom(), emp.getEmail(), emp.getPhone(), emp.getSalaire(), emp.getRole(), emp.getPoste()};
            model.addRow(row);
        }

        view.employeeTable.setModel(model);
    }

    // Méthode pour supprimer un employé
    private void deleteEmployee() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(view, "Entrez l'ID de l'employé à supprimer :"));
            dao.delete(id);
            JOptionPane.showMessageDialog(view, "Employé supprimé avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    // Méthode pour modifier un employé
    // Méthode pour modifier un employé
private void modifyEmployee() {
    try {
        // Vérifier si une ligne est sélectionnée dans le tableau
        int selectedRow = view.employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Veuillez sélectionner un employé dans le tableau.");
            return;
        }

        // Récupérer l'ID de l'employé sélectionné
        int id = (int) view.employeeTable.getValueAt(selectedRow, 0);

        // Rechercher l'employé depuis la base de données
        Employee existingEmployee = dao.findById(id);
        if (existingEmployee == null) {
            JOptionPane.showMessageDialog(view, "Aucun employé trouvé avec cet ID.");
            return;
        }

        // Charger les informations dans les champs
        view.nameField.setText(existingEmployee.getNom());
        view.surnameField.setText(existingEmployee.getPrenom());
        view.emailField.setText(existingEmployee.getEmail());
        view.phoneField.setText(existingEmployee.getPhone());
        view.salaryField.setText(String.valueOf(existingEmployee.getSalaire()));

        // Utiliser une méthode sécurisée pour l'énumération
        setComboSelection(view.roleCombo, existingEmployee.getRole().name());
        setComboSelection(view.posteCombo, existingEmployee.getPoste().name());

        // Demander confirmation de la mise à jour
        int confirmation = JOptionPane.showConfirmDialog(view, "Confirmez-vous la modification ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            // Mettre à jour les informations
            existingEmployee.setNom(view.nameField.getText());
            existingEmployee.setPrenom(view.surnameField.getText());
            existingEmployee.setEmail(view.emailField.getText());
            existingEmployee.setPhone(view.phoneField.getText());
            existingEmployee.setSalaire(Double.parseDouble(view.salaryField.getText()));
            existingEmployee.setRole(getRoleFromCombo(view.roleCombo));
            existingEmployee.setPoste(getPosteFromCombo(view.posteCombo));

            // Sauvegarder dans la base de données
            dao.update(existingEmployee, id);

            // Rafraîchir la table
            JOptionPane.showMessageDialog(view, "Employé mis à jour avec succès.");
            listEmployees();
        } else {
            JOptionPane.showMessageDialog(view, "Modification annulée.");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
    }
}

// Mapping pour Role
private Role getRoleFromCombo(JComboBox<String> comboBox) {
    Map<String, Role> roleMap = new HashMap<>();
    roleMap.put("Admin", Role.ADMIN);
    roleMap.put("Employé", Role.EMPLOYE);

    String selectedRole = (String) comboBox.getSelectedItem();
    return roleMap.getOrDefault(selectedRole, null);
}

// Mapping pour Poste
private Poste getPosteFromCombo(JComboBox<String> comboBox) {
    Map<String, Poste> posteMap = new HashMap<>();
    posteMap.put("Ingénieur Etude et Développement", Poste.INGENIEURE_ETUDE_ET_DEVELOPPEMENT);
    posteMap.put("Team Leader", Poste.TEAM_LEADER);
    posteMap.put("Pilote", Poste.PILOTE);

    String selectedPoste = (String) comboBox.getSelectedItem();
    return posteMap.getOrDefault(selectedPoste, null);
}

// Méthode pour sélectionner l'élément approprié dans le combo
private void setComboSelection(JComboBox<String> comboBox, String enumValue) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
        if (comboBox.getItemAt(i).equalsIgnoreCase(enumValue.replace("_", " "))) {
            comboBox.setSelectedIndex(i);
            return;
        }
    }
}

    
    
}
