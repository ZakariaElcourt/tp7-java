package Controller;

import DAO.GenericDAO;
import DAO.EmployeeDAOImpl;
import Model.Employee;
import Model.Poste;
import Model.Role;
import View.EmployeeView;
import View.HolidayView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmployeeController {
    private final EmployeeView view;
    private final GenericDAO<Employee> dao;
    private final HolidayView holidayView;

    public EmployeeController(EmployeeView view, HolidayView holidayView) {
        this.view = view;
        this.dao = new EmployeeDAOImpl();
        this.holidayView = holidayView;

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
        // ActionListener for the "Gérer les Congés" button
         view.switchViewButton.addActionListener(new ActionListener() {
         @Override
          public void actionPerformed(ActionEvent e) {
              view.setVisible(false);  // Hide Employee view
                holidayView.setVisible(true);  // Show Holiday view
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

        // ActionListener pour le bouton "Gérer les Congés"
        view.switchViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setVisible(false); // Hide the Employee View
                holidayView.setVisible(true); // Show the Holiday View
            }
        });
    }

    // Méthodes pour gérer les employés (inchangées)
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

    private void deleteEmployee() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(view, "Entrez l'ID de l'employé à supprimer :"));
            dao.delete(id);
            JOptionPane.showMessageDialog(view, "Employé supprimé avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    private void modifyEmployee() {
        try {
            int selectedRow = view.employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un employé dans le tableau.");
                return;
            }

            int id = (int) view.employeeTable.getValueAt(selectedRow, 0);
            String nom = view.nameField.getText();
            String prenom = view.surnameField.getText();
            String email = view.emailField.getText();
            String phone = view.phoneField.getText();
            double salaire = Double.parseDouble(view.salaryField.getText());
            Role role = Role.valueOf(view.roleCombo.getSelectedItem().toString().toUpperCase());
            Poste poste = Poste.valueOf(view.posteCombo.getSelectedItem().toString().toUpperCase());

            Employee updatedEmployee = new Employee(nom, prenom, email, phone, salaire, role, poste);
            dao.update(updatedEmployee, id);

            JOptionPane.showMessageDialog(view, "Employé mis à jour avec succès.");
            listEmployees();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }
}
