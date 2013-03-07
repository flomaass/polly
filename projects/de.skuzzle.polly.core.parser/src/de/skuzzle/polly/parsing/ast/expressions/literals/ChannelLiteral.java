package de.skuzzle.polly.parsing.ast.expressions.literals;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Transformation;

/**
 * Represents a channel literal. Channels are identifiers preceded by '#'.
 * @author Simon Taddiken
 */
public class ChannelLiteral extends StringLiteral {
    
    /**
     * Creates a new ChannelLiteral.
     * 
     * @param position The position of this literal within the source.
     * @param value This literal's value (including '#').
     */
    public ChannelLiteral(Position position, String value) {
        super(position, value, Type.CHANNEL);
    }

    
    
    @Override
    public Literal castTo(Type type) throws ASTTraversalException {
        if (type.equals(Type.STRING)) {
            return new StringLiteral(this.getPosition(), this.getValue());
        }
        return super.castTo(type);
    }
    
    
    
    @Override
    public String format(LiteralFormatter formatter) {
        return formatter.formatChannel(this);
    }
    
    
    
    @Override
    public Expression transform(Transformation transformation) throws ASTTraversalException {
        return transformation.transformString(this);
    }
}