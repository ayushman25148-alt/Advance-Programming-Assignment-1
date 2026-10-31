package capabilities;
import exceptions.InvalidOperationException;
import exceptions.InsufficientResourceException;

public interface BatteryPowered{
    void recharge(double amount) throws InvalidOperationException, InsufficientResourceException;
    double getBatteryLevel();
    double getBatteryCapacity();
    double consumeBattery(double amount) throws InsufficientResourceException;
    boolean hasBatteryFor(double amount);
}
