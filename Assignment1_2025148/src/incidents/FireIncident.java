package incidents;
import exceptions.InvalidOperationException;
//Entirely based on 5.3 of pdf
public class FireIncident extends Incident{
    //Adding the extra variable for this class
    private double affectedArea;
    private boolean hazardousMaterial;
//Handling error and constructor
    public FireIncident(String id, String description, double distanceFromBase, int severity, double affectedArea, boolean hazardousMaterial) throws InvalidOperationException {
        super(id, description, distanceFromBase, severity);
        if (affectedArea<0){
            throw new InvalidOperationException("Affected area can't be negative");
        }
        this.affectedArea=affectedArea;
        this.hazardousMaterial=hazardousMaterial;
    }
    public String getRequiredCapability(){
        return "FIRE";
    }
    public double getPriorityWeight(){
        if (hazardousMaterial){
            return (7*getSeverity())+(15);
        }
        else{
            return (7*getSeverity());
        }
    }
    public double getWorkload(){
        return (300*getSeverity())+(0.2*affectedArea);
    }
    //Creating rest of the method to get the info other than the ones inherited
    public double getAffectedArea(){
        return affectedArea;
    }
    public boolean getHazardousMaterial(){
        return hazardousMaterial;
    }
    //Overrriding displayIncidentDetails()
    public void displayIncidentDetails(){
        super.displayIncidentDetails();
        System.out.println("Affected Area: " + affectedArea);
        System.out.println("Hazardous Material: " + hazardousMaterial);
    }
}
