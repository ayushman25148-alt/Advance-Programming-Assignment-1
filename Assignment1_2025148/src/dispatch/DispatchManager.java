package dispatch;
import units.ResponseUnit;
import incidents.Incident;
import exceptions.InvalidOperationException;
import exceptions.NoSuitableUnitException;
import exceptions.DuplicateIdException;
import exceptions.InsufficientResourceException;
import capabilities.FuelPowered;
import capabilities.BatteryPowered;
import capabilities.WaterCarrier;
import capabilities.SupplyCarrier;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import units.Ambulance;
import units.FireEngine;
import units.RepairVan;
import units.SearchDrone;
import incidents.MedicalIncident;
import incidents.FireIncident;
import incidents.InfrastructureIncident;
import incidents.SearchIncident;
import units.HazmatUnit;
import incidents.HazmatIncident;
//With reference to section 8
public class DispatchManager {
    private ResponseUnit[] units;
    private Incident[] incidents;
    private int unitCount;
    private int incidentCount;
    private DispatchPolicy policy;
    //Making constructor with default value
    public DispatchManager() {
        this.units= new ResponseUnit[50];
        this.incidents=new Incident[100];
        this.unitCount=0;
        this.incidentCount=0;
        this.policy= new DispatchPolicy();
    }
    //Making constructor with given value
    public DispatchManager(int maxUnits,int maxIncident){
        if(maxUnits<=0){
            this.units= new ResponseUnit[50];
        }else{
            this.units= new ResponseUnit[maxUnits];
        }
        if(maxIncident<=0){
            this.incidents=new Incident[100];
        }else{
            this.incidents=new Incident[maxIncident];
        }
        this.unitCount=0;
        this.incidentCount=0;
        this.policy=new DispatchPolicy();
    }
    //Adding unit (8.1)
    public void addUnit(ResponseUnit unit) throws InvalidOperationException, DuplicateIdException {
        if(unit==null){
            throw new InvalidOperationException("Unit cannot be null.");
        }
        if(unitCount>=units.length){
            throw new InvalidOperationException("Cannot add more units");
        }
        for(int i=0;i<unitCount;i++){
            if(units[i].getId().equals(unit.getId())){
                throw new DuplicateIdException("Unit with the same ID as "+ unit.getId() +" already exists");
            }
        }
        units[unitCount]=unit;
        unitCount++;
    }
    //Adding incident (8.2)
    public void addIncident(Incident incident) throws InvalidOperationException, DuplicateIdException {
        if(incident==null){
            throw new InvalidOperationException("Incident cannot be null.");
        }
        if(incidentCount>=incidents.length){
            throw new InvalidOperationException("Cannot add more incidents");
        }
        for(int i=0;i<incidentCount;i++){
            if(incidents[i].getId().equals(incident.getId())){
                throw new DuplicateIdException("Incident with the same ID as "+ incident.getId() +" already exists");
            }
        }
        incidents[incidentCount]=incident;
        incidentCount++;
    }
    //Searching (8.3)
    public ResponseUnit findUnit(String id) throws InvalidOperationException{
        for (int i= 0;i<unitCount; i++){
            if(units[i].getId().equals(id)){
                return units[i];
            }
        }
        throw new InvalidOperationException("No unit found of ID: " + id);
    }
    public Incident findIncident(String id) throws InvalidOperationException{
        for (int i= 0;i<incidentCount; i++){
            if(incidents[i].getId().equals(id)){
                return incidents[i];
            }
        }
        throw new InvalidOperationException("No incident found of ID: " + id);
    }
    //Removing response unit (8.4)
    public void removeUnit(String id) throws InvalidOperationException{
        boolean found=false;
        int index=0;
        for(int i=0;i<unitCount;i++){
            if(units[i].getId().equals(id)){
                found=true;
                index=i;
                break;
            }
        }
        if(!found){
            throw new InvalidOperationException("No unit found with unit ID: " + id);
        }
        if(units[index].isAvailable()==false) {
            throw new InvalidOperationException("Cannot remove unit that is currently assigned to an incident");
        }
        for(int i=index;i<unitCount-1;i++){
            units[i]=units[i+1];//Shifting the elements which are after the responce unit that is deleted
        }
        units[unitCount-1]=null;//last element assigned as null
        unitCount--;//Bringing down the count   
    }
    //Removing incident (8.5)
    public void removeIncident(String id) throws InvalidOperationException{
        boolean found=false;
        int index=0;
        for(int i=0;i<incidentCount;i++){
            if(incidents[i].getId().equals(id)){
                found=true;
                index=i;
                break;
            }
        }
        if(!found){
            throw new InvalidOperationException("No incident found with incident ID: " + id);
        }
        if(incidents[index].getStatus().equals(Incident.S_Assigned)) {
            throw new InvalidOperationException("Cannot remove incident that is currently assigned to a unit");
        }
        for(int i=index;i<incidentCount-1;i++){
            incidents[i]=incidents[i+1];//Shifting the elements which are after the incident that is deleted
        }
        incidents[incidentCount-1]=null;//last element assigned as null
        incidentCount--;//Bringing down the count   
    }
    //Automatic dispatch based on section 9
    public ResponseUnit dispatchBestUnit(String incidentId) throws InvalidOperationException, NoSuitableUnitException{
        Incident incident=findIncident(incidentId);
        if(incident==null){
            throw new InvalidOperationException("Incident with ID: " + incidentId + "does not exist");
        }
        String currentStatus=incident.getStatus();
        if(currentStatus.equals(Incident.S_Open)==false){
            throw new InvalidOperationException("Incident: " + incidentId + " is not OPEN, current status: " + currentStatus);
        }
        ResponseUnit Unit=null;
        double score=0;
        boolean found=false;
        int index=0;
        while(index<unitCount){
            ResponseUnit currentUnit=units[index];
            boolean isAvailable=currentUnit.isAvailable();
            boolean canHandle=currentUnit.canHandle(incident);
            boolean hasResources = currentUnit.hasResourcesFor(incident);
            if(isAvailable && canHandle && hasResources){
                double currentScore=currentUnit.calculateDispatchScore(incident, policy);
                if(found==false){
                    Unit=currentUnit;
                    score=currentScore;
                    found=true;
                }else{
                    if(currentScore<score){
                        Unit=currentUnit;
                        score=currentScore;
                    }else if(score==currentScore){
                        String candidateId=currentUnit.getId();
                        String bestId=Unit.getId();
                        int comparison=candidateId.compareTo(bestId);
                        if(comparison<0){
                            Unit=currentUnit;
                            score=currentScore;
                        }
                    }
                }
            }
            index++;
        }
        if(found==false){
            throw new NoSuitableUnitException("No suitable unit found for the incident");
        }
        Unit.assignIncident(incident.getId());
        incident.assignUnit(Unit.getId());
        double distanceToTravel=incident.getDistanceFromBase();
        try{Unit.move(distanceToTravel);}catch(InsufficientResourceException e){
            throw new InvalidOperationException("Unexpected resource failure while trying to dispatch");
        }
        return Unit;
    }
    //Private helper used only by displayDispatchCandidates - doesn't touch dispatchBestUnit
    private ResponseUnit findBestCandidate(Incident incident,boolean printTrace){
        ResponseUnit bestUnit=null;
        double bestScore=0;
        boolean found=false;
        int index=0;
        while(index<unitCount){
            ResponseUnit currentUnit=units[index];
            boolean isAvailable=currentUnit.isAvailable();
            boolean canHandle= currentUnit.canHandle(incident);
            boolean hasResources=currentUnit.hasResourcesFor(incident);
            if(isAvailable&&canHandle&&hasResources){
                double currentScore=currentUnit.calculateDispatchScore(incident, policy);
                if(printTrace){System.out.printf(currentUnit.getId()+" ELIGIBLE Score = %.2f%n", currentScore);}
                if(found==false){
                    bestUnit=currentUnit;
                    bestScore=currentScore;
                    found=true;
                }else if(currentScore<bestScore){
                    bestUnit =currentUnit;
                    bestScore=currentScore;
                }else if(currentScore==bestScore){
                    if(currentUnit.getId().compareTo(bestUnit.getId())<0){
                        bestUnit=currentUnit;
                        bestScore=currentScore;
                    }
                }
            }else{
                if(printTrace){
                    String reason;
                    if(isAvailable==false){
                        reason="Currently assigned";
                    }else if(canHandle==false){
                        reason="Capability mismatch";
                    }else{
                        reason="Insufficient resources";
                    }
                    System.out.println(currentUnit.getId()+" REJECTED "+reason);
                }
            }
            index++;
        }
        return bestUnit;
    }
    //Section 10) Dispatch Decision Trace using the private helper findBestCandidate
    public void displayDispatchCandidates(String incidentId) throws InvalidOperationException {
        Incident incident = findIncident(incidentId);
        System.out.println("Incident: " + incident.getId());
        System.out.println("Description: " + incident.getDescription());
        System.out.println("Severity: " + incident.getSeverity());
        ResponseUnit bestUnit = findBestCandidate(incident,true);
        if(bestUnit!=null){
            System.out.println("Best candidate: " + bestUnit.getId());
        }else{
            System.out.println("Best candidate: None");
        }
    }
    //Resolving an incident
    public void resolveIncident(String incidentId) throws InvalidOperationException,InsufficientResourceException{
        Incident incident=findIncident(incidentId);
        if(incident==null){
            throw new InvalidOperationException("Incident with ID: " + incidentId + "does not exist");
        }
        String currentStatus=incident.getStatus();
        if(currentStatus.equals(Incident.S_Assigned)==false){
            throw new InvalidOperationException("Incident: " + incidentId + " is not ASSIGNED to any unit, current status: " + currentStatus);
        }
        ResponseUnit unit=findUnit(incident.getAssignedUnitId());
        if(unit==null){
            throw new InvalidOperationException("Assigned unit with this ID does not exist");
        }
        try{
            unit.performWork(incident);//Polymorphic call, no instanceof checks needed
        }catch(InsufficientResourceException e){
            throw new InvalidOperationException("Unexpected resource failure while trying to resolve the incident");
        }
        unit.move(incident.getDistanceFromBase());
        unit.releaseIncident();
        unit.incrementCompleteIncidents();
        incident.resolveIncident();
    }
    //Refueling fuel powered units (12.1)
    public void refuelAll(double amount) throws InvalidOperationException{
        for(int i=0;i<unitCount;i++){
            ResponseUnit unit=units[i];
            if(unit instanceof FuelPowered){
                FuelPowered fuelUnit=(FuelPowered)unit;
                try{fuelUnit.refuel(amount);}catch(InvalidOperationException e){
                    throw new InvalidOperationException("Error while refueling the unit: "+e.getMessage());
                }
            }
        }
    }
    //Recharging battery powered units (12.2)
    public void rechargeAll(double amount) throws InvalidOperationException, InsufficientResourceException{
        for(int i=0;i<unitCount;i++){
            ResponseUnit unit=units[i];
            if(unit instanceof BatteryPowered){
                BatteryPowered batteryUnit=(BatteryPowered)unit;
                try{batteryUnit.recharge(amount);}catch(InvalidOperationException e){
                    throw new InvalidOperationException("Error while recharging the unit: "+e.getMessage());
                }
            }
        }
    }
    //Refill water(12.3)
    public void refillAllWater(double amount) throws InvalidOperationException{
        for(int i=0;i<unitCount;i++){
            ResponseUnit unit=units[i];
            if(unit instanceof WaterCarrier){
                WaterCarrier waterUnit=(WaterCarrier)unit;
                try{waterUnit.refillWater(amount);}catch(InvalidOperationException e){
                    throw new InvalidOperationException("Error while refilling water for the unit: "+e.getMessage());
                }
            }
        }
    }
    //Restock supply(12.4)
    public void restockAllSupplies(int amount) throws InvalidOperationException{
        for(int i=0;i<unitCount;i++){
            ResponseUnit unit=units[i];
            if(unit instanceof SupplyCarrier){
                SupplyCarrier supplyUnit=(SupplyCarrier)unit;
                try{supplyUnit.loadSupplies(amount);}catch(InvalidOperationException e){
                    throw new InvalidOperationException("Error while restocking supply for the unit: "+e.getMessage());
                }
            }
        }
    }
    //Section 13- Reporting
    public String generateReport(){
        String report="";
        int totalUnits=unitCount;
        int availableUnits=0;
        int busyUnits=0;
        double totalDistance= 0;
        int totalCompleted=0;
        String topUnitId="None";
        int topUnitCompleted=-1;
        int i =0;
        while(i<unitCount){
            ResponseUnit u=units[i];
            if(u.isAvailable()==true){availableUnits++;}else{busyUnits++;}
            totalDistance=totalDistance+u.getTotalDistanceTravelled();
            totalCompleted=totalCompleted+u.getCompleteIncidents();
            if(u.getCompleteIncidents()>topUnitCompleted){
                topUnitCompleted=u.getCompleteIncidents();
                topUnitId=u.getId();
            }
            i++;
        }
        int openCount=0;
        int assignedCount=0;
        int resolvedCount=0;
        int severeUnresolved= 0;
        String topPriorityId="None";
        double topPriorityWeight= -1;
        int unserviceableCount= 0;
        int j=0;
        while(j<incidentCount){
            Incident inc=incidents[j];
            String status=inc.getStatus();
            if(status.equals(Incident.S_Open)){
                openCount++;
                j++;
                if (inc.getPriorityWeight()>topPriorityWeight){
                    topPriorityWeight=inc.getPriorityWeight();
                    topPriorityId=inc.getId();
                }
                //Checking serviceability inline right here, no helper method
                boolean serviceable= false;
                int k=0;
                while(k<unitCount){
                    ResponseUnit u=units[k];
                    if((u.isAvailable()==true)&&(u.canHandle(inc)==true)&&(u.hasResourcesFor(inc)==true)){
                        serviceable = true;
                    }
                    k=k+1;
                }
                if(serviceable==false){
                    unserviceableCount=unserviceableCount+1;
                }
            }else if(status.equals(Incident.S_Assigned)){
                assignedCount=assignedCount+1;
            }else if(status.equals(Incident.S_Resolved)){
                resolvedCount=resolvedCount+1;
            }
            if(status.equals(Incident.S_Resolved)==false){
                if(inc.getSeverity()==4||inc.getSeverity()==5){
                    severeUnresolved++;
                }
            }
            j++;
        }
        report=report+ "===== SYSTEM REPORT =====\n";
        report=report+"Total Response Units: "+totalUnits+"\n";
        report=report+"Available Units: "+availableUnits+"\n";
        report=report+"Busy Units: "+busyUnits+"\n";
        report=report+"Total Distance Travelled: "+totalDistance+"\n";
        report=report+"Total Incidents Completed: "+totalCompleted+"\n";
        report=report+"Open Incidents: "+openCount+"\n";
        report=report+"Assigned Incidents: "+assignedCount+"\n";
        report=report+"Resolved Incidents: "+resolvedCount+"\n";
        report=report+"Unresolved Severity 4/5 Incidents: "+severeUnresolved+"\n";
        report=report+"Top Unit (most completed): "+topUnitId+" ("+topUnitCompleted+")\n";
        report=report+"Highest Priority Open Incident: "+topPriorityId+" (weight= "+topPriorityWeight+")\n";
        report=report+"Unserviceable Open Incidents: "+unserviceableCount+"\n";
        return report;
    }
    //Currently Unserviceable Incidents: 13.1
    public void displayUnserviceableIncidents(){
        System.out.println("Currently Unserviceable Incidents");
        boolean found= false;
        int i=0;
        while(i<incidentCount){
            Incident inc=incidents[i];
            if(inc.getStatus().equals(Incident.S_Open)){
                boolean serviceable=false;
                boolean anyCapableAvailable=false;
                int j=0;
                while(j<unitCount){
                    ResponseUnit u=units[j];
                    if((u.isAvailable()==true)&&(u.canHandle(inc)==true)){
                        anyCapableAvailable=true;
                        if(u.hasResourcesFor(inc)==true){
                            serviceable=true;
                        }
                    }
                    j=j+1;
                }
                if(serviceable==false){
                    String reason;
                    if(anyCapableAvailable==false){
                        reason="No suitable available unit";
                    }else{reason="Insufficient resources";}
                    System.out.println("Incident ID: "+inc.getId()+" Description: "+inc.getDescription()+" Severity: "+inc.getSeverity()+" Reason: "+reason);
                    found=true;
                }
            }
            i++;
        }
        if(found==false){
            System.out.println("No unserviceable incidents found.");
        }
    }
    //Based on section 15. Persistence
    public void saveState(String prefix) throws InvalidOperationException{
        try{
            FileWriter unitWriter=new FileWriter(prefix + "_units.csv");
            int i=0;
            while(i<unitCount){
                ResponseUnit u=units[i];
                String line="";
                if(u instanceof Ambulance){
                    Ambulance a=(Ambulance)u;
                    line= "AMBULANCE,"+a.getId()+","+a.getName()+","+a.getMaxSpeed()+","+a.getTrafficFactor()
                    +","+a.getTotalDistanceTravelled()+","+a.isAvailable()+","+
                    (a.getAssignedIncidentId()==null?"-":a.getAssignedIncidentId())+","+a.getCompleteIncidents()+
                    ","+a.getFuelLevel()+","+a.getCurrentPatients();
                }else if(u instanceof FireEngine){
                    FireEngine f=(FireEngine)u;
                    line= "FIREENGINE," +f.getId() +","+f.getName()+","+f.getMaxSpeed()+","
                    +f.getTrafficFactor()+","+f.getTotalDistanceTravelled()+","+f.isAvailable()
                    +","+(f.getAssignedIncidentId()==null?"-":f.getAssignedIncidentId())+
                    ","+f.getCompleteIncidents()+","+f.getFuelLevel()+","+f.getWaterLevel();
                } else if (u instanceof RepairVan) {
                    RepairVan r=(RepairVan)u;
                    line="REPAIRVAN,"+r.getId()+","+r.getName()+","+r.getMaxSpeed()+","+
                    r.getTrafficFactor() + "," + r.getTotalDistanceTravelled()+"," + r.isAvailable()
                    + "," + (r.getAssignedIncidentId() == null ? "-" : r.getAssignedIncidentId()) + ","
                    + r.getCompleteIncidents() + "," + r.getFuelLevel() + "," + r.getSupplyLevel();
                } else if (u instanceof SearchDrone) {
                    SearchDrone s=(SearchDrone)u;
                    line= "SEARCHDRONE," + s.getId() + "," + s.getName() + "," + s.getMaxSpeed() + "," 
                    +s.getTotalDistanceTravelled() + "," + s.isAvailable() + "," + 
                    (s.getAssignedIncidentId() == null ? "-" : s.getAssignedIncidentId()) + "," + 
                    s.getCompleteIncidents() + "," + s.getBatteryLevel();
                } else if (u instanceof HazmatUnit) {
                    HazmatUnit h = (HazmatUnit) u;
                    line = "HAZMATUNIT," + h.getId() + "," + h.getName() + "," + h.getMaxSpeed() +
                    "," + h.getTrafficFactor() + "," + h.getTotalDistanceTravelled() + ","
                    + h.isAvailable() + "," + (h.getAssignedIncidentId() == null ? "-" : h.getAssignedIncidentId())
                    + "," + h.getCompleteIncidents() + "," + h.getFuelLevel() + "," + h.getSupplyLevel();
                }
                unitWriter.write(line + "\n");
                i++;
            }
            unitWriter.close();
            FileWriter incWriter = new FileWriter(prefix+"_incidents.csv");
            int j=0;
            while(j<incidentCount){
                Incident inc=incidents[j];
                String line="";
                if(inc instanceof MedicalIncident){
                    MedicalIncident m = (MedicalIncident) inc;
                    line = "MEDICAL," + m.getId() + "," + m.getDescription() + "," +
                    m.getDistanceFromBase() + "," + m.getSeverity() + "," + m.getStatus() + ","
                    + (m.getAssignedUnitId() == null ? "-" : m.getAssignedUnitId()) + "," + 
                    m.getPatientCount() + "," + m.getCriticalPatients();
                }else if(inc instanceof FireIncident) {
                    FireIncident f = (FireIncident) inc;
                    line = "FIRE," + f.getId() + "," + f.getDescription() + "," +
                    f.getDistanceFromBase() + "," + f.getSeverity() + "," + f.getStatus() + ","
                    + (f.getAssignedUnitId() == null ? "-" : f.getAssignedUnitId()) + "," +
                    f.getAffectedArea() + "," + f.getHazardousMaterial();
                } else if (inc instanceof InfrastructureIncident) {
                    InfrastructureIncident r = (InfrastructureIncident) inc;
                    line = "INFRASTRUCTURE," + r.getId() + "," + r.getDescription() + "," +
                    r.getDistanceFromBase() + "," + r.getSeverity() + "," + r.getStatus() + "," + 
                    (r.getAssignedUnitId() == null ? "-" : r.getAssignedUnitId()) + "," + 
                    r.getAffectedUsers() + "," + r.getCriticalService();
                } else if (inc instanceof SearchIncident) {
                    SearchIncident s = (SearchIncident) inc;
                    line = "SEARCH," + s.getId() + "," + s.getDescription() + "," + 
                    s.getDistanceFromBase() + "," + s.getSeverity() + "," + s.getStatus() + "," + 
                    (s.getAssignedUnitId() == null ? "-" : s.getAssignedUnitId()) + "," + s.getMissingPersons() + 
                    "," + s.getSearchArea();
                } else if (inc instanceof HazmatIncident) {
                    HazmatIncident h = (HazmatIncident) inc;
                    line = "HAZMAT," + h.getId() + "," + h.getDescription() + "," + 
                    h.getDistanceFromBase() + "," + h.getSeverity() + "," + h.getStatus() + "," + 
                    (h.getAssignedUnitId() == null ? "-" : h.getAssignedUnitId()) + "," + 
                    h.getContaminationArea() + "," + h.getAirborneRisk();
                }
                incWriter.write(line + "\n");
                j++;
            }
            incWriter.close();
        } catch (IOException e) {
            throw new InvalidOperationException("Error while saving state: " + e.getMessage());
        }
    }
    public void loadState(String prefix) throws InvalidOperationException {
        units = new ResponseUnit[units.length];
        incidents = new Incident[incidents.length];
        unitCount = 0;
        incidentCount = 0;
        try{
            BufferedReader unitReader = new BufferedReader(new FileReader(prefix + "_units.csv"));
            String line;
            while ((line = unitReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                try{
                    String type = parts[0];
                    String id = parts[1];
                    String name = parts[2];
                    double maxSpeed = Double.parseDouble(parts[3]);
                    if (type.equals("AMBULANCE")) {
                        double trafficFactor = Double.parseDouble(parts[4]);
                        double totalDistance = Double.parseDouble(parts[5]);
                        boolean available = Boolean.parseBoolean(parts[6]);
                        String assignedId = parts[7].equals("-") ? null : parts[7];
                        int completed = Integer.parseInt(parts[8]);
                        double fuelLevel = Double.parseDouble(parts[9]);
                        int currentPatients = Integer.parseInt(parts[10]);
                        Ambulance a = new Ambulance(id, name, maxSpeed, trafficFactor);
                        a.setTotalDistanceTravelled(totalDistance);
                        a.setAvailable(available);
                        a.setAssignedIncidentId(assignedId);
                        a.setCompletedIncidents(completed);
                        a.setFuelLevel(fuelLevel);
                        a.setCurrentPatients(currentPatients);
                        units[unitCount] = a;
                        unitCount++;
                    } else if (type.equals("FIREENGINE")) {
                        double trafficFactor = Double.parseDouble(parts[4]);
                        double totalDistance = Double.parseDouble(parts[5]);
                        boolean available = Boolean.parseBoolean(parts[6]);
                        String assignedId = parts[7].equals("-") ? null : parts[7];
                        int completed = Integer.parseInt(parts[8]);
                        double fuelLevel = Double.parseDouble(parts[9]);
                        double waterLevel = Double.parseDouble(parts[10]);
                        FireEngine f = new FireEngine(id, name, maxSpeed, trafficFactor);
                        f.setTotalDistanceTravelled(totalDistance);
                        f.setAvailable(available);
                        f.setAssignedIncidentId(assignedId);
                        f.setCompletedIncidents(completed);
                        f.setFuelLevel(fuelLevel);
                        f.setWaterLevel(waterLevel);
                        units[unitCount] = f;
                        unitCount++;
                    } else if (type.equals("REPAIRVAN")) {
                        double trafficFactor = Double.parseDouble(parts[4]);
                        double totalDistance = Double.parseDouble(parts[5]);
                        boolean available = Boolean.parseBoolean(parts[6]);
                        String assignedId = parts[7].equals("-") ? null : parts[7];
                        int completed = Integer.parseInt(parts[8]);
                        double fuelLevel = Double.parseDouble(parts[9]);
                        int supplyLevel = Integer.parseInt(parts[10]);
                        RepairVan r = new RepairVan(id, name, maxSpeed, trafficFactor);
                        r.setTotalDistanceTravelled(totalDistance);
                        r.setAvailable(available);
                        r.setAssignedIncidentId(assignedId);
                        r.setCompletedIncidents(completed);
                        r.setFuelLevel(fuelLevel);
                        r.setSupplyLevel(supplyLevel);
                        units[unitCount] = r;
                        unitCount++;
                    } else if (type.equals("SEARCHDRONE")) {
                        double totalDistance = Double.parseDouble(parts[4]);
                        boolean available = Boolean.parseBoolean(parts[5]);
                        String assignedId = parts[6].equals("-") ? null : parts[6];
                        int completed = Integer.parseInt(parts[7]);
                        double batteryLevel = Double.parseDouble(parts[8]);
                        SearchDrone s = new SearchDrone(id, name, maxSpeed);
                        s.setTotalDistanceTravelled(totalDistance);
                        s.setAvailable(available);
                        s.setAssignedIncidentId(assignedId);
                        s.setCompletedIncidents(completed);
                        s.setBatteryLevel(batteryLevel);
                        units[unitCount] = s;
                        unitCount++;
                    } else if (type.equals("HAZMATUNIT")) {
                        double trafficFactor = Double.parseDouble(parts[4]);
                        double totalDistance = Double.parseDouble(parts[5]);
                        boolean available = Boolean.parseBoolean(parts[6]);
                        String assignedId = parts[7].equals("-") ? null : parts[7];
                        int completed = Integer.parseInt(parts[8]);
                        double fuelLevel = Double.parseDouble(parts[9]);
                        int supplyLevel = Integer.parseInt(parts[10]);
                        HazmatUnit h = new HazmatUnit(id, name, maxSpeed, trafficFactor);
                        h.setTotalDistanceTravelled(totalDistance);
                        h.setAvailable(available);
                        h.setAssignedIncidentId(assignedId);
                        h.setCompletedIncidents(completed);
                        h.setFuelLevel(fuelLevel);
                        h.setSupplyLevel(supplyLevel);
                        units[unitCount] = h;
                        unitCount++;
                    }
                } catch (Exception e) {
                    System.out.println("Skipping a malformed unit record: " + line);
                }
            }
            unitReader.close();
            BufferedReader incReader = new BufferedReader(new FileReader(prefix + "_incidents.csv"));
            while ((line = incReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                try {
                    String type = parts[0];
                    String id = parts[1];
                    String description = parts[2];
                    double distance = Double.parseDouble(parts[3]);
                    int severity = Integer.parseInt(parts[4]);
                    String status = parts[5];
                    String assignedUnitId = parts[6].equals("-") ? null : parts[6];
                    if (type.equals("MEDICAL")) {
                        int patientCount = Integer.parseInt(parts[7]);
                        int criticalPatients = Integer.parseInt(parts[8]);
                        MedicalIncident m = new MedicalIncident(id, description, distance, severity, 
                            patientCount, criticalPatients);
                        m.setStatus(status);
                        m.setAssignedUnitId(assignedUnitId);
                        incidents[incidentCount] = m;
                        incidentCount++;
                    } else if (type.equals("FIRE")) {
                        double affectedArea = Double.parseDouble(parts[7]);
                        boolean hazardous = Boolean.parseBoolean(parts[8]);
                        FireIncident f = new FireIncident(id, description, distance, severity, 
                            affectedArea, hazardous);
                        f.setStatus(status);
                        f.setAssignedUnitId(assignedUnitId);
                        incidents[incidentCount] = f;
                        incidentCount++;
                    } else if (type.equals("INFRASTRUCTURE")) {
                        int affectedUsers = Integer.parseInt(parts[7]);
                        boolean criticalService = Boolean.parseBoolean(parts[8]);
                        InfrastructureIncident r = new InfrastructureIncident(id, description, distance,
                            severity, affectedUsers, criticalService);
                        r.setStatus(status);
                        r.setAssignedUnitId(assignedUnitId);
                        incidents[incidentCount] = r;
                        incidentCount++;
                    } else if (type.equals("SEARCH")) {
                        int missingPersons = Integer.parseInt(parts[7]);
                        double searchArea = Double.parseDouble(parts[8]);
                        SearchIncident s = new SearchIncident(id, description, distance, severity, 
                            missingPersons, searchArea);
                        s.setStatus(status);
                        s.setAssignedUnitId(assignedUnitId);
                        incidents[incidentCount] = s;
                        incidentCount++;
                    } else if (type.equals("HAZMAT")) {
                        double contaminationArea = Double.parseDouble(parts[7]);
                        boolean airborneRisk = Boolean.parseBoolean(parts[8]);
                        HazmatIncident h = new HazmatIncident(id, description, distance, severity,
                            contaminationArea, airborneRisk);
                        h.setStatus(status);
                        h.setAssignedUnitId(assignedUnitId);
                        incidents[incidentCount] = h;
                        incidentCount++;
                    }
                }catch(Exception e){
                    System.out.println("Skipping a malformed incident record: " + line);
                }
            }
            incReader.close();
        } catch (IOException e) {
            throw new InvalidOperationException("Error found while loading state: " + e.getMessage());
        }
    }
    //For Option 7 - List All Response Units in CLI
    public void displayAllUnits(){
        if(unitCount==0){
            System.out.println("No response units currently in the system.");
            return;
        }
        int i=0;
        while(i<unitCount){
            units[i].displayUnitInfo();
            System.out.println("-----");
            i++;
        }
    }
    //For Option 8 - List All Incidents in CLI
    public void displayAllIncidents(){
        if(incidentCount==0){
            System.out.println("No incidents currently in the system.");
            return;
        }
        int i=0;
        while(i<incidentCount){
            incidents[i].displayIncidentDetails();
            System.out.println("-----");
            i++;
        }
    }
}
