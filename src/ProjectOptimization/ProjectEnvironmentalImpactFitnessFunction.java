/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import IDF.POption;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.util.List;
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
        StringBuilder geneSequence = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
        {
            EI += entry.getValue().getCo2();
            
            String assemName = entry.getValue().getName();
            
//            List<POption> opts = projIndv.getParametrics().get(entry.getKey());
//            for(POption opt : opts)
//            {
//                if(opt.getName().equalsIgnoreCase(assemName))
//                    geneSequence.append(opt.getValue());
//                
//                if(count++ != projIndv.getCurrentAssemblies().size() - 1)
//                    geneSequence.append("-");
//            }
        }
//        System.out.printf("GeneSequence is : %s\n", geneSequence.toString());
        
        //update EI and Cost based on simulation results
        return EI;
    }
}
