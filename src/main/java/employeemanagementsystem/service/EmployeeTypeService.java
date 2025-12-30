package employeemanagementsystem.service;

import java.sql.*;
import java.util.*;
import employeemanagementsystem.dao.Database;

// Service class for handling employee type related logic
public class EmployeeTypeService {

    public Map<String, List<String>> getAcademicPositions() {
        Map<String, List<String>> positions = new HashMap<>();

        List<String> lecturerDepts = Arrays.asList(
                "Computer Science", "Mathematics", "Physics", "Chemistry",
                "Biology", "Economics", "Electrical Engineering",
                "Mechanical Engineering", "Civil Engineering", "Business Administration"
        );
        positions.put("Lecturer", lecturerDepts);

        List<String> researcherDepts = Arrays.asList(
                "Computer Science", "Mathematics", "Physics", "Chemistry",
                "Biology", "Economics", "Electrical Engineering",
                "Mechanical Engineering", "Civil Engineering"
        );
        positions.put("Researcher", researcherDepts);

        List<String> teachingAssistantDepts = Arrays.asList(
                "Computer Science", "Mathematics", "Physics", "Chemistry",
                "Biology", "Economics"
        );
        positions.put("Teaching Assistant", teachingAssistantDepts);

        List<String> technicalAssistantDepts = Arrays.asList(
                "Engineering Lab", "IT Lab", "Physics Lab", "Chemistry Lab", "Biology Lab"
        );
        positions.put("Technical Assistant", technicalAssistantDepts);

        List<String> labInstructorDepts = Arrays.asList(
                "Physics Lab", "Chemistry Lab", "Biology Lab"
        );
        positions.put("Lab Instructor", labInstructorDepts);

        List<String> professorDepts = Arrays.asList(
                "Computer Science", "Mathematics", "Physics", "Chemistry",
                "Biology", "Economics", "Business Administration"
        );
        positions.put("Professor", professorDepts);

        return positions;
    }

    public Map<String, List<String>> getAdministrativePositions() {
        Map<String, List<String>> positions = new HashMap<>();

        positions.put("HR Officer", Arrays.asList("HR Department", "Recruitment", "Employee Relations"));
        positions.put("Finance Officer", Arrays.asList("Finance Department", "Payroll", "Budgeting"));
        positions.put("Registrar Officer", Arrays.asList("Registrar Office", "Student Records"));
        positions.put("Legal Officer", Arrays.asList("Legal Affairs", "Compliance"));
        positions.put("ICT Officer", Arrays.asList("IT Services", "Software Support"));
        positions.put("Network Engineer", Collections.singletonList("Network Management"));
        positions.put("Librarian", Arrays.asList("University Library", "Archives", "Digital Resources"));
        positions.put("Procurement Officer", Arrays.asList("Procurement Office", "Inventory"));
        positions.put("Maintenance Staff", Arrays.asList("Maintenance", "Electrical Maintenance"));
        positions.put("Security Staff", Collections.singletonList("Security"));
        positions.put("Driver", Collections.singletonList("Transport"));

        return positions;
    }

    public Map<String, List<String>> getQualificationsByPosition(String position) {
        Map<String, List<String>> qualifications = new HashMap<>();

        switch (position) {
            case "Lecturer":
            case "Researcher":
            case "Professor":
                qualifications.put(position, Arrays.asList("PhD", "MSc", "MA"));
                break;
            case "Teaching Assistant":
            case "Lab Instructor":
            case "Registrar Officer":
                qualifications.put(position, Arrays.asList("BSc", "BA", "MSc", "MA"));
                break;
            case "Technical Assistant":
            case "ICT Officer":
                qualifications.put(position, Arrays.asList("BSc", "Diploma", "Certificate"));
                break;
            case "HR Officer":
            case "Procurement Officer":
                qualifications.put(position, Arrays.asList("BSc", "BA", "MBA"));
                break;
            case "Finance Officer":
                qualifications.put(position, Arrays.asList("BSc", "BA", "MBA", "CPA"));
                break;
            case "Legal Officer":
                qualifications.put(position, Arrays.asList("LLB", "LLM", "JD"));
                break;
            case "Librarian":
                qualifications.put(position, Arrays.asList("BSc", "BA", "MLIS"));
                break;
            case "Maintenance Staff":
            case "Security Staff":
            case "Driver":
                qualifications.put(position, Arrays.asList("Certificate", "Diploma"));
                break;
            default:
                qualifications.put(position, Arrays.asList("BSc", "BA", "Diploma"));
        }

        return qualifications;
    }

    public Map<String, List<String>> getSpecializationsByDepartmentAndPosition(String department, String position) {
        Map<String, List<String>> specializations = new HashMap<>();
        String key = department + "_" + position;

        // Computer Science Specializations
        if (department.equals("Computer Science")) {
            switch (position) {
                case "Lecturer":
                case "Professor":
                    specializations.put(key, Arrays.asList(
                            "Artificial Intelligence", "Data Science", "Software Engineering",
                            "Networks & Security", "Database Systems"
                    ));
                    break;
                case "Researcher":
                    specializations.put(key, Arrays.asList(
                            "Machine Learning", "Computer Vision", "Natural Language Processing",
                            "Robotics", "Big Data Analytics"
                    ));
                    break;
                case "Teaching Assistant":
                    specializations.put(key, Arrays.asList(
                            "Programming", "Algorithms", "Data Structures",
                            "Web Development", "Mobile Development"
                    ));
                    break;
                case "Technical Assistant":
                    specializations.put(key, Arrays.asList(
                            "Networking", "Hardware", "System Administration",
                            "Database Administration"
                    ));
                    break;
            }
        }
        // Add more departments as needed...

        // Default specialization
        if (specializations.isEmpty()) {
            specializations.put(key, Collections.singletonList("General"));
        }

        return specializations;
    }
}