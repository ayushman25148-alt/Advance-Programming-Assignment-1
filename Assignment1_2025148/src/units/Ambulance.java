package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
import capabilities.FuelPowered;
import capabilities.PatientCarrier;
//With reference to section 6.1 of the given pdf
public class Ambulance extends GroundResponseUnit implements FuelPowered,PatientCarrier {
    private double fuelLevel;//Required for FuelPowered interface 
    private double fuelCapacity;//Stated in the pdf
    private double fuelEfficiency;//Km per litre and stated in the pdf
    private int patientCapacity;//Stated in the pdf
    private int currentPatients;//Required for PatientCarrier interface
    public Ambulance(String id,String name,double maxSpeed,double trafficFactor) throws InvalidOperationException{
        super(id,name,maxSpeed,trafficFactor);
        this.fuelCapacity=80;
        this.fuelEfficiency=12;
        this.patientCapacity=4;
        this.fuelLevel=fuelCapacity;//Assuming the ambulance starts with a full tank
        this.currentPatients=0;//Assuming the ambulance starts with no patients
    }
    public String getCapability(){
        return "MEDICAL";
    }
    public void refuel(double amount) throws InvalidOperationException{
        if(amount<=0){
            throw new InvalidOperationException("Amount of fuel to refuel cannot be negative");
        }
        if(amount>this.fuelCapacity-this.fuelLevel){
            throw new InvalidOperationException("Amount of fuel to refuel exceeds fuel capacity");
        }
        this.fuelLevel+=amount;
    }
    //Methods to get the attributes of the ambulance
    public double getFuelLevel(){
        return this.fuelLevel;
    }
    public double getFuelCapacity(){
        return this.fuelCapacity;
    }
    public double consumeFuel(double distance) throws InsufficientResourceException{
        double fuelNeeded=(distance/this.fuelEfficiency);
        if(fuelNeeded>this.fuelLevel){
            throw new InsufficientResourceException("Not enough fuel to cover the distance");
        }
        this.fuelLevel-=fuelNeeded;
        return fuelNeeded;
    }
    public boolean hasFuelFor(double distance){
        return (distance/this.fuelEfficiency)<=this.fuelLevel;
    }
    //PatientCarrier interface methods
    public void boardPatients(int number) throws InvalidOperationException{
        if(number<=0){
            throw new InvalidOperationException("Number of patients to board cannot be negative or zero");
        }
        if(this.currentPatients+number>this.patientCapacity){
            throw new InvalidOperationException("Number of patients to board exceeds patient capacity of the unit "+this.getId());
        }
        this.currentPatients+=number;
    }
    public void releasePatients(int number) throws InvalidOperationException{
        if(number<=0){
            throw new InvalidOperationException("Number of patients to release can't be negative or zero");
        }
        if(this.currentPatients-number<0){
            throw new InvalidOperationException("Number of patients to release exceeds current number of patients");
        }
        this.currentPatients-=number;
    }
    public int getPatientCapacity(){
        return this.patientCapacity;
    }
    public int getCurrentPatients(){
        return this.currentPatients;
    }
    //Overriding move
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(!this.hasFuelFor(distance)){
            throw new InsufficientResourceException("Not enough fuel to move the distance");
        }
        this.consumeFuel(distance);
        super.move(distance);
    }
    //Now we are checking for sufficient resources are present or not
    public boolean hasResourcesFor(Incident incident){
        int requiredPatients=(int)incident.getWorkload();
        if((patientCapacity-currentPatients)<requiredPatients){
            return false;
        }
        double totalDistance=incident.getDistanceFromBase()*2;
        return this.hasFuelFor(totalDistance);
    }
    //Dispatch score
    public double calculateDispatchScore(Incident incident, DispatchPolicy policy){
        double b = super.calculateDispatchScore(incident, policy);
        double score = b+(3*currentPatients);
        if(fuelLevel<policy.getLowFuelThreshold()*fuelCapacity){
            score+=policy.getLowFuelPenalty();
        }
        return score;
    }
    public void performWork(Incident incident) throws InvalidOperationException,InsufficientResourceException{
        int patientCount =(int)incident.getWorkload();
        boardPatients(patientCount);
        releasePatients(patientCount);
    }
    public void setFuelLevel(double fuelLevel){this.fuelLevel = fuelLevel;}
    public void setCurrentPatients(int currentPatients){this.currentPatients = currentPatients;}
}
