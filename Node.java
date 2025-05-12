/**
 * Represents a node within a 2-3 tree, storing composite keys and a value,
 * along with pointers to parent, children, and siblings for tree navigation.
 *
 * @param <P> type of the primary key, must be Comparable
 * @param <S> type of the secondary key, must be Comparable
 * @param <V> type of the stored value
 */
public class Node<P extends Comparable<P>, S extends Comparable<S>, V> implements Comparable<Node<P, S, V>> {
    /** Composite primary and secondary keys for ordering. */
    private Keys<P, S> keys;
    /** Associated value stored in this node. */
    private V value;
    /** Pointers to parent and up to three children (left, middle, right). */
    private Node<P, S, V> parent, left, middle, right;
    /** Number of descendant leaf nodes (subtree size). */
    private int size;
    /** Sibling pointers for in-order traversal of leaf nodes. */
    private Node<P, S, V> leftSibling, rightSibling;

    /**
     * Creates a leaf node with specified key components and value.
     *
     * @param primary   primary key
     * @param secondary secondary key
     * @param value     value to store
     */
    public Node(P primary, S secondary, V value) {
        this.keys = new Keys<P, S>(primary, secondary);
        this.value = value;
        this.size = 1;
        this.leftSibling = null;
        this.rightSibling = null;
    }

    /**
     * Constructs a sentinel node representing plus or minus infinity.
     *
     * @param sign true for plus-infinity sentinel, false for minus-infinity
     */
    private Node(boolean sign) {
        if (sign) this.keys = Keys.createPlusInfinity();
        else this.keys = Keys.createMinusInfinity();
        this.value = null;
        this.size = 0;
        this.leftSibling = null;
        this.rightSibling = null;
    }

    /**
     * Factory method to create the negative-infinity sentinel node.
     *
     * @param <P> type of the primary key
     * @param <S> type of the secondary key
     * @param <V> type of the stored value
     * @return minus-infinity sentinel node
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> createLeftSentinel() {
        return new Node<>(false);
    }

    /**
     * Factory method to create the positive-infinity sentinel node.
     *
     * @param <P> type of the primary key
     * @param <S> type of the secondary key
     * @param <V> type of the stored value
     * @return plus-infinity sentinel node
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> createRightSentinel() {
        return new Node<>(true);
    }

    /**
     * Checks if this node is a leaf (no children).
     *
     * @return true if leaf node, false otherwise
     */
    public boolean isLeaf() {
        return this.left == null && this.middle == null && this.right == null;
    }

    /**
     * Recomputes this node's key and size based on its children.
     */
    public void update() {
        this.updateKey();
        this.updateSize();
    }

    /**
     * Updates this node's key to the maximum key of its children.
     */
    private void updateKey() {
        if (this.isLeaf()) return;
        this.setKeys(this.getLeft().getKeys());
        if (this.getMiddle() != null) this.setKeys(this.getMiddle().getKeys());
        if (this.getRight() != null) this.setKeys(this.getRight().getKeys());
    }

    /**
     * Updates this node's size to the sum of its children's sizes.
     */
    private void updateSize() {
        if (this.keys.isMinusInfinity() || this.keys.isPlusInfinity()) {
            this.size = 0;
            return;
        }
        if (this.isLeaf()) {
            this.size = 1;
            return;
        }
        int sum = 0;
        if (this.left != null) sum += this.left.getSize();
        if (this.middle != null) sum += this.middle.getSize();
        if (this.right != null) sum += this.right.getSize();
        this.size = sum;
    }

    /**
     * Computes the in-order rank of this node within the tree.
     *
     * @return one-based rank among leaves
     */
    public int rank() {
        Node<P, S, V> x = this;
        int rank = 1;
        Node<P, S, V> y = x.getParent();
        while (x != null && y != null) {
            if (y.getLeft() != null && x.equals(y.getMiddle())) {
                rank += y.left.size;
            } else if (x.equals(y.getRight())) {
                if (y.getLeft() != null) {
                    rank += y.left.size;
                }
                if (y.getMiddle() != null) {
                    rank += y.middle.size;
                }
            }
            x = y;
            y = y.getParent();
        }
        return rank;
    }

    /**
     * Compares this node's keys to another node's keys.
     *
     * @param otherNode node to compare against
     * @return negative if less, zero if equal, positive if greater
     * @throws IllegalArgumentException if otherNode is null
     */
    public int compareTo(Node<P, S, V> otherNode) {
        if (otherNode == null) throw new IllegalArgumentException("Comparing with a null value!");
        return this.keys.compareTo(otherNode.keys);
    }

    /**
     * Checks equality based on composite keys.
     *
     * @param object object to compare
     * @return true if keys are equal, false otherwise
     */
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Node)) return false;
        return this.compareTo((Node<P, S, V>) object) == 0;
    }

    /**
     * Retrieves this node's composite keys.
     *
     * @return node keys
     */
    public Keys<P, S> getKeys() {
        return keys;
    }

    /**
     * Sets this node's composite keys.
     *
     * @param keys new keys to assign
     */
    public void setKeys(Keys<P, S> keys) {
        this.keys = keys;
    }

    /**
     * Retrieves the stored value.
     *
     * @return node value
     */
    public V getValue() {
        return value;
    }

    /**
     * Updates the stored value.
     *
     * @param value new value to store
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Retrieves the parent node.
     *
     * @return parent node or null if root
     */
    public Node<P, S, V> getParent() {
        return parent;
    }

    /**
     * Sets the parent node pointer.
     *
     * @param parent parent node to assign
     */
    public void setParent(Node<P, S, V> parent) {
        this.parent = parent;
    }

    /**
     * Retrieves the left child.
     *
     * @return left child node
     */
    public Node<P, S, V> getLeft() {
        return left;
    }

    /**
     * Assigns the left child pointer.
     *
     * @param left node to set as left child
     */
    public void setLeft(Node<P, S, V> left) {
        this.left = left;
    }

    /**
     * Retrieves the middle child.
     *
     * @return middle child node or null
     */
    public Node<P, S, V> getMiddle() {
        return middle;
    }

    /**
     * Assigns the middle child pointer.
     *
     * @param middle node to set as middle child
     */
    public void setMiddle(Node<P, S, V> middle) {
        this.middle = middle;
    }

    /**
     * Retrieves the right child.
     *
     * @return right child node or null
     */
    public Node<P, S, V> getRight() {
        return right;
    }

    /**
     * Assigns the right child pointer.
     *
     * @param right node to set as right child
     */
    public void setRight(Node<P, S, V> right) {
        this.right = right;
    }

    /**
     * Retrieves the subtree size at this node.
     *
     * @return number of descendant leaves
     */
    public int getSize() {
        return size;
    }

    /**
     * Updates this node's subtree size.
     *
     * @param size new size value
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Checks if this is the plus-infinity sentinel.
     *
     * @return true for plus-infinity sentinel
     */
    public boolean isPlusInfinity() {
        return this.keys.isPlusInfinity();
    }

    /**
     * Checks if this is the minus-infinity sentinel.
     *
     * @return true for minus-infinity sentinel
     */
    public boolean isMinusInfinity() {
        return this.keys.isMinusInfinity();
    }

    /**
     * Retrieves the in-order left sibling.
     *
     * @return left sibling node or null
     */
    public Node<P, S, V> getLeftSibling() {
        return leftSibling;
    }

    /**
     * Sets the in-order left sibling pointer.
     *
     * @param leftSibling node to assign as left sibling
     */
    public void setLeftSibling(Node<P, S, V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    /**
     * Retrieves the in-order right sibling.
     *
     * @return right sibling node or null
     */
    public Node<P, S, V> getRightSibling() {
        return rightSibling;
    }

    /**
     * Sets the in-order right sibling pointer.
     *
     * @param rightSibling node to assign as right sibling
     */
    public void setRightSibling(Node<P, S, V> rightSibling) {
        this.rightSibling = rightSibling;
    }

}
