package ProjectOptimization;

import IDF.EnergySimParametricOptionReader;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class EnvironmentalImpactFitnessFunction implements FitnessFunction
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

        double EI = 0.0;

        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
            EI += entry.getValue().getCo2();

        return EI;
    }
}
