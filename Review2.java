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

public class Review2 {
    private static int review_id = 1;
    private static final String ARCHIVE_FILE = "reviews_archive.json"; 

    private String request_id;
    private String rating;
    private String review;
    boolean taken_review = false;

    Review2() {
        this.rating = "0";
        this.request_id = " ";
        this.review = " ";
        this.taken_review = false;
        review_id++;
    }

    Review2(String request_id, String rating, String review) {
        this.request_id = request_id;
        this.rating = rating;
        this.review = review;
        this.taken_review = true; 
        review_id++;
    }

    public static int getreview_id() {
        return review_id;
    }

    // --- Getters and Setters ---
    public String getReq_id() { return this.request_id; }
    public void setReq_id(String req_id) { this.request_id = req_id; }
    public String getReview() { return this.review; }
    public void setReview(String review) { this.review = review; }
    public String getrating() { return this.rating; }
    public void setrating(String rating) { this.rating = rating; }
    public void settakenreview() { this.taken_review = true; }
    public void display() {
        System.out.println("Request ID: " + this.getReq_id() + ", Rating: " + this.getrating() + ", Review: " + this.getReview());
    }
    // ------------------------------------------------------------------


    // --- Core I/O Methods for Single File (JSON Array) ---
    
    /**
     * Helper function to extract a string value corresponding to a key in a JSON object string.
     * @param jsonStr The single JSON object string.
     * @param key The key to look for (e.g., "request_id").
     * @return The extracted value, or an empty string if not found.
     */
    private static String extractJsonValue(String jsonStr, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = jsonStr.indexOf(searchKey);
        
        if (start == -1) {
            return "";
        }
        start += searchKey.length();
        
        int end = jsonStr.indexOf("\"", start);
        
        if (end == -1) {
            return "";
        }
        
        return jsonStr.substring(start, end);
    }

    /**
     * Reads all existing review data from the single archive file.
     * @return A list of Review2 objects parsed from the file, or an empty list if file not found or empty.
     */
    private static List<Review2> readAllReviewsFromArchive() {
        List<Review2> reviews = new ArrayList<>();
        File file = new File(ARCHIVE_FILE);
        
        if (!file.exists() || file.length() == 0) {
            return reviews; 
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(ARCHIVE_FILE)));
            
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
            }
            String[] reviewStrings = content.split("\\}, \\{");
            
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
                    
                    if (!request_id.isEmpty() && !rating.isEmpty()) {
                        reviews.add(new Review2(request_id, rating, review));
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.err.println("Warning: Corrupt review data found in file. Skipping entry.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading archive file: " + e.getMessage());
        }
        return reviews;
    }
    public boolean writeReviewToSingleFile() {
       
        List<Review2> reviews = readAllReviewsFromArchive();

        reviews.add(new Review2(this.request_id, this.rating, this.review));
 
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        
        for (int i = 0; i < reviews.size(); i++) {
            Review2 r = reviews.get(i);
            String reviewJson = String.format(
                "{\"request_id\":\"%s\",\"rating\":\"%s\",\"review\":\"%s\",\"taken\":true}",
                r.request_id, r.rating, r.review.replaceAll("\"", "\\\"") 
            );
            jsonBuilder.append(reviewJson);
            
            if (i < reviews.size() - 1) {
                jsonBuilder.append(", "); 
            }
        }
        jsonBuilder.append("]");
        try (FileWriter writer = new FileWriter(ARCHIVE_FILE)) {
            writer.write(jsonBuilder.toString());
            this.settakenreview();
            System.out.println("\nReview successfully written and archived in " + ARCHIVE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to archive file: " + e.getMessage());
            return false;
        }
    }
    public static void viewAllReviews() {
        System.out.println("\n==================================");
        System.out.println("       SAVED CUSTOMER REVIEWS      ");
        System.out.println("==================================");

        List<Review2> reviews = readAllReviewsFromArchive();
        
        if (reviews.isEmpty()) {
            System.out.println("No reviews found in the archive.");
        } else {
            int index = 1;
            for (Review2 r : reviews) {
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
                Review2 r = new Review2();
                int nextReviewId = readAllReviewsFromArchive().size() + 1;
                r.setReq_id("REQ" + nextReviewId); 

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
                        r.writeReviewToSingleFile();
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