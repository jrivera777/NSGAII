package ProjectOptimization;

import IDF.EnergySimParametricOptionReader;
import IDF.NoEnergyResultFoundException;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.util.Map;

/**
 *
 * @author Joseph Rivera
 */
public class CostESFitnessFunction implements FitnessFunction
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

        // Not considering energy simulations
        if (projIndv.getEnergyResults() == null)
            return cost;

        StringBuilder geneSequence = projIndv.buildGeneSequence();
        String gs = geneSequence.toString();
        EnergySimParametricOptionReader pReader = new EnergySimParametricOptionReader();

        // Update cost based on simulation results
        double electricity = - 1;
        try
        {
            electricity = pReader.getSimulationElectricity(indv, gs);
            if (electricity >= 0)
            {
                double electKWH = electricity;
                double gas = 0.0; //TODO: Find appropriate conversion for natural gas costs

                double elecCostYear = electKWH * Constants.US_AVG_COST_DOLLARS_PER_KWH;
                double gasCostYear = gas; //TODO: Calculate cost of natural gas for a year

                cost += elecCostYear + gasCostYear;
            }
        }
        catch (NoEnergyResultFoundException e)
        {
            // If energy simulations are being considered(i.e. there is a known
            // engery directory), but no result is found for this individual, 
            // give it a terrible fitness so it will never be chosen.
            return Double.MAX_VALUE;
        }
        return cost;
    }
}
