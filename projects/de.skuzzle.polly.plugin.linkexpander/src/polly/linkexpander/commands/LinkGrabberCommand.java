package polly.linkexpander.commands;

import polly.linkexpander.MSG;
import polly.linkexpander.MyPlugin;
import polly.linkexpander.core.LinkGrabberManager;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;


public class LinkGrabberCommand extends Command {

    private LinkGrabberManager grabber;
    
    public LinkGrabberCommand(MyPolly polly, LinkGrabberManager grabber) 
            throws DuplicatedSignatureException {
        super(polly, "grabber"); //$NON-NLS-1$
        this.grabber = grabber;
        
        this.createSignature(MSG.linkGrabberSig0Desc, 
            MyPlugin.GRABBER_PERMISSION,
            new Parameter(MSG.linkGrabberSig0Status, Types.BOOLEAN));
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException, InsufficientRightsException {
        
        if (this.match(signature, 0)) {
            this.grabber.setEnabled(signature.getBooleanValue(0));
            String state = this.grabber.isEnabled() ? 
                    MSG.linkGrabberOn : MSG.linkGrabberOff;
            
            this.reply(channel, MSG.bind(MSG.linkGrabberStatus, state));
        }
        
        return false;
    }
}
