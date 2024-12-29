package Main;

import Controller.EmployeeController;
import Controller.HolidayController;
import Controller.LoginController;
import DAO.EmployeeDAOImpl;
import DAO.HolidayDAOImpl;
import DAO.LoginDAOImpl;
import Model.Employee;
import Model.EmployeeModel;
import Model.HolidayModel;
import Model.LoginModel;
import View.EmployeeView;
import View.HolidayView;
import View.LoginView;
import View.PanelsView;

public class Main {

    public static void main(String[] args) {
        /*pour la gestion des employes et conges avec login (TP1&2) */
        LoginController loginController = new LoginController(new LoginModel(new LoginDAOImpl()), LoginView.getInstance());
       // le main ecrit sur le tp des E/S
       /* EmployeeView employeeView = new EmployeeView();  
        HolidayView holidayView = new HolidayView();

        EmployeeDAOImpl daoEmploye = new EmployeeDAOImpl();  
        HolidayDAOImpl daoHoliday = new HolidayDAOImpl();  

        EmployeeModel employeeModel = new EmployeeModel(daoEmploye);  
        HolidayModel holidayModel = new HolidayModel(daoHoliday);  

        Employee employee = new Employee(); 

        PanelsView view = PanelsView.getInstance(employeeView, holidayView);  

        new EmployeeController(employeeModel, employeeView, employee);  
        new HolidayController(holidayModel, holidayView, employee);  

        view.setVisible(true);  */
        
    }
}