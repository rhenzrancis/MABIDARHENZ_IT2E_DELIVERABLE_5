
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public final class AdminMenu extends UserMenu {

    private final UserDao userDao;

    public AdminMenu(Scanner scanner, User user) {
        super(scanner, user, "ADMIN MENU");
        this.userDao = new UserDao();
    }

    @Override
    public void show() {
        System.out.println(
                "\nLogin successful! Welcome, " + user.username() + "."
        );

        boolean running = true;

        while (running) {
            System.out.println("\n==============================================");
            System.out.println("                  ADMIN MENU");
            System.out.println("==============================================");
            System.out.println("Username : " + user.username());
            System.out.println("Role     : " + user.roles());
            System.out.println("Status   : " + user.status());
            System.out.println("----------------------------------------------");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.println("==============================================");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayUsers(userDao.getAllUsers());

                case "2" -> displayUsers(userDao.getPendingUsers());

                case "3" -> approveUserAccount();

                case "4" -> {
                    System.out.println("You have been logged out.");
                    running = false;
                }

                default ->
                        System.out.println("Invalid choice. Please select 1-4.");
            }
        }
    }

    private void approveUserAccount() {
        List<User> pendingUsers = userDao.getPendingUsers();

        System.out.println("\n========== PENDING REGISTRATIONS ==========");
        displayUsers(pendingUsers);

        if (pendingUsers.isEmpty()) {
            System.out.println("There are no pending accounts to approve.");
            return;
        }

        System.out.println("Enter 0 to cancel.");
        System.out.print("Enter User ID to approve: ");

        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }

        final int userId;

        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric User ID.");
            return;
        }

        boolean exists = pendingUsers.stream()
                .anyMatch(u -> u.id() == userId);

        if (!exists) {
            System.out.println("User ID not found in the pending list.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("User account approved successfully!");
        } else {
            System.out.println(
                    "Approval failed. The account may already have been updated."
            );
        }
    }

    private void displayUsers(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";

        System.out.println(border);
        System.out.printf(
                "| %-5s | %-15s | %-12s | %-10s |%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf(
                    "| %-5s | %-15s | %-12s | %-10s |%n",
                    "-", "No user records", "found", "-"
            );
        } else {
            for (User u : users) {
                System.out.printf(
                        "| %-5d | %-15s | %-12s | %-10s |%n",
                        u.id(),
                        fit(u.username(), 15),
                        fit(u.roles(), 12),
                        fit(u.status(), 10)
                );
            }
        }

        System.out.println(border);
        System.out.println("Total records: " + users.size());
    }

    private String fit(String value, int width) {
        if (value == null) {
            return "";
        }

        return value.length() > width
                ? value.substring(0, width)
                : value;
    }
}
