package capabilities;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;

public interface WaterCarrier {
    void refillWater(double amount) throws InvalidOperationException;
    void useWater(double amount) throws InvalidOperationException, InsufficientResourceException;
    double getWaterLevel();
    double getWaterCapacity();
}
