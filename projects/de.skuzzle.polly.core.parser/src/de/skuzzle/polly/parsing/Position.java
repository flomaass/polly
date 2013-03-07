package de.skuzzle.polly.parsing;

import java.io.Serializable;


import de.skuzzle.polly.tools.EqualsHelper;
import de.skuzzle.polly.tools.Equatable;
import de.skuzzle.polly.tools.Immutable;

/**
 * Represents a span within a one-lined String. It consists of a inclusive 
 * start position and an exclusive end position.
 * 
 * For convenience, a Position has methods to retrieve substrings of a given String:
 * {@link #prefix(String)} which returns a String which consists of all characters 
 * berfore this Position.
 * {@link #substring(String)} which returns exactly the String which Position this
 * object represents and {@link #postfix(String)} which returns a String consisting of
 * all characters which follow this span.   
 * 
 * @author Simon
 */
public class Position implements Serializable, Equatable, Immutable {
    
    private static final long serialVersionUID = 1L;

    /**
     * A Position instance that represents no valid position within a String. Can be used
     * for internally created AST Nodes, that have no matching representation within
     * the input String.
     */
    public final static Position NONE = new Position(-1, -1);
    
    
    
    private final int start;
    private final int end;
    
    
    
    /**
     * Creates a new Position Object with given start and end index.
     * 
     * @param start The inclusive start index of this Position.
     * @param end The exclusive end index of this position.
     */
    public Position(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    
    
    /**
     * Creates a new Position using the positions of the given tokens. The new
     * position will span from the start of the left token until the end of the 
     * right token.
     * 
     * @param left The left token.
     * @param right The right token.
     */
    public Position(Token left, Token right) {
        this(left.getPosition(), right.getPosition());
    }
    
    
    
    /**
     * Creates a new Position using the start and end indizes of the given positions.
     * The new Position will span from the start of the left position until the end of
     * the right position.
     * 
     * @param left The left position.
     * @param right The right position.
     */
    public Position(Position left, Position right) {
        this(left.getStart(), right.getEnd());
    }
    
    
    /**
     * Returns the starting index of this Position.
     * @return The inclusive start index.
     */
    public int getStart() {
        return this.start;
    }
    
    
    
    /**
     * Returns the end index of this Position.
     * @return The inclusive end index.
     */
    public int getEnd() {
        return this.end;
    }
    
    
    
    /**
     * Gets the count of characters that this position spans.
     * 
     * @return Character count between end and start position.
     */
    public int getWidth() {
        return this.end - this.start;
    }
    
    
    
    /**
     * Gives a String which indicates this position. That means, the returned String will
     * consist of spaces until the start position. There, a "^" will be printed to 
     * indicate the beginning of this position. If {@link #getWidth()} is greater than
     * 1, a second "^" will be printed at the end position. So the returned String will
     * have the length <code>getWidth() > 1 ? this.getEnd() : this.getStart()</code>
     * 
     * @return A String indicating this position.
     */
    public String errorIndicatorString() {
        if (this == Position.NONE) {
            return "";
        }
        final StringBuilder b = new StringBuilder(this.end);
        int i = 0;
        for (; i < this.start; ++i) {
            b.append(" ");
        }
        b.append("^");
        if (this.getWidth() > 1) {
            for (; i < this.end - 2; ++i) {
                b.append("-");
            }
            b.append("^");
        }
        return b.toString();
    }
    
    
    
    /**
     * Returns a prefix of the given String consisting of all characters with index
     * lower than {@link #getStart()}.
     * 
     * @param original The String in which this object represents a Position.
     * @return A substring of the original String.
     * @throws IllegalArgumentException If the original String is too short.
     */
    public String prefix(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start > original.length()) {
            throw new IllegalArgumentException("Original String is too short!");
        }
        return original.substring(0, this.start);
    }
    
    
    
    /**
     * Returns the postfix of the given string which consists of all characters that
     * occur after this positions end index (including the character at the end index
     * itself, because it is exclusive).
     * 
     * @param original The string to create the postfix from.
     * @return A postfix of that string.
     */
    public String postfix(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start == this.end - 1 && this.start == original.length()) {
            return " ";
        }
        return original.substring(this.end);
    }
    
    
    
    /**
     * Returns the substring of the given string that this position represents. If this
     * is {@link Position#NONE}, the whole other string will be returned.
     * 
     * @param original The string to create the substring from.
     * @return The substring.
     */
    public String substring(String original) {
        if (this.equals(Position.NONE)) {
            return original;
        } else if (this.start == this.end - 1 && this.start == original.length() || this.getWidth() == 0) {
            return " ";
        }
        return original.substring(this.start, this.end);
    }
    
    
    
    public String mark(String original) {
        return this.mark(original, "4\u001F", "\u001F");
    }
    
    
    
    public String mark(String original, String begin, String end) {
        StringBuilder result = new StringBuilder(original.length() + 19);

        result.append(this.prefix(original));
        result.append(begin);
        result.append(this.substring(original));
        result.append(end);
        result.append(this.postfix(original));
        return result.toString();
    }
    
    
    
    @Override
    public String toString() {
        return (this.start + 1) + (this.getWidth() == 0 ? "" : "-" + (this.end + 1));
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.end;
        result = prime * result + this.start;
        return result;
    }

    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return Position.class;
    }
    


    @Override
    public final boolean equals(Object obj) {
        return EqualsHelper.testEquality(this, obj);
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final Position other = (Position) o;
        return this.start == other.start && this.end == other.end;
    }
}