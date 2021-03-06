package polly.rx.core.orion.pathplanning;

import java.util.List;

import polly.rx.core.orion.model.Sector;
import de.skuzzle.polly.sdk.Types.TimespanType;

public class RouteOptions {

    final TimespanType totalJumpTime;
    final TimespanType currentJumpTime;
    final int maxWaitSpotDistance;
    final List<Sector> personalPortals;
    final boolean blockTail;
    final boolean blockEntryPortal;
    final boolean renderDark;


    public RouteOptions(TimespanType totalJumpTime, TimespanType currentJumpTime,
            List<Sector> personalPortals, boolean blockTail, boolean blockEntryPortals, 
            boolean renderDark) {
        this.totalJumpTime = totalJumpTime;
        this.currentJumpTime = currentJumpTime;
        this.maxWaitSpotDistance = 3;
        this.blockTail = blockTail;
        this.blockEntryPortal = blockEntryPortals;
        this.personalPortals = personalPortals;
        this.renderDark = renderDark;
    }



    public TimespanType getCurrentJumpTime() {
        return this.currentJumpTime;
    }



    public TimespanType getTotalJumpTime() {
        return this.totalJumpTime;
    }



    public boolean doBlockEntryPortal() {
        return this.blockEntryPortal;
    }
    

    
    public boolean doBlockTail() {
        return this.blockTail;
    }
    
    
    
    public boolean doRenderDark() {
        return this.renderDark;
    }
}