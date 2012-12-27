package de.skuzzle.polly.parsing.ast.visitor;

import de.skuzzle.polly.parsing.ast.Identifier;
import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Declaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Braced;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.parameters.FunctionParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.ListParameter;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;

/**
 * Visitor that sets the parent attribute for each node in the AST
 * 
 * @author Simon Taddiken
 */
public class ParentSetter extends DepthFirstVisitor {
    
    /** Convenience constant that holds an instance of this class */
    public final static Visitor DEFAULT_INSTANCE = new ParentSetter();
    
    

    @Override
    public void beforeAccess(NamespaceAccess access) throws ASTTraversalException {
        access.getLhs().setParent(access);
        access.getRhs().setParent(access);
    }
    
    
    
    @Override
    public void beforeAssignment(Assignment assign) throws ASTTraversalException {
        assign.getExpression().setParent(assign);
        assign.getName().setParent(assign);
    }
    
    
    
    @Override
    public void beforeBraced(Braced braced) throws ASTTraversalException {
        braced.getExpression().setParent(braced);
    }
    
    
    @Override
    public void beforeCall(Call call) throws ASTTraversalException {
        call.getLhs().setParent(call);
        call.getRhs().setParent(call);
    }
    
    
    
    @Override
    public void beforeFunctionLiteral(FunctionLiteral func) throws ASTTraversalException {
        func.getExpression().setParent(func);
        for (final Parameter p : func.getFormal()) {
            p.setParent(func);
        }
    }
    
    
    
    @Override
    public void beforeFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        param.getName().setParent(param);
        for (final Identifier id : param.getSignature()) {
            id.setParent(param);
        }
    }
    
    
    
    @Override
    public void beforeListParameter(ListParameter param) throws ASTTraversalException {
        param.getTypeName().setParent(param);
    }
    
    
    
    @Override
    public void beforeOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeCall(call);
    }
    
    
    
    @Override
    public void beforeParameter(Parameter param) throws ASTTraversalException {
        param.getName().setParent(param);
        param.getTypeName().setParent(param);
    }
    
    
    
    @Override
    public void beforeRoot(Root root) throws ASTTraversalException {
        root.getCommand().setParent(root);
        for(final Expression exp : root.getExpressions()) {
            exp.setParent(root);
        }
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
        access.getIdentifier().setParent(access);
    }
    
    
    
    @Override
    public void beforeDecl(Declaration decl) throws ASTTraversalException {
        decl.getExpression().setParent(decl);
        decl.getName().setParent(decl);
    }
}
