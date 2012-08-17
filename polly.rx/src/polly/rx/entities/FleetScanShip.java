package polly.rx.entities;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "BY_REVORIX_ID",
        query= "SELECT ship FROM FleetScanShip ship WHERE ship.rxId = ?1"
    ),
    @NamedQuery(
        name = "ALL_SHIPS",
        query = "SELECT ship FROM FleetScanShip ship"
    )
})
public class FleetScanShip {
    
    public final static String BY_REVORIX_ID = "BY_REVORIX_ID";
    public final static String All_SHIPS = "ALL_SHIPS";
    
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    private int rxId;
    
    private String name;
    
    private int techlevel;
    
    private String owner;
    
    private String ownerClan;
    
    

    @OneToMany(cascade = CascadeType.ALL)
    private List<FleetScanHistoryEntry> history;
    
    
    
    public FleetScanShip() {
        this(0, "", 0, "", "");
    }
    
    
    
    public FleetScanShip(int rxId, String name, int techlevel, String owner, 
            String ownerClan) {
        super();
        this.rxId = rxId;
        this.name = name;
        this.techlevel = techlevel;
        this.owner = owner;
        this.ownerClan = ownerClan;
        this.history = new LinkedList<FleetScanHistoryEntry>();
    }


    
    public int getId() {
        return this.id;
    }


    
    public int getRxId() {
        return this.rxId;
    }


    
    public String getName() {
        return this.name;
    }


    
    public int getTechlevel() {
        return this.techlevel;
    }


    
    public String getOwner() {
        return this.owner;
    }
    
    
    
    public String getOwnerClan() {
        return this.ownerClan;
    }

    
    
    public List<FleetScanHistoryEntry> getHistory() {
        return this.history;
    }
    
    
    
    public void update(FleetScanShip other) {
        if (this.rxId != other.rxId) {
            throw new RuntimeException("cannot update ships with different rxId's");
        }
        FleetScanHistoryEntry historyEntry = new FleetScanHistoryEntry(new Date());
        if (!this.name.equals(other.name)) {
            historyEntry.getChanges().add(
                "Name changed from '" + this.name + "' to '" + other.name); 
            this.name = other.name;
        }
        
        if (!this.owner.equals(other.owner)) {
            historyEntry.getChanges().add("Spotted with differen owner: " + other.owner);
            this.owner = other.owner;
        }
        if (!this.ownerClan.equals(other.ownerClan)) {
            historyEntry.getChanges().add("Owner changed clan from '" + this.ownerClan + 
                "' to '" + other.ownerClan + "'");
            this.ownerClan = other.ownerClan;
        }
        this.history.add(historyEntry);
    }
}