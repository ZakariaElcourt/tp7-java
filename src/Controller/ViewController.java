package Controller;

import View.EmployeeView;
import View.HolidayView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewController {
    private final EmployeeView employeeView;
    private final HolidayView holidayView;

    public ViewController(EmployeeView employeeView, HolidayView holidayView) {
        this.employeeView = employeeView;
        this.holidayView = holidayView;

        // Action pour passer de EmployeeView à HolidayView
        employeeView.switchViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeView.setVisible(false);
                holidayView.setVisible(true);
            }
        });

        // Action pour passer de HolidayView à EmployeeView
        holidayView.switchViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                holidayView.setVisible(false);
                employeeView.setVisible(true);
            }
        });
    }

    // Méthode principale pour démarrer l'application
    public void start() {
        employeeView.setVisible(true); // Afficher la vue par défaut
    }

    public static void main(String[] args) {
        EmployeeView employeeView = new EmployeeView();
        HolidayView holidayView = new HolidayView();
        ViewController controller = new ViewController(employeeView, holidayView);
        controller.start();
    }
}
