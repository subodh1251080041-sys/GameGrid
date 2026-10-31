package com.gamegrid.model;

/**
 * System Administrator with store management privileges.
 * Demonstrates Inheritance and role-based encapsulation.
 */
public class Admin extends User {
    private String department;
    private boolean canManageCatalog;

    public Admin(String id, String username, String email, String password, String department) {
        super(id, username, email, password);
        this.department = (department != null && !department.trim().isEmpty()) ? department.trim() : "Store Operations";
        this.canManageCatalog = true;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDashboardGreeting() {
        return String.format("Admin Console | Staff: %s [Dept: %s]", getUsername(), department);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isCanManageCatalog() {
        return canManageCatalog;
    }
}
