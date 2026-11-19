
package com.kiet.studentjdbc;

import java.sql.*;
import java.util.*;

public class StudentDAO {

    // 1. Validate student scores
    private void validateStudent(Student s) {

        if (s.getProgrammingScore() < 0 ||
                s.getProgrammingScore() > 100 ||
                s.getMathematicsScore() < 0 ||
                s.getMathematicsScore() > 100) {

            throw new IllegalArgumentException(
                    "Scores must be between 0 and 100"
            );
        }
    }

    // Helper: Insert technical skills
    private void insertSkills(Connection con, Student s)
            throws SQLException {

        String sql = "INSERT INTO student_skills VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (String skill : s.getTechnicalSkills()) {

                ps.setInt(1, s.getStudentId());
                ps.setString(2, skill);
                ps.executeUpdate();
            }
        }
    }

    // Helper: Read technical skills
    private List<String> getSkills(Connection con, int id)
            throws SQLException {

        List<String> skills = new ArrayList<>();

        String sql =
                "SELECT skill FROM student_skills WHERE student_id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    skills.add(rs.getString("skill"));
                }
            }
        }

        return skills;
    }

    // Helper: Convert ResultSet into Student object
    private Student mapStudent(Connection con, ResultSet rs)
            throws SQLException {

        int id = rs.getInt("student_id");

        Student s = new Student();

        s.setStudentId(id);
        s.setStudentName(rs.getString("student_name"));
        s.setEmail(rs.getString("email"));
        s.setCourse(rs.getString("course"));
        s.setProgrammingScore(rs.getDouble("programming_score"));
        s.setMathematicsScore(rs.getDouble("mathematics_score"));
        s.setTechnicalSkills(getSkills(con, id));

        return s;
    }

    // 2. Add student
    public void addStudent(Student s) throws SQLException {

        validateStudent(s);

        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, s.getStudentId());
                    ps.setString(2, s.getStudentName());
                    ps.setString(3, s.getEmail());
                    ps.setString(4, s.getCourse());
                    ps.setDouble(5, s.getProgrammingScore());
                    ps.setDouble(6, s.getMathematicsScore());

                    ps.executeUpdate();
                }

                insertSkills(con, s);

                con.commit();

                System.out.println("Student added successfully!");

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }
        }
    }

    // 3. Display all students
    public List<Student> getAllStudents() throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    students.add(mapStudent(con, rs));
                }
            }
        }

        return students;
    }

    // 4. Search student by ID
    public Student getStudentById(int id) throws SQLException {

        String sql = "SELECT * FROM students WHERE student_id=?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return mapStudent(con, rs);
                    }
                }
            }
        }

        return null;
    }

    // 5. Search by email
    public List<Student> searchByEmail(String email)
            throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE email LIKE ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, "%" + email + "%");

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        students.add(mapStudent(con, rs));
                    }
                }
            }
        }

        return students;
    }

    // 6. Search by course
    public List<Student> searchByCourse(String course)
            throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE course LIKE ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, "%" + course + "%");

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        students.add(mapStudent(con, rs));
                    }
                }
            }
        }

        return students;
    }

    // 7. Update student scores and skills
    public void updateStudent(Student s) throws SQLException {

        validateStudent(s);

        String sql = "UPDATE students SET programming_score=?, " +
                "mathematics_score=? WHERE student_id=?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            con.setAutoCommit(false);

            try {
                int rows;

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setDouble(1, s.getProgrammingScore());
                    ps.setDouble(2, s.getMathematicsScore());
                    ps.setInt(3, s.getStudentId());

                    rows = ps.executeUpdate();
                }

                if (rows == 0) {
                    throw new SQLException("Student not found");
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM student_skills WHERE student_id=?")) {

                    ps.setInt(1, s.getStudentId());
                    ps.executeUpdate();
                }

                insertSkills(con, s);

                con.commit();

                System.out.println("Student updated successfully!");

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }
        }
    }

    // 8. Delete student
    public void deleteStudent(int id) throws SQLException {

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM student_skills WHERE student_id=?")) {

                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM students WHERE student_id=?")) {

                    ps.setInt(1, id);

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        throw new SQLException("Student not found");
                    }
                }

                con.commit();

                System.out.println("Student deleted successfully!");

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }
        }
    }

    // 9. Generate merit list
    public List<Student> getMeritList() throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY " +
                "(programming_score + mathematics_score)/2 DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    students.add(mapStudent(con, rs));
                }
            }
        }

        return students;
    }

    // 10. Find students with identical technical skills
    public Map<String, List<Student>> getSameSkills()
            throws SQLException {

        Map<String, List<Student>> groups = new HashMap<>();

        List<Student> students = getAllStudents();

        for (Student s : students) {

            List<String> sortedSkills =
                    new ArrayList<>(s.getTechnicalSkills());

            Collections.sort(sortedSkills,
                    String.CASE_INSENSITIVE_ORDER);

            String key = String.join(", ", sortedSkills);

            groups.computeIfAbsent(
                    key, k -> new ArrayList<>()
            ).add(s);
        }

        // Remove groups containing only one student
        groups.entrySet().removeIf(
                entry -> entry.getValue().size() < 2
        );

        return groups;
    }

    // 11. Students needing programming improvement
    public List<Student> getStudentsNeedingImprovement()
            throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students " +
                "WHERE programming_score < 50 " +
                "AND mathematics_score >= 75";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed");
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    students.add(mapStudent(con, rs));
                }
            }
        }

        return students;
    }
}
