package ProjectOptimization;

import IDF.ParametricOptionReader;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 *
 * @author Joseph Rivera
 */
public class ProjectCostFitnessFunction implements FitnessFunction
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
        StringBuilder geneSequence = projIndv.buildGeneSequence();

        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
            cost += entry.getValue().getCost();

        double elecJ = 0;
        double gasJ = 0;
        geneSequence.append(".csv");
        String gs = geneSequence.toString();

        //update cost based on simulation results
        File energySim = ParametricOptionReader.getEnergySimulationResult(projIndv, gs);
        if (energySim != null)
        {
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(energySim));
                //Assumptions: columns 2 and 17 contain the
                //desired values: electricity:facility(RunPeriod)
                //and Gas:Facility(RunPeriod).
                //Both are in Joules.
                //Only the last row (December) contains a useful value
                for (int i = 0; i < 12; i++)
                    reader.readLine();
                String[] data = reader.readLine().split(",");
                elecJ = Double.parseDouble(data[1]);
                gasJ = Double.parseDouble(data[16]);
            }
            catch (Exception e)
            {
                return cost;
            }

            double electKWH = elecJ * Constants.MWH_CONVERSION * 1000;
            double gas = 0.0; //TODO: Find appropriate conversion for natural gas costs

            double elecCostYear = electKWH * Constants.US_AVG_COST_DOLLARS_PER_KWH;
            double gasCostYear = gas; //TODO: Calculate cost of natural gas for a year

            cost += elecCostYear + gasCostYear;
        }
        return cost;
    }
}
