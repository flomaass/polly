package de.skuzzle.polly.parsing.ast.visitor.resolving;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.declarations.types.TypeVar;
import de.skuzzle.polly.parsing.ast.expressions.Call;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.VarAccess;
import de.skuzzle.polly.parsing.ast.expressions.literals.ListLiteral;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;


class SecondPassTypeResolver extends AbstractTypeResolver {

    private final boolean DO_NOT_UNIFY = false;
    private final boolean DO_UNIFY = true;
    
    
    
    public SecondPassTypeResolver(FirstPassTypeResolver fptr) {
        super(fptr.getCurrentNameSpace());
    }
    
    
    
    private void applyType(Expression parent, Expression child) 
            throws ASTTraversalException {
        if (parent.getTypes().size() == 1 && !child.typeResolved()) {
            child.setUnique(parent.getTypes().get(0));
        } else {
            this.typeError(parent);
        }
    }
    
    
    
    @Override
    public void visitRoot(Root root) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        this.beforeRoot(root);
        
        for (final Expression exp : root.getExpressions()) {
            exp.visit(this);
        }
        
        this.afterRoot(root);
    }
    
    
    
    @Override
    public void visitListLiteral(ListLiteral list) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeListLiteral(list);
        
        for (final Expression exp : list.getContent()) {
            exp.visit(this);
            list.getUnique().isUnifiableWith(exp.getUnique(), DO_UNIFY);
        }
        
        this.afterListLiteral(list);
    }
    
    
    
    @Override
    public void beforeVarAccess(VarAccess access) throws ASTTraversalException {
    }
    
    
    
    @Override
    public void visitCall(Call call) throws ASTTraversalException {
        if (this.aborted) {
            return;
        }
        
        this.beforeCall(call);
        final Type t = call.typeResolved() ? call.getUnique() : TypeVar.create();
        
        MapTypeConstructor mtc = null;
        boolean uniqueMatch = false;
        for (final ProductTypeConstructor s : call.getSignatureTypes()) {
            if (uniqueMatch) {
                // Ambiguous types found
                this.typeError(call);
            }
            final MapTypeConstructor tmp = new MapTypeConstructor(s, t);
            
            for (final Type lhsType : call.getLhs().getTypes()) {
                uniqueMatch = lhsType.isUnifiableWith(tmp, DO_NOT_UNIFY);
                if (uniqueMatch) {
                    lhsType.isUnifiableWith(tmp, DO_UNIFY);
                    mtc = (MapTypeConstructor) lhsType;
                }
            }
        }
        
        if (!uniqueMatch) {
            // no matching type found
            this.typeError(call);
        }

        call.getLhs().setUnique(mtc);
        call.setUnique(t);
        
        // invariant: mtc.source.size == call.getParameters.size
        final Iterator<Type> uniqueIt = mtc.getSource().getTypes().iterator();
        for (final Expression exp : call.getParameters()) {
            exp.setUnique(uniqueIt.next());
        }
        
        call.getLhs().visit(this);

        
        this.afterCall(call);
    }
}