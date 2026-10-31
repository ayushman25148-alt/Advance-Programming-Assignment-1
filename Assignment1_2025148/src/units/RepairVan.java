package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
import capabilities.FuelPowered;
import capabilities.SupplyCarrier;
//Section 6.3 of the given pdf
public class RepairVan extends GroundResponseUnit implements FuelPowered, SupplyCarrier {
    private double fuelLevel;//Required for FuelPowered interface
    private double fuelCapacity;//Stated in the pdf
    private double fuelEfficiency;//Km per litre, stated in the pdf
    private int supplyLevel;//Required for SupplyCarrier interface
    private int supplyCapacity;//Stated in the pdf
    public RepairVan(String id, String name,double maxSpeed,double trafficFactor) throws InvalidOperationException{
        super(id, name, maxSpeed, trafficFactor);
        this.fuelCapacity=100;
        this.fuelEfficiency=10;
        this.supplyCapacity= 30;
        this.fuelLevel =fuelCapacity;//Assuming the repair van starts with a full tank
        this.supplyLevel=supplyCapacity;//Assuming the repair van starts with a full supply
    }
    public String getCapability(){
        return "REPAIR";
    }
    //FuelPowered interface methods
    public double getFuelLevel(){
        return fuelLevel;
    }
    public double getFuelCapacity(){
        return fuelCapacity;
    }
    public void refuel(double amount) throws InvalidOperationException{
        if(amount<=0){
            throw new InvalidOperationException("Refuel amount needs to be positive.");
        }
        if(fuelLevel+ amount>fuelCapacity){
            throw new InvalidOperationException("Cannot refuel more than the fuel capacity.");
        }
        fuelLevel+=amount;
    }
    public boolean hasFuelFor(double distance){
        return (distance/this.fuelEfficiency)<=this.fuelLevel;
    }
    public double consumeFuel(double distance) throws InsufficientResourceException{
        double fuelRequired=distance/fuelEfficiency;
        if(fuelRequired>fuelLevel){
            throw new InsufficientResourceException("Not enough fuel to cover the distance.");
        }
        fuelLevel-=fuelRequired;
        return fuelRequired;
    }
    //SupplyCarrier interface methods
    public void loadSupplies(int number) throws InvalidOperationException{
        if(number<=0){
            throw new InvalidOperationException("Supplies amount needs to be positive.");
        }
        if(supplyLevel+number>supplyCapacity){
            throw new InvalidOperationException("Can't load more than the supply capacity.");
        }
        supplyLevel+=number;
    }
    public void useSupplies(int number) throws InvalidOperationException, InsufficientResourceException{
        if(number<=0){
            throw new InvalidOperationException("Supplies amount needs to be positive.");
        }
        if(number >supplyLevel){
            throw new InsufficientResourceException("Not enough supplies to use.");
        }
        supplyLevel-=number;
    }
    public int getSupplyLevel(){
        return supplyLevel;
    }
    public int getSupplyCapacity(){
        return supplyCapacity;
    }
    //Move override
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(distance<=0){
            throw new InvalidOperationException("Distance must be positive.");
        }
        if(!hasFuelFor(distance)){
            throw new InsufficientResourceException("Not enough fuel to move the specified distance.");
        }
        consumeFuel(distance);
        super.move(distance);
    }//Seeing that we can moke round trip or not and we have enoungh resource
    public boolean hasResourcesForRoundTrip(Incident incident){
        return (hasFuelFor(2*incident.getDistanceFromBase()) && (supplyLevel>=incident.getWorkload()));
    }
    //Dispatch policy override
    public double calculateDispatchScore(Incident incident, DispatchPolicy policy){
        double score=super.calculateDispatchScore(incident, policy);
        if(fuelLevel<policy.getLowFuelThreshold()*fuelCapacity){
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
