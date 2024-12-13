package Main;

import Controller.EmployeeController;
import View.EmployeeView;
import View.HolidayView;

public class Main {
    public static void main(String[] args) {
        // Create the views
        EmployeeView employeeView = new EmployeeView();
        HolidayView holidayView = new HolidayView();

        // Create the controller and pass both views to it
        new EmployeeController(employeeView, holidayView);

        // Set the Employee view to be visible
        employeeView.setVisible(true);
    }
}
