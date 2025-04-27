package com.company.database;

public class Queries {
    // Login queries, employee queries, etc. can be written here as constants.
    public static final String INSERT_JOB_APPLICATION = "INSERT INTO job_applications(full_name, birthdate, work_experience, profile_pic, resume, interested_sector, email, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SELECT_PENDING_APPLICATIONS = "SELECT * FROM job_applications WHERE status='pending'";
    // ... add more queries as needed.
}
