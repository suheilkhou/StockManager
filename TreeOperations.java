/**
 * Utility class providing static operations for a 2-3 tree structure.
 * Includes methods for searching, finding minimum/predecessor/successor,
 * inserting, deleting, and maintaining sibling links.
 *
 * @param <P> type of the primary key, must be Comparable
 * @param <S> type of the secondary key, must be Comparable
 * @param <V> type of the stored value
 */
public interface TreeOperations<P extends Comparable<P>, S extends Comparable<S>, V> {

    /**
     * Searches for the leaf node in which the given key would reside.
     *
     * @param x starting node of type Node<P,S,V>
     * @param k composite key to search for
     * @return the leaf Node where the search terminates
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> search(Node<P, S, V> x, Keys<P, S> k) {
        if (x.isLeaf()) {
            return x;
        }
        if (k.smallerEqual(x.getLeft().getKeys())) {
            return search(x.getLeft(), k);
        } else if (k.smallerEqual(x.getMiddle().getKeys())) {
            return search(x.getMiddle(), k);
        } else return search(x.getRight(), k);
    }

    /**
     * Searches for the leaf node where keys are strictly larger than the given key.
     *
     * @param x starting node
     * @param k key to compare
     * @return the leaf Node that holds a larger key
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> searchLarger(Node<P, S, V> x, Keys<P, S> k) {
        if (x.isLeaf()) {
            return x;
        }
        if (k.smaller(x.getLeft().getKeys())) {
            return searchLarger(x.getLeft(), k);
        } else if (k.smaller(x.getMiddle().getKeys())) {
            return searchLarger(x.getMiddle(), k);
        } else return searchLarger(x.getRight(), k);
    }

    /**
     * Finds the node containing the minimum key in the given tree.
     *
     * @param T the tree to search
     * @return node with the smallest non-infinite key
     * @throws IllegalArgumentException if the tree is empty
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> minimum(Tree<P, S, V> T) {
        Node<P, S, V> x = T.getRoot();
        while (!x.isLeaf()) {
            x = x.getLeft();
        }
        x = x.getParent();
        if (!x.getKeys().isPlusInfinity() && !x.getKeys().isMinusInfinity()) return x;
        throw new IllegalArgumentException("The tree is empty!");
    }

    /**
     * Finds the immediate predecessor of the given node in key order.
     *
     * @param x node whose predecessor is sought
     * @return the predecessor node, or null if none exists
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> predecessor(Node<P, S, V> x) {
        Node<P, S, V> z = x.getParent();
        Node<P, S, V> y;
        while ((x.equals(z.getLeft())) || (z.getLeft() == null && x.equals(z.getMiddle()))) {
            x = z;
            z = z.getParent();
        }
        if (x.equals(z.getRight())) {
            y = z.getMiddle();
        } else {
            y = z.getLeft();
        }
        while (!y.isLeaf()) {
            if (y.getRight() != null) y = y.getRight();
            else y = y.getMiddle();
        }
        if (!y.getKeys().isMinusInfinity()) {
            return y;
        }
        return null;
    }

    /**
     * Sets the left, middle, and right children of a node, and updates parent links.
     *
     * @param x parent node to receive children
     * @param l left child
     * @param m middle child (may be null)
     * @param r right child (may be null)
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> void setChildren(Node<P, S, V> x, Node<P, S, V> l, Node<P, S, V> m, Node<P, S, V> r) {
        x.setLeft(l);
        x.setMiddle(m);
        x.setRight(r);
        l.setParent(x);
        if (m != null) m.setParent(x);
        if (r != null) r.setParent(x);
        x.update();
    }

    /**
     * Inserts a new node into a parent node and splits if necessary.
     *
     * @param x parent node where insertion occurs
     * @param z new node to insert
     * @return a new sibling node if split occurred, otherwise null
     */
    private static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> insertAndSplit(Node<P, S, V> x, Node<P, S, V> z) {
        Node<P, S, V> l = x.getLeft();
        Node<P, S, V> m = x.getMiddle();
        Node<P, S, V> r = x.getRight();
        if (r == null) {
            if (z.getKeys().smaller(l.getKeys())) setChildren(x, z, l, m);
            else if (z.getKeys().smaller(m.getKeys())) {
                setChildren(x, l, z, m);
            } else setChildren(x, l, m, z);
            return null;
        }
        Node<P, S, V> y = new Node<>(null, null, null);
        if (z.getKeys().smaller(l.getKeys())) {
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        } else if (z.getKeys().smaller(m.getKeys())) {
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        } else if (z.getKeys().smaller(r.getKeys())) {
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        } else {
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        return y;
    }

    /**
     * Handles the recursive insertion logic, including splitting and root adjustment.
     *
     * @param T tree into which to insert
     * @param z node to insert
     */
    private static <P extends Comparable<P>, S extends Comparable<S>, V> void insertt(Tree<P, S, V> T, Node<P, S, V> z) {
        Keys<P, S> key = z.getKeys();
        Node<P, S, V> y = T.getRoot();
        while (!y.isLeaf()) {
            if (z.getKeys().smaller(y.getLeft().getKeys())) {
                y = y.getLeft();
            } else if (z.getKeys().smaller(y.getMiddle().getKeys())) {
                y = y.getMiddle();
            } else {
                y = y.getRight();
            }
        }
        Node<P, S, V> x = y.getParent();
        z = insertAndSplit(x, z);

        while (x != T.getRoot()) {
            x = x.getParent();
            if (z != null) z = insertAndSplit(x, z);
            else x.update();
        }

        if (z != null) {
            Node<P, S, V> w = new Node<>(null, null, null);
            setChildren(w, x, z, null);
            T.setRoot(w);
        }
    }

    /**
     * Rebalances the tree by borrowing from or merging with a sibling node.
     *
     * @param y node needing rebalance
     * @return parent node affected by the merge or borrow
     */
    private static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> borrowOrMerge(Node<P, S, V> y) {
        Node<P, S, V> z = y.getParent();
        Node<P, S, V> x;
        if (y.equals(z.getLeft())) {
            x = z.getMiddle();
            if (x.getRight() != null) {
                setChildren(y, y.getLeft(), x.getLeft(), null);
                setChildren(x, x.getMiddle(), x.getRight(), null);
            } else {
                setChildren(x, y.getLeft(), x.getLeft(), x.getMiddle());
                setChildren(z, x, z.getRight(), null);
            }
            return z;
        }
        if (y.equals(z.getMiddle())) {
            x = z.getLeft();
            if (x.getRight() != null) {
                setChildren(y, x.getRight(), y.getLeft(), null);
                setChildren(x, x.getLeft(), x.getMiddle(), null);
            } else {
                setChildren(x, x.getLeft(), x.getMiddle(), y.getLeft());
                setChildren(z, x, z.getRight(), null);
            }
            return z;
        }
        x = z.getMiddle();
        if (x.getRight() != null) {
            setChildren(y, x.getRight(), y.getLeft(), null);
            setChildren(x, x.getLeft(), x.getMiddle(), null);
        } else {
            setChildren(x, x.getLeft(), x.getMiddle(), y.getLeft());
            setChildren(z, z.getLeft(), x, null);
        }
        return z;
    }

    /**
     * Deletes a node from the tree and rebalances by borrowing or merging as needed.
     *
     * @param T tree from which to delete
     * @param x node to remove
     */
    private static <P extends Comparable<P>, S extends Comparable<S>, V> void deletee(Tree<P, S, V> T, Node<P, S, V> x) {
        Node<P, S, V> y = x.getParent();

        if (x.equals(y.getLeft())) setChildren(y, y.getMiddle(), y.getRight(), null);
        else if (x.equals(y.getMiddle())) {
            setChildren(y, y.getLeft(), y.getRight(), null);
        } else {
            setChildren(y, y.getLeft(), y.getMiddle(), null);
        }

        while (y != null) {
            if (y.getMiddle() != null) {
                y.update();
                y = y.getParent();
            } else {
                if (y != T.getRoot()) {
                    y = borrowOrMerge(y);
                } else {
                    T.setRoot(y.getLeft());
                    y.getLeft().setParent(null);
                    return;
                }
            }
        }
    }

    /**
     * Finds the immediate successor of the given node in key order.
     *
     * @param x node whose successor is sought
     * @return the successor node, or null if none exists
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> Node<P, S, V> successor(Node<P, S, V> x) {
        Node<P, S, V> z = x.getParent();
        Node<P, S, V> y;
        while ((z.getRight() == null && x == z.getMiddle()) || x == z.getRight()) {
            x = z;
            z = z.getParent();
        }
        if (x == z.getLeft()) {
            y = z.getMiddle();
        } else {
            y = z.getRight();
        }
        while (!y.isLeaf()) {
            y = y.getLeft();
        }
        if (!y.getKeys().isPlusInfinity()) {
            return y;
        } else {
            return null;
        }
    }

    /**
     * Checks if a given key exists in the tree.
     *
     * @param T tree to search
     * @param x key to check
     * @return true if key is found, false otherwise
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> boolean exists(Tree<P, S, V> T, Keys<P, S> x) {
        Node<P, S, V> foundNode = search(T.getRoot(), x);
        return foundNode.getKeys().equals(x);
    }

    /**
     * Updates sibling pointers for nodes surrounding the given key.
     *
     * @param T tree in which to link siblings
     * @param x key whose node will have siblings linked
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> void handleSiblings(Tree<P, S, V> T, Keys<P, S> x) {
        if (x.isPlusInfinity() || x.isMinusInfinity()) return;
        Node<P, S, V> node = TreeOperations.search(T.getRoot(), x);
        Node<P, S, V> successor = TreeOperations.successor(node);
        if (successor == null) {
            successor = T.getRightSentinel();
        }
        Node<P, S, V> predecessor = TreeOperations.predecessor(node);
        if (predecessor == null) {
            predecessor = T.getLeftSentinel();
        }
        successor.setLeftSibling(node);
        predecessor.setRightSibling(node);
        node.setLeftSibling(predecessor);
        node.setRightSibling(successor);
    }

    /**
     * Inserts a node into the tree and updates sibling pointers.
     *
     * @param T tree into which to insert
     * @param z node to insert
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> void insert(Tree<P, S, V> T, Node<P, S, V> z) {
        insertt(T, z);
        Node<P, S, V> node = TreeOperations.search(T.getRoot(), z.getKeys());
        Node<P, S, V> predecessor = TreeOperations.predecessor(node);
        Node<P, S, V> successor = TreeOperations.successor(node);
        if (predecessor == null) {
            predecessor = T.getLeftSentinel();
        }
        if (successor == null) {
            successor = T.getRightSentinel();
        }
        predecessor.setRightSibling(node);
        successor.setLeftSibling(node);
        node.setLeftSibling(predecessor);
        node.setRightSibling(successor);
    }

    /**
     * Removes a node from the tree and updates sibling pointers accordingly.
     *
     * @param T tree from which to delete
     * @param x node to remove
     */
    public static <P extends Comparable<P>, S extends Comparable<S>, V> void delete(Tree<P, S, V> T, Node<P, S, V> x) {
        Node<P, S, V> node = TreeOperations.search(T.getRoot(), x.getKeys());
        Node<P, S, V> left = node.getLeftSibling();
        Node<P, S, V> right = node.getRightSibling();
        left.setRightSibling(right);
        right.setLeftSibling(left);
        deletee(T, x);
    }

}
