
package com.kiet.studentjdbc;

import java.sql.SQLException;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static StudentDAO dao = new StudentDAO();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search by ID");
            System.out.println("4. Search by Email");
            System.out.println("5. Search by Course");
            System.out.println("6. Update Student");
            System.out.println("7. Delete Student");
            System.out.println("8. Merit List");
            System.out.println("9. Students with Same Skills");
            System.out.println("10. Programming Improvement List");
            System.out.println("0. Exit");

            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        addStudent();
                        break;

                    case 2:
                        displayStudents(dao.getAllStudents());
                        break;

                    case 3:
                        searchById();
                        break;

                    case 4:
                        displayStudents(
                                dao.searchByEmail(read("Enter email: "))
                        );
                        break;

                    case 5:
                        displayStudents(
                                dao.searchByCourse(read("Enter course: "))
                        );
                        break;

                    case 6:
                        updateStudent();
                        break;

                    case 7:
                        int id = readInt("Enter student ID: ");
                        dao.deleteStudent(id);
                        break;

                    case 8:
                        System.out.println("\n===== MERIT LIST =====");
                        displayStudents(dao.getMeritList());
                        break;

                    case 9:
                        System.out.println("\n===== SAME SKILLS =====");

                        Map<String, List<Student>> groups =
                                dao.getSameSkills();

                        if (groups.isEmpty()) {
                            System.out.println("No matching skill groups.");
                        }

                        for (String key : groups.keySet()) {
                            System.out.println("\nSkills: " + key);
                            displayStudents(groups.get(key));
                        }
                        break;

                    case 10:
                        System.out.println(
                                "\n===== PROGRAMMING IMPROVEMENT ====="
                        );
                        displayStudents(
                                dao.getStudentsNeedingImprovement()
                        );
                        break;

                    case 0:
                        System.out.println("Thank you! Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");

                choice = -1;

            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());

                choice = -1;

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());

                choice = -1;
            }

        } while (choice != 0);

        sc.close();
    }

    // Read string
    static String read(String message) {
        System.out.print(message);
        return sc.nextLine();
    }

    // Read integer
    static int readInt(String message) {
        return Integer.parseInt(read(message));
    }

    // Read score
    static double readDouble(String message) {
        return Double.parseDouble(read(message));
    }

    // Read technical skills
    static List<String> readSkills() {

        System.out.print("Enter skills separated by commas: ");

        String input = sc.nextLine();

        List<String> skills = new ArrayList<>();

        for (String skill : input.split(",")) {

            String value = skill.trim();

            if (!value.isEmpty() && !skills.contains(value)) {
                skills.add(value);
            }
        }

        return skills;
    }

    // Add student
    static void addStudent() throws SQLException {

        int id = readInt("Student ID: ");
        String name = read("Student Name: ");
        String email = read("Email: ");
        String course = read("Course: ");

        double programming =
                readDouble("Programming Score: ");

        double mathematics =
                readDouble("Mathematics Score: ");

        List<String> skills = readSkills();

        Student s = new Student(
                id, name, email, course,
                programming, mathematics, skills
        );

        dao.addStudent(s);
    }

    // Search by ID
    static void searchById() throws SQLException {

        int id = readInt("Enter student ID: ");

        Student s = dao.getStudentById(id);

        if (s == null) {
            System.out.println("Student not found!");
        } else {
            System.out.println(s);
        }
    }

    // Update student
    static void updateStudent() throws SQLException {

        int id = readInt("Enter student ID: ");

        Student existing = dao.getStudentById(id);

        if (existing == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("Existing Student:");
        System.out.println(existing);

        double programming =
                readDouble("New Programming Score: ");

        double mathematics =
                readDouble("New Mathematics Score: ");

        List<String> skills = readSkills();

        existing.setProgrammingScore(programming);
        existing.setMathematicsScore(mathematics);
        existing.setTechnicalSkills(skills);

        dao.updateStudent(existing);
    }

    // Display students
    static void displayStudents(List<Student> students) {

        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }

        for (Student s : students) {
            System.out.println("\n--------------------");
            System.out.println(s);
        }
    }
}
