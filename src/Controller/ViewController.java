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

        // Button action for switching to the Holiday view
        employeeView.switchViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeView.setVisible(false);  // Hide Employee view
                holidayView.setVisible(true);    // Show Holiday view
            }
        });

        // Button action for switching to the Employee view
        holidayView.switchViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                holidayView.setVisible(false);  // Hide Holiday view
                employeeView.setVisible(true);  // Show Employee view
            }
        });
    }
}
