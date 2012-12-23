package de.skuzzle.polly.parsing.ast.lang;

import java.util.Arrays;
import java.util.Collection;

import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.ResolvableIdentifier;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.MapTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.ProductTypeConstructor;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.Expression;
import de.skuzzle.polly.parsing.ast.expressions.literals.FunctionLiteral;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.parameters.Parameter;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.ast.visitor.resolving.TypeResolver;
import de.skuzzle.polly.parsing.util.Stack;



public abstract class TernaryOperator<FIRST extends Literal, SECOND extends Literal, 
        THIRD extends Literal> extends Operator {

    private static final long serialVersionUID = 1L;
    
    private final static ResolvableIdentifier FIRST_PARAM_NAME = 
            new ResolvableIdentifier(Position.NONE, "$first");
    private final static ResolvableIdentifier SECOND_PARAM_NAME =
            new ResolvableIdentifier(Position.NONE, "$second");
    private final static ResolvableIdentifier THIRD_PARAM_NAME =
        new ResolvableIdentifier(Position.NONE, "$third");
    
    private Type first;
    private Type second;
    private Type third;
    
    
    
    public TernaryOperator(OpType id) {
        super(id);
    }
    
    
    
    /**
     * Initializes the result- and operand types for this operator.
     * 
     * @param resulType The result type of this operator.
     * @param first Type of the first operand.
     * @param second Type of the second operand.
     * @param third Type of the third operand.
     */
    protected final void initTypes(Type resulType, Type first, Type second, Type third) {
        this.setUnique(resulType);
        this.first = first;
        this.second = second;
        this.third = third;

    }
    
    
    
    @Override
    protected FunctionLiteral createFunction() {
        Collection<Parameter> p = Arrays.asList(new Parameter[] {
            this.typeToParameter(this.first, FIRST_PARAM_NAME),
            this.typeToParameter(this.second, SECOND_PARAM_NAME),
            this.typeToParameter(this.third, THIRD_PARAM_NAME),
        });
        
        final FunctionLiteral func = new FunctionLiteral(Position.NONE, p, this);
        func.setUnique(
            new MapTypeConstructor(new ProductTypeConstructor(
                this.first, 
                this.second,
                this.third), this.getUnique()));
        return func;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public final void execute(Stack<Literal> stack, Namespace ns, 
            Visitor execVisitor) throws ASTTraversalException {
        
        final FIRST first = (FIRST) ns.resolveHere(FIRST_PARAM_NAME).getExpression();
        final SECOND second = (SECOND) ns.resolveHere(SECOND_PARAM_NAME).getExpression();
        final THIRD third = (THIRD) ns.resolveHere(THIRD_PARAM_NAME).getExpression();
        
        this.exec(stack, ns, first, second, third, 
            new Position(first.getPosition(), third.getPosition()), execVisitor);
    }
    
    
    /**
     * Called to generate the result of this operator on the stack.
     * 
     * @param stack The current execution stack.
     * @param ns The current execution namespace.
     * @param first First operand of this operator.
     * @param second Second operand of this operator.
     * @param third Third operand of this operator.
     * @param resultPos Position that can be used as position for the result literal.
     * @param execVisitor Current execution visitor.
     * @throws ASTTraversalException If executing fails for any reason.
     */
    protected abstract void exec(Stack<Literal> stack, Namespace ns, 
        FIRST first, SECOND second, THIRD third, Position resultPos, Visitor execVisitor) 
            throws ASTTraversalException;
    
    
    
    @Override
    public final void resolveType(Namespace ns, Visitor typeResolver)
            throws ASTTraversalException {
        final Expression first = ns.resolveHere(FIRST_PARAM_NAME).getExpression();
        final Expression second = ns.resolveHere(SECOND_PARAM_NAME).getExpression();
        final Expression third = ns.resolveHere(THIRD_PARAM_NAME).getExpression();
        
        this.resolve(first, second, third, ns, typeResolver);
    }
    
    
    
    /**
     * Called to context check this operator. Default implementation does nothing.
     * 
     * @param first The first operand of the operator call.
     * @param second The second operand of the operator call.
     * @param third The third operand of the operator call.
     * @param ns The current namespace.
     * @param typeResolver The {@link TypeResolver}.
     * @throws ASTTraversalException If context checking fails.
     */
    protected void resolve(Expression first, Expression second, Expression third, 
            Namespace ns, Visitor typeResolver) throws ASTTraversalException {
        // empty
    }
}
