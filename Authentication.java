import java.util.ArrayList;
import java.util.List;

public class Authentication {
    
    private static List<UserAccount> accounts = new ArrayList<>();

    static {
        accounts.add(new UserAccount("admin1", "adminpass", "Admin"));
        accounts.add(new UserAccount("recept23", "frontdesk", "Receptionist"));
        accounts.add(new UserAccount("housekpr", "clean123", "Staff"));
        accounts.add(new UserAccount("guest305", "pass123", "Customer"));
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
    
    public static void generateAccount(String username, String password) {
        accounts.add(new UserAccount(username, password, "Customer"));
        System.out.println("\n[SYSTEM] New Customer account generated: " + username);
    }
}