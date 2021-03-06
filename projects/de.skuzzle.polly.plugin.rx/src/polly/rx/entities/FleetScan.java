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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.skuzzle.polly.sdk.time.Time;


@Entity
@NamedQueries({
    @NamedQuery(
        name = FleetScan.ALL_SCANS,
        query= "SELECT scan FROM FleetScan scan"
    ),
    @NamedQuery(
        name = FleetScan.CONTAINING_SHIP,
        query = "SELECT scan From FleetScan scan, IN(scan.ships) ship WHERE ship.rxId = ?1"
    ),
    @NamedQuery(
        name = FleetScan.CONTAINING_OWNER,
        query = "SELECT DISTINCT scan FROM FleetScan scan, IN(scan.ships) ship WHERE ship.owner = ?1"
    ),
    @NamedQuery(
        name = FleetScan.SCANS_BY_CLAN,
        query = "SELECT DISTINCT scan FROM FleetScan scan, IN(scan.ships) ship WHERE scan.ownerClan = ?1 OR ship.ownerClan = ?1"
    ),
    @NamedQuery(
        name = FleetScan.SCANS_BY_LOCATION,
        query = "SELECT DISTINCT scan FROM FleetScan scan WHERE scan.quadrant = ?1"
    )
    
})
public class FleetScan {

    public final static String ALL_SCANS = "ALL_SCANS"; //$NON-NLS-1$
    public final static String CONTAINING_SHIP = "CONTAINING_SHIP"; //$NON-NLS-1$
    public final static String CONTAINING_OWNER = "CONTAINING_OWNER"; //$NON-NLS-1$
    public final static String SCANS_BY_CLAN = "SCANS_BY_CLAN"; //$NON-NLS-1$
    public final static String SCANS_BY_LOCATION = "SCANS_BY_LOCATION"; //$NON-NLS-1$
    
    
    @Id@GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
    
    private int localSens;
    
    private String fleetName;
    
    private String ownerName;
    
    private String ownerClan;
    
    private String fleetTag;
    
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<FleetScanShip> ships;

    private String metaData;
    
    private int x;
    
    private int y;
    
    private String quadrant;
    
    
    
    public FleetScan() {
        this(0, "", "", "", "", new LinkedList<FleetScanShip>(), "", 0, 0, ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }
    
    
    
    public FleetScan(int localSens, String fleetName, String ownerName, String ownerClan,
        String fleetTag, List<FleetScanShip> ships, String quadrant, int x, int y,
        String metaData) {
        
        super();
        this.localSens = localSens;
        this.fleetName = fleetName;
        this.ownerClan = ownerClan;
        this.ownerName = ownerName;
        this.fleetTag = fleetTag;
        this.ships = ships;
        this.metaData = metaData;
        this.x = x;
        this.y = y;
        this.quadrant = quadrant;
        this.date = Time.currentTime();
    }


    
    public int getId() {
        return this.id;
    }
    
    
    
    public String getQuadrant() {
        return this.quadrant;
    }
    
    
    
    
    public String getMetaData() {
        return this.metaData;
    }
    
    
    
    
    public int getX() {
        return this.x;
    }
    
    
    
    public int getY() {
        return this.y;
    }
    
    
    
    
    public Date getDate() {
        return this.date;
    }


    
    public int getLocalSens() {
        return this.localSens;
    }


    
    public String getFleetName() {
        return this.fleetName;
    }


    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    
    
    public String getOwnerClan() {
        return this.ownerClan;
    }



    public String getFleetTag() {
        return this.fleetTag;
    }

    
    
    public int getShipCount() {
        return this.ships.size();
    }
    

    
    public List<FleetScanShip> getShips() {
        return this.ships;
    }
    

    
    public void setShips(List<FleetScanShip> ships) {
        this.ships = ships;
    }
}
