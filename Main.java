import java.util.Scanner;

/**
 * Presents a menu for adding, removing, updating, querying,
 * and exiting, reading user input from standard input.
 */
public class Main {
    /**
     * Entry point of the application.
     *
     *
     */
    public static void main(String[] args) {
        StockManager stockManager = new StockManager();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Display the main menu options to the user
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Stock");
            System.out.println("2. Remove Stock");
            System.out.println("3. Update Stock");
            System.out.println("4. Get Stock Price");
            System.out.println("5. Remove Stock Timestamp");
            System.out.println("6. Get Stock Count in Price Range");
            System.out.println("7. Get Stock IDs in Price Range");
            System.out.println("8. Exit");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                // Option 1: Add a new stock with ID, timestamp, and initial price
                if (choice == 1) {
                    System.out.print("Stock ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Timestamp: ");
                    long time = scanner.nextLong();
                    System.out.print("Price: ");
                    float price = scanner.nextFloat();
                    scanner.nextLine();
                    stockManager.addStock(id, time, price);
                    System.out.println("Stock added.");
                }
                // Option 2: Remove an existing stock by ID
                else if (choice == 2) {
                    System.out.print("Stock ID: ");
                    String id = scanner.nextLine();
                    stockManager.removeStock(id);
                    System.out.println("Stock removed.");
                }
                // Option 3: Update a stock's price by applying a price difference
                else if (choice == 3) {
                    System.out.print("Stock ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Timestamp: ");
                    long time = scanner.nextLong();
                    System.out.print("Price difference: ");
                    float diff = scanner.nextFloat();
                    scanner.nextLine();
                    stockManager.updateStock(id, time, diff);
                    System.out.println("Stock updated.");
                }
                // Option 4: Retrieve and display the current price of a stock
                else if (choice == 4) {
                    System.out.print("Stock ID: ");
                    String id = scanner.nextLine();
                    Float price = stockManager.getStockPrice(id);
                    System.out.println("Stock price: " + price);
                }
                // Option 5: Remove a specific price change (timestamp) from a stock
                else if (choice == 5) {
                    System.out.print("Stock ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Timestamp: ");
                    long time = scanner.nextLong();
                    scanner.nextLine();
                    stockManager.removeStockTimestamp(id, time);
                    System.out.println("Timestamp removed.");
                }
                // Option 6: Count stocks within a given price range
                else if (choice == 6) {
                    System.out.print("Min price: ");
                    float min = scanner.nextFloat();
                    System.out.print("Max price: ");
                    float max = scanner.nextFloat();
                    scanner.nextLine();
                    int count = stockManager.getAmountStocksInPriceRange(min, max);
                    System.out.println("Stock count in range: " + count);
                }
                // Option 7: List stock IDs within a given price range
                else if (choice == 7) {
                    System.out.print("Min price: ");
                    float min = scanner.nextFloat();
                    System.out.print("Max price: ");
                    float max = scanner.nextFloat();
                    scanner.nextLine();
                    String[] ids = stockManager.getStocksInPriceRange(min, max);
                    System.out.println("Stock IDs in range:");
                    for (String s : ids) {
                        System.out.println("- " + s);
                    }
                }
                // Option 8: Exit the application loop
                else if (choice == 8) {
                    System.out.println("Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
            // Handle any exceptions thrown during menu processing
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}