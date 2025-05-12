/**
 * Composite key consisting of a primary and secondary component, with optional
 * plus- or minus-infinity sentinels for boundary handling.
 *
 * @param <P> type of the primary key, must implement Comparable
 * @param <S> type of the secondary key, must implement Comparable
 */
public class Keys<P extends Comparable<P>, S extends Comparable<S>> implements Comparable<Keys<P, S>> {
    /** Primary component of the composite key. */
    private P primary;
    /** Secondary component of the composite key. */
    private S secondary;
    /** Flag indicating this key represents positive infinity. */
    private boolean isPlusInfinity;
    /** Flag indicating this key represents negative infinity. */
    private boolean isMinusInfinity;

    /**
     * Constructs a composite key with the given primary and secondary values.
     * Both infinity flags default to false.
     *
     * @param primary   primary key component
     * @param secondary secondary key component
     */
    public Keys(P primary, S secondary) {
        this.primary = primary;
        this.secondary = secondary;
        this.isPlusInfinity = false;
        this.isMinusInfinity = false;
    }

    /**
     * Constructs a key with only a primary component; secondary is null.
     *
     * @param primary primary key component
     */
    private Keys(P primary) {
        this.primary = primary;
        this.secondary = null;
        this.isPlusInfinity = false;
        this.isMinusInfinity = false;
    }

    /**
     * Constructs an infinity sentinel key.
     *
     * @param isPlusInfinity  true to mark plus-infinity
     * @param isMinusInfinity true to mark minus-infinity
     */
    private Keys(boolean isPlusInfinity, boolean isMinusInfinity) {
        this.primary = null;
        this.secondary = null;
        this.isMinusInfinity = isMinusInfinity;
        this.isPlusInfinity = isPlusInfinity;
    }

    /**
     * Creates a key representing negative infinity.
     *
     * @param <P> primary key type
     * @param <S> secondary key type
     * @return a minus-infinity sentinel key
     */
    public static <P extends Comparable<P>, S extends Comparable<S>> Keys<P, S> createMinusInfinity() {
        return new Keys<>(false, true);
    }

    /**
     * Creates a key representing positive infinity.
     *
     * @param <P> primary key type
     * @param <S> secondary key type
     * @return a plus-infinity sentinel key
     */
    public static <P extends Comparable<P>, S extends Comparable<S>> Keys<P, S> createPlusInfinity() {
        return new Keys<>(true, false);
    }

    /**
     * Compares infinity flags between this and another key.
     *
     * @param other the key to compare against
     * @return 0 if both share the same infinity status,
     *         1 if this is greater (plus-inf vs normal or normal vs minus-inf),
     *         -1 if this is lesser (minus-inf vs normal or normal vs plus-inf),
     *         5 if neither key is infinite.
     */
    private int infinityComparing(Keys<P, S> other) {
        if ((this.isPlusInfinity && other.isPlusInfinity) || (this.isMinusInfinity && other.isMinusInfinity)) return 0;
        if (this.isPlusInfinity || other.isMinusInfinity) return 1;
        if (this.isMinusInfinity || other.isPlusInfinity) return -1;
        return 5;
    }

    /**
     * Compares this key to another, handling infinities first, then primary,
     * then secondary components.
     *
     * @param other the key to compare against
     * @return negative if this < other, zero if equal, positive if this > other
     * @throws IllegalArgumentException if primary components are null
     */
    @Override
    public int compareTo(Keys<P, S> other) {
        int infinityComparing = this.infinityComparing(other);
        if (infinityComparing != 5) return infinityComparing;
        if (this.primary == null || other.primary == null) throw new IllegalArgumentException("Primary keys are null!");
        if (this.getPrimary().compareTo(other.getPrimary()) != 0)
            return this.getPrimary().compareTo(other.getPrimary());
        if (this.secondary == null || other.secondary == null) return 0;
        return this.getSecondary().compareTo(other.getSecondary());
    }

    /**
     * Compares only the primary components of two keys, after handling infinities.
     *
     * @param other the key to compare against
     * @return comparison result of primary components or infinity result
     * @throws IllegalArgumentException if primary components are null
     */
    public int firstLayerCompare(Keys<P, S> other) {
        int infinityComparing = this.infinityComparing(other);
        if (infinityComparing != 5) return infinityComparing;
        if (this.primary == null || other.primary == null) throw new IllegalArgumentException("Primary keys are null!");
        return this.primary.compareTo(other.primary);
    }

    /**
     * Checks equality based solely on primary components, after handling infinities.
     *
     * @param obj object to compare
     * @return true if primaries equal or both infinite in same direction
     */
    public boolean firstLayerEquals(Object obj) {
        if (!(obj instanceof Keys)) return false;
        Keys<P, S> keys = (Keys<P, S>) obj;
        int infinityComparing = this.infinityComparing(keys);
        if (infinityComparing != 5) return infinityComparing == 0;
        if (this.primary == null || keys.primary == null) throw new IllegalArgumentException("Primary keys are null!");
        return this.primary.equals(keys.primary);
    }

    /**
     * Checks full equality of two keys, including primary, secondary,
     * and infinity status.
     *
     * @param obj object to compare
     * @return true if keys are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Keys)) return false;
        Keys<P, S> otherKey = (Keys<P, S>) obj;
        int infinityComparing = this.infinityComparing(otherKey);
        if (infinityComparing != 5) return infinityComparing == 0;
        return this.compareTo(otherKey) == 0;
    }

    /**
     * Checks if this key is less than or equal to another key.
     *
     * @param keys key to compare
     * @return true if this <= keys
     */
    public boolean smallerEqual(Keys<P, S> keys) {
        return this.compareTo(keys) <= 0;
    }

    /**
     * Checks if this key is strictly less than another key.
     *
     * @param keys key to compare
     * @return true if this < keys
     */
    public boolean smaller(Keys<P, S> keys) {
        return this.compareTo(keys) < 0;
    }

    /** Returns the primary key component. */
    public P getPrimary() {
        return primary;
    }

    /** Sets the primary key component. */
    public void setPrimary(P primary) {
        this.primary = primary;
    }

    /** Returns the secondary key component. */
    public S getSecondary() {
        return secondary;
    }

    /** Sets the secondary key component. */
    public void setSecondary(S secondary) {
        this.secondary = secondary;
    }

    /** Returns true if this key is plus-infinity. */
    public boolean isPlusInfinity() {
        return isPlusInfinity;
    }

    /** Sets the plus-infinity flag. */
    public void setPlusInfinity(boolean plusInfinity) {
        isPlusInfinity = plusInfinity;
    }

    /** Returns true if this key is minus-infinity. */
    public boolean isMinusInfinity() {
        return isMinusInfinity;
    }

    /** Sets the minus-infinity flag. */
    public void setMinusInfinity(boolean minusInfinity) {
        isMinusInfinity = minusInfinity;
    }
}
