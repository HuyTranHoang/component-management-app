package vn.aptech.componentmanagementapp.model;

public class LoginInfo {
    private String id;
    private String email;
    private String password;
    private String citizen_id;
    private long department_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCitizen_id() {
        return citizen_id;
    }

    public void setCitizen_id(String citizen_id) {
        this.citizen_id = citizen_id;
    }

    public long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(long department_id) {
        this.department_id = department_id;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", citizen_id='" + citizen_id + '\'' +
                ", department_id=" + department_id +
                '}';
    }
}
