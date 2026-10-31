Emergency Response Dispatch and Incident Management System
Advanced Programming - Assignment 1

Name: Ayushman Tripathy				Roll Number: 2025148

1. PROJECT OVERVIEW
This project is an Emergency Response Dispatch and Incident Management System built in Java. It basically
simulates how different emergency units like ambulances, fire engines, etc. get dispatched to incidents
based on their location, resources available, and dispatch scores. The code covers OOP concepts like inheritance,
polymorphism, interfaces, exception handling and file read/write for CSVs using a menu-driven CLI.

2. PROJECT STRUCTURE
	src/
	|-- capabilities/
	|-- dispatch/
	|-- exceptions/
	|-- incidents/
	|-- units/
	|-- Main.java

3. COMPILATION
You can simply go and run the Main.java file on your VSCode via run button or open terminal inside 
the src folder and run:    'javac capabilities/*.java exceptions/*.java incidents/*.java units/*.java dispatch/*.java Main.java'

4. RUNNING THE PROGRAM
Run the program from terminal using: java Main
(Or if you are on VS Code just open Main.java and click the run button)

To test save/load, use option 18 to save with some prefix (say "sample"),then option 19 with the 
same prefix to load it back. Sample files are included with this submission file using the same way.

5. FEATURES
	-Add and manage different response units
	-Register new incidents in system
	-Automatically dispatch the best unit for an incident
	-Preview which units are eligible/rejected before dispatching
	-Resolve ongoing incidents
	-View system status and print reports
	-Save or load state from CSV files
	-Support multiple unit and incident types

6. CLASS HIERARCHY
	ResponseUnit (concrete)
  		1] GroundResponseUnit (adds trafficFactor)
        		-Ambulance implements FuelPowered, PatientCarrier
        		-FireEngine implements FuelPowered, WaterCarrier
        		-RepairVan implements FuelPowered, SupplyCarrier
        		-HazmatUnit implements FuelPowered, SupplyCarrier (extension as asked in the assignment PDF)
  		2] SearchDrone (extends ResponseUnit directly) implements BatteryPowered

	Incident (concrete)
   		1]MedicalIncident
   		2]FireIncident
   		3]InfrastructureIncident
   		4]SearchIncident
   		5]HazmatIncident (extension)

DispatchPolicy holds threshold/penalty values, DispatchManager holds
ResponseUnit[] and Incident[] arrays and all the core logic, Main is
the CLI entry point.

7. RESPONSE UNIT TYPES
	1.Ambulance
	2.Fire Engine
	3.Repair Van
	4.Search Drone
	5.Hazmat Unit (My individual extension cause my roll number ends with "8")

Each unit handles moving, resource usage, scoring, and doing work during
incident resolution a bit differently.

8. INCIDENT TYPES
	-Medical Incident
	-Fire Incident
	-Infrastructure Incident
	-Search Incident
	-Hazmat Incident (Individual Extension)

Each type calculates its own workload and priority differently.

9. EXAMPLES OF METHOD OVERRIDING
GroundResponseUnit overrides estimateArrivalTime() and move() from ResponseUnit to account for trafficFactor.
SearchDrone overrides estimateArrivalTime() to multiply base time by 0.85 (drones take more direct paths).
Every unit subclass (Ambulance, FireEngine, RepairVan, SearchDrone, HazmatUnit) overrides hasResourcesFor(), calculateDispatchScore(),
    move() and performWork() with its own resource-specific logic.
Every incident subclass overrides getRequiredCapability(), getPriorityWeight() and getWorkload() with its own formula.

10. RUNTIME POLYMORPHISM 
1. dispatchBestUnit() loops over ResponseUnit[] and calls unit.canHandle(), unit.hasResourcesFor() and
   	unit.calculateDispatchScore() - even though the array holds mixed subclasses, the correct overridden
	version runs for each object automatically, no instanceof needed for behaviour.
2. resolveIncident() calls unit.performWork(incident) - an Ambulance boards/releases patients, a FireEngine drains water,
	a RepairVan drains supplies, a SearchDrone drains battery, a HazmatUnit drains containment agent - all through one polymorphic call.
3. generateReport() and displayUnserviceableIncidents() call incident.getPriorityWeight()/getWorkload() on an Incident[] holding mixed subtypes,
	and each one's own formula runs depending on its actual runtime type.
4. displayAllUnits() calls unit.displayUnitInfo() and move() indirectly through the same ResponseUnit[] loop - a GroundResponseUnit-based
   	unit prints its road-specific movement message while SearchDrone prints the base one, without the loop knowing which is which.
5. ResponseUnit's own calculateDispatchScore() internally calls estimateArrivalTime(distance) - since GroundResponseUnit and SearchDrone 
	override estimateArrivalTime(), calling this base(non-overridden) method on a GroundResponseUnit/SearchDrone object still ends up 
	running the subclass's overridden version, since Java resolves it at runtime based on the actual object type, not where the calling 
	code is written.

11. INTERFACES USED
FuelPowered - lets a unit refuel/consume fuel and check if it has enough for a distance.
		 Implemented by Ambulance, FireEngine, RepairVan, HazmatUnit.
BatteryPowered - same idea but for battery. Implemented by SearchDrone only.
PatientCarrier - lets a unit board/release patients within a capacity. Implemented by Ambulance only.
WaterCarrier - lets a unit refill/use water. Implemented by FireEngine only.
SupplyCarrier - lets a unit load/use supply units. Implemented by RepairVan and HazmatUnit.

These are kept separate from the unit class hierarchy on purpose since they're independent capabilities - a unit "is-a" ResponseUnit
but "has-a" capability like FuelPowered, which is why interfaces are used instead of more inheritance.

instanceof is only used to check if a unit has a capability (like in
refuelAll checking instanceof FuelPowered), never for branching on
actual dispatch behaviour.

12. INDIVIDUAL EXTENSION
I implemented Hazardous Material Response System for my custom extension. Classes added:
	A)HazmatUnit - extends GroundResponseUnit, implements FuelPowered and SupplyCarrier.
	               Fuel cap 120L, efficiency 8km/L, supply cap 40 units.
	B)HazmatIncident - extends Incident. It has contaminationArea and
  	                   airborneRisk fields, priority weight adds +20 if airborne.

These integrate directly with the main dispatch logic (dispatchBestUnit,
resolveIncident) without needing to modify the dispatch algorithm itself.

13. PERSISTENCE
Saves and loads whole system state using CSV format. Files generated:
	a) prefix_units.csv
	b) prefix_incidents.csv

Each row starts with a type tag (like AMBULANCE, MEDICAL, HAZMAT etc) followed by comma separated fields. 
loadState reads the type first and builds the right subclass object from it. Bad/malformed rows just get
skipped with a warning instead of crashing the load.

Included sample CSV files for testing this out.

14. ASSUMPTIONS
Units start off with full resources.
Refill/recharge operations won't take 0 or negative values.
Resource check happens before dispatching (round trip fuel etc).
Trying to resolve an already resolved incident does nothing.
Max default limits are set to 50 units and 100 incidents, but can be changed by passing values to DispatchManager's constructor.

15. TESTING
Tested manually against all assignment scenarios:
	1. Heterogeneous array - listed 6 unit subclasses using option 7.
	2. Duplicate ID - re-adding U01 was correctly rejected.
	3. Full array - checked array length bounds check in code for add unit/incident.
	4. Array removal - removed U02 from middle, rest units shifted left properly without gaps.
	5. Search - found U02 ok, U99 threw exception and caught cleanly.
	6. Dispatch selection - I01 (Medical) had 2 valid units, selected lower score unit (U05).
	7. Tie-break - verified lexicographical order ID wins in compareTo logic.
	8. Insufficient travel resource - I05 (1000km) rejected ambulances because fuel wasnt enough for round trip.
	9. Insufficient operational resource - I04 needed 50000 area water which FireEngine couldn't meet so rejected.
	10. Busy unit - U05 was busy with I01 so showed REJECTED "Currently assigned" when tried to assign again.
	11. Unserviceable incident - I04 became unserviceable when only FireEngine was removed (option 17).
	12. Incident resolution - I01 resolved using option 11: patient boarded, fuel deducted, unit freed up.
	13. Redispatch - after resolving I01, same unit got reassigned to I07 successfully.
	14. Persistence - saved state with prefix "sample", restarted program, loaded back state and verified.
	15. Hazmat extension - HazmatUnit H01 dispatched and resolved I_HAZ1 fine, while I_HAZ2 rejected due to low supply capacity.
