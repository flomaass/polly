package de.skuzzle.polly.parsing.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import de.skuzzle.polly.parsing.ExecutionException;
import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.parsing.declarations.Declaration;
import de.skuzzle.polly.parsing.declarations.Namespace;
import de.skuzzle.polly.parsing.declarations.VarDeclaration;
import de.skuzzle.polly.parsing.tree.literals.CommandLiteral;
import de.skuzzle.polly.parsing.tree.literals.IdentifierLiteral;
import de.skuzzle.polly.parsing.tree.literals.Literal;




public class Root {
    
    private CommandLiteral name;
    private boolean collapsed;
    private List<Expression> parameters;
    private List<Literal> results;
    
    
    public Root(CommandLiteral name) {
        this.name = name;
        this.parameters = new ArrayList<Expression>();
        this.results = new LinkedList<Literal>();
    }
    
    
    
    public CommandLiteral getName() {
        return this.name;
    }
    
    
    
    public List<Expression> getParameters() {
        return this.parameters;
    }
    
    
    
    public List<Literal> getResults() {
        if (!collapsed) {
            throw new RuntimeException("Expressions not collapsed.");
        }
        
        return this.results;
    }
    

    public Expression contextCheck(Namespace context) throws ParseException {
        // at this time, the context contains only toplevel declarations and no 
        // local declarations within a functioncall/declaration
        
        for (int i = 0; i < this.parameters.size(); ++i) {
            Expression e = this.parameters.get(i).contextCheck(context);
            Declaration ans = new VarDeclaration(new IdentifierLiteral("ans"), e);
            context.addRoot(ans);
            this.parameters.set(i, e);
        }
        
        return null;
    }

    
    
    public void collapse(Stack<Literal> stack, Queue<Literal> answers) 
            throws ExecutionException {
        for (Expression e : this.parameters) {
            e.collapse(stack);
            
            if (!stack.isEmpty()) {
                Literal l = stack.pop();
                this.results.add(l);
                answers.add(l);
            }
        }
        
        this.collapsed = true;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        if (this.collapsed) {
            for (Literal r : this.results) {
                result.append(r.toString());
                result.append(" ");
            }
        }
        
        return result.toString();
    }


}
