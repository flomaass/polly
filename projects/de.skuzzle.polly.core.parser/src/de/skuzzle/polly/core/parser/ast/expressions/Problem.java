package de.skuzzle.polly.core.parser.ast.expressions;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.declarations.types.MissingType;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ASTVisitor;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class Problem extends Expression {
    
    private static int id;
    
    public Problem(Position position) {
        super(position, new MissingType(
            new Identifier(position, "$problem_" + (id++))));
    }
    
    
    
    @Override
    public boolean isProblem() {
        return false;
    }
    
    

    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }

    
    
    @Override
    public Node transform(Transformation transformation) throws ASTTraversalException {
        return this;
    }

    
    
    @Override
    public boolean traverse(ASTTraversal visitor) throws ASTTraversalException {
        return visitor.before(this) != ASTTraversal.ABORT 
            && visitor.after(this) == ASTTraversal.CONTINUE;
    }

}
