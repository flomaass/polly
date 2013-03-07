package polly.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;


import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import de.skuzzle.polly.sdk.ConfigurationValidator;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationEvent;
import de.skuzzle.polly.sdk.eventlistener.ConfigurationListener;
import de.skuzzle.polly.tools.events.Dispatchable;
import de.skuzzle.polly.tools.events.EventProvider;
import de.skuzzle.polly.tools.events.Listeners;
import de.skuzzle.polly.tools.events.SynchronousEventProvider;


public class ConfigurationProviderImpl implements ConfigurationProvider {

    public final static ConfigurationValidator NOP_VALIDATOR = 
            new ConfigurationValidator() {
                @Override
                public void validate(Configuration config) {
                    // do nothing!
                }
            }; 
            

            
    private final static Logger logger = Logger.getLogger(
            ConfigurationProviderImpl.class.getName());
            
            
    private File configDir;
    private File pluginConfigDir;
    
    private Map<File, ConfigurationImpl> cfgCache;
    private Configuration rootCfg;
    private EventProvider eventProvider;
    
    
    
    public ConfigurationProviderImpl(File configDir) {
        this.configDir = configDir;
        this.pluginConfigDir = new File(configDir, "pluginconfigs");
        this.cfgCache = new WeakHashMap<File, ConfigurationImpl>();
        this.eventProvider = new SynchronousEventProvider();
    }
    
    
    
    public Configuration open(String cfgName, boolean isRoot, 
            ConfigurationValidator validator) 
                    throws FileNotFoundException, IOException, ConfigurationException {
        
        logger.trace("Trying to locate '" + cfgName + "'");
        File cfgFile = this.searchFor(cfgName);
        ConfigurationImpl cached = this.cfgCache.get(cfgFile.getCanonicalFile());
        if (cached == null) {
            cached = new ConfigurationImpl(cfgFile, this);
            validator.validate(cached);
            cached.setValidator(validator);
            this.cfgCache.put(cfgFile.getCanonicalFile(), cached);
            logger.info("Loaded configuration into memory\n" + cached.toString());
        } else {
            logger.trace("Configuration restored from cache");
        }
        
        if (isRoot) {
            logger.info("Setting new root configuration");
            this.rootCfg = cached;
        }
        return cached;
    }
    
    
    
    @Override
    public Configuration open(String cfgName, ConfigurationValidator validator)
            throws FileNotFoundException, IOException, ConfigurationException {
        return this.open(cfgName, false, validator);
    }
    
    
    
    @Override
    public Configuration open(String cfgName) throws FileNotFoundException, IOException {
        try {
            return this.open(cfgName, NOP_VALIDATOR);
        } catch (ConfigurationException e) {
            // HACK: this can not happen!
            throw new RuntimeException(e);
        }
    }
    
    
    
    @Override
    public Configuration emptyConfiguration() {
        return new ConfigurationImpl(this);
    }
    
    
    
    @Override
    public Configuration getRootConfiguration() {
        return this.rootCfg;
    }

    
    
    private File searchFor(String cfgName) throws FileNotFoundException {
        File cfgFile = new File(this.configDir, cfgName);
        if (!cfgFile.exists()) {
            cfgFile = new File(this.pluginConfigDir, cfgName);
            if (!cfgFile.exists()) {
                throw new FileNotFoundException(cfgName);
            }
        }
        return cfgFile;
    }
    
    
    
    @Override
    public void addConfigurationListener(ConfigurationListener listener) {
        this.eventProvider.addListener(ConfigurationListener.class, listener);
    }
    
    
    
    @Override
    public void removeConfigurationListener(ConfigurationListener listener) {
        this.eventProvider.removeListener(ConfigurationListener.class, listener);
    }
    
    
    
    public void fireConfigurationChanged(ConfigurationEvent e) {
        final Listeners<ConfigurationListener> listeners = 
            this.eventProvider.getListeners(ConfigurationListener.class);
        
        Dispatchable<ConfigurationListener, ConfigurationEvent> d = 
            new Dispatchable<ConfigurationListener, ConfigurationEvent>(listeners, e) {
    
                @Override
                public void dispatch(ConfigurationListener listener,
                        ConfigurationEvent event) {
                    listener.configurationChange(event);
                }
        };
        this.eventProvider.dispatchEvent(d);
    }
}