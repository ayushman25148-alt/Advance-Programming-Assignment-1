package units;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;
//Made with reference to the section 3.2 of the given pdf
public class GroundResponseUnit extends ResponseUnit {
    //Making the only extra attribute stated
    private double trafficFactor;
    public GroundResponseUnit(String id, String name, double maxSpeed, double trafficFactor) throws InvalidOperationException{
        super(id,name,maxSpeed);
        if(trafficFactor<1){
            throw new InvalidOperationException("Traffic factor needs to be greater than factor of 1");
        }
        this.trafficFactor=trafficFactor;
    }
    //Creating the left method to get the traffic factor
    public double getTrafficFactor(){
        return this.trafficFactor;
    }
    //Overriding the estimateArrivalTime method to include traffic factor and the move method
    public double estimateArrivalTime(double distance){
        return ((distance/this.getMaxSpeed())*(this.trafficFactor));
    }
    public void move(double distance) throws InvalidOperationException, InsufficientResourceException{
        if(distance<0){
            throw new InvalidOperationException("Distance cannot be negative");
        }
        super.move(distance);//Adds the distance to the total distance travelled using the parent class method
        System.out.println("Groundunit "+getId()+" driving on road, factoring traffic conditions.");
    }
}

