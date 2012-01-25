package de.skuzzle.polly.parsing.tree;

import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.Position;
import de.skuzzle.polly.parsing.Type;
import de.skuzzle.polly.parsing.declarations.Declaration;
import de.skuzzle.polly.parsing.declarations.FunctionDeclaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.Literal;



public class AssignmentExpression extends Expression {

    private static final long serialVersionUID = 1L;

    private Expression expression;
    private Declaration declaration;

    public AssignmentExpression(Expression expression, Position position,
            Declaration declaration) {
        super(position);
        this.expression = expression;
        this.declaration = declaration;
    }
    
    
    
    @Override
    public Expression contextCheck(Namespace context) throws ParseException {       
        /*
         * Note: check FunctionDeclaration before VarDeclaration, because 
         * FunctionDeclaration is a subclass of VarDeclaration! 
         */
        
        if (this.declaration instanceof FunctionDeclaration) {
            // no context check here!
            FunctionDeclaration func = (FunctionDeclaration) this.declaration;
            
            /* Resolve parameter types and add them as "empty" Expressions to the
             * local declarations. That means that any occurrence of a parameter 
             * variable on the left hand side of the declaration is replaced by an
             * Expression that only returns the type specified on the right hand side
             * of the declaration.
             */
            context.enter();
            
            Expression checked = null;
            try {
                for (VarDeclaration param : func.getFormalParameters()) {
                    // Resolve the declared type
                    Type type = param.getExpression().contextCheck(context).getType();
                    param.setType(type);
                    
                    context.add(param);
                }
                
                /* The name of the function being declared may not occur on the 
                 * left side. So mark it as forbidden.
                 */
                context.forbidFunction(func);
    
                // Check context, but do not replace the root of the left subtree 
                // (=> store result as a new expression)
                checked = this.expression.contextCheck(context);
            } finally {
                // make sure to always leave the declarations clean
                context.leave();
                context.allowFunction();
            }
            
            
            
            this.expression.setType(checked.getType());
            func.setExpression(this.expression);
            context.add(func);

            // Function definitions do not have a return value (yet?)
            this.setType(Type.UNKNOWN);
        } else if (this.declaration instanceof VarDeclaration) {
            
            this.expression = this.expression.contextCheck(context);
            
            ((VarDeclaration) this.declaration).setExpression(this.expression);
            context.add(this.declaration);
            this.setType(this.expression.getType());
            
            return this.expression;
        }
        return this;
    }

    
    
    @Override
    public void collapse(Stack<Literal> stack) throws ExecutionException {
        // nothing happens here. if this was a VarDeclaration, this expression will
        // be replaced by the declared expression during context check.
        // If this is aFunctionDeclaration: nothing to do here
    }



    @Override
    public Object clone() {
        AssignmentExpression result = new AssignmentExpression(
            (Expression)this.expression.clone(), 
            this.getPosition(), 
            (Declaration) this.declaration.clone());
        return result;
    }
}
