
import NSGAII.Individual;
import NSGAII.NSGA2Listener;
import NSGAII.NSGA2Event;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author Joseph Rivera
 */
public class ScheduleNSGA2Listener implements NSGA2Listener
{

    /**
     * Performs the specified NSGA-II event.
     * <p>
     * Every 100 generations, the best individuals found so far are printed.
     * 
     * @param nsga2event NSGA-II event
     */
    public void performNSGA2Event(NSGA2Event nsga2event)
    {
        if (nsga2event.getNumberGeneration() % 100 == 0)
        {
            System.out.println();
            System.out.println("Generation: " + nsga2event.getNumberGeneration());

            LinkedList<Individual> bestIndividuals = nsga2event.getBestIndividuals();

            LinkedList<IndividualSchedule> bestSchedules = new LinkedList<IndividualSchedule>();
            for (Individual individual : bestIndividuals)
            {
                bestSchedules.add((IndividualSchedule) individual);
            }

            printBestSchedules(bestSchedules);
        }
    }

    /**
     * Prints the specified individuals.
     * 
     * @param bestSchedules individuals
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

    private static class IndividualScheduleComparator implements Comparator<IndividualSchedule>
    {

        /**
         * Compares the two specified Schedule individuals. First criterion is a 
         * low duration, second one is a low Total Cost and the third one is a low
         * Environmental Impact value.
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

            return 0;
        }
    }
}
