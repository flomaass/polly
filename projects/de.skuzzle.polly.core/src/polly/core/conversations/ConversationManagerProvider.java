package polly.core.conversations;

import polly.core.ShutdownManagerImpl;
import polly.moduleloader.AbstractProvider;
import polly.moduleloader.ModuleLoader;
import polly.moduleloader.SetupException;
import polly.moduleloader.annotations.Module;
import polly.moduleloader.annotations.Require;
import polly.moduleloader.annotations.Provide;;

@Module(
    requires = @Require(component = ShutdownManagerImpl.class),
    provides = @Provide(component = ConversationManagerImpl.class))
public class ConversationManagerProvider extends AbstractProvider {

    public ConversationManagerProvider(ModuleLoader loader) {
        super("CONVERSATION_MANAGER_PROVIDER", loader, true);
    }
    
    

    @Override
    public void setup() throws SetupException {
        ConversationManagerImpl conversationManager = new ConversationManagerImpl();
        this.provideComponent(conversationManager);
        ShutdownManagerImpl shutdownManager = this.requireNow(
                ShutdownManagerImpl.class, true);
        shutdownManager.addDisposable(conversationManager);
    }

}