package incidents;
import exceptions.InvalidOperationException;
//Entirely based on 5.4 of pdf
public class InfrastructureIncident extends Incident{
    //Adding the extra variable for this class
    private int affectedUsers;
    private boolean criticalService;
//Handling error and constructor
    public InfrastructureIncident(String id, String description, double distanceFromBase, int severity, int affectedUsers, boolean criticalService) throws InvalidOperationException {
        super(id, description, distanceFromBase, severity);
        if (affectedUsers<0){
            throw new InvalidOperationException("Affected number of people can't be negative");
        }
        this.affectedUsers=affectedUsers;
        this.criticalService=criticalService;
    }
    //Overriding the methods from parent class
    public String getRequiredCapability(){
        return "REPAIR";
    }
    public double getPriorityWeight(){
        double returnValue= 5*getSeverity();
        if (criticalService){
            returnValue+=10;
        }
        double extraComponent=affectedUsers/100.0;
        if(extraComponent>10){
            returnValue+=10;
        }else{
            returnValue+=extraComponent;
        }
        return returnValue;
    }
    public double getWorkload(){
        return Math.ceil(getSeverity()+(affectedUsers/500.0));
    }
    //Creating rest of the method to get the info other than the ones inherited
    public int getAffectedUsers(){
        return affectedUsers;
    }
    public boolean getCriticalService(){
        return criticalService;
    }
    public void displayIncidentDetails(){
        super.displayIncidentDetails();
        System.out.println("Affected Users: " + affectedUsers);
        System.out.println("Critical Service: " + criticalService);
    }
    
}
