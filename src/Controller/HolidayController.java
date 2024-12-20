package Controller;

import DAO.HolidayDAOImpl;
import Model.Employee;
import Model.Holiday;
import Model.Type;
import View.HolidayView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HolidayController {
    private final HolidayView view;
    private final HolidayDAOImpl dao;

    public HolidayController(HolidayView view) {
        this.view = view;
        this.dao = new HolidayDAOImpl();

        loadEmployeeNames();
        refreshHolidayTable();

        view.addButton.addActionListener(e -> addHoliday());
        view.deleteButton.addActionListener(e -> deleteHoliday());
        view.modifyButton.addActionListener(e -> modifyHoliday());

        // Listener pour la sélection d'une ligne dans le tableau
        view.holidayTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsForSelectedHoliday();
            }
        });
    }

    private void loadEmployeeNames() {
        view.employeeNameComboBox.removeAllItems();
        List<String> names = dao.getAllEmployeeNames();
        names.forEach(view.employeeNameComboBox::addItem);
    }

    private void refreshHolidayTable() {
        List<Holiday> holidays = dao.listAll();
        String[] columnNames = {"ID", "Employé", "Date Début", "Date Fin", "Type"};
        Object[][] data = new Object[holidays.size()][5];

        for (int i = 0; i < holidays.size(); i++) {
            Holiday h = holidays.get(i);
            data[i] = new Object[]{h.getId(), h.getEmployeeName(), h.getStartDate(), h.getEndDate(), h.getType()};
        }

        view.holidayTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        view.employeeNameComboBox.setEnabled(true);
    }

    private void populateFieldsForSelectedHoliday() {
        int selectedRow = view.holidayTable.getSelectedRow();
        if (selectedRow != -1) {
            view.employeeNameComboBox.setEnabled(false);
    
            int id = (int) view.holidayTable.getValueAt(selectedRow, 0);
            String employeeName = (String) view.holidayTable.getValueAt(selectedRow, 1);
            String startDate = (String) view.holidayTable.getValueAt(selectedRow, 2);
            String endDate = (String) view.holidayTable.getValueAt(selectedRow, 3);
            Type type = Type.valueOf(view.holidayTable.getValueAt(selectedRow, 4).toString());
    
            view.employeeNameComboBox.setSelectedItem(employeeName);
            view.startDateField.setText(startDate);
            view.endDateField.setText(endDate);
            view.typeCombo.setSelectedItem(type.toString());
            view.modifyButton.setActionCommand(String.valueOf(id));
        } else {
            clearFields(); // Vider les champs si aucune ligne n'est sélectionnée
        }
    }
    

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        return end.isAfter(start);
    }

    private void addHoliday() {
        try {
            String employeeName = (String) view.employeeNameComboBox.getSelectedItem();
            String startDate = view.startDateField.getText();
            String endDate = view.endDateField.getText();
            Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase());

            int employeeId = dao.getEmployeeIdByName(employeeName);
            if (dao.isHolidayOverlapping(employeeId, startDate, endDate)) {
                throw new IllegalArgumentException("Un congé existe déjà sur cette période.");
            }

            Holiday holiday = new Holiday(employeeName, startDate, endDate, type);
            dao.add(holiday);
            refreshHolidayTable();
            JOptionPane.showMessageDialog(view, "Congé ajouté avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }

    private void modifyHoliday() {
        try {
            String actionCommand = view.modifyButton.getActionCommand();
            if (actionCommand != null && !actionCommand.isEmpty()) {
                int id = Integer.parseInt(actionCommand);

                String startDate = view.startDateField.getText();
                String endDate = view.endDateField.getText();
                Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase());

                if (!isValidDate(startDate) || !isValidDate(endDate)) {
                    throw new IllegalArgumentException("Les dates doivent être au format YYYY-MM-DD.");
                }
                if (!isEndDateAfterStartDate(startDate, endDate)) {
                    throw new IllegalArgumentException("La date de fin doit être après la date de début.");
                }

                Holiday holiday = new Holiday(null, startDate, endDate, type);
                dao.update(holiday, id);
                refreshHolidayTable();
                clearFields(); 
                JOptionPane.showMessageDialog(view, "Congé modifié avec succès.");
            } else {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un congé.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }
    private void clearFields() {
        // Réactiver le combo box des employés
        view.employeeNameComboBox.setEnabled(true);
        // Réinitialiser la sélection du combo box des employés au premier élément
        if (view.employeeNameComboBox.getItemCount() > 0) {
            view.employeeNameComboBox.setSelectedIndex(0);
        }
    
        // Vider les champs de date
        view.startDateField.setText("");
        view.endDateField.setText("");
    
        // Réinitialiser la sélection du type de congé au premier élément
        if (view.typeCombo.getItemCount() > 0) {
            view.typeCombo.setSelectedIndex(0);
        }
    
        // Effacer l'ID sauvegardé dans le bouton de modification (ActionCommand)
        view.modifyButton.setActionCommand("");
    }
    

    private void deleteHoliday() {
        try {
            int selectedRow = view.holidayTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un congé.");
                return;
            }
    
            int id = (int) view.holidayTable.getValueAt(selectedRow, 0);
            Holiday holiday = dao.findById(id);
            String employeeName = holiday.getEmployeeName();
            String startDate = holiday.getStartDate();
            String endDate = holiday.getEndDate();
    
            // Afficher une boîte de dialogue de confirmation
            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Êtes-vous sûr de vouloir supprimer le congé de " + employeeName +
                            " du " + startDate + " au " + endDate + " ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION
            );
    
            // Si l'utilisateur annule, arrêter la suppression
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
    
            // Vérifier si la date de début est dans le futur
            LocalDate start = LocalDate.parse(startDate);
            if (start.isAfter(LocalDate.now())) {
                int employeeId = dao.getEmployeeIdByName(employeeName);
                Employee employee = dao.getEmployeeById(employeeId);
                long days = java.time.temporal.ChronoUnit.DAYS.between(start, LocalDate.parse(holiday.getEndDate()));
                employee.addSolde((int) days);
                dao.updateEmployeeSolde(employee);
            }
    
            // Supprimer le congé
            dao.delete(id);
            refreshHolidayTable();
            clearFields(); // Réinitialiser les champs après suppression
            JOptionPane.showMessageDialog(view, "Congé supprimé avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur : " + ex.getMessage());
        }
    }
    
}
