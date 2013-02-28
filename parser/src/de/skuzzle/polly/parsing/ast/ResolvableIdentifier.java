package de.skuzzle.polly.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.ASTVisitor;

/**
 * ResolvableIdentifers are identifiers that refer to declarations. The actual declaration
 * of this identifier must be resolved during type checking.
 *  
 * @author Simon Taddiken
 *
 */
public class ResolvableIdentifier extends Identifier {
    
    private Declaration decl;
    private final List<Declaration> decls;
    
    
    /**
     * Creates a new ResolvableIdentifier.
     * 
     * @param position The source position of the identifier.
     * @param id The identifier.
     * @param wasEscaped Whether the identifier was created from an escaped token.
     */
    public ResolvableIdentifier(Position position, String id, boolean wasEscaped) {
        super(position, id, wasEscaped);
        this.decls = new ArrayList<Declaration>();
    }
    
    
    
    /**
     * Creates a new ResolvableIdentifier.
     * 
     * @param position The source position of the identifier.
     * @param id The identifier.
     */
    public ResolvableIdentifier(Position position, String id) {
        super(position, id);
        this.decls = new ArrayList<Declaration>();
    }
    
    
    
    /**
     * Creates a new ResolvableIdentifier from an existing identifier.
     * 
     * @param id The identifier.
     */
    public ResolvableIdentifier(Identifier id) {
        super(id.getPosition(), id.getId(), id.wasEscaped());
        this.decls = new ArrayList<Declaration>();
    }
    
    
    
    /**
     * Adds a possible declaration for this identifier.
     * 
     * @param decl The declaration.
     */
    public void addDeclaration(Declaration decl) {
        this.decls.add(decl);
    }
    
    
    
    /**
     * Gets all possible declarations for this identifier.
     * 
     * @return The possible declarations.
     */
    public List<Declaration> getDeclarations() {
        return this.decls;
    }
    
    
    
    /**
     * Sets the declaration of this identifier.
     * 
     * @param decl The declaration.
     */
    public void setDeclaration(Declaration decl) {
        this.decl = decl;
    }
    
    
    
    
    /**
     * Gets the declaration of this identifier.
     * 
     * @return The declaration.
     */
    public Declaration getDeclaration() {
        return this.decl;
    }
    
    
    
    @Override
    public boolean visit(ASTVisitor visitor) throws ASTTraversalException {
        return visitor.visit(this);
    }
}
