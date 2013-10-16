package commands;

import polly.core.MSG;
import polly.core.MyPlugin;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;


public class TalkCommand extends Command {

    public TalkCommand(MyPolly polly) throws DuplicatedSignatureException {
        super(polly, "talk"); //$NON-NLS-1$
        this.createSignature(MSG.talkSig0Desc.s, 
            MyPlugin.TALK_PERMISSION,
            new Parameter(MSG.talkSig0Msg.s, Types.STRING));
        this.createSignature(MSG.talkSig1Desc.s,
            MyPlugin.TALK_PERMISSION,
            new Parameter(MSG.talkSig1Channel.s, Types.CHANNEL), 
            new Parameter(MSG.talkSig1Msg.s, Types.STRING));
        this.setRegisteredOnly();
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) {
        return true;
    }
    
    
    
    @Override
    protected void executeOnChannel(User executer, String channel,
            Signature signature) {
        if (this.match(signature, 0)) {
            String m = signature.getStringValue(0);
            this.reply(channel, m);
        } else if (this.match(signature, 1)) {
            String c = signature.getStringValue(0);
            String m = signature.getStringValue(1);
            this.reply(c, m);
        }
    }
    
    
    
    @Override
    protected void executeOnQuery(User executer, Signature signature) {
        if (this.match(signature, 1)) {
            String c = signature.getStringValue(0);
            String m = signature.getStringValue(1);
            this.reply(c, m);
        }
    }
}
