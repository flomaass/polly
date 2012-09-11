package polly.core.http.actions;

import org.apache.log4j.Logger;

import polly.core.http.HttpInterface;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.http.HttpAction;
import de.skuzzle.polly.sdk.http.HttpEvent;
import de.skuzzle.polly.sdk.http.HttpTemplateContext;


public class LogoutHttpAction extends HttpAction {

    private final static Logger logger = Logger
        .getLogger(LogoutHttpAction.class.getName());
    
    public LogoutHttpAction(MyPolly myPolly) {
        super("/logout", myPolly);
    }
    
    
    
    @Override
    public HttpTemplateContext execute(HttpEvent e) {
        logger.info("HTTP logout: " + e.getSession().getUser());
        HttpTemplateContext context = new HttpTemplateContext(HttpInterface.PAGE_HOME);
        e.getSource().closeSession(e.getSession());
        return context;
    }

}
