
import NSGAII.ActivityList;
import NSGAII.Activity;
import NSGAII.NSGA2;
import NSGAII.Individual;
import NSGAII.Schedule;
import NSGAII.NSGA2Configuration;
import NSGAII.FitnessFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Joseph Rivera
 */
public class ScheduleTest
{

    private static final double MUTATION_PROBABILITY = 0.05;  // A much higher mutation rate seems to have a negative effect!
    private static final double CROSSOVER_PROBABILITY = 0.9;
    private static final int POPULATION_SIZE = 40;
    private static final int NUMBER_OF_GENERATIONS = 100;
    private static final double DIFFERENCE_THRESHOLD = .10;

    /**
     * Main method
     * 
     * @param args arguments (not used)
     */
    public static void main(String[] args)
    {
        //create ActivityList of AllAlternates
        ActivityList alternates = new ActivityList();
        alternates.loadActivities("activities.txt");
        System.out.println(calculatePossibleSchedules(alternates));
        // create NSGA-II instance
        TimeFitnessFunction fitnessFunction0 = new TimeFitnessFunction();
        CostFitnessFunction fitnessFunction1 = new CostFitnessFunction();
        EnvironmentalImpactFitnessFunction fitnessFunction2 =
                new EnvironmentalImpactFitnessFunction();
        FitnessFunction[] fitnessFunctions = new FitnessFunction[3];
        fitnessFunctions[0] = fitnessFunction0;
        fitnessFunctions[1] = fitnessFunction1;
        fitnessFunctions[2] = fitnessFunction2;
        NSGA2Configuration conf = new NSGA2Configuration(fitnessFunctions,
                                                         MUTATION_PROBABILITY,
                                                         CROSSOVER_PROBABILITY,
                                                         DIFFERENCE_THRESHOLD,
                                                         POPULATION_SIZE,
                                                         NUMBER_OF_GENERATIONS);
        NSGA2 nsga2 = new NSGA2(conf);
        nsga2.addNSGA2Listener(new ScheduleNSGA2Listener());

        // create start population
        LinkedList<Individual> startPopulation = new LinkedList<Individual>();

        for (int i = 0; i < POPULATION_SIZE; i++)
        {
            IndividualSchedule indv = new IndividualSchedule(nsga2, new Schedule("associations.txt", alternates), alternates);
            startPopulation.add(indv);
        }

        // start evolution
        LinkedList<Individual> bestIndividuals = nsga2.evolve(startPopulation);

        LinkedList<IndividualSchedule> bestSchedules = new LinkedList<IndividualSchedule>();
        for (Individual individual : bestIndividuals)
        {
            bestSchedules.add((IndividualSchedule) individual);
        }

        printBestSchedules(bestSchedules);
    }

    private static double calculatePossibleSchedules(ActivityList list)
    {
        double totalPoss = 1;
        for (Map.Entry<String, ArrayList<Activity>> entry : list.getAltsByName().entrySet())
        {
            int count = entry.getValue().size();
            totalPoss = totalPoss * count;
        }

        return totalPoss;
    }

    /**
     * Prints the specified schedule individuals.
     * 
     * @param bestSchedules schedule individuals
     */
    private static void printBestSchedules(LinkedList<IndividualSchedule> bestSchedules)
    {
        if (bestSchedules == null)
        {
            throw new IllegalArgumentException("'bestSchedules' must not be null.");
        }

        // sort best schedules
        IndividualSchedule[] array =
                bestSchedules.toArray(new IndividualSchedule[bestSchedules.size()]);
        Arrays.sort(array, new IndividualScheduleComparator());

        System.out.println();
        System.out.println("Number of offered solutions: " + bestSchedules.size());

        for (int i = 0; i < array.length; i++)
        {
            System.out.print(" Duration: " + array[i].getFitnessValue(0));
            System.out.print(" / Cost: " + array[i].getFitnessValue(1));
            System.out.println(" / Environmental Impact: " + array[i].getFitnessValue(2));
        }
    }

    /**
     * This inner class implements a comparator for two schedule individuals.
     */
    private static class IndividualScheduleComparator implements Comparator<IndividualSchedule>
    {

        /**
         * Compares the two specified schedule individuals. 
         * 
         * @param individual1 first individual
         * @param individual2 second individual
         * @return -1, 0 or 1 as the first individual is less than, equal to, or greater than the
         *         second one
         */
        public int compare(IndividualSchedule individual1, IndividualSchedule individual2)
        {
            if (individual1 == null)
            {
                throw new IllegalArgumentException("'individual1' must not be null.");
            }
            if (individual2 == null)
            {
                throw new IllegalArgumentException("'individual2' must not be null.");
            }

            
            if (individual1.getFitnessValue(0) < individual2.getFitnessValue(0))
            {
                return -1;
            }

            if (individual1.getFitnessValue(0) > individual2.getFitnessValue(0))
            {
                return 1;
            }
          
            if (individual1.getFitnessValue(1) < individual2.getFitnessValue(1))
            {
                return -1;
            }

            if (individual1.getFitnessValue(1) > individual2.getFitnessValue(1))
            {
                return 1;
            }
            
            if (individual1.getFitnessValue(2) < individual2.getFitnessValue(2))
            {
                return -1;
            }

            if (individual1.getFitnessValue(2) > individual2.getFitnessValue(2))
            {
                return 1;
            }

            // both individuals are equal
            return 0;
        }
    }
}
