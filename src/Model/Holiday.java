package Model;

public class Holiday {

    private int id;
    private int employeeId; // Reference to the employee
    private String startDate;
    private String endDate;
    private Type type; // Enum type for holiday type

    // Enum for holiday types

    // Constructor with id
    public Holiday(int id, int employeeId, String startDate, String endDate, Type type) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    // Constructor without id
    public Holiday(int employeeId, String startDate, String endDate, Type type) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", type=" + type +
                '}';
    }
}
