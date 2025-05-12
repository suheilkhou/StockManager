/**
 * 2-3 tree implementation with sentinel leaf nodes for boundary handling.
 * Maintains a root node linked to left and right sentinels representing
 * negative and positive infinity bounds, respectively.
 *
 * @param <P> type of the primary key, must be Comparable
 * @param <S> type of the secondary key, must be Comparable
 * @param <V> type of the stored value
 */
public class Tree<P extends Comparable<P>, S extends Comparable<S>, V> implements TreeOperations<P, S, V> {
    /** Root node of the 2-3 tree. */
    private Node<P, S, V> root;
    /** Left sentinel leaf representing negative infinity bound. */
    private Node<P, S, V> leftSentinel = Node.createLeftSentinel();
    /** Right sentinel leaf representing positive infinity bound. */
    private Node<P, S, V> rightSentinel = Node.createRightSentinel();

    /**
     * Constructs an empty 2-3 tree.
     * Initializes root as an internal node whose children are
     * the left and right sentinel leaves, and links sentinel siblings.
     */
    public Tree() {
        this.root = new Node<>(null, null, null);
        this.root.setLeft(leftSentinel);
        this.root.setMiddle(rightSentinel);
        this.leftSentinel.setParent(this.root);
        this.rightSentinel.setParent(this.root);
        this.leftSentinel.setRightSibling(this.rightSentinel);
        this.rightSentinel.setLeftSibling(this.leftSentinel);
        this.root.update();
    }

    /**
     * Checks whether the tree contains any real data nodes.
     *
     * @return true if no non-sentinel nodes are present
     */
    public boolean isEmpty() {
        return this.root.getLeft().equals(this.leftSentinel) && this.root.getMiddle().equals(this.rightSentinel);
    }

    /**
     * Returns the current root node of the tree.
     *
     * @return root node
     */
    public Node<P, S, V> getRoot() {
        return root;
    }

    /**
     * Sets a new root node for the tree.
     *
     * @param root new root node
     */
    public void setRoot(Node<P, S, V> root) {
        this.root = root;
    }

    /**
     * Returns the left sentinel node (negative infinity bound).
     *
     * @return left sentinel leaf
     */
    public Node<P, S, V> getLeftSentinel() {
        return leftSentinel;
    }

    /**
     * Updates the left sentinel node.
     *
     * @param leftSentinel new left sentinel leaf
     */
    public void setLeftSentinel(Node<P, S, V> leftSentinel) {
        this.leftSentinel = leftSentinel;
    }

    /**
     * Returns the right sentinel node (positive infinity bound).
     *
     * @return right sentinel leaf
     */
    public Node<P, S, V> getRightSentinel() {
        return rightSentinel;
    }

    /**
     * Updates the right sentinel node.
     *
     * @param rightSentinel new right sentinel leaf
     */
    public void setRightSentinel(Node<P, S, V> rightSentinel) {
        this.rightSentinel = rightSentinel;
    }
}
