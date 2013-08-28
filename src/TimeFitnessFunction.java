
import NSGAII.Individual;
import NSGAII.Schedule;
import NSGAII.FitnessFunction;




/**
 *
 * @author Joseph Rivera
 * 
 * Implements FitnessFunction for Time component of a given Schedule. 
 */
public class TimeFitnessFunction implements FitnessFunction
{
    /**
     * 
     * @param indv
     * @return Estimated Time cost for a given Schedule.
     */
    @Override
    public double evaluate(Individual indv)
    {
        if(indv == null)
        {
            throw new IllegalArgumentException("Individual must not be null.");
        }
        if (!(indv instanceof IndividualSchedule))
        {
            throw new IllegalArgumentException("Individual must be of type IndividualSchedule.");
        }

        IndividualSchedule schedIndv = (IndividualSchedule) indv;
        
        Schedule sched = schedIndv.getSchedule();
        
        double time = sched.TimeObjective();
       
        return time;
    }
}
