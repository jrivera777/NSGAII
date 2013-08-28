
import NSGAII.Individual;
import NSGAII.Schedule;
import NSGAII.FitnessFunction;

public class CostFitnessFunction implements FitnessFunction
{
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
        
        return sched.CostObjective();
    }
}
