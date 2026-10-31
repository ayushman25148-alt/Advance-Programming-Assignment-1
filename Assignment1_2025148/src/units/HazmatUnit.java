package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
import capabilities.FuelPowered;
import capabilities.SupplyCarrier;
//Individual extension - Hazardous Material Response (roll ending in 8)
public class HazmatUnit extends GroundResponseUnit implements FuelPowered, SupplyCarrier {
    private double fuelLevel;
    private double fuelCapacity;
    private double fuelEfficiency;
    private int supplyLevel;
    private int supplyCapacity;
    public HazmatUnit(String id, String name, double maxSpeed, double trafficFactor) throws InvalidOperationException{
        super(id, name, maxSpeed, trafficFactor);
        this.fuelCapacity =120;
        this.fuelEfficiency= 8;
        this.supplyCapacity= 40;
        this.fuelLevel= fuelCapacity;
        this.supplyLevel= supplyCapacity;
    }
    public String getCapability(){
        return "HAZMAT";
    }
    //FuelPowered
    public void refuel(double amount) throws InvalidOperationException {
        if(amount<=0) {
            throw new InvalidOperationException("Refuel amount cannot be negative or zero");
        }
        if(amount>fuelCapacity-fuelLevel){
            throw new InvalidOperationException("Refuel amount exceeds fuel capacity");
        }
        fuelLevel+=amount;
    }
    public double getFuelLevel(){return fuelLevel;}
    public double getFuelCapacity(){return fuelCapacity;}
    public double consumeFuel(double distance) throws InsufficientResourceException{
        double needed=distance/fuelEfficiency;
        if(needed>fuelLevel){
            throw new InsufficientResourceException("Not enough fuel in the unit");
        }
        fuelLevel-=needed;
        return needed;
    }
    public boolean hasFuelFor(double distance){
        return (distance / fuelEfficiency) <= fuelLevel;
    }
    //SupplyCarrier (containment/neutralizing agent)
    public void loadSupplies(int number) throws InvalidOperationException{
        if(number<=0){
            throw new InvalidOperationException("Supply amount must be positive");
        }
        if(number>supplyCapacity-supplyLevel){
            throw new InvalidOperationException("Supply amount exceeds capacity");
        }
        supplyLevel+=number;
    }
    public void useSupplies(int number) throws InvalidOperationException, InsufficientResourceException {
        if(number<0){
            throw new InvalidOperationException("Negative supplies stated");
        }
        if(number>supplyLevel){
            throw new InsufficientResourceException("Not enough containment supplies");
        }
        supplyLevel-=number;
    }
    public int getSupplyLevel(){return supplyLevel;}
    public int getSupplyCapacity(){return supplyCapacity;}
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(!hasFuelFor(distance)){
            throw new InsufficientResourceException("Insufficient fuel");
        }
        consumeFuel(distance);
        super.move(distance);
    }
    public boolean hasResourcesFor(Incident incident){
        double totalDistance=incident.getDistanceFromBase()*2;
        if (!hasFuelFor(totalDistance)){
            return false;
        }
        return supplyLevel>=incident.getWorkload();
    }
    public double calculateDispatchScore(Incident incident, DispatchPolicy policy) {
        double score=super.calculateDispatchScore(incident, policy);
        if (fuelLevel<policy.getLowFuelThreshold()*fuelCapacity){
            score+=policy.getLowFuelPenalty();
        }
        if(supplyLevel<policy.getLowSupplyThreshold()*supplyCapacity){
            score+=policy.getLowSupplyPenalty();
        }
        return score;
    }
    public void performWork(Incident incident) throws InvalidOperationException,InsufficientResourceException{
        useSupplies((int)incident.getWorkload());
    }
    public void setFuelLevel(double fuelLevel){this.fuelLevel=fuelLevel;}
    public void setSupplyLevel(int supplyLevel){this.supplyLevel=supplyLevel;}
}