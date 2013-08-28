/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.util.Map;

/**
 *
 * @author fdot
 */
public class ProjectEnvironmentalImpactFitnessFunction implements FitnessFunction
{
     @Override
    public double evaluate(Individual indv)
    {
        if(indv == null)
        {
            throw new IllegalArgumentException("Individual must not be null.");
        }
        if (!(indv instanceof IndividualProject))
        {
            throw new IllegalArgumentException("Individual must be of type IndividualSchedule.");
        }

        IndividualProject projIndv = (IndividualProject) indv;
                
        double EI = 0.0;
        
        for(Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
        {
            EI += entry.getValue().getCo2();
        }
        
        return EI;
    }
}
