package de.skuzzle.polly.core.parser.ast.expressions.literals;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.Expression;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.Transformation;


public class UserLiteral extends StringLiteral {
    
    public UserLiteral(Position position, String value) {
        super(position, value, Type.USER);
    }

    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatUser(this);
    }
    
    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type == Type.STRING) {
            return new StringLiteral(this.getPosition(), this.getValue());
        }
        return super.castTo(type);
    }
    
    
    @Override
    public Expression transform(Transformation transformation)
            throws ASTTraversalException {
        return transformation.transformUser(this);
    }
}
