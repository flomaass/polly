package polly.rx.core.orion.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.skuzzle.polly.tools.Check;
import polly.rx.core.orion.model.DefaultSector;
import polly.rx.core.orion.model.Fleet;
import polly.rx.core.orion.model.Portal;
import polly.rx.core.orion.model.Sector;

public class FromClientSector extends DefaultSector {

    private final String self;
    private final Fleet selectedFleet;
    private final boolean shareFleets;
    private final Collection<? extends Fleet> ownFleets;
    private final Collection<? extends Fleet> fleets;
    private final Collection<? extends Portal> clanPortals;
    private final Collection<? extends Portal> personalPortals;



    public FromClientSector(Sector s, String self, Fleet selectedFleet, boolean shareFleets,
            Collection<? extends Fleet> ownFleets,
            Collection<? extends Fleet> fleets, 
            Collection<? extends Portal> clanPortals,
            Collection<? extends Portal> personalPortals) {
        super(s);
        Check.objects(self, ownFleets, fleets, clanPortals, 
                personalPortals).notNull();
        this.self = self;
        this.selectedFleet = selectedFleet;
        this.shareFleets = shareFleets;
        this.ownFleets = Collections.unmodifiableCollection(new ArrayList<>(ownFleets));
        this.fleets = Collections.unmodifiableCollection(new ArrayList<>(fleets));
        this.clanPortals = Collections
                .unmodifiableCollection(new ArrayList<>(clanPortals));
        this.personalPortals = Collections.unmodifiableCollection(new ArrayList<>(
                personalPortals));
    }

    
    
    public String getSelf() {
        return this.self;
    }
    
    
    
    public boolean doShareFleets() {
        return this.shareFleets;
    }
    
    
    
    public Fleet getSelectedFleet() {
        return this.selectedFleet;
    }


    public Collection<? extends Fleet> getOwnFleets() {
        return this.ownFleets;
    }



    public Collection<? extends Fleet> getFleets() {
        return this.fleets;
    }



    public Collection<? extends Portal> getClanPortals() {
        return this.clanPortals;
    }



    public Collection<? extends Portal> getPersonalPortals() {
        return this.personalPortals;
    }
}
