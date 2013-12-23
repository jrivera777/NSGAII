package ProjectOptimization;

import IDF.EnergySimParametricOptionReader;
import IDF.NoEnergyResultFoundException;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.util.Map;

public class CostFitnessFunction implements FitnessFunction
{

    @Override
    public double evaluate(Individual indv)
    {
        if (indv == null)
        {
            throw new IllegalArgumentException("Individual must not be null.");
        }
        if (!(indv instanceof IndividualProject))
        {
            throw new IllegalArgumentException("Individual must be of type IndividualSchedule.");
        }

        IndividualProject projIndv = (IndividualProject) indv;

        double cost = 0.0;
        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
            cost += entry.getValue().getCost();

        return cost;
    }
}
