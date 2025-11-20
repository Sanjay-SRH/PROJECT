import java.util.Scanner;

public class HotelManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String[] ROLES = {"Admin", "Receptionist", "Staff", "Customer"};

    public static void main(String[] args) {
        System.out.println("--- Hotel Management System ---");
        
        Authentication.generateAccount("newguest101", "roomkey");

        String authenticatedRole = null;
        while (authenticatedRole == null) {
            
            System.out.println("\n==================================");
            String selectedRole = selectRole();
            
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            
            authenticatedRole = Authentication.verifyLogin(username, password, selectedRole);
            
            if (authenticatedRole == null) {
                System.out.println("\n*** Login FAILED. Please try again. ***");
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
            }
        }
        return ROLES[choice - 1];
    }
    
    private static void loadDashboard(String role) {
        System.out.println("\n==================================");
        System.out.println("WELCOME, " + role.toUpperCase() + "!");
        System.out.println("==================================");
        
        switch (role) {
            case "Admin":
                System.out.println("Loading Admin Dashboard (Full system oversight)...");
                break;
            case "Receptionist":
                System.out.println("Loading Receptionist Dashboard (Booking/Allotment/Billing)...");
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