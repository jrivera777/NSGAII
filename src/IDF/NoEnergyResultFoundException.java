package IDF;

/***
 * Exception for Missing gene sequences in the simulation results file.
 * 
 * @author Joseph Rivera
 */
public class NoEnergyResultFoundException extends RuntimeException{

    public NoEnergyResultFoundException(String msg)
    {
        super(msg);
    }
}
