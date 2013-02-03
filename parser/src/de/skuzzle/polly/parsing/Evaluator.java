package de.skuzzle.polly.parsing;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.skuzzle.polly.parsing.ast.Root;
import de.skuzzle.polly.parsing.ast.declarations.Namespace;
import de.skuzzle.polly.parsing.ast.visitor.ASTTraversalException;
import de.skuzzle.polly.parsing.ast.visitor.DebugExecutionVisitor;
import de.skuzzle.polly.parsing.ast.visitor.ParentSetter;
import de.skuzzle.polly.parsing.ast.visitor.Unparser;
import de.skuzzle.polly.parsing.ast.visitor.Visitor;
import de.skuzzle.polly.parsing.ast.visitor.resolving.TypeResolver;


/**
 * This is the main class for accessing the most often used parser feature: evaluating
 * an input String. It parses the String, resolves all types and executes the input in
 * the context of a provided {@link Namespace}. Evaluation may be either successful or
 * fail. In the latter case, you can retrieve the Exception that caused the fail using
 * {@link #getLastError()}. If no exception occurred, you may retrieve the result using
 * {@link #getRoot()}.  
 * 
 * @author Simon Taddiken
 */
public class Evaluator {
    
    // TEST:
    public static void main(String[] args) throws IOException {
        String testMe = ":foo map({1,2,\"3\"}, \\-)";
        //testMe = ":foo if 3!=2 ? !{1,2,3} : {4,5,6}";
        final Evaluator eval = new Evaluator(testMe, "ISO-8859-1");
        File decls = new File("decls");
        decls.mkdirs();
        Namespace.setDeclarationFolder(decls);
        
        final Namespace ns = Namespace.forName("me");
        
        eval.evaluate(ns);
        
        if (eval.errorOccurred()) {
            final ASTTraversalException e = eval.getLastError(); 
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(testMe);
            System.out.println(e.getPosition().errorIndicatorString());
        } else {
            System.out.println(eval.getRoot());
            System.out.println(eval.unparse());
            System.out.println(ns.toString());
        }
        
        String testMe2 = ":bloo map({1,2,3,4}, String)->a";
        final Namespace other = Namespace.forName("other");
        final Evaluator eval2 = new Evaluator(testMe2, "ISO-8859-1");
        
        eval2.evaluate(other);
        
        if (eval2.errorOccurred()) {
            final ASTTraversalException e = eval2.getLastError(); 
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(testMe2);
            System.out.println(e.getPosition().errorIndicatorString());
        } else {
            System.out.println(eval2.getRoot());
            System.out.println(eval2.unparse());
        }
    }
    
    

    private final String input;
    private final String encoding;
    private Root lastResult;
    private ASTTraversalException lastError;
    
    
    
    public Evaluator(String input, String encoding) {
        this.input = input;
        this.encoding = encoding;
    }
    
    
    
    /**
     * Gets the input String which is parsed by this evaluator.
     * 
     * @return The input string.
     */
    public String getInput() {
        return this.input;
    }
    
    
    
    public void evaluate(Namespace namespace) throws UnsupportedEncodingException {
        try {
            final ExpInputParser parser = new ExpInputParser(this.input, this.encoding);
            this.lastResult = parser.parse();
            
            if (this.lastResult == null) {
                return;
            }
            
            // set parent attributes for all nodes
            final Visitor parentSetter = new ParentSetter();
            this.lastResult.visit(parentSetter);
            
            // resolve types
            TypeResolver.resolveAST(this.lastResult, namespace);
            
            final Visitor executor = new DebugExecutionVisitor(namespace);
            this.lastResult.visit(executor);
            
        } catch (ASTTraversalException e) {
            this.lastError = e;
        }
    }
    
    
    
    public boolean errorOccurred() {
        return this.lastError != null;
    }
    
    
    
    public ASTTraversalException getLastError() {
        return this.lastError;
    }
    
    
    
    public Root getRoot() {
        /*if (this.errorOccurred()) {
            throw new IllegalStateException("no valid result available");
        }*/
        return this.lastResult;
    }
    
    
    
    public String unparse() {
        return Unparser.toString(this.getRoot());
    }
}