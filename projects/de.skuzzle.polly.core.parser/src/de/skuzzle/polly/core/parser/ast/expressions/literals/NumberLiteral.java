package de.skuzzle.polly.core.parser.ast.expressions.literals;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;
import de.skuzzle.polly.core.parser.problems.ProblemReporter;
import de.skuzzle.polly.core.parser.problems.Problems;
import de.skuzzle.polly.tools.Equatable;

/**
 * Represents a number literal. Numbers are always floating point doubles.
 * 
 * @author Simon Taddiken
 */
public class NumberLiteral extends Literal {

    private final double value;
    private int radix;
    
    
    /**
     * Creates a new NumberLiteral.
     * 
     * @param position Position of this literal within the source.
     * @param value Value of this literal.
     */
    public NumberLiteral(Position position, double value) {
        super(position, Type.NUM);
        this.value = value;
        this.radix = 10;
    }
    
    
    
    /**
     * Gets the radix of this number. Only suitable for integer numbers and will only
     * be used when formatting this literal using {@link #format(LiteralFormatter)}.
     * 
     * @return The radix of this literal.
     */
    public int getRadix() {
        return this.radix;
    }
    
    
    
    /**
     * Sets the radix for this literal. Only suitable for integer numbers and will only
     * have effect on the output of {@link #format(LiteralFormatter)}.
     * 
     * @param radix The new radix for this number.
     */
    public void setRadix(int radix) {
        this.radix = radix;
    }
    
    
    
    /**
     * Gets the value of this literal.
     * 
     * @return The literal's value.
     */
    public double getValue() {
        return this.value;
    }
    
    
    
    /**
     * Asserts that this literal's value is an integer number. If not, an 
     * {@link ASTTraversalException} will be thrown. The exception uses this literal's
     * position.
     * 
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as integer.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int isInteger(ProblemReporter reporter) throws ASTTraversalException {
        return this.isInteger(this.getPosition(), reporter);
    }
    
    
    
    /**
     * Asserts that this literal's value is an integer number. If not, an 
     * {@link ASTTraversalException} will be thrown.
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as integer.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int isInteger(Position pos, ProblemReporter reporter) 
            throws ASTTraversalException {
        if (Math.round(this.getValue()) != this.getValue()) {
            reporter.runtimeProblem(Problems.INTEGER_REQUIRED, pos);
        }
        return (int) Math.round(this.getValue());
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero and integer. If not, an 
     * {@link ASTTraversalException} will be thrown. The exception uses this literal's
     * position.
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public int nonZeroInteger(ProblemReporter reporter) throws ASTTraversalException {
        this.nonZero(reporter);
        return this.isInteger(reporter);
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero and integer. If not, 
     * an {@link ASTTraversalException} will be thrown.
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0 or the value
     *          is no integer.
     */
    public int nonZeroInteger(Position pos, ProblemReporter reporter) 
            throws ASTTraversalException {
        this.nonZero(pos, reporter);
        return this.isInteger(pos, reporter);
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero. If it is zero, an 
     * {@link ASTTraversalException} exception is thrown. The exception uses the position
     * of this literal.
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public double nonZero(ProblemReporter reporter) throws ASTTraversalException {
        return this.nonZero(this.getPosition(), reporter);
    }
    
    
    
    /**
     * Asserts that this literal's value is non zero. If it is zero, an 
     * {@link ASTTraversalException} exception is thrown. 
     * 
     * @param pos Position that will be reported in the thrown exception.
     * @param reporter Error reporter which will report the assertion error.
     * @return This literal's value as returned by {@link #getValue()}.
     * @throws ASTTraversalException If {@link #getValue()} returns 0.
     */
    public double nonZero(Position pos, ProblemReporter reporter) 
            throws ASTTraversalException {
        if (this.getValue() == 0.0) {
            reporter.runtimeProblem(Problems.DIVISION_BY_ZERO, pos);
        }
        
        return this.getValue();
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatNumberLiteral(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformNumber(this);
    }

    
    
    @Override
    public int compareTo(Literal o) {
        if (o instanceof NumberLiteral) {
            return Double.compare(this.value, ((NumberLiteral) o).value);
        }
        return super.compareTo(o);
    }
    
    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.equals(Type.TIMESPAN)) {
            // treat this number as milliseconds
            return new TimespanLiteral(this.getPosition(), (int)(this.getValue() / 1000));
        }
        return super.castTo(type);
    }
    
    
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    
    
    @Override
    public Class<?> getEquivalenceClass() {
        return NumberLiteral.class;
    }
    
    
    
    @Override
    public boolean actualEquals(Equatable o) {
        final NumberLiteral other = (NumberLiteral) o;
        return Double.compare(this.value, other.value) == 0;
    }
}
