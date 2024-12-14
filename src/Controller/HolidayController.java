package Controller;

import DAO.GenericDAO;
import DAO.HolidayDAOImpl;
import Model.Holiday;
import Model.Type;
import View.HolidayView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HolidayController {
    private final HolidayView view;
    private final GenericDAO<Holiday> dao;

    public HolidayController(HolidayView view) {
        this.view = view;
        this.dao = new HolidayDAOImpl();

        // Load employee names into JComboBox
        loadEmployeeNames();

        // Adding action listener to the "Add" button using a lambda expression
        view.addButton.addActionListener(e -> addHoliday());
    }

    // Load employee names from the database into the JComboBox
    private void loadEmployeeNames() {
        List<String> employeeNames = ((HolidayDAOImpl) dao).getAllEmployeeNames();
        for (String name : employeeNames) {
            view.employeeNameComboBox.addItem(name);
        }
    }

    // Add holiday to the database
    private void addHoliday() {
        try {
            // Get the selected employee, start date, end date, and holiday type
            String employeeName = (String) view.employeeNameComboBox.getSelectedItem();
            String startDate = view.startDateField.getText();
            String endDate = view.endDateField.getText();
            Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase());

            // Validate inputs before creating Holiday object
            if (employeeName == null || startDate.isEmpty() || endDate.isEmpty()) {
                throw new IllegalArgumentException("Veuillez remplir tous les champs.");
            }

            // Get the employeeId from the database based on the employeeName
            int employeeId = ((HolidayDAOImpl) dao).getEmployeeIdByName(employeeName);

            if (employeeId == -1) {
                throw new IllegalArgumentException("L'employé sélectionné n'existe pas.");
            }

            // Create a Holiday object using the employeeId
            Holiday holiday = new Holiday(employeeId, startDate, endDate, type);
            dao.add(holiday);

            // Show success message
            JOptionPane.showMessageDialog(view, "Congé ajouté avec succès.");

        } catch (IllegalArgumentException ex) {
            // Catch validation errors
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        } catch (Exception ex) {
            // Catch other exceptions
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }
}
