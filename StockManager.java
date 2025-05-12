/**
 * Manages a collection of stocks, indexed by ID and price, allowing
 * addition, removal, updates, and range queries. Internally maintains
 * two 2-3 trees for fast lookup by stockId and by price.
 */
public class StockManager {
    /** Tree indexing stocks by their unique ID. */
    private Tree<String, Float, Stock> IdTree;
    /** Tree indexing stocks by their current price. */
    private Tree<Float, String, Stock> priceTree;

    /**
     * Constructs a StockManager with empty ID and price trees.
     */
    public StockManager() {
        this.IdTree = new Tree<String, Float, Stock>();
        this.priceTree = new Tree<Float, String, Stock>();
    }

    /**
     * Reinitializes both ID and price trees, clearing all managed stocks.
     */
    public void initStocks() {
        this.IdTree = new Tree<String, Float, Stock>();
        this.priceTree = new Tree<Float, String, Stock>();
    }

    /**
     * Adds a new stock with given ID, timestamp, and initial price.
     *
     * @param stockId unique stock identifier
     * @param timestamp timestamp of the initial price
     * @param price initial positive price value
     * @throws IllegalArgumentException for invalid price, timestamp, or existing stock
     */
    public void addStock(String stockId, long timestamp, Float price) {
        if (price <= 0) throw new IllegalArgumentException("Initial price must be positive!");
        if (timestamp <= 0) throw new IllegalArgumentException("Timestamp can't be negative!");
        if (doesStockExist(stockId)) throw new IllegalArgumentException("Stock already exists!");
        Stock stockToInsert = stockBuilder(stockId, timestamp, price);
        Node<String, Float, Stock> idNode = idNodeBuilder(stockToInsert);
        Node<Float, String, Stock> priceNode = priceNodeBuilder(stockToInsert);
        TreeOperations.insert(this.IdTree, idNode);
        TreeOperations.insert(this.priceTree, priceNode);
    }

    /**
     * Removes the stock with the given ID from management.
     *
     * @param stockId identifier of the stock to remove
     * @throws IllegalArgumentException if no such stock exists
     */
    public void removeStock(String stockId) {
        if (!doesStockExist(stockId)) throw new IllegalArgumentException("No such stock with that stock id!");
        Keys<String, Float> idKey = new Keys<>(stockId, null);
        Node<String, Float, Stock> idNode = TreeOperations.search(this.IdTree.getRoot(), idKey);
        Keys<Float, String> priceKey = new Keys<Float, String>(idNode.getKeys().getSecondary(), stockId);
        Node<Float, String, Stock> priceNode = TreeOperations.search(this.priceTree.getRoot(), priceKey);
        TreeOperations.delete(this.IdTree, idNode);
        TreeOperations.delete(this.priceTree, priceNode);
    }

    /**
     * Updates the stock by recording a price change at the given timestamp.
     *
     * @param stockId identifier of the stock to update
     * @param timestamp time of the price change
     * @param priceDifference amount to adjust the price by
     * @throws IllegalArgumentException for invalid parameters or if stock/timestamp invalid
     */
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        if (timestamp < 0) throw new IllegalArgumentException("Timestamp can't be negative!");
        if (priceDifference == 0) throw new IllegalArgumentException("Price difference can't be 0!");
        if (!doesStockExist(stockId)) throw new IllegalArgumentException("No such stock with that stock id!");
        Keys<String, Float> idKey = new Keys<>(stockId, null);
        Node<String, Float, Stock> idNode = TreeOperations.search(this.IdTree.getRoot(), idKey);
        Keys<Float, String> priceKey = new Keys<Float, String>(idNode.getKeys().getSecondary(), stockId);
        Node<Float, String, Stock> priceNode = TreeOperations.search(this.priceTree.getRoot(), priceKey);
        Stock stock = idNode.getValue();
        if (TreeOperations.exists(stock.getChangeTree(), new Keys<Long, Long>(timestamp, timestamp)))
            throw new IllegalArgumentException("Such timestamp already exists!");
        this.removeStock(stockId);
        stock.addChange(timestamp, priceDifference);
        Keys<String, Float> updatedIdKey = new Keys<>(stockId, stock.getPrice());
        Keys<Float, String> updatedPriceKey = new Keys<>(stock.getPrice(), stockId);
        idNode.setKeys(updatedIdKey);
        priceNode.setKeys(updatedPriceKey);
        TreeOperations.insert(this.IdTree, idNode);
        TreeOperations.insert(this.priceTree, priceNode);
    }

    /**
     * Retrieves the current price of the stock with the given ID.
     *
     * @param stockId identifier of the stock
     * @return current price of the stock
     * @throws IllegalArgumentException if the stock does not exist
     */
    public Float getStockPrice(String stockId) {
        if (!doesStockExist(stockId)) throw new IllegalArgumentException("No such stock with that stock id!");
        Keys<String, Float> idKey = new Keys<>(stockId, null);
        Node<String, Float, Stock> idNode = TreeOperations.search(this.IdTree.getRoot(), idKey);
        return idNode.getValue().getPrice();
    }

    /**
     * Removes a specific price change entry from the stock's history.
     *
     * @param stockId identifier of the stock
     * @param timestamp time of the change to remove
     * @throws IllegalArgumentException if stock or timestamp invalid or initial timestamp
     */
    public void removeStockTimestamp(String stockId, long timestamp) {
        if (!doesStockExist(stockId)) throw new IllegalArgumentException("No such stock with that stock id!");
        Keys<String, Float> idKey = new Keys<>(stockId, null);
        Node<String, Float, Stock> idNode = TreeOperations.search(this.IdTree.getRoot(), idKey);
        Keys<Float, String> priceKey = new Keys<Float, String>(idNode.getKeys().getSecondary(), stockId);
        Node<Float, String, Stock> priceNode = TreeOperations.search(this.priceTree.getRoot(), priceKey);
        Stock stock = idNode.getValue();
        if (!TreeOperations.exists(stock.getChangeTree(), new Keys<Long, Long>(timestamp, timestamp)))
            throw new IllegalArgumentException("Timestamp does not exist for this stock!");
        if (stock.getFirstTime().equals(timestamp))
            throw new IllegalArgumentException("Removing the initial timestamp is not possible!");
        TreeOperations.delete(this.IdTree, idNode);
        TreeOperations.delete(this.priceTree, priceNode);
        stock.removeChange(timestamp);
        idNode.getKeys().setSecondary(stock.getPrice());
        priceNode.getKeys().setPrimary(stock.getPrice());
        TreeOperations.insert(this.IdTree, idNode);
        TreeOperations.insert(this.priceTree, priceNode);
    }

    /**
     * Counts the number of stocks whose current prices lie within the given range.
     *
     * @param price1 lower bound price
     * @param price2 upper bound price
     * @return count of stocks in range [price1, price2]
     * @throws IllegalArgumentException for null bounds or invalid interval
     */
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null) throw new IllegalArgumentException("Prices can't be null");
        if (price1 > price2) throw new IllegalArgumentException("The price interval is not possible!");
        Keys<Float, String> lowerKey = new Keys<>(price1, null);
        Keys<Float, String> upperKey = new Keys<>(price2, null);
        Node<Float, String, Stock> lower = TreeOperations.search(this.priceTree.getRoot(), lowerKey);
        Node<Float, String, Stock> upper = TreeOperations.searchLarger(this.priceTree.getRoot(), upperKey);
        if (upper.getValue() == null) upper = TreeOperations.predecessor(upper);
        if (lower.getValue() == null) lower = TreeOperations.predecessor(lower);
        if (lower == null || upper == null) return 0;
        if (lower.getValue().getPrice() > price2 || upper.getValue().getPrice() < price1 || upper.getValue().getPrice() < lower.getValue().getPrice())
            return 0;
        if (upperKey.smaller(upper.getKeys())) upper = TreeOperations.predecessor(upper);
        return upper.rank() - lower.rank() + 1;
    }

    /**
     * Lists the IDs of stocks whose current prices lie within the given range.
     *
     * @param price1 lower bound price
     * @param price2 upper bound price
     * @return array of stock IDs in ascending price order
     * @throws IllegalArgumentException for null bounds or invalid interval
     */
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null) throw new IllegalArgumentException("Prices can't be null");
        int amount = getAmountStocksInPriceRange(price1, price2);
        String[] stocks = new String[amount];
        Keys<Float, String> lowerKey = new Keys<>(price1, null);
        Node<Float, String, Stock> lower = TreeOperations.search(this.priceTree.getRoot(), lowerKey);
        for (int i = 0; i < amount; i++) {
            stocks[i] = lower.getValue().getStockId();
            lower = lower.getRightSibling();
        }
        return stocks;
    }

    /**
     * Builds a new Stock instance, validating inputs.
     *
     * @param stockId unique stock identifier
     * @param firstTime initial timestamp
     * @param initialPrice starting price
     * @return new Stock object
     * @throws IllegalArgumentException for invalid parameters
     */
    private static Stock stockBuilder(String stockId, Long firstTime, Float initialPrice) {
        if (stockId == null || firstTime == null || initialPrice == null || initialPrice < 0)
            throw new IllegalArgumentException("The stock's values are incorrect!");
        return new Stock(stockId, firstTime, initialPrice);
    }

    /**
     * Checks if a stock with the given ID is currently managed.
     *
     * @param stockId identifier to check
     * @return true if stock exists, false otherwise
     * @throws IllegalArgumentException if stockId is null
     */
    private boolean doesStockExist(String stockId) {
        if (stockId == null) throw new IllegalArgumentException("StockId is null!");
        Keys<String, Float> key = new Keys<>(stockId, null);
        return TreeOperations.exists(this.IdTree, key);
    }

    /**
     * Creates a Node for the ID-index tree from a Stock.
     *
     * @param stock Stock object to wrap
     * @return Node keyed by stockId then price
     */
    private static Node<String, Float, Stock> idNodeBuilder(Stock stock) {
        return new Node<String, Float, Stock>(stock.getStockId(), stock.getPrice(), stock);
    }

    /**
     * Creates a Node for the price-index tree from a Stock.
     *
     * @param stock Stock object to wrap
     * @return Node keyed by price then stockId
     */
    private static Node<Float, String, Stock> priceNodeBuilder(Stock stock) {
        return new Node<Float, String, Stock>(stock.getPrice(), stock.getStockId(), stock);
    }
}
