/**
 * Represents a stock with a unique identifier and maintains its price
 * history through a 2-3 tree of timestamped price changes.
 *
 * @param stockId      unique stock identifier
 * @param firstTime    timestamp of the initial price
 * @param initialPrice initial price value
 */
public class Stock {
    /** Unique identifier for this stock. */
    private final String stockId;
    /** Current price of the stock. */
    private Float price = 0F;
    /** Tree storing all historical price changes keyed by timestamp. */
    private final Tree<Long, Long, Float> changeTree;
    /** Timestamp of the initial price entry. */
    private final Long firstTime;

    /**
     * Constructs a Stock instance, initializes its change tree,
     * and records the initial price change.
     *
     * @param stockId      unique stock identifier
     * @param firstTime    timestamp of the first price entry
     * @param initialPrice initial price value
     */
    public Stock(String stockId, Long firstTime, Float initialPrice) {
        this.changeTree = new Tree<Long, Long, Float>();
        this.stockId = stockId;
        this.firstTime = firstTime;
        this.addChange(firstTime, initialPrice);
    }

    /**
     * Increases the current price by the specified change amount.
     *
     * @param change amount to add to the price
     */
    private void incrementPrice(Float change) {
        this.price += change;
        //TODO Check this!
//        this.price = scaleAndAdd(price, change);
    }

    /**
     * Decreases the current price by the specified change amount.
     *
     * @param change amount to subtract from the price
     */
    private void decrementPrice(Float change) {
        this.price -= change;
        //TODO Check this!
//        this.price = scaleAndAdd(price, -1*change);
    }

    /**
     * Creates a new tree node representing a price change event.
     *
     * @param timestamp time of the change
     * @param change    amount of the price change
     * @return a new Node instance with the given key and value
     */
    private static Node<Long, Long, Float> createChangeNode(Long timestamp, Float change) {
        return new Node<>(timestamp, timestamp, change);
    }

    /**
     * Checks whether a price change at the given timestamp exists.
     *
     * @param timestamp the timestamp to look up
     * @return true if a change node exists for the timestamp
     * @throws IllegalArgumentException if timestamp is null
     */
    private boolean doesExist(Long timestamp) {
        if (timestamp == null) throw new IllegalArgumentException("Timestamp is null!");
        Keys<Long, Long> key = new Keys<>(timestamp, timestamp);
        return TreeOperations.exists(this.changeTree, key);
    }

    /**
     * Adds a new price change to the tree and updates the current price.
     *
     * @param timestamp time of the change
     * @param change    amount to add to the price
     * @throws IllegalArgumentException if timestamp is null or already exists
     */
    public void addChange(Long timestamp, Float change) {
        if (this.doesExist(timestamp)) throw new IllegalArgumentException("Timestamp already exists!");
        TreeOperations.insert(this.changeTree, createChangeNode(timestamp, change));
        this.incrementPrice(change);
    }

    /**
     * Removes an existing price change and adjusts the current price.
     *
     * @param timestamp time of the change to remove
     * @throws IllegalArgumentException if timestamp is null, not found,
     *                                  or refers to the initial entry
     */
    public void removeChange(Long timestamp) {
        if (!doesExist(timestamp)) throw new IllegalArgumentException("No such timestamp");
        if (timestamp == this.firstTime)
            throw new IllegalArgumentException("Removing the initial timestamp is not possible!");
        Node<Long, Long, Float> nodeToRemove = this.searchTimestamp(timestamp);
        Float pastPrice = nodeToRemove.getValue();
        TreeOperations.delete(this.changeTree, nodeToRemove);
        this.decrementPrice(pastPrice);
    }

    /**
     * Searches the change tree for a node at the specified timestamp.
     *
     * @param timestamp the timestamp to search for
     * @return the matching Node if found
     * @throws IllegalArgumentException if timestamp is null
     */
    private Node<Long, Long, Float> searchTimestamp(Long timestamp) {
        if (timestamp == null) throw new IllegalArgumentException("Timestamp is null!");
        Keys<Long, Long> key = new Keys<>(timestamp, timestamp);
        return TreeOperations.search(this.changeTree.getRoot(), key);
    }

    /**
     * Retrieves the stock's unique identifier.
     *
     * @return the stockId
     */
    public String getStockId() {
        return stockId;
    }

    /**
     * Retrieves the current price of the stock.
     *
     * @return current price value
     */
    public Float getPrice() {
        return price;
    }

    /**
     * Sets the current price of the stock.
     *
     * @param price new price value
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     * Retrieves the tree of historical price changes.
     *
     * @return changeTree containing timestamped changes
     */
    public Tree<Long, Long, Float> getChangeTree() {
        return changeTree;
    }

    /**
     * Retrieves the initial timestamp of the first price entry.
     *
     * @return firstTime value
     */
    public Long getFirstTime() {
        return firstTime;
    }

    /**
     * Adds two floats with scaling to preserve decimal precision.
     *
     * @param num1 first operand
     * @param num2 second operand
     * @return precise sum of the two numbers
     */
    private static float scaleAndAdd(float num1, float num2) {
        int scale1 = getScaleFactor(num1);
        int scale2 = getScaleFactor(num2);
        int scaleFactor = Math.max(scale1, scale2);
        int scaledNum1 = Math.round(num1 * scaleFactor);
        int scaledNum2 = Math.round(num2 * scaleFactor);
        int sum = scaledNum1 + scaledNum2;
        return (float) sum / scaleFactor;
    }

    /**
     * Determines the scale factor based on decimal places in a float.
     *
     * @param value the float to inspect
     * @return power-of-ten scale factor for value
     */
    private static int getScaleFactor(float value) {
        String floatStr = Float.toString(value);
        int decimalPlaces = floatStr.length() - floatStr.indexOf('.') - 1;
        return (int) Math.pow(10, decimalPlaces);
    }
}
