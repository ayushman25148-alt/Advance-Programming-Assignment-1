package incidents;
import exceptions.InvalidOperationException;
//Individual extension - Hazardous Material Response (roll ending in 8)
public class HazmatIncident extends Incident{
    private double contaminationArea;
    private boolean airborneRisk;
    public HazmatIncident(String id, String description, double distanceFromBase, int severity,
                           double contaminationArea, boolean airborneRisk) throws InvalidOperationException {
        super(id, description, distanceFromBase, severity);
        if(contaminationArea<0){
            throw new InvalidOperationException("Contamination area cannot be negative");
        }
        this.contaminationArea=contaminationArea;
        this.airborneRisk=airborneRisk;
    }
    public String getRequiredCapability(){
        return "HAZMAT";
    }
    //Priority: base severity weight, plus a heavy penalty if the hazard is airborne (spreads faster, higher risk)
    public double getPriorityWeight(){
        double weight=8*getSeverity();
        if(airborneRisk){
            weight+=20;
        }
        return weight;
    }
    //Workload: required containment/neutralizing supply units
    public double getWorkload(){
        return Math.ceil((contaminationArea/50.0)+(getSeverity()*2));
    }
    public double getContaminationArea(){
        return contaminationArea;
    }
    public boolean getAirborneRisk(){
        return airborneRisk;
    }
    public void displayIncidentDetails(){
        super.displayIncidentDetails();
        System.out.println("Contamination Area: "+contaminationArea);
        System.out.println("Airborne Risk: "+airborneRisk);
    }
}