package polly.eventhandler;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import de.skuzzle.polly.parsing.ParseException;
import de.skuzzle.polly.sdk.CommandManager;
import de.skuzzle.polly.sdk.eventlistener.IrcUser;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.CommandException;
import de.skuzzle.polly.sdk.exceptions.InsufficientRightsException;
import de.skuzzle.polly.sdk.exceptions.UnknownCommandException;
import de.skuzzle.polly.sdk.exceptions.UnknownSignatureException;


import polly.core.users.User;
import polly.core.users.UserManagerImpl;




public class MessageHandler implements MessageListener {
    
    private final static int OFF = 0;
    private final static int SIMPLE = 1;

    
	private static Logger logger = Logger.getLogger(MessageHandler.class.getName());
    private CommandManager commands;
    private UserManagerImpl userManager;
    private ExecutorService executorThreadPool;
    private int parseErrorDetails;
    
    
    public MessageHandler(CommandManager commandManager, UserManagerImpl userManager, 
            ExecutorService executorThreadPool, int parseErrorDetails) {
        this.commands = commandManager;
        this.userManager = userManager;
        this.executorThreadPool = executorThreadPool;
        this.parseErrorDetails = parseErrorDetails;
    }
    

    
    @Override
    public void publicMessage(MessageEvent e) {
        this.execute(e, false);
    }

    
    
    @Override
    public void privateMessage(MessageEvent e) {
        this.execute(e, true);
    }

    
    
    @Override
    public void actionMessage(MessageEvent e) {
        this.getUser(e.getUser()).setLastMessageTime(System.currentTimeMillis());
    }
    
    
    
    private void execute(final MessageEvent e, final boolean isQuery) {
        final polly.core.users.User executor = this.getUser(e.getUser());
        executor.setLastMessageTime(System.currentTimeMillis());
        
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    MessageHandler.this.commands.executeString(e.getMessage(), e
                        .getChannel(), isQuery, executor, e.getSource());
                } catch(CommandException e1) {
                    if (e1.getCause() instanceof ParseException) {
                        ParseException e2 = (ParseException) e1.getCause();
                        MessageHandler.this.reportParseError(e, e2);
                    } else {
                        e.getSource().sendMessage(e.getChannel(), 
                            "Fehler beim Ausf�hren des Befehls: " + e1.getMessage());
                    }
                    logger.debug("", e1);
                } catch (UnknownCommandException e1) {
                    e.getSource().sendMessage(e.getChannel(), "Unbekannter Befehl: " + 
                            e1.getMessage());
                } catch (UnknownSignatureException e1) {
                    e.getSource().sendMessage(e.getChannel(), "Unbekannte Signatur: " + 
                            e1.getSignature().toString());
                } catch (InsufficientRightsException e1) {
                    e.getSource().sendMessage(e.getChannel(), "Du kannst den Befehl '" + 
                            e1.getObject() + "' nicht ausf�hren. Ben�tigte Rechte: " + 
                            e1.getObject().getRequiredPermission());
                } catch (Exception e1) {
                    logger.error("Exception while executing command: " + 
                            e1.getMessage(), e1);
                    e.getSource().sendMessage(e.getChannel(), 
                            "Interner Fehler beim Ausf�hren des Befehls.");
                }
            }
        };
        
        this.executorThreadPool.execute(command);
    }
    
    
    
    private void reportParseError(MessageEvent e, ParseException ex) {
        int detail = this.parseErrorDetails;
        if (detail == OFF) {
            return;
        }
        
        if (detail > OFF) {
            e.getSource().sendMessage(e.getChannel(), ex.getMessage());
        }
        if (detail > SIMPLE) {
            e.getSource().sendMessage(e.getChannel(), 
                ex.getPosition().mark(e.getMessage()));
        }
    }
    
    
    
    private polly.core.users.User getUser(IrcUser user) {
        polly.core.users.User u = (polly.core.users.User) this.userManager.getUser(user);
        if (u == null) {
            u = (User) this.userManager.createUser("~UNKNOWN", "blabla");
            u.setCurrentNickName(user.getNickName());
        }
        return u;
    }



    @Override
    public void noticeMessage(MessageEvent e) {
        this.getUser(e.getUser()).setLastMessageTime(System.currentTimeMillis());
    }
}
