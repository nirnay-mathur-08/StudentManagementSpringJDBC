
package com.kiet.studentjdbc;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private int studentId;
    private String studentName;
    private String email;
    private String course;

    private double programmingScore;
    private double mathematicsScore;

    private List<String> technicalSkills;

    // Default constructor
    public Student() {
        technicalSkills = new ArrayList<>();
    }

    // Parameterized constructor
    public Student(int studentId, String studentName,
                   String email, String course,
                   double programmingScore,
                   double mathematicsScore,
                   List<String> technicalSkills) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;

        this.technicalSkills = technicalSkills == null
                ? new ArrayList<>()
                : new ArrayList<>(technicalSkills);
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    // Calculate average score
    public double calculateAverage() {
        return (programmingScore + mathematicsScore) / 2;
    }

    // Determine performance category
    public String getPerformanceCategory() {

        double average = calculateAverage();

        if (average >= 85)
            return "Excellent";
        else if (average >= 70)
            return "Good";
        else if (average >= 50)
            return "Satisfactory";
        else
            return "Needs Improvement";
    }

    // Identify stronger subject
    public String getStrongerSubject() {

        if (programmingScore > mathematicsScore)
            return "Programming";

        else if (mathematicsScore > programmingScore)
            return "Mathematics";

        else
            return "Equal";
    }

    // Display student information
    @Override
    public String toString() {

        return "Student ID: " + studentId +
                "\nName: " + studentName +
                "\nEmail: " + email +
                "\nCourse: " + course +
                "\nProgramming Score: " + programmingScore +
                "\nMathematics Score: " + mathematicsScore +
                "\nAverage: " + calculateAverage() +
                "\nCategory: " + getPerformanceCategory() +
                "\nStronger Subject: " + getStrongerSubject() +
                "\nTechnical Skills: " + technicalSkills;
    }
}
