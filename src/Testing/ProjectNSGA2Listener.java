/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import NSGAII.Individual;
import NSGAII.NSGA2Event;
import NSGAII.NSGA2Listener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author fdot
 */
public class ProjectNSGA2Listener implements NSGA2Listener
{
    
    private final int NUM_GEN = 25;
     /**
     * Performs the specified NSGA-II event.
     * <p>
     * Every 100 generations, the best individuals found so far are printed.
     * 
     * @param nsga2event NSGA-II event
     */
    public void performNSGA2Event(NSGA2Event nsga2event)
    {
        if (nsga2event.getNumberGeneration() % NUM_GEN == 0)
        {
            System.out.println();
            System.out.println("Generation: " + nsga2event.getNumberGeneration());

            LinkedList<Individual> bestIndividuals = nsga2event.getBestIndividuals();

            LinkedList<IndividualProject> bestProjects = new LinkedList<IndividualProject>();
            for (Individual individual : bestIndividuals)
            {
                bestProjects.add((IndividualProject) individual);
            }

            printBestProjects(bestProjects);
        }
    }

    /**
     * Prints the specified individuals.
     */
    private static void printBestProjects(LinkedList<IndividualProject> bestProjects)
    {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        NumberFormat precision = new DecimalFormat("#.00");
        if (bestProjects == null)
        {
            throw new IllegalArgumentException("'bestProjects' must not be null.");
        }

        IndividualProject[] array =
                bestProjects.toArray(new IndividualProject[bestProjects.size()]);
        Arrays.sort(array, new IndividualScheduleComparator());

        System.out.println();
        System.out.println("Number of offered solutions: " + bestProjects.size());

        for (int i = 0; i < array.length; i++)
        {
            System.out.print(" Cost: " + fmt.format(array[i].getFitnessValue(0)));
            System.out.print(" / Environmental Impact: " + precision.format(array[i].getFitnessValue(1)));
            System.out.println(" / Estimated Duration: " + array[i].getFitnessValue(2));
        }
        
    }

    private static class IndividualScheduleComparator implements Comparator<IndividualProject>
    {

        public int compare(IndividualProject individual1, IndividualProject individual2)
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

            if (individual1.getFitnessValue(2) < individual2.getFitnessValue(1))
            {
                return -1;
            }

            if (individual1.getFitnessValue(2) > individual2.getFitnessValue(1))
            {
                return 1;
            }
            return 0;
        }
    }
}
