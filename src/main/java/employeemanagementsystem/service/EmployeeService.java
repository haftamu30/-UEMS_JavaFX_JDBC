package employeemanagementsystem.service;

import employeemanagementsystem.model.EmployeeData;
import employeemanagementsystem.dao.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    
    public List<EmployeeData> getAllEmployees() {
        List<EmployeeData> listData = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        
        try {
            connect = Database.connectDb();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                EmployeeData employee = new EmployeeData(
                    result.getInt("employee_id"),
                    result.getString("username"),
                    result.getString("fullName"),
                    result.getString("gender"),
                    result.getString("phoneNum"),
                    result.getString("position"),
                    result.getString("email"),
                    result.getString("image"),
                    result.getDate("date"),
                    result.getString("status"),
                    result.getString("address"),
                    result.getString("qualification"),
                    result.getString("specialization"),
                    result.getString("department"),
                    result.getString("employee_type"),
                    result.getDate("date_of_birth"),
                    result.getString("emergency_contact"),
                    result.getDate("hire_date"),
                    result.getString("salary_grade"),
                    result.getString("bank_account"),
                    result.getString("tax_number"),
                    result.getString("insurance_number")
                );
                listData.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    public boolean addEmployee(EmployeeData employee) {
        // Implementation for adding employee
        // This logic is currently in the controller, but could be moved here
        return false;
    }
    
    public boolean updateEmployee(EmployeeData employee) {
        // Implementation for updating employee
        return false;
    }
    
    public boolean deleteEmployee(int employeeId) {
        // Implementation for deleting employee
        return false;
    }
}