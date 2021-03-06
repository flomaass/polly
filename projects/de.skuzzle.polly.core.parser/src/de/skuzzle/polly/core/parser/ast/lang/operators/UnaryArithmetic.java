package de.skuzzle.polly.core.parser.ast.lang.operators;


import de.skuzzle.polly.core.parser.Position;
import de.skuzzle.polly.core.parser.ast.declarations.Namespace;
import de.skuzzle.polly.core.parser.ast.declarations.types.Type;
import de.skuzzle.polly.core.parser.ast.expressions.literals.Literal;
import de.skuzzle.polly.core.parser.ast.expressions.literals.NumberLiteral;
import de.skuzzle.polly.core.parser.ast.lang.UnaryOperator;
import de.skuzzle.polly.core.parser.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.core.parser.ast.visitor.ExecutionVisitor;
import de.skuzzle.polly.tools.collections.Stack;


public class UnaryArithmetic extends UnaryOperator<NumberLiteral> {

    public UnaryArithmetic(OpType op) {
        super(op);
        this.initTypes(Type.NUM, Type.NUM);
    }

    
    @Override
    protected void exec(Stack<Literal> stack, Namespace ns, NumberLiteral operand,
            Position resultPos, ExecutionVisitor execVisitor) throws ASTTraversalException {
        
        switch (this.getOp()) {
        case EXCLAMATION:
            stack.push(new NumberLiteral(resultPos, 
                (double) ~operand.isInteger(execVisitor.getReporter())));
            break;
        case SUB:
            stack.push(new NumberLiteral(resultPos, -operand.getValue()));
            break;
        case LOG:
            stack.push(new NumberLiteral(resultPos, Math.log10(operand.getValue())));
            break;
        case LN:
            stack.push(new NumberLiteral(resultPos, Math.log(operand.getValue())));
            break;
        case SQRT:
            stack.push(new NumberLiteral(resultPos, Math.sqrt(operand.getValue())));
            break;
        case CEIL:
            stack.push(new NumberLiteral(resultPos, Math.ceil(operand.getValue())));
            break;
        case FLOOR:
            stack.push(new NumberLiteral(resultPos, Math.floor(operand.getValue())));
            break;
        case ROUND:
            stack.push(new NumberLiteral(resultPos, Math.round(operand.getValue())));
            break;
        case SIG:
            stack.push(new NumberLiteral(resultPos, Math.signum(operand.getValue())));
            break;
        case COS:
            stack.push(new NumberLiteral(resultPos, Math.cos(operand.getValue())));
            break;
        case SIN:
            stack.push(new NumberLiteral(resultPos, Math.sin(operand.getValue())));
            break;
        case TAN:
            stack.push(new NumberLiteral(resultPos, Math.tan(operand.getValue())));
            break;
        case ASIN:
            stack.push(new NumberLiteral(resultPos, Math.asin(operand.getValue())));
            break;
        case ACOS:
            stack.push(new NumberLiteral(resultPos, Math.acos(operand.getValue())));
            break;
        case ATAN:
            stack.push(new NumberLiteral(resultPos, Math.atan(operand.getValue())));
            break;
        case ABS:
            stack.push(new NumberLiteral(resultPos, Math.abs(operand.getValue())));
            break;
        case TO_DEGREES:
            stack.push(new NumberLiteral(resultPos, Math.toDegrees(operand.getValue())));
            break;
        case TO_RADIANS:
            stack.push(new NumberLiteral(resultPos, Math.toRadians(operand.getValue())));
            break;
        case EXP:
            stack.push(new NumberLiteral(resultPos, Math.exp(operand.getValue())));
            break;
        default:
            this.invalidOperatorType(this.getOp());
        }
    }
}
