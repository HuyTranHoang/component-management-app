package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.DepartmentDAO;
import vn.aptech.componentmanagementapp.dao.DepartmentDAOImpl;
import vn.aptech.componentmanagementapp.dao.PositionDAO;
import vn.aptech.componentmanagementapp.dao.PositionDAOImpl;

import java.time.LocalDate;

public class Employee extends User{
    private String password;

    private double salary;
    private String image;
    private String citizenID;
    private LocalDate dateOfBirth;
    private LocalDate dateOfHire;
    private Long departmentId;
    private Long positionId;

    // Lazy-loaded Department and Position objects
    private Department department;
    private Position position;
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfHire() {
        return dateOfHire;
    }

    public void setDateOfHire(LocalDate dateOfHire) {
        this.dateOfHire = dateOfHire;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Department getDepartment() {
        if (department == null) {
            // Perform lazy loading for Department object
            DepartmentDAO departmentDAO = new DepartmentDAOImpl();
            department = departmentDAO.getById(departmentId);
        }
        return department;
    }

    public Position getPosition() {
        if (position == null) {
            // Perform lazy loading for Position object
            PositionDAO positionDAO = new PositionDAOImpl();
            position = positionDAO.getById(positionId);
        }
        return position;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name=" + name +
                "salary=" + salary +
                ", image='" + image + '\'' +
                ", citizenID='" + citizenID + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", dateOfHire=" + dateOfHire +
                ", departmentId=" + departmentId +
                ", positionId=" + positionId +
                ", department=" + department +
                ", position=" + position +
                '}';
    }
}
