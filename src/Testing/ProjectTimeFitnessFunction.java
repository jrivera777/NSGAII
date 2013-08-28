package Testing;

import NSGAII.FitnessFunction;
import NSGAII.Individual;

/**
 *
 * @author fdot
 */
public class ProjectTimeFitnessFunction implements FitnessFunction
{

    @Override
    public double evaluate(Individual individual)
    {
        if(individual == null)
        {
            throw new IllegalArgumentException("Individual must not be null.");
        }
        if (!(individual instanceof IndividualProject))
        {
            throw new IllegalArgumentException("Individual must be of type IndividualSchedule.");
        }
        
        IndividualProject projIndv = (IndividualProject) individual;
        
        double duration = 0.0;
        //projIndv.getCurrentOrder().printPath("END");
        projIndv.getCurrentOrder().negateEdges();
        //projIndv.getCurrentOrder().acyclic("START");
        //duration = projIndv.getCurrentOrder().getTotalCostAbs(Assembly.END.getName());
        projIndv.getCurrentOrder().negative(Assembly.START.getName());
        duration = projIndv.getCurrentOrder().getTotalCostAbs(Assembly.END.getName());
//        projIndv.getCurrentOrder().negateEdges();
//        projIndv.getCurrentOrder().dijkstra("START");
       
        
        return duration;
    }
}
