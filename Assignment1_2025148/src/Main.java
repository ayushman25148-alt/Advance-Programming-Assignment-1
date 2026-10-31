import java.util.Scanner;
import units.*;
import incidents.*;
import dispatch.DispatchManager;
import dispatch.DispatchPolicy;
import exceptions.*;
//Section 16 which is the CLI
public class Main{
    static Scanner sc=new Scanner(System.in);
    static DispatchManager manager= new DispatchManager();
        public static void main(String[] args){
        boolean running=true;
        while(running){
            printMenu();
            int choice=readInt("Enter your choice: ");
            try{
                switch(choice){
                    case 1:addResponseUnit();
                        break;
                    case 2:removeResponseUnit();
                        break;
                    case 3:reportNewIncident(); 
                        break;
                    case 4:removeIncident(); 
                        break;
                    case 5:findResponseUnit(); 
                        break;
                    case 6:findIncident(); 
                        break;
                    case 7:listAllUnits(); 
                        break;
                    case 8:listAllIncidents(); 
                        break;
                    case 9:previewDispatch(); 
                        break;
                    case 10:dispatchBest(); 
                        break;
                    case 11:resolveIncident(); 
                        break;
                    case 12:refuelAll(); 
                        break;
                    case 13:rechargeAll(); 
                        break;
                    case 14:refillWaterAll(); 
                        break;
                    case 15:restockSuppliesAll(); 
                        break;
                    case 16:generateReport(); 
                        break;
                    case 17:manager.displayUnserviceableIncidents(); 
                        break;
                    case 18:saveState(); 
                        break;
                    case 19:loadState(); 
                        break;
                    case 20:running=false;
                        System.out.println("Exiting the system. Goodbye!");
                        break;
                    default: System.out.println("Invalid choice given,please try again.");
                }
            }catch(Exception e){
                System.out.println("Error occurred: " + e.getMessage());
            }
        }
        sc.close();
    }
    static void printMenu(){
        System.out.println("================ EMERGENCY DISPATCH SYSTEM ================");
        System.out.println("");
        System.out.println("1. Add Response Unit");
        System.out.println("2. Remove Response Unit");
        System.out.println("");
        System.out.println("3. Report New Incident");
        System.out.println("4. Remove Incident");
        System.out.println("");
        System.out.println("5. Find Response Unit");
        System.out.println("6. Find Incident");
        System.out.println("");
        System.out.println("7. List All Response Units");
        System.out.println("8. List All Incidents");
        System.out.println("");
        System.out.println("9. Preview Dispatch Candidates");
        System.out.println("10. Dispatch Best Unit");
        System.out.println("11. Resolve Incident");
        System.out.println("");
        System.out.println("12. Refuel Fuel-Powered Units");
        System.out.println("13. Recharge Battery-Powered Units");
        System.out.println("14. Refill Water Carriers");
        System.out.println("15. Restock Supply Carriers");
        System.out.println("");
        System.out.println("16. Generate System Report");
        System.out.println("17. Display Unserviceable Incidents");
        System.out.println("");
        System.out.println("18. Save State");
        System.out.println("19. Load State");
        System.out.println("");
        System.out.println("20. Exit");
        System.out.println("===========================================================");
    }
    static int readInt(String prompt){
        System.out.print(prompt);
        while(!sc.hasNextInt()){
            System.out.println("Invalid input, enter a number.");
            sc.next();
            System.out.print(prompt);
        }
        int value=sc.nextInt();
        sc.nextLine();
        return value;
    }
    static double readDouble(String prompt){
        System.out.print(prompt);
        while(!sc.hasNextDouble()){
            System.out.println("Invalid input, enter a number.");
            sc.next();
            System.out.print(prompt);
        }
        double value=sc.nextDouble();
        sc.nextLine();
        return value;
    }
    static String readLine(String prompt){
        System.out.print(prompt);
        return sc.nextLine();
    }
    static boolean readBoolean(String prompt){
        System.out.print(prompt+" (true/false): ");
        while(!sc.hasNextBoolean()){
            System.out.println("Invalid input, enter true or false.");
            sc.next();
            System.out.print(prompt+" (true/false): ");
        }
        boolean value= sc.nextBoolean();
        sc.nextLine();
        return value;
    }
    //Option 1
    static void addResponseUnit() throws InvalidOperationException, DuplicateIdException {
        System.out.println("Select type: 1-Ambulance 2-FireEngine 3-RepairVan 4-SearchDrone 5-HazmatUnit");
        int type = readInt("Type: ");
        String id = readLine("ID: ");
        String name = readLine("Name: ");
        double maxSpeed = readDouble("Max Speed: ");
        ResponseUnit unit = null;
        if(type==1){
            double traffic = readDouble("Traffic Factor: ");
            unit = new Ambulance(id, name, maxSpeed, traffic);
        }else if(type==2){
            double traffic = readDouble("Traffic Factor: ");
            unit = new FireEngine(id, name, maxSpeed, traffic);
        }else if(type==3){
            double traffic = readDouble("Traffic Factor: ");
            unit = new RepairVan(id, name, maxSpeed, traffic);
        }else if(type==4){
            unit = new SearchDrone(id, name, maxSpeed);
        }else if(type== 5){
            double traffic = readDouble("Traffic Factor: ");
            unit = new HazmatUnit(id, name, maxSpeed, traffic);
        }else{
            System.out.println("Invalid type selected.");
            return;
        }
        manager.addUnit(unit);
        System.out.println("Unit added successfully.");
    }
    //Option 2
    static void removeResponseUnit() throws InvalidOperationException{
        String id=readLine("Enter Unit ID to remove: ");
        manager.removeUnit(id);
        System.out.println("Unit removed successfully.");
    }
    //Option 3
    static void reportNewIncident() throws InvalidOperationException, DuplicateIdException{
        System.out.println("Select type: 1-Medical 2-Fire 3-Infrastructure 4-Search 5-Hazmat");
        int type = readInt("Type: ");
        String id = readLine("ID: ");
        String description = readLine("Description: ");
        double distance = readDouble("Distance from base: ");
        int severity = readInt("Severity (1-5): ");
        Incident incident = null;
        if(type == 1){
            int patientCount =readInt("Patient Count: ");
            int criticalPatients= readInt("Critical Patients: ");
            incident = new MedicalIncident(id, description, distance, severity, patientCount, criticalPatients);
        }else if(type == 2){
            double affectedArea =readDouble("Affected Area: ");
            boolean hazardous =readBoolean("Hazardous Material");
            incident= new FireIncident(id, description, distance, severity, affectedArea, hazardous);
        }else if(type == 3){
            int affectedUsers =readInt("Affected Users: ");
            boolean criticalService= readBoolean("Critical Service");
            incident = new InfrastructureIncident(id, description, distance, severity, affectedUsers, criticalService);
        }else if(type == 4){
            int missingPersons =readInt("Missing Persons: ");
            double searchArea= readDouble("Search Area: ");
            incident = new SearchIncident(id, description, distance, severity, missingPersons, searchArea);
        }else if(type == 5){
            double contaminationArea = readDouble("Contamination Area: ");
            boolean airborneRisk = readBoolean("Airborne Risk");
            incident=new HazmatIncident(id, description, distance, severity, contaminationArea, airborneRisk);
        }else{
            System.out.println("Invalid type selected.");
            return;
        }
        manager.addIncident(incident);
        System.out.println("Incident reported successfully.");
    }
    //Option 4
    static void removeIncident() throws InvalidOperationException{
        String id=readLine("Enter Incident ID to remove: ");
        manager.removeIncident(id);
        System.out.println("Incident removed successfully.");
    }
    //Option 5
    static void findResponseUnit() throws InvalidOperationException{
        String id=readLine("Enter Unit ID: ");
        ResponseUnit u=manager.findUnit(id);
        u.displayUnitInfo();
    }
    //Option 6
    static void findIncident() throws InvalidOperationException{
        String id=readLine("Enter Incident ID: ");
        Incident inc=manager.findIncident(id);
        inc.displayIncidentDetails();
    }
    //Option 7
    static void listAllUnits(){
        manager.displayAllUnits();
    }
    //Option 8
    static void listAllIncidents(){
        manager.displayAllIncidents();
    }
    //Option 9
    static void previewDispatch() throws InvalidOperationException{
        String id=readLine("Enter Incident ID: ");
        manager.displayDispatchCandidates(id);
    }
    //Option 10
    static void dispatchBest() throws InvalidOperationException, NoSuitableUnitException{
        String id=readLine("Enter Incident ID: ");
        ResponseUnit u =manager.dispatchBestUnit(id);
        System.out.println("Dispatched unit: " + u.getId());
    }
    //Option 11
    static void resolveIncident() throws InvalidOperationException, InsufficientResourceException{
        String id= readLine("Enter Incident ID: ");
        manager.resolveIncident(id);
        System.out.println("Incident resolved successfully.");
    }
    //Option 12
    static void refuelAll() throws InvalidOperationException{
        double amount = readDouble("Enter refuel amount: ");
        manager.refuelAll(amount);
        System.out.println("Refuelled all fuel-powered units.");
    }
    //Option 13
    static void rechargeAll() throws InvalidOperationException, InsufficientResourceException{
        double amount = readDouble("Enter recharge amount: ");
        manager.rechargeAll(amount);
        System.out.println("Recharged all battery-powered units.");
    }
    //Option 14
    static void refillWaterAll() throws InvalidOperationException{
        double amount = readDouble("Enter water refill amount: ");
        manager.refillAllWater(amount);
        System.out.println("Refilled all water carriers.");
    }
    //Option 15
    static void restockSuppliesAll() throws InvalidOperationException{
        int amount = readInt("Enter supply restock amount: ");
        manager.restockAllSupplies(amount);
        System.out.println("Restocked all supply carriers.");
    }
    //Option 16
    static void generateReport(){
        String report = manager.generateReport();
        System.out.println(report);
    }
    //Option 18
    static void saveState() throws InvalidOperationException{
        String prefix = readLine("Enter file prefix (e.g. emergency): ");
        manager.saveState(prefix);
        System.out.println("State saved successfully with prefix: " + prefix);
    }
    //Option 19
    static void loadState() throws InvalidOperationException{
        String prefix = readLine("Enter file prefix to load (e.g. emergency): ");
        manager.loadState(prefix);
        System.out.println("State loaded successfully with prefix: " + prefix);
    }
}