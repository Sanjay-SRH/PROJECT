import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

public class Authentication {
    
    private static List<UserAccount> accounts = new ArrayList<>();
    
    private static final String ADMIN_FILE = "admin_credentials.txt";
    private static final String STAFF_FILE = "staff_credentials.txt";
    private static final String CUSTOMER_FILE = "customer_credentials.txt";

    static {
        loadAccountsFromFile(ADMIN_FILE, "Admin");
        loadAccountsFromFile(STAFF_FILE, "Staff");
        loadAccountsFromFile(CUSTOMER_FILE, "Customer"); 
    }
    
    private static void loadAccountsFromFile(String fileName, String role) {
        File file = new File(fileName);
        
        if (!file.exists()) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                
                if (role.equals("Customer")) {
                    if (parts.length == 3) {
                        String username = parts[1].trim(); 
                        String password = parts[2].trim();
                        accounts.add(new UserAccount(username, password, role));
                    } 
                } else {
                    if (parts.length == 2) {
                        String username = parts[0].trim();
                        String password = parts[1].trim();
                        accounts.add(new UserAccount(username, password, role));
                    } 
                }
            }
            
        } catch (IOException e) {
            
        }
    }

    public static String verifyLogin(String username, String password, String requiredRole) {
        for (UserAccount account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                if (account.getRole().equals(requiredRole)) {
                    System.out.println("\n*** Login SUCCESSFUL. ***");
                    return account.getRole();
                } else {
                    System.out.println("\nError: Credentials verified, but selected role (" + requiredRole + ") does not match account role (" + account.getRole() + ").");
                    return null;
                }
            }
        }
        return null;
    }
    
    private static boolean isRoomOccupied(String roomNumber) {
         try {
            List<String> lines = Files.readAllLines(Path.of(CUSTOMER_FILE), StandardCharsets.UTF_8);
            for (String line : lines) {
                 if (line.trim().isEmpty()) continue;
                 String[] parts = line.split(",");
                 if (parts.length >= 1 && parts[0].trim().equals(roomNumber)) {
                     return true; 
                 }
            }
        } catch (IOException e) {
        }
        return false;
    }

    private static boolean isUsernameTaken(String username) {
        for (UserAccount account : accounts) {
            if (account.getUsername().equals(username)) {
                return true; 
            }
        }
        return false;
    }

    public static boolean registerNewCustomer(String roomNumber, String username, String password) {
        
        if (isRoomOccupied(roomNumber)) {
            System.out.println("\n*** Registration failed: Room Number " + roomNumber + " is already occupied. ***");
            return false;
        }

        if (isUsernameTaken(username)) {
            System.out.println("\n*** Registration failed: Username " + username + " is already taken. ***");
            return false;
        }
        
        UserAccount newAccount = new UserAccount(username, password, "Customer");
        accounts.add(newAccount);
        
        try {
            String data = roomNumber + "," + username + "," + password + "\n";
            Path path = Path.of(CUSTOMER_FILE);
            Files.write(path, data.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("\n[ERROR] Failed to save new customer account due to file error.");
            accounts.remove(newAccount); 
            return false;
        }
    }
}