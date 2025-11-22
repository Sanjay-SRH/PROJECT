import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Review {
    private static int nextReviewIndex = 1;
    private static final String ARCHIVE_FILE = "reviews_archive.json"; 

    private String request_id;
    private String rating;
    private String review;
    private boolean taken = false; 

    Review() {
        this.rating = "0";
        this.request_id = " ";
        this.review = " ";
        this.taken = false;
    }

    
    Review(String request_id, String rating, String review, boolean taken) {
        this.request_id = request_id;
        this.rating = rating;
        this.review = review;
        this.taken = taken;
    }

    // --- Getters and Setters ---
    public String getReq_id() { return this.request_id; }
    public void setReq_id(String req_id) { this.request_id = req_id; }
    public String getReview() { return this.review; }
    public void setReview(String review) { this.review = review; }
    public String getrating() { return this.rating; }
    public void setrating(String rating) { this.rating = rating; }
    public void settaken() { this.taken = true; }
    public void display() {
        System.out.println("Request ID: " + this.getReq_id() + ", Rating: " + this.getrating() + ", Review: " + this.getReview());
    }

    private static String extractJsonValue(String jsonStr, String key) {
       
        Pattern stringPattern = Pattern.compile("\"" + Pattern.quote(key) + "\":\"([^\"]*)\"");
        Matcher stringMatcher = stringPattern.matcher(jsonStr);
        if (stringMatcher.find()) {
            return stringMatcher.group(1);
        }
        Pattern literalPattern = Pattern.compile("\"" + Pattern.quote(key) + "\":(\\w+)");
        Matcher literalMatcher = literalPattern.matcher(jsonStr);
        if (literalMatcher.find()) {
            return literalMatcher.group(1);
        }
        
        return "";
    }
    private static List<Review> readAllReviewsFromArchive() {
        List<Review> reviews = new ArrayList<>();
        File file = new File(ARCHIVE_FILE);
        int maxReviewNum = 0;
        if (!file.exists() || file.length() == 0) {
            nextReviewIndex = 1;
            return reviews; 
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(ARCHIVE_FILE)));
            
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
            }
            String[] reviewStrings = content.split("(?<=})\\s*,\\s*(?=\\{)");
            
            for (String reviewStr : reviewStrings) {
                String fullReviewStr = reviewStr.trim();
                if (!fullReviewStr.startsWith("{")) {
                    fullReviewStr = "{" + fullReviewStr;
                }
                if (!fullReviewStr.endsWith("}")) {
                    fullReviewStr = fullReviewStr + "}";
                }
                
                try {
                    String request_id = extractJsonValue(fullReviewStr, "request_id");
                    String rating = extractJsonValue(fullReviewStr, "rating");
                    String review = extractJsonValue(fullReviewStr, "review");
                    String takenStr = extractJsonValue(fullReviewStr, "taken");
                    boolean taken = Boolean.parseBoolean(takenStr);
                    
                    if (!request_id.isEmpty() && !rating.isEmpty()) {
                        reviews.add(new Review(request_id, rating, review, taken));
                        
                        if (request_id.startsWith("REQ")) {
                            try {
                                int currentNum = Integer.parseInt(request_id.substring(3));
                                if (currentNum > maxReviewNum) {
                                    maxReviewNum = currentNum;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Corrupt review data found in file. Skipping entry. Error: " + e.getMessage());
                }
            }
            nextReviewIndex = maxReviewNum + 1;

        } catch (IOException e) {
            System.err.println("Error reading archive file: " + e.getMessage());
        }
        return reviews;
    }
    public boolean writeReviewToSingleFile() {
        List<Review> reviews = readAllReviewsFromArchive();
        reviews.add(new Review(this.request_id, this.rating, this.review, true));
        this.settaken();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n"); 
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            String safeReview = r.review.replaceAll("\"", "\\\""); 
            
            // Start object (indented by 4 spaces)
            jsonBuilder.append("    {\n");
            jsonBuilder.append(String.format("        \"request_id\":\"%s\",\n", r.request_id));

            jsonBuilder.append(String.format("        \"rating\":\"%s\",\n", r.rating));
            
            jsonBuilder.append(String.format("        \"review\":\"%s\",\n", safeReview));
            jsonBuilder.append(String.format("        \"taken\":%s\n", r.taken));
            jsonBuilder.append("    }");

            if (i < reviews.size() - 1) {
                jsonBuilder.append(",\n"); 
            } else {
                jsonBuilder.append("\n"); 
            }
        }
        jsonBuilder.append("]"); 
        try (FileWriter writer = new FileWriter(ARCHIVE_FILE)) {
            writer.write(jsonBuilder.toString());
            System.out.println("\nReview successfully written and archived in " + ARCHIVE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to archive file: " + e.getMessage());
            return false;
        }
    }
    
    public static void viewAllReviews() {
        System.out.println("\n==================================");
        System.out.println("         SAVED CUSTOMER REVIEWS      ");
        System.out.println("==================================");

        List<Review> reviews = readAllReviewsFromArchive();
        
        if (reviews.isEmpty()) {
            System.out.println("No reviews found in the archive.");
        } else {
            int index = 1;
            for (Review r : reviews) {
                System.out.print("Review #" + index + ": ");
                r.display();
                index++;
            }
        }
        System.out.println("==================================");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;
        readAllReviewsFromArchive(); 

        do {
            System.out.println("\n----------------------------------");
            System.out.println("Choose an option:");
            System.out.println("1. Submit a new Review");
            System.out.println("2. View All Saved Reviews");
            System.out.println("3. Exit");
            System.out.println("----------------------------------");
            System.out.print("Enter choice (1, 2, or 3): ");
            
            input = sc.nextLine().trim();

            if (input.equals("1")) {
                Review r = new Review();
                r.setReq_id("REQ" + nextReviewIndex); 

                System.out.println("\n--- Submitting a new Review for " + r.getReq_id() + " ---");
                System.out.println("Enter <rating> <review text> (e.g., 5 Great_service!): ");
                
                String temp = sc.nextLine();
                StringTokenizer st = new StringTokenizer(temp, " ");

                if (st.countTokens() >= 2) {
                    String ratingInput = st.nextToken();
                    StringBuilder reviewText = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        reviewText.append(st.nextToken()).append(" ");
                    }
                    try {
                        int ratingValue = Integer.parseInt(ratingInput);
                        if (ratingValue < 1 || ratingValue > 5) {
                            throw new InputMismatchException("Rating must be between 1 and 5.");
                        }
                        r.setrating(ratingInput);
                        r.setReview(reviewText.toString().trim());
                        
                        if (r.writeReviewToSingleFile()) {
                             nextReviewIndex++; 
                        }
                        r.display();

                    } catch (NumberFormatException e) {
                        System.err.println("\nError: The rating '" + ratingInput + "' is not a valid number. Please try again.");
                    } catch (InputMismatchException e) {
                        System.err.println("\nError: " + e.getMessage() + " Please try again.");
                    }

                } else {
                    System.out.println("Invalid input format. Please enter both rating and review.");
                }
            } else if (input.equals("2")) {
                viewAllReviews();
            } else if (input.equals("3")) {
                System.out.println("Exiting Review Application. Goodbye!");
            } else {
                System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        } while (!input.equals("3"));

        sc.close();
    }
}
