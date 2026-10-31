package exceptions;

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
//This is a custom exception class that extends the built-in Exception class.
//Its role is for findUnit or like findIncident when the id itself  was not there (it does not overload over InvalidOperationException)
}
