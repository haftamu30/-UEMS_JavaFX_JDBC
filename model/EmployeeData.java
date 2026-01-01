package employeemanagementsystem.model;

import java.sql.Date;

// Model class for Employee data
public class EmployeeData {
    private final Integer employeeId;
    private String username;
    private String fullName;
    private String gender;
    private String phoneNum;
    private final String position;
    private String email;
    private String image;
    private Date date;
    private Double salary;
    private String status;
    private String address;
    private String qualification;
    private String specialization;
    private String department;
    private String employeeType;
    private Date dateOfBirth;
    private String emergencyContact;
    private Date hireDate;
    private String salaryGrade;
    private String bankAccount;
    private String taxNumber;
    private String insuranceNumber;

    // Constructor for main employee data
    public EmployeeData(Integer employeeId, String username, String fullName, String gender,
                        String phoneNum, String position, String email, String image, Date date,
                        String status, String address, String qualification, String specialization,
                        String department, String employeeType, Date dateOfBirth, String emergencyContact,
                        Date hireDate, String salaryGrade, String bankAccount, String taxNumber,
                        String insuranceNumber) {
        this.employeeId = employeeId;
        this.username = username;
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNum = phoneNum;
        this.position = position;
        this.email = email;
        this.image = image;
        this.date = date;
        this.status = status;
        this.address = address;
        this.qualification = qualification;
        this.specialization = specialization;
        this.department = department;
        this.employeeType = employeeType;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.hireDate = hireDate;
        this.salaryGrade = salaryGrade;
        this.bankAccount = bankAccount;
        this.taxNumber = taxNumber;
        this.insuranceNumber = insuranceNumber;
    }

    // Constructor for salary info
    public EmployeeData(Integer employeeId, String fullName, String position, Double salary) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.position = position;
        this.salary = salary;
    }

    // Getters
    public Integer getEmployeeId() { return employeeId; }
    
    // Helper method to get formatted ID
    public String getFormattedEmployeeId() {
        return employeeId != null ? String.format("%04d", employeeId) : null;
    }

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public String getPhoneNum() { return phoneNum; }
    public String getPosition() { return position; }
    public String getEmail() { return email; }
    public String getImage() { return image; }
    public Date getDate() { return date; }
    public Double getSalary() { return salary; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getQualification() { return qualification; }
    public String getSpecialization() { return specialization; }
    public String getDepartment() { return department; }
    public String getEmployeeType() { return employeeType; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public String getEmergencyContact() { return emergencyContact; }
    public Date getHireDate() { return hireDate; }
    public String getSalaryGrade() { return salaryGrade; }
    public String getBankAccount() { return bankAccount; }
    public String getTaxNumber() { return taxNumber; }
    public String getInsuranceNumber() { return insuranceNumber; }
}