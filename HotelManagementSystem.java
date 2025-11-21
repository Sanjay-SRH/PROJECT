import java.util.Scanner;

public class HotelManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String[] ROLES = {"Admin", "Staff", "Customer"}; 

    public static void main(String[] args) {
        System.out.println("--- Hotel Management System ---");
        
        String authenticatedRole = null;
        while (authenticatedRole == null) {
            
            System.out.println("\n==================================");
            String selectedRole = selectRole();
            
            String attemptedLogin = null; 

            if ("Customer".equals(selectedRole)) {
                authenticatedRole = customerPortalMenu();
            } else {
                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();
                
                attemptedLogin = Authentication.verifyLogin(username, password, selectedRole);
                
                if (attemptedLogin == null) {
                    System.out.println("\n*** Login FAILED. Please try again. ***");
                }
                
                authenticatedRole = attemptedLogin;
            }
        }
        
        loadDashboard(authenticatedRole);
        
        scanner.close();
    }
    
    private static String selectRole() {
        System.out.println("Select Login Role:");
        for (int i = 0; i < ROLES.length; i++) {
            System.out.println((i + 1) + ". " + ROLES[i]);
        }
        
        int choice = -1;
        while (choice < 1 || choice > ROLES.length) {
            System.out.print("Enter number (1-" + ROLES.length + "): "); 
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return ROLES[choice - 1];
    }
    
    private static String customerPortalMenu() {
        System.out.println("\n--- Customer Portal ---");
        System.out.println("1. Existing Customer Login");
        System.out.println("2. New Customer Registration (Check-in)");
        
        String choice = null;
        while(choice == null || (!"1".equals(choice) && !"2".equals(choice))) {
            System.out.print("Enter choice (1 or 2): ");
            choice = scanner.nextLine();
        }
        
        if ("1".equals(choice)) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            
            String result = Authentication.verifyLogin(username, password, "Customer");
            if (result == null) {
                System.out.println("\n*** Login FAILED. Please try again. ***");
            }
            return result;

        } else {
            System.out.println("\n--- New Customer Registration ---");
            
            boolean registrationComplete = false;
            while (!registrationComplete) {
                
                String roomNumber = null;
                while (roomNumber == null) {
                    System.out.print("Enter Room Number (100-400): ");
                    String input = scanner.nextLine();
                    try {
                        int number = Integer.parseInt(input);
                        if (number >= 100 && number <= 400) {
                            roomNumber = input;
                        } else {
                            System.out.println("Error: Room number must be between 100 and 400.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number format.");
                    }
                }
                
                System.out.print("Enter New Username: ");
                String newUsername = scanner.nextLine();

                String newPassword = null;
                String confirmPassword = null;
                while (newPassword == null || !newPassword.equals(confirmPassword)) {
                    System.out.print("Enter New Password: ");
                    newPassword = scanner.nextLine();
                    
                    System.out.print("Confirm New Password: ");
                    confirmPassword = scanner.nextLine();

                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("Error: Passwords do not match. Try again.");
                    }
                }
                
                if (Authentication.registerNewCustomer(roomNumber, newUsername, newPassword)) {
                     System.out.println("\n*** Registration SUCCESSFUL. Please select your role and log in. ***"); 
                     registrationComplete = true; 
                } else {
                     System.out.println("\n--- Please Re-attempt Registration ---");
                }
            }
            
            return null; 
        }
    }
    
    private static void loadDashboard(String role) {
        System.out.println("\n==================================");
        System.out.println("WELCOME, " + role.toUpperCase() + "!");
        System.out.println("==================================");
        
        switch (role) {
            case "Admin":
                System.out.println("Loading Admin Dashboard (Full system oversight)...");
                break;
            case "Staff":
                System.out.println("Loading General Staff Dashboard (View assigned service requests)...");
                break;
            case "Customer":
                System.out.println("Loading Customer Portal (Food Order, Complaints, View Bill)...");
                break;
            default:
                System.out.println("Unknown role, ending session.");
                break;
        }
    }
}