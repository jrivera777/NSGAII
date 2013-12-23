package ProjectOptimization;

import IDF.POption;
import IDF.EnergySimParametricOptionReader;
import IDF.NoEnergyResultFoundException;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Joseph Rivera
 */
public class EnvironmentalImpactSMFitnessFunction implements FitnessFunction
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
        StringBuilder geneSequence = projIndv.buildGeneSequence();

        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
            EI += entry.getValue().getCo2();

        double gasJ = 0; //TODO: Add method for gas to SimulationReader Interface
        String gs = geneSequence.toString();
        EnergySimParametricOptionReader pReader = new EnergySimParametricOptionReader();

        //update EI based on simulation results
        double electricity = - 1;
        try
        {
            electricity = pReader.getSimulationElectricity(indv, gs);
            if (electricity >= 0)
            {
                double electKWH = electricity;
                double elecMWH = electKWH / 1000.0;
                double gasTherms = gasJ * Constants.THERM_CONVERSION;
                double elecKgYear = (elecMWH * Constants.US_AVG_CO2_LBS_PER_MWH) * Constants.KG_PER_LB;//MHW * CO2lbsCnvt * kgCvnt = CO2 in kg
                double gasKgYear = (gasTherms * Constants.METRIC_TONS_CO2_PER_THERM) * 1000; //therms * MetricTonCnvt*kgCvnt = CO2 in kg

                EI += elecKgYear + gasKgYear;
            }
        }
        catch (NoEnergyResultFoundException e)
        {
            // If energy simulations are being considered(i.e. there is a known
            // engery directory), but no result is found for this individual, 
            // give it a terrible fitness so it will never be chosen.
            return Double.MAX_VALUE;
        }

        return EI;
    }
}
