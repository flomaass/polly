package commands;

import java.util.Collections;

import polly.logging.MSG;
import polly.logging.MyPlugin;
import core.DefaultLogFormatter;
import core.LogFormatter;
import core.PollyLoggingManager;
import core.output.IrcLogOutput;
import core.output.LogOutput;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import entities.LogEntry;

public class SeenCommand extends Command {
    
    private PollyLoggingManager logManager;
    
    public SeenCommand(MyPolly polly, PollyLoggingManager logManager) 
            throws DuplicatedSignatureException {
        super(polly, "seen"); //$NON-NLS-1$
        this.logManager = logManager;
        this.createSignature(MSG.seenSig0Desc, 
            MyPlugin.SEEN_PERMISSION,
            new Parameter(MSG.seendSig0User, Types.USER));
        this.setHelpText(MSG.seenHelp);
    }
    
    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
            Signature signature) throws CommandException {
        
        if (this.match(signature, 0)) {
            String user = signature.getStringValue(0);
            try {
                LogEntry le = this.logManager.seenUser(user);
                
                LogFormatter lf = new DefaultLogFormatter();
                LogOutput lo = new IrcLogOutput();
                if (!this.getMyPolly().irc().isOnChannel(le.getChannel(), 
                        executer.getCurrentNickName())) {
                    le = LogEntry.forMessage(le.getNickname(), MSG.seenHidden, 
                        le.getChannel(), le.getDate());
                }
                lo.outputLogs(this.getMyPolly().irc(), channel, 
                    Collections.singletonList(le), 1, lf, this.getMyPolly().formatting());
            } catch (Exception e) {
                throw new CommandException(e);
            }
        }
        
        return false;
    }

}
