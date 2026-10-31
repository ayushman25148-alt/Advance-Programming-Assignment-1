package dispatch;
//With reference to section 7 
public class DispatchPolicy {
    //Making up the default attributes
    private double lowFuelThreshold;
    private double lowFuelPenalty;
    private double lowBatteryThreshold;
    private double lowBatteryPenalty;
    private double lowWaterThreshold;
    private double lowWaterPenalty;
    private double lowSupplyThreshold;
    private double lowSupplyPenalty;
    //Making out the constructor that sets the value defaulty 
    public DispatchPolicy(){
        this.lowFuelThreshold=0.25;
        this.lowFuelPenalty=12;
        this.lowBatteryThreshold=0.30;
        this.lowBatteryPenalty=15;
        this.lowWaterThreshold=0.40;
        this.lowWaterPenalty=20;
        this.lowSupplyThreshold=0.25;
        this.lowSupplyPenalty=10;
    }
    //Making the constructor that sets the value based on the given input by the tester as asked in pdf
    public DispatchPolicy(double lowFuelThreshold, double lowFuelPenalty, double lowBatteryThreshold, 
        double lowBatteryPenalty, double lowWaterThreshold, double lowWaterPenalty, 
        double lowSupplyThreshold, double lowSupplyPenalty){
        this.lowFuelThreshold=lowFuelThreshold;
        this.lowFuelPenalty=lowFuelPenalty;
        this.lowBatteryThreshold=lowBatteryThreshold;
        this.lowBatteryPenalty=lowBatteryPenalty;
        this.lowWaterThreshold=lowWaterThreshold;
        this.lowWaterPenalty=lowWaterPenalty;
        this.lowSupplyThreshold=lowSupplyThreshold;
        this.lowSupplyPenalty=lowSupplyPenalty;
    }
    public double getLowFuelThreshold(){
        return this.lowFuelThreshold;
    }
    public double getLowFuelPenalty(){
        return this.lowFuelPenalty;
    }
    public double getLowBatteryThreshold(){
        return this.lowBatteryThreshold;
    }
    public double getLowBatteryPenalty(){
        return this.lowBatteryPenalty;
    }
    public double getLowWaterThreshold(){
        return this.lowWaterThreshold;
    }
    public double getLowWaterPenalty(){
        return this.lowWaterPenalty;
    }
    public double getLowSupplyThreshold(){
        return this.lowSupplyThreshold;
    }
    public double getLowSupplyPenalty(){
        return this.lowSupplyPenalty;
    }
}
