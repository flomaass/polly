package de.skuzzle.polly.parsing.ast.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.Node;
import de.skuzzle.polly.parsing.ast.declarations.FunctionParameter;
import de.skuzzle.polly.parsing.ast.declarations.ListParameter;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.Parameter;
import de.skuzzle.polly.parsing.ast.declarations.TypeDeclaration;
import de.skuzzle.polly.parsing.ast.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.ast.expressions.Assignment;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.NamespaceAccess;
import de.skuzzle.polly.parsing.ast.expressions.OperatorCall;
import de.skuzzle.polly.parsing.ast.expressions.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.types.FunctionType;
import de.skuzzle.polly.parsing.types.ListType;
import de.skuzzle.polly.parsing.types.Type;


public class TypeResolver extends DepthFirstVisitor {
    
    /**
     * Expression that does nothing except to represent the type that has been set in the
     * Constructor.
     * 
     * @author Simon Taddiken
     */
    private final static class EmptyExpression extends Expression {

        public EmptyExpression(Type type) {
            super(Position.EMPTY, type);
        }
        
        @Override
        public void visit(Visitor visitor) throws ASTTraversalException {}
    }
    
    

    private Namespace nspace;
    private final Namespace rootNs;
    private final Set<Node> checked;
    
    
    
    public TypeResolver(String namespace) {
        // create temporary namespace for executing user
        this.nspace = Namespace.forName(namespace);
        this.rootNs = nspace;
        this.checked = new HashSet<Node>();
    }
    
    
    
    /**
     * Creates a new sub namespace of the current namespace and sets that new namespace
     * as the current one.
     * 
     * @return The created namespace.
     */
    private Namespace enter() {
        return this.nspace = this.nspace.enter();
    }
    
    
    
    /**
     * Sets the current namespace as the parent of the current namespace.
     * 
     * @return The parent of the former current namespace.
     */
    private Namespace leave() {
        return this.nspace = this.nspace.getParent();
    }
    
    
    
    /**
     * Tests whether the given node has already been checked. If it was not already 
     * checked, it will be marked checked by the time you call this method.
     * 
     * @param node The node to check.
     * @return Whether the nodes type has already been resolved.
     */
    private final boolean testIsChecked(Node node) {
        return !this.checked.add(node);
    }
    
    
    
    @Override
    public void visitParameter(Parameter param) throws ASTTraversalException {
        this.beforeParameter(param);
        
        final TypeDeclaration decl = Namespace.resolveType(param.getTypeName());
        param.setType(decl.getType());
        
        this.afterParameter(param);
    }
    
    
    
    @Override
    public void visitListParameter(ListParameter param) throws ASTTraversalException {
        this.beforeListParameter(param);
        
        final TypeDeclaration mainTypeDecl = Namespace.resolveType(
            param.getMainTypeName());
        
        if (mainTypeDecl.getType() != Type.LIST) {
            throw new ASTTraversalException(param.getMainTypeName().getPosition(), 
                "Nur Listen k�nnen Typ-Parameter haben.");
        }
        final TypeDeclaration subTypeDecl = Namespace.resolveType(param.getTypeName());
        param.setType(new ListType(subTypeDecl.getType()));
        
        this.afterListParameter(param);
    }
    
    
    @Override
    public void visitFunctionParameter(FunctionParameter param)
            throws ASTTraversalException {
        this.beforeFunctionParameter(param);
        
        final Iterator<ResolvableIdentifier> it = param.getSignature().iterator();
        
        // first element is the return type
        final TypeDeclaration returnTypeDecl = Namespace.resolveType(it.next());
        final Collection<Type> types = new ArrayList<Type>(param.getSignature().size());
        while (it.hasNext()) {
            final TypeDeclaration decl = Namespace.resolveType(it.next());
            types.add(decl.getType());
        }
        
        param.setType(new FunctionType(returnTypeDecl.getType(), types));
        
        this.afterFunctionParameter(param);
    }
    
    
    
    @Override
    public void visitFunctionLiteral(FunctionLiteral func)
            throws ASTTraversalException {
        
        if (this.testIsChecked(func)) {
            return;
        }
        
        this.beforeFunctionLiteral(func);

        // add formal parameters as empty expressions into new local namespace, then
        // context check the functions expression to resolve the return type
        this.enter();
        final Iterator<Parameter> formalIt = func.getFormal().iterator();
        while (formalIt.hasNext()) {
            final Parameter p = formalIt.next();
            
            // resolve parameter type
            p.visit(this);
            
            final VarDeclaration vd = new VarDeclaration(
                p.getPosition(), p.getName(), 
                new EmptyExpression(p.getType()));
            vd.setParameter(true);
            
            this.nspace.declare(vd);
        }
        // now determine type of the function's expression
        func.getExpression().visit(this);
        this.leave();
        
        this.afterFunctionLiteral(func);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.testIsChecked(list)) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        if (list.getContent().isEmpty()) {
            list.setType(Type.EMPTY_LIST);
        } else {
            // resolve expression types
            Type last = null; 
            for (final Expression exp : list.getContent()) {
                exp.visit(this);
                if (last != null && !last.check(exp.getType())) {
                    throw new ASTTraversalException(exp.getPosition(), 
                        "Listen d�rfen nur Elemente des selben Typs beinhalten");
                }
                last = exp.getType();
            }
            list.setType(new ListType(last));
        }
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void visitAssignment(Assignment assign) throws ASTTraversalException {
        if (this.testIsChecked(assign)) {
            return;
        }
        
        this.beforeAssignment(assign);
        // resolve assignments type
        assign.getExpression().visit(this);
        assign.setType(assign.getExpression().getType());
        
        final VarDeclaration vd = new VarDeclaration(assign.getPosition(), 
            assign.getName(), assign.getExpression());
        
        // declarations are always stored in the root namespace!
        this.rootNs.declare(vd);
        
        this.afterAssignment(assign);
    }
    
    
    
    @Override
    public void visitOperatorCall(OperatorCall call) throws ASTTraversalException {
        this.beforeOperatorCall(call);
        // treat as normal call
        this.visitCall(call);
        this.afterOperatorCall(call);
    }

    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.testIsChecked(call)) {
            return;
        }
        
        this.beforeCall(call);
        
        // check what type of call this is. Might be a lambda call or a VarAccess which
        // in turn references a function
        call.getLhs().visit(this);
        
        // resolve actual parameter types
        for (final Expression exp : call.getParameters()) {
            exp.visit(this);
        }
        
        // create signature from actual parameter types.
        // signature does *not* obey return value as it is unknown by now.
        final FunctionType signature = call.createSignature();
        
        if (!call.getLhs().getType().check(signature)) {
            Type.typeError(call.getLhs().getType(), signature, call.getPosition());
        }
        
        // get lhs' type as FunctionType. This type already has a return type set,
        // which will be the return type of this call
        final FunctionType lhsType = (FunctionType) call.getLhs().getType();
        call.setType(lhsType.getReturnType());
        
        
        this.afterCall(call);
    }
    
    
    
    @Override
    public void visitVarAccess(VarAccess access) throws ASTTraversalException {
        if (this.testIsChecked(access)) {
            return;
        }
        
        this.beforeVarAccess(access);
        
        final VarDeclaration vd = this.nspace.resolveVar(
                access.getIdentifier(), Type.ANY);
        access.setType(vd.getType());
        
        this.afterVarAccess(access);
    }
    
    
    
    @Override
    public void visitAccess(NamespaceAccess access) throws ASTTraversalException {
        if (this.testIsChecked(access)) {
            return;
        }
        this.beforeAccess(access);
        // remember current nspace
        final Namespace backup = this.nspace;
        
        // get namespace which is accessed here and has the current namespace as 
        // parent. 
        final Expression lhs = access.getLhs();
        if (!(lhs instanceof VarAccess) || lhs instanceof Call) {
            throw new ASTTraversalException(lhs.getPosition(), 
                "Linker Operand muss ein Namespace-Name sein.");
        }
        final VarAccess va = (VarAccess) lhs;
        this.nspace = Namespace.forName(va.getIdentifier()).derive(this.nspace);
        access.getRhs().visit(this);
        this.nspace = backup;
        
        access.setType(access.getRhs().getType());
        
        this.afterAccess(access);
    }
}