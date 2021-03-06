package de.skuzzle.polly.core.parser.ast.lang;

import java.util.Collection;
import java.util.Collections;

import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.ResolvableIdentifier;
import de.skuzzle.polly.core.parser.ast.declarations.Declaration;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.ProductType;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;



/**
 * This represents a casting operator. Casting operators are normal function calls
 * to a function which name equals the name of the cast target type.
 * 
 * @author Simon Taddiken
 *
 */
public class Cast extends Operator {

    protected final static ResolvableIdentifier PARAM_NAME = new ResolvableIdentifier(
        Position.NONE, "$param");

    
    private final Type operandType;
    
    /**
     * Creates a new Casting operator.
     * 
     * @param operator The operator type. Important: Must be one of the values from the
     *          'casting' section in <code>OpType</code>.
     * @param target Target type to cast to.
     */
    public Cast(OpType operator, Type target) {
        super(operator);
        this.setUnique(target);
        this.operandType = Type.newTypeVar();
    }
    
    

    @Override
    public void execute(Stack<Literal> stack, Namespace ns, ExecutionVisitor execVisitor)
            throws ASTTraversalException {
        
        // on a function call, parameters are already executed to be a Literal
        final Literal operand = (Literal) ns.resolveFirst(PARAM_NAME).getExpression();
        
        stack.push(operand.castTo(this.getUnique()));
    }


    
    @Override
    protected FunctionLiteral createFunction() {
        // create parameter that accepts any expression (Type.ANY)
        final Collection<Declaration> p = Collections.singletonList(
            this.typeToParameter(this.operandType, PARAM_NAME));
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(new ProductType(this.operandType).mapTo(this.getUnique()));
        return func;
    }
    
    

    @Override
    public Declaration createDeclaration() {
        final FunctionLiteral func = this.createFunction();
        final Declaration vd = new Declaration(
            func.getPosition(), this.getUnique().getName(), func);
        vd.setNative(true);
        return vd;
    }
}
