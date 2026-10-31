package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
import capabilities.BatteryPowered;
//Section 6.4
public class SearchDrone extends ResponseUnit implements BatteryPowered{
    private double batteryLevel;//Required for BatteryPowered interface
    private double batteryCapacity;//Stated in the pdf
    private double travelConsumption;//UNit as Battery per km, given in the pdf
    public SearchDrone(String id, String name,double maxSpeed) throws InvalidOperationException{
        super(id,name,maxSpeed);
        this.batteryCapacity=100;
        this.travelConsumption=0.6;
        this.batteryLevel=batteryCapacity;//Taking the drone starts with a full battery
    }
    public String getCapability(){
        return "SEARCH";
    }
    //Override estimateArrivalTime()  as asked
    public double estimateArrivalTime(double distance){
        return (super.estimateArrivalTime(distance)*(0.85));
    }
    //BatteryPowered interface method
    public void recharge(double amount) throws InvalidOperationException{
        if(amount<=0){
            throw new InvalidOperationException("Battery to recharge can't be negative or zero");
        }
        if(amount>this.batteryCapacity-this.batteryLevel){
            throw new InvalidOperationException("Battery asked to recharge exceeds battery capacity.");
        }
        this.batteryLevel+=amount;
    }
    public double getBatteryLevel(){
        return this.batteryLevel;
    }
    public double getBatteryCapacity(){
        return this.batteryCapacity;
    }
    public boolean hasBatteryFor(double amount){
        return (amount<=this.batteryLevel);
    }
    public double consumeBattery(double amount) throws InsufficientResourceException{
        if(amount>this.batteryLevel){
            throw new InsufficientResourceException("Not enough battery in the unit");
        }
        this.batteryLevel-=amount;
        return amount;
    }
    //overriding move
    public void move(double distance) throws InsufficientResourceException, InvalidOperationException{
        if(!hasBatteryFor(distance*this.travelConsumption)){
            throw new InsufficientResourceException("Not enough battery to cover the distance");
        }
        this.consumeBattery(distance*this.travelConsumption);
        super.move(distance);
    }
    //Checking resources
    public boolean hasResourcesFor(Incident incident){
        double d= incident.getDistanceFromBase();
        double total= (2*d*travelConsumption)+incident.getWorkload();
        return this.hasBatteryFor(total);
    }
    //Dispatch score
    public double calculateDispatchScore(Incident incident, DispatchPolicy policy){
        double score=super.calculateDispatchScore(incident, policy);
        if(batteryLevel<policy.getLowBatteryThreshold()*batteryCapacity){
            score+=policy.getLowBatteryPenalty();
        }
        return score;
    }
    public void performWork(Incident incident) throws InvalidOperationException,InsufficientResourceException{
        consumeBattery(incident.getWorkload());
    }
    public void setBatteryLevel(double batteryLevel){this.batteryLevel=batteryLevel;}
}
