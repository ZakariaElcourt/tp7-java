package Controller;

import DAO.GenericDAO;
import DAO.HolidayDAOImpl;
import Model.Holiday;
import Model.Type; // Importing the correct enum Type
import View.HolidayView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HolidayController {
    private final HolidayView view;
    private final GenericDAO<Holiday> dao;

    public HolidayController(HolidayView view) {
        this.view = view;
        this.dao = new HolidayDAOImpl();

        // Écouteur pour le bouton Ajouter
        view.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHoliday();
            }
        });

        // Écouteur pour le bouton Lister
        view.listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listHolidays();
            }
        });

        // Écouteur pour le bouton Supprimer
        view.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteHoliday();
            }
        });

        // Écouteur pour le bouton Modifier
        view.modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyHoliday();
            }
        });
    }

    // Méthodes pour gérer les congés
    private void addHoliday() {
        try {
            int employeeId = Integer.parseInt(view.employeeIdField.getText());
            String startDate = view.startDateField.getText();
            String endDate = view.endDateField.getText();
            Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase()); // Using Type enum

            Holiday holiday = new Holiday(employeeId, startDate, endDate, type);
            dao.add(holiday);
            JOptionPane.showMessageDialog(view, "Congé ajouté avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    private void listHolidays() {
        List<Holiday> holidays = dao.listAll();
        String[] columnNames = {"ID", "ID Employé", "Date Début", "Date Fin", "Type"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Holiday holiday : holidays) {
            Object[] row = {holiday.getId(), holiday.getEmployeeId(), holiday.getStartDate(), holiday.getEndDate(), holiday.getType()};
            model.addRow(row);
        }

        view.holidayTable.setModel(model);
    }

    private void deleteHoliday() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(view, "Entrez l'ID du congé à supprimer :"));
            dao.delete(id);
            JOptionPane.showMessageDialog(view, "Congé supprimé avec succès.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }

    private void modifyHoliday() {
        try {
            int selectedRow = view.holidayTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Veuillez sélectionner un congé dans le tableau.");
                return;
            }

            int id = (int) view.holidayTable.getValueAt(selectedRow, 0);
            int employeeId = Integer.parseInt(view.employeeIdField.getText());
            String startDate = view.startDateField.getText();
            String endDate = view.endDateField.getText();
            Type type = Type.valueOf(view.typeCombo.getSelectedItem().toString().toUpperCase()); // Using Type enum

            Holiday updatedHoliday = new Holiday(id, employeeId, startDate, endDate, type);
            dao.update(updatedHoliday, id);

            JOptionPane.showMessageDialog(view, "Congé mis à jour avec succès.");
            listHolidays();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erreur: " + ex.getMessage());
        }
    }
}
