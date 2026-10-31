package incidents;
import exceptions.InvalidOperationException;
//Entirely based on 5.1 of pdf
public class Incident {
//Making the attributes as it is stated in the pdf file
    private String id;
    private String description;
    private double distanceFromBase;
    private int severity;
    private String status;
    private String assignedUnitId;
//Making 3 different status
    public static final String S_Open ="OPEN";
    public static final String S_Assigned ="ASSIGNED";
    public static final String S_Resolved="RESOLVED";
//Handling error and constructor
    public Incident(String id, String description, double distanceFromBase, int severity) throws InvalidOperationException {
        if(id==null||id.isEmpty()){
            throw new InvalidOperationException("Incident ID is null or empty");
        }
        if(description==null||description.isEmpty()){
            throw new InvalidOperationException("Incident description is null or empty.");
        }
        if(distanceFromBase<0){
            throw new InvalidOperationException("Distance can't be negative.");
        }
        if(severity<1||severity>5){
            throw new InvalidOperationException("Severity scales between 1 and 5.");
        }
        this.id=id;
        this.description=description;
        this.distanceFromBase=distanceFromBase;
        this.severity=severity;
        this.status=S_Open; // Setting default as Open
        this.assignedUnitId=null; // No unit assigned initially
    }
//Making the method stated and given in the pdf
    public String getRequiredCapability(){
        return "GENERAL";
    }
    public double getPriorityWeight(){
        return severity*5;
    }
    public double getWorkload(){
        return severity;
    }
//Making the rest of the asked methods
    public String getId(){
        return id;
    }
    public String getDescription(){
        return description;
    }
    public double getDistanceFromBase(){
        return distanceFromBase;
    }
    public int getSeverity(){
        return severity;
    }
    public String getStatus() {
        return status;
    }
    public String getAssignedUnitId(){
        return assignedUnitId;
    }

    public void assignUnit(String unitID) throws InvalidOperationException{
        if(this.status.equals(S_Resolved)){
            throw new InvalidOperationException("Already resovled the incident "+id);
        }
        if(this.status.equals(S_Assigned)){
            throw new InvalidOperationException("Incident is assigned to unit "+assignedUnitId);
        }
        this.assignedUnitId = unitID;
        this.status = S_Assigned;
    }

    public void resolveIncident() throws InvalidOperationException{
        if(this.status.equals(S_Resolved)){
            return;
        }else if(this.status.equals(S_Open)){
            throw new InvalidOperationException("Incident is not assigned to any unit.");
        }else if(this.status.equals(S_Assigned)){
            this.status = S_Resolved;
        }
    }

    public void displayIncidentDetails(){
        System.out.println("Incident ID: " + id);
        System.out.println("Description: " + description);
        System.out.println("Distance from Base: " + distanceFromBase);
        System.out.println("Severity: " + severity);
        System.out.println("Status: " + status);
        if (assignedUnitId != null){
            System.out.println("Assigned Unit ID: " + assignedUnitId);
        }else{
            System.out.println("No unit assigned.");
        }
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setAssignedUnitId(String assignedUnitId){
        this.assignedUnitId= assignedUnitId;
    }
}
