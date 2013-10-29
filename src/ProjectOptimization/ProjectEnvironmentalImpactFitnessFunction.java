package ProjectOptimization;

import IDF.POption;
import IDF.ParametricOptionReader;
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
public class ProjectEnvironmentalImpactFitnessFunction implements FitnessFunction
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

        double elecJ = 0;
        double gasJ = 0;
        geneSequence.append(".csv");
        String gs = geneSequence.toString();

        //update EI based on simulation results
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
                return EI;
            }

            double elecMWH = elecJ * Constants.MWH_CONVERSION;
            double gasTherms = gasJ * Constants.THERM_CONVERSION;
            double elecKgYear = (elecMWH * Constants.US_AVG_CO2_LBS_PER_MWH) * Constants.KG_PER_LB;//MHW * CO2lbsCnvt * kgCvnt = CO2 in kg
            double gasKgYear = (gasTherms * Constants.METRIC_TONS_CO2_PER_THERM) * 1000; //therms * MetricTonCnvt*kgCvnt = CO2 in kg

            EI += elecKgYear + gasKgYear;
        }
        return EI;
    }
}
