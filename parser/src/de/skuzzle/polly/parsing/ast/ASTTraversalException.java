package de.skuzzle.polly.parsing.ast;

import de.skuzzle.polly.parsing.Position;

/**
 * This exception can be thrown by {@link Visitor}s to instantly abort the traversal 
 * process. It contains the source position of where the error occurred.
 * 
 * @author Simon Taddiken
 */
public class ASTTraversalException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Position position;
    
    
    public ASTTraversalException(Position position) {
        super();
        this.position = position;
    }
    
    

    public ASTTraversalException(Position position, String message, Throwable cause) {
        super(message, cause);
        this.position = position;
    }
    
    

    public ASTTraversalException(Position position, String message) {
        super(message);
        this.position = position;
    }

    
    
    public ASTTraversalException(Position position, Throwable cause) {
        super(cause);
        this.position = position;
    }
    
    
    
    public Position getPosition() {
        return this.position;
    }
}
