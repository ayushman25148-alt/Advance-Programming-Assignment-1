package capabilities;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;

public interface FuelPowered {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double getFuelCapacity();
    double consumeFuel(double distance) throws InsufficientResourceException;
    boolean hasFuelFor(double distance);
}