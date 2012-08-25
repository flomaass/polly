package polly.rx.core;

import java.util.List;

import polly.rx.entities.TrainEntityV2;



public class TrainBillV2 {

    private List<TrainEntityV2> trains;
    
    
    
    public TrainBillV2(List<TrainEntityV2> trains) {
        this.trains = trains;
    }
    
    
    
    public List<TrainEntityV2> getTrains() {
        return this.trains;
    }
    
    
    
    private int weightedSumCache = -1;
    private int sumCache = -1;
    
    public synchronized int weightedSum() {
        if (this.weightedSumCache == -1) {
            this.weightedSumCache = 0;
            for (TrainEntityV2 train : this.trains) {
                this.weightedSumCache += train.getCosts() * train.getFactor();
            }
        }
        return this.weightedSumCache;
    }
    
    
    
    public synchronized int sum() {
        if (this.sumCache == -1) {
            this.sumCache = 0;
            for (TrainEntityV2 train : this.trains) {
                this.sumCache += train.getCosts();
            }
        }
        return this.sumCache;
    }
    
    
    
    @Override
    public String toString() {
        if (this.trains.isEmpty()) {
            return "Keine offene Rechnung.";
        } else {
            return "Rechnung f�r " + this.trains.size() + " Trainings: " + 
                this.weightedSum() + " Cr.";
        }
    }
    
    
    
    public void closeBill() {
        for (TrainEntityV2 train : this.trains) {
            train.setClosed(true);
        }
    }
}