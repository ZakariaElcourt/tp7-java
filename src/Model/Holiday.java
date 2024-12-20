package Model;

public class Holiday {
    private int id;
    private int employeeId;
    private String employeeName;
    private String startDate;
    private String endDate;
    private Type type;

    // Constructeur avec employeeName pour listAll()
    public Holiday(int id, String employeeName, String startDate, String endDate, Type type) {
        this.id = id;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public Holiday(String employeeName, String startDate, String endDate, Type type) {
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public Holiday(int employeeId, String startDate, String endDate, Type type) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Type getType() {
        return type;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
