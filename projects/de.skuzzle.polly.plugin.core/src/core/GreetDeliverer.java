package core;


import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Types.StringType;
import de.skuzzle.polly.sdk.eventlistener.UserEvent;
import de.skuzzle.polly.sdk.eventlistener.UserListener;

public class GreetDeliverer implements UserListener {

    private MyPolly myPolly;
    
    public GreetDeliverer(MyPolly myPolly) {
        this.myPolly = myPolly;
    }

    @Override
    public void userSignedOn(UserEvent e) {
        final StringType greet = (StringType) e.getUser().getAttribute(MyPlugin.GREETING);
        if (greet != null) {
            // try to execute command. this either fails by exception or by returning
            // false. In that case, just deliver the plain greeting.
            try {
                if (!this.myPolly.commands().executeString(greet.getValue(), 
                        e.getUser().getCurrentNickName(), true, e.getUser(), 
                        this.myPolly.irc())) {
                    
                    this.myPolly.irc().sendMessage(e.getUser().getCurrentNickName(), 
                            greet.getValue(), this);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                this.myPolly.irc().sendMessage(e.getUser().getCurrentNickName(), 
                    greet.getValue(), this);
            }
        }
    }

    
    
    @Override
    public void userSignedOff(UserEvent e) {}
}
