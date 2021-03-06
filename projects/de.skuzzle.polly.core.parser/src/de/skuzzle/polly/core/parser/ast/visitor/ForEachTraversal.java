package de.skuzzle.polly.core.parser.ast.visitor;

import de.skuzzle.polly.core.parser.ast.Identifier;
import de.skuzzle.polly.core.parser.ast.Node;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.Root;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.directives.DelayDirective;
import de.skuzzle.polly.core.parser.ast.directives.ProblemDirective;
import de.skuzzle.polly.core.parser.ast.expressions.Assignment;
import de.skuzzle.polly.core.parser.ast.expressions.Braced;
import de.skuzzle.polly.core.parser.ast.expressions.Call;
import de.skuzzle.polly.core.parser.ast.expressions.Delete;
import de.skuzzle.polly.core.parser.ast.expressions.Inspect;
import de.skuzzle.polly.core.parser.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.core.parser.ast.expressions.Native;
import de.skuzzle.polly.core.parser.ast.expressions.OperatorCall;
import de.skuzzle.polly.core.parser.ast.expressions.Problem;
import de.skuzzle.polly.core.parser.ast.expressions.VarAccess;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.ProductLiteral;

/**
 * AST traversal which calls one overridable function for each node in a AST:
 * 
 * @author Simon Taddiken
 */
public class ForEachTraversal implements ASTTraversal {

    /**
     * This method is called before any node is visited.
     * 
     * @param node The node being currently visited.
     * @return Whether to continue traversal.
     */
    protected int beforeEach(Node node) {
        return CONTINUE;
    }
    
    
    
    /**
     * This method is called after any node is visited.
     * 
     * @param node The node that has been visited.
     * @return Whether to continue traversal.
     */
    protected int afterEach(Node node) {
        return CONTINUE;
    }
    
    
    
    @Override
    public int before(Root node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Root node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Literal node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Literal node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Identifier node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Identifier node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(ResolvableIdentifier node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(ResolvableIdentifier node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Assignment node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Assignment node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Declaration node) throws ASTTraversalException {
        return this.beforeEach(node);
    }

    
    
    @Override
    public int after(Declaration node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    

    @Override
    public int before(Call node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Call node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    

    @Override
    public int before(OperatorCall node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(OperatorCall node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Native node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Native node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    

    @Override
    public int before(NamespaceAccess node) throws ASTTraversalException {
        return this.beforeEach(node);
    }

    
    
    @Override
    public int after(NamespaceAccess node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(VarAccess node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(VarAccess node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(FunctionLiteral node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(FunctionLiteral node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    

    @Override
    public int before(ListLiteral node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(ListLiteral node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(ProductLiteral node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(ProductLiteral node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Braced node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Braced node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Delete node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    

    @Override
    public int after(Delete node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    

    @Override
    public int before(Inspect node) throws ASTTraversalException {
        return this.beforeEach(node);
    }

    
    
    @Override
    public int after(Inspect node) throws ASTTraversalException {
        return this.afterEach(node);
    }

    
    
    @Override
    public int before(Problem node) throws ASTTraversalException {
        return this.beforeEach(node);
    }

    
    
    @Override
    public int after(Problem node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    
    
    @Override
    public int before(DelayDirective node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    

    
    @Override
    public int after(DelayDirective node) throws ASTTraversalException {
        return this.afterEach(node);
    }
    
    
    
    @Override
    public int before(ProblemDirective node) throws ASTTraversalException {
        return this.beforeEach(node);
    }
    
    
    
    @Override
    public int after(ProblemDirective node) throws ASTTraversalException {
        return this.afterEach(node);
    }
}
