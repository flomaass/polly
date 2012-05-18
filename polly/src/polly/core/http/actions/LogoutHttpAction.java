package polly.core.http.actions;

import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class LogoutHttpAction extends HttpAction {

    public LogoutHttpAction() {
        super("/logout");
    }
    
    
    
    @Override
    public void execute(HttpEvent e, HttpTemplateContext context) {
        context.setTemplate("webinterface/pages/home.html");
        e.getSession().setUser(null);
    }

}