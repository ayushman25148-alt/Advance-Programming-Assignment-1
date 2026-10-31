package incidents;
import exceptions.InvalidOperationException;
public class MedicalIncident extends Incident{
//Adding additional attributes stated in 5.2
    private int patientCount;
    private int criticalPatients;
//Handling error and constructor
    public MedicalIncident(String id, String description, double distanceFromBase, int severity, int patientCount, int criticalPatients) throws InvalidOperationException {
        super(id, description, distanceFromBase, severity);
        if (patientCount<0){
            throw new InvalidOperationException("Patient count can't be negative.");
        }
        if (criticalPatients<0){
            throw new InvalidOperationException("Critical patients count should always be non-negative.");
        }
        if (criticalPatients>patientCount){
            throw new InvalidOperationException("Critical patients count can't be greater than total patient count.");
        }
        this.patientCount = patientCount;
        this.criticalPatients = criticalPatients;
    }
    public String getRequiredCapability(){
        return "MEDICAL";
    }
    //Overriding the methods to get the priority weight and workload
    public double getPriorityWeight(){
        return (6*getSeverity())+(3*criticalPatients);
    }
    public double getWorkload(){
        return patientCount;
    }
    //Creating rest of the method to get the info other than the ones inherited
    public int getPatientCount() {
        return patientCount;
    }
    public int getCriticalPatients() {
        return criticalPatients;
    }
    //Overrriding displayIncidentDetails()
    public void displayIncidentDetails() {
        super.displayIncidentDetails();
        System.out.println("Patient Count: " + patientCount);
        System.out.println("Critical Patients: " + criticalPatients);
    }
}
