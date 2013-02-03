package polly.core.commands;

import java.io.IOException;

import de.skuzzle.polly.sdk.Configuration;
import de.skuzzle.polly.sdk.ConfigurationProvider;
import polly.configuration.ConfigurationProviderImpl;
import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = {
               @Require(component = ConfigurationProviderImpl.class)},
    provides = @Provide(component = CommandManagerImpl.class))
public class CommandManagerProvider extends AbstractProvider {

    
    private final static String COMMAND_CFG_NAME = "commandManager.cfg";


    public CommandManagerProvider(ModuleLoader loader) {
        super("COMMAND_MANAGER_PROVIDER", loader, true);
    }



    @Override
    public void setup() throws SetupException {
        ConfigurationProvider configProvider = 
                this.requireNow(ConfigurationProviderImpl.class, true);
        Configuration commandCfg = null;
        Configuration pollyCfg = null;
        try {
            commandCfg = configProvider.open(COMMAND_CFG_NAME);
            pollyCfg = configProvider.getRootConfiguration();
        } catch (IOException e) {
            throw new SetupException(e);
        }
        
        CommandManagerImpl commandManager = 
                new CommandManagerImpl(
                pollyCfg.readString(Configuration.ENCODING),
                commandCfg);
        this.provideComponent(commandManager);
    }
}
