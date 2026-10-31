package incidents;
import exceptions.InvalidOperationException;
public class SearchIncident extends Incident{
    //Adding few new attributes for this class
    private int missingPersons;
    private double searchArea;
//Handling a bit of exceptions and creating the constructor
    public SearchIncident(String id, String description, double distanceFromBase, int severity, int missingPersons, double searchArea) throws InvalidOperationException {
        super(id, description, distanceFromBase, severity);
        if (missingPersons<0){
            throw new InvalidOperationException("Missing number of people can't be negative");
        }
        if (searchArea<0){
            throw new InvalidOperationException("Search area can't be negative");
        }
        this.missingPersons=missingPersons;
        this.searchArea=searchArea;
    }
//Overriding few methods
    public String getRequiredCapability(){
        return "SEARCH";
    }
    public double getPriorityWeight(){
        return (4*getSeverity())+(2*missingPersons);
    }
    public double getWorkload(){
        return 1.5*searchArea;
    }
//Creating rest of the method to get the info other than the ones inherited
    public int getMissingPersons(){
        return missingPersons;
    }
    public double getSearchArea(){
        return searchArea;
    }
    //Overrriding displayIncidentDetails()
    public void displayIncidentDetails(){
        super.displayIncidentDetails();
        System.out.println("Missing Persons: " + missingPersons);
        System.out.println("Missing Search Area: " + searchArea);
    }
}
