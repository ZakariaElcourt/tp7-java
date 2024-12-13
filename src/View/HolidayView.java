package View;

import javax.swing.*;
import java.awt.*;

public class HolidayView extends JFrame {
    public JTable holidayTable;
    public JButton addButton, listButton, deleteButton, modifyButton, switchViewButton;
    public JTextField employeeIdField, startDateField, endDateField;
    public JComboBox<String> typeCombo;

    public HolidayView() {
        setTitle("Gestion des Congés");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 lignes et 2 colonnes

        inputPanel.add(new JLabel("Employé ID:"));
        employeeIdField = new JTextField();
        inputPanel.add(employeeIdField);

        inputPanel.add(new JLabel("Date Début:"));
        startDateField = new JTextField();
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("Date Fin:"));
        endDateField = new JTextField();
        inputPanel.add(endDateField);

        inputPanel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"CONGE_PAYE", "CONGE_MALADIE", "CONGE_SANS_SOLDE"});
        inputPanel.add(typeCombo);

        add(inputPanel, BorderLayout.NORTH);

        holidayTable = new JTable();
        add(new JScrollPane(holidayTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Ajouter");
        buttonPanel.add(addButton);
        listButton = new JButton("Afficher");
        buttonPanel.add(listButton);
        deleteButton = new JButton("Supprimer");
        buttonPanel.add(deleteButton);
        modifyButton = new JButton("Modifier");
        buttonPanel.add(modifyButton);

        // Adding the navigation button to switch to the Employee view
        switchViewButton = new JButton("Voir les Employés");
        buttonPanel.add(switchViewButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
