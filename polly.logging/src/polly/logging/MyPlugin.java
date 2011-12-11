package polly.logging;


import commands.ChannelLogCommand;
import commands.SeenCommand;
import commands.UserLogCommand;

import core.IrcLogCollector;
import core.PollyLoggingManager;
import core.pasteservice.PasteServiceManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.PollyPlugin;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.exceptions.IncompatiblePluginException;
import entities.LogEntry;



public class MyPlugin extends PollyPlugin {
    
    public final static String LOG_CACHE_SIZE = "logCacheSize";
    public final static String LOG_PASTE_TRESHOLD = "logPasteTreshold";
    public final static String LOG_MAX_LOGS = "logMaxLogs";
    
    public final static int DEFAULT_LOG_CACHE_SIZE = 100;
    public final static int DEFAULT_LOG_TRESHOLD = 10;
    public final static int DEFAULT_MAX_LOGS = 100;

    
    private IrcLogCollector logCollector;
    private PollyLoggingManager logManager;
    private PasteServiceManager pasteServiceManager;
    
    
    
    public MyPlugin(MyPolly myPolly) throws IncompatiblePluginException, DuplicatedSignatureException {
        super(myPolly);

        myPolly.persistence().registerEntity(LogEntry.class);
        
        
        int cacheSize = myPolly.configuration().readInt(
                LOG_CACHE_SIZE, DEFAULT_LOG_CACHE_SIZE);
        int pasteTreshold = myPolly.configuration().readInt(
            LOG_PASTE_TRESHOLD, DEFAULT_LOG_TRESHOLD);
        int maxLogs = myPolly.configuration().readInt(LOG_MAX_LOGS, DEFAULT_MAX_LOGS);
        

        this.pasteServiceManager = new PasteServiceManager("pastebin");
        this.logManager = new PollyLoggingManager(
            myPolly, this.pasteServiceManager, cacheSize, pasteTreshold, maxLogs);
        
        this.logCollector = new IrcLogCollector(this.logManager);
        
        this.addDisposable(this.logManager);
        
        myPolly.irc().addJoinPartListener(this.logCollector);
        myPolly.irc().addMessageListener(this.logCollector);
        myPolly.irc().addQuitListener(this.logCollector);
        myPolly.irc().addNickChangeListener(this.logCollector);
        
        this.addCommand(new UserLogCommand(myPolly, this.logManager));
        this.addCommand(new ChannelLogCommand(myPolly, this.logManager));
        this.addCommand(new SeenCommand(myPolly, this.logManager));
    }
    
    
    
    @Override
    protected void actualDispose() throws DisposingException {
        super.actualDispose();
        
        this.getMyPolly().irc().removeJoinPartListener(this.logCollector);
        this.getMyPolly().irc().removeMessageListener(this.logCollector);
        this.getMyPolly().irc().removeQuitListener(this.logCollector);
        this.getMyPolly().irc().removeNickChangeListener(this.logCollector);
    }
    
    
    

}