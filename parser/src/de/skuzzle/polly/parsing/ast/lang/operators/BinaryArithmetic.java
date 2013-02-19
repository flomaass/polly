package de.skuzzle.polly.parsing.ast.lang.operators;


import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.declarations.types.Type;
import de.skuzzle.polly.parsing.ast.expressions.literals.Literal;
import de.skuzzle.polly.parsing.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.parsing.ast.lang.BinaryOperator;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.util.Stack;

/**
 * Contains arithmetic operators that operate on {@link NumberLiteral}s and produce a
 * new NumberLiteral.
 *  
 * @author Simon Taddiken
 */
public class BinaryArithmetic extends BinaryOperator<NumberLiteral, NumberLiteral> {

    private static final long serialVersionUID = 1L;
    
    
    public BinaryArithmetic(OpType id) {
        super(id);
        this.initTypes(Type.NUM, Type.NUM, Type.NUM);
    }
    
    

    @Override
    protected void exec(Stack<Literal> stack, Namespace ns,
            NumberLiteral left, NumberLiteral right, 
            Position resultPos, Visitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case ADD: 
            stack.push(new NumberLiteral(resultPos, left.getValue() + right.getValue()));
            break;
        case SUB:
            stack.push(new NumberLiteral(resultPos, left.getValue() - right.getValue()));
            break;
        case MUL:
            stack.push(new NumberLiteral(resultPos, left.getValue() * right.getValue()));
            break;
        case DIV:
            right.nonZero();
            stack.push(new NumberLiteral(resultPos, left.getValue() / right.getValue()));
            break;
        case INTDIV:
            right.nonZero();
            // XXX: implicit conversion
            stack.push(new NumberLiteral(resultPos, 
                Math.ceil(left.getValue()) / Math.ceil(right.getValue())));
            break;
        case MOD:
            int r = right.nonZeroInteger();
            int l = (left.isInteger()  + r) % r;
            stack.push(new NumberLiteral(resultPos, l));
        case INT_AND:
            stack.push(new NumberLiteral(resultPos, 
                left.isInteger() & right.isInteger()));
            break;
        case INT_OR:
            stack.push(new NumberLiteral(resultPos, 
                left.isInteger() | right.isInteger()));
            break;
        case LEFT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                    left.isInteger() << right.isInteger()));
            break;
        case RIGHT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                left.isInteger() >> right.isInteger()));
            break;
        case URIGHT_SHIFT:
            stack.push(new NumberLiteral(resultPos,
                left.isInteger() >>> right.isInteger()));
            break;
        case RADIX:
            right.setRadix(left.isInteger());
            stack.push(right);
            break;
        case POWER:
            stack.push(new NumberLiteral(resultPos, 
                Math.pow(left.getValue(), right.getValue())));
            break;
        case MIN:
            stack.push(new NumberLiteral(resultPos, 
                Math.min(left.getValue(), right.getValue())));
            break;
        case MAX:
            stack.push(new NumberLiteral(resultPos, 
                Math.max(left.getValue(), right.getValue())));
            break;
        case XOR:
            stack.push(new NumberLiteral(
                resultPos, left.isInteger() ^ right.isInteger()));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}