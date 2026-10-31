package units;
import incidents.Incident;
import dispatch.DispatchPolicy;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
//Made with reference to the section 3.1 of the given pdf
public class ResponseUnit{
    //Adding given attributes 
    private String id;
    private String name;
    private double maxSpeed;
    private double totalDistanceTravelled;
    private boolean available;
    private String assignedIncidentId;
    private int completeIncidents;
    //Making a constructor with atleast all the minimum required attributes that needs to be assigned
    public ResponseUnit(String id, String name, double maxSpeed) throws InvalidOperationException{
        if(id==null||id.isEmpty()){
            throw new InvalidOperationException("Invalid id received");
        }
        if(name==null||name.isEmpty()){
            throw new InvalidOperationException("Invalid name received");
        }
        if(maxSpeed<=0){
            throw new InvalidOperationException("Max speed needs to be greater than 0");
        }
        this.id=id;
        this.name=name;
        this.maxSpeed=maxSpeed;
        this.totalDistanceTravelled=0;
        this.available=true;
        this.assignedIncidentId=null;
        this.completeIncidents=0;
    }
    //Creating the methods asked
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(distance<0){
            throw new InvalidOperationException("Distance cannot be negative");
        }
        this.totalDistanceTravelled+=distance;
        System.out.println("Unit "+this.id+" moved "+distance+" units. Total distance travelled by the given unit: "+this.totalDistanceTravelled);
    }
    public double estimateArrivalTime(double distance){
        return distance/this.maxSpeed;//Returns time in hr
    }
    public String getCapability(){
        return "GENERAL";
    }
    public boolean canHandle(Incident incident){
        if(getCapability().equals(incident.getRequiredCapability())||incident.getRequiredCapability().equals("GENERAL")){
            return true;
        }
        return false;
    }
    public boolean hasResourcesFor(Incident incident){
        return true;
    }
    public double calculateDispatchScore(Incident incident, DispatchPolicy policy){
        //Base class is not using the policy paramter but the subclass eg ambulance will be using it,therefore i had make it present in the parent class as well
        return (estimateArrivalTime(incident.getDistanceFromBase())*60)-incident.getPriorityWeight();
    }
    //Assignment related methods given in the pdf
    public void assignIncident(String incidentId) throws InvalidOperationException{
        if(!this.available){
            throw new InvalidOperationException("Unit is not available for assignment as it is already assigned to incident");
        }
        this.assignedIncidentId=incidentId;
        this.available=false;
        //If we assign incident id to the unit then the unit is not available for any other incident and is working on this incident only
    }
    public void releaseIncident() throws InvalidOperationException{
        if(this.available){
            throw new InvalidOperationException("Unit is already available and not assigned to any incident");
        }
        this.assignedIncidentId=null;
        this.available=true;
    }
    public void incrementCompleteIncidents(){
        this.completeIncidents++;
    }
    //Getters for the attributes
    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public double getMaxSpeed(){
        return this.maxSpeed;
    }
    public double getTotalDistanceTravelled(){
        return this.totalDistanceTravelled;
    }
    public boolean isAvailable(){
        return this.available;
    }
    public String getAssignedIncidentId(){
        return this.assignedIncidentId;
    }
    public int getCompleteIncidents(){
        return this.completeIncidents;
    }
    public void displayUnitInfo() {
        System.out.println("Unit ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Max Speed: " + maxSpeed);
        System.out.println("Total Distance Travelled: " + totalDistanceTravelled);
        System.out.println("Available: " + available);
        System.out.println("Assigned Incident: " + assignedIncidentId);
        System.out.println("Completed Incidents: " + completeIncidents);
    }
    public void performWork(Incident incident) throws InvalidOperationException, InsufficientResourceException {
    //Base implementation does nothing; subclasses override with their own resource consumption
    }
    //Setters for the attributes for easier management of the attributes
    public void setTotalDistanceTravelled(double totalDistanceTravelled){
        this.totalDistanceTravelled=totalDistanceTravelled;
    }
    public void setAvailable(boolean available) {
        this.available=available;
    }
    public void setAssignedIncidentId(String assignedIncidentId) {
        this.assignedIncidentId=assignedIncidentId;
    }
    public void setCompletedIncidents(int completedIncidents) {
        this.completeIncidents=completedIncidents;
    }
}
