package polly.core;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.skuzzle.polly.tools.concurrent.ThreadFactoryBuilder;

import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;

@Module
public class JanitorProvider extends AbstractProvider {

    private final static int RATE = 1000 * 60 * 10; // 10 minutes
    private final static Logger logger = Logger.getLogger(JanitorProvider.class.getName());
    
    public JanitorProvider(ModuleLoader loader) {
        super("JANITOR_PROVIDER", loader, false);
    }
    

    @Override
    public void setup() throws SetupException {
        Executors.newScheduledThreadPool(1, 
            new ThreadFactoryBuilder("JANITOR").setPriority(Thread.MIN_PRIORITY))
                .scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                Runtime r = Runtime.getRuntime();
                
                logger.trace("Janitoring");
                logger.trace("Total memory: " + r.totalMemory());
                logger.trace("Free memory before: " + r.freeMemory());
                System.gc();
                logger.trace("Free memory after: " + r.freeMemory());
            }
        }, RATE, RATE, TimeUnit.MILLISECONDS);
    }
}