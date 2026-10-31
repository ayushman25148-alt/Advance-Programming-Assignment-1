package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
import capabilities.FuelPowered;
import capabilities.WaterCarrier;
//With reference to section 6.2 of the given pdf
public class FireEngine extends GroundResponseUnit implements FuelPowered, WaterCarrier{
    private double fuelLevel;//Required for FuelPowered interface
    private double fuelCapacity;//Stated in the pdf
    private double fuelEfficiency;//Km per litre, stated in the pdf
    private double waterLevel;//Required for WaterCarrier interface
    private double waterCapacity;//Stated in the pdf
    public FireEngine(String id, String name,double maxSpeed,double trafficFactor) throws InvalidOperationException{
        super(id, name, maxSpeed, trafficFactor);
        this.fuelCapacity=150;
        this.fuelEfficiency= 5;
        this.waterCapacity=3000;
        this.fuelLevel=fuelCapacity;//Assuming the fire engine starts with a full tank
        this.waterLevel=waterCapacity;//Assuming the fire engine starts with a full water tank
    }
    public String getCapability(){
        return "FIRE";
    }
    //FuelPowered interface methods initialized
    public void refuel(double amount) throws InvalidOperationException{
        if(amount<=0){
            throw new InvalidOperationException("Amount of fuel to refuel cannot be negative or zero");
        }
        if(amount>this.fuelCapacity-this.fuelLevel){
            throw new InvalidOperationException("Amount of fuel to refuel exceeds fuel capacity");
        }
        this.fuelLevel+=amount;
    }
    public double consumeFuel(double distance) throws InsufficientResourceException{
        double fuelNeeded =(distance/this.fuelEfficiency);
        if(fuelNeeded>this.fuelLevel){
            throw new InsufficientResourceException("Not enough fuel to cover the distance");
        }
        this.fuelLevel-=fuelNeeded;
        return fuelNeeded;
    }
    public boolean hasFuelFor(double distance){
        return (distance/this.fuelEfficiency)<=this.fuelLevel;
    }
    public double getFuelLevel(){
        return this.fuelLevel;
    }
    public double getFuelCapacity(){
        return this.fuelCapacity;
    }
    //WaterCarrier interface methods initialized
    public void refillWater(double amount) throws InvalidOperationException{
        if(amount<=0){
            throw new InvalidOperationException("Amount of water to refill cannot be negative or zero");
        }
        if(amount>this.waterCapacity-this.waterLevel){
            throw new InvalidOperationException("Amount of water to refill exceeds water capacity");
        }
        this.waterLevel+=amount;
    }
    public double getWaterLevel(){
        return this.waterLevel;
    }
    public double getWaterCapacity(){
        return this.waterCapacity;
    }
    public void useWater(double amount) throws InvalidOperationException, InsufficientResourceException{
        if(amount<0){
            throw new InvalidOperationException("Amount of water to use cannot be negative");
        }
        if(amount>this.waterLevel){
            throw new InsufficientResourceException("Not enough water to use the requested amount");
        }
        this.waterLevel-=amount;
    }
    //Overriding move
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(!this.hasFuelFor(distance)){
            throw new InsufficientResourceException("Insufficient fuel");
        }
        this.consumeFuel(distance);
        super.move(distance);
    }
    //Checking resource
    public boolean hasResourcesFor(Incident incident){
        double totalDistance = incident.getDistanceFromBase()*2;
        if(!this.hasFuelFor(totalDistance)){
            return false;
        }
        if(this.waterLevel<incident.getWorkload()){
            return false;
        }
        return true;
    }
    //Dispatch score
        public double calculateDispatchScore(Incident incident, DispatchPolicy policy){
        double score = super.calculateDispatchScore(incident, policy);
        if(fuelLevel<policy.getLowFuelThreshold()*fuelCapacity){
            score+=policy.getLowFuelPenalty();
        }
        if(waterLevel<policy.getLowWaterThreshold()*waterCapacity){
            score+=policy.getLowWaterPenalty();
        }
        return score;
    }
    public void performWork(Incident incident) throws InvalidOperationException,InsufficientResourceException{
        useWater(incident.getWorkload());
    }
    public void setFuelLevel(double fuelLevel){this.fuelLevel=fuelLevel;}
    public void setWaterLevel(double waterLevel){this.waterLevel=waterLevel;}
}
