package de.skuzzle.polly.core.parser.ast.expressions;

import java.util.Collection;
import java.util.List;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;

/**
 * Encapsulates another expression for the sole reason to represent that that expression
 * was entered in braces. 
 * 
 * @author Simon Taddiken
 */
public class Braced extends Expression {
    
    private Expression expression;
    
    
    
    /**
     * Creates a new braced expression.
     * 
     * @param position Source position of this expression.
     * @param braced The expression in braces.
     */
    public Braced(Position position, Expression braced) {
        super(position, braced.getUnique());
        this.expression = braced;
    }
    
    
    
    @Override
    public void setUnique(Type type) {
        this.expression.setUnique(type);
    }
    
    
    
    @Override
    public Type getUnique() {
        return this.expression.getUnique();
    }
    
    
    
    @Override
    public boolean addType(Type type) {
        return this.expression.addType(type);
    }
    
    
    
    @Override
    public void addTypes(Collection<? extends Type> types) {
        this.expression.addTypes(types);
    }
    
    
    
    @Override
    public List<Type> getTypes() {
        return this.expression.getTypes();
    }
    

    
    /**
     * Sets the expression in parenthesis.
     * 
     * @param expression The braced expression.
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) 
            throws ASTTraversalException {
        return transformation.transformBraced(this);
    }

    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        switch (visitor.before(this)) {
        case ASTTraversal.SKIP: return true;
        case ASTTraversal.ABORT: return false;
        }
        if (!this.expression.traverse(visitor)) {
            return false;
        }
        return visitor.after(this) == ASTTraversal.CONTINUE;
    }
    
    
    
    /**
     * Gets the encapsulated expression.
     * 
     * @return The expression that occurred in braces.
     */
    public Expression getExpression() {
        return this.expression;
    }
    
    
    
    @Override
    public String toString() {
        return "(" + this.expression.toString() + ")";
    }
}
