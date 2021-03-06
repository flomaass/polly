package de.skuzzle.polly.core.internal;

import de.skuzzle.polly.core.internal.users.UserManagerImpl;
import de.skuzzle.polly.core.moduleloader.AbstractProvider;
import de.skuzzle.polly.core.moduleloader.ModuleLoader;
import de.skuzzle.polly.core.moduleloader.SetupException;
import de.skuzzle.polly.core.moduleloader.annotations.Module;
import de.skuzzle.polly.core.moduleloader.annotations.Require;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.constraints.Constraints;

@Module(
    requires = {
        @Require(state = ModuleStates.PERSISTENCE_READY),
        @Require(state = ModuleStates.USERS_READY)
    }
)
public class DefaultUserAttributesProvider extends AbstractProvider {
    
    public final static String AUTO_LOGON = "AUTO_LOGON";  //$NON-NLS-1$
    public final static String AUTO_LOGON_DESCRIPTION = MSG.autoLogonDescription;
    
    public DefaultUserAttributesProvider(ModuleLoader loader) {
        super("DEFAULT_ATTRIBUTES_PROVIDER", loader, false); //$NON-NLS-1$
    }

    
    
    @Override
    public void run() throws Exception {
        UserManagerImpl userManager = this.requireNow(UserManagerImpl.class, false);
        
        try {
            userManager.addAttribute(AUTO_LOGON, new Types.BooleanType(true), 
                AUTO_LOGON_DESCRIPTION, "Core", Constraints.BOOLEAN); //$NON-NLS-1$
        } catch (Exception e) {
            throw new SetupException(e);
        }
    }



    @Override
    public void setup() throws SetupException {}
}
