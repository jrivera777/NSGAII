/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import IDF.POption;
import IDF.ParametricOptionReader;
import NSGAII.*;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFrame;

/**
 *
 * @author fdot
 */
public class ProjectTest
{

    static enum DebugMode
    {

        SIMPLE, FULL, COMMAND
    };

    static enum DisplayMode
    {

        DISPLAY, NO_DISPLAY
    };
    private static double MUTATION_PROBABILITY = 0.05;  // A much higher mutation rate seems to have a negative effect!
    private static final double CROSSOVER_PROBABILITY = 0.8;
    private static int POPULATION_SIZE = 200;
    private static int NUMBER_OF_GENERATIONS = 500;
    private static final double DIFFERENCE_THRESHOLD = .05;

    /**
     * Main method
     *
     * @param args arguments (not used)
     */
    public static void main(String[] args)
    {
        //create ActivityList of All Alternates
        // create NSGA-II instance
        String compFile, orderFile, poFile;
        compFile = orderFile = poFile = "";

        //===============DEBUGGING MODES===========//
        DebugMode dbgMode = DebugMode.SIMPLE;
        DisplayMode disMode = DisplayMode.NO_DISPLAY;
        //=========================================//

        switch (dbgMode)
        {
            case SIMPLE:
            {
                compFile = "testing.xml";
                orderFile = "testingorder.xml";
                break;
            }
            case FULL:
                compFile = "C:\\Documents and Settings\\fdot\\My Documents\\NetBeansProjects\\NSGAII\\SmartHouseComponents3_5_2013.xml";
                orderFile = "C:\\Documents and Settings\\fdot\\My Documents\\NetBeansProjects\\NSGAII\\SmartHouseOrder3_5_2013.xml";
                break;
            case COMMAND:
            {
                if (args.length < 2)
                {
                    System.out.println("Missing input. Try: <program> "
                            + "<componentsFile> <precedenceFile> <population size> "
                            + "<# of generations>\nOr <program> <componentsFile> <precedenceFile>");
                    System.exit(-1);
                }
                compFile = args[0];
                orderFile = args[1];
                if(args.length < 4)
                    System.out.println("Using default population = 200, # of generations = 500");
                else
                {
                    POPULATION_SIZE = Integer.parseInt(args[2]);
                    NUMBER_OF_GENERATIONS = Integer.parseInt(args[3]);
                    
                    if(POPULATION_SIZE % 4 != 0)
                    {
                        System.out.println("Population size must be divisible by 4.");
                        System.exit(-1);
                    }
                }
                if(args.length < 5)
                    System.out.println("No ParametricOptions file found. Energy simulation data will not be considered!");
                else
                {
                    
                }
                
                break;
            }
            default:
                return;
        }

        AssemblySet assemSet = new AssemblySet(compFile);
        MUTATION_PROBABILITY = 1 / assemSet.size();
        ProjectCostFitnessFunction fitnessFunction1 = new ProjectCostFitnessFunction();
        ProjectEnvironmentalImpactFitnessFunction fitnessFunction2 =
                new ProjectEnvironmentalImpactFitnessFunction();
        ProjectTimeFitnessFunction fitnessFunction3 = new ProjectTimeFitnessFunction();
        FitnessFunction[] fitnessFunctions = new FitnessFunction[3];
        fitnessFunctions[0] = fitnessFunction1;
        fitnessFunctions[1] = fitnessFunction2;
        fitnessFunctions[2] = fitnessFunction3;
        NSGA2Configuration conf = new NSGA2Configuration(fitnessFunctions,
                MUTATION_PROBABILITY,
                CROSSOVER_PROBABILITY,
                DIFFERENCE_THRESHOLD,
                POPULATION_SIZE,
                NUMBER_OF_GENERATIONS);
        NSGA2 nsga2 = new NSGA2(conf);
        nsga2.addNSGA2Listener(new ProjectNSGA2Listener());

        // create start population
        LinkedList<Individual> startPopulation = new LinkedList<Individual>();
        //create the set of all options for each assembly
        ArrayList<Precedence> order = ComponentOrderReader.ReadXml(orderFile);
        Map<String, List<POption>> parametrics = ParametricOptionReader.readParametricOptions(poFile);
        for (int i = 0; i < POPULATION_SIZE; i++)
        {
            IndividualProject indv = new IndividualProject(nsga2, assemSet, order);
            indv.setParametrics(parametrics);
            startPopulation.add(indv);
        }

        // start evolution
        LinkedList<Individual> bestIndividuals = nsga2.evolve(startPopulation);

        LinkedList<IndividualProject> bestProjects = new LinkedList<IndividualProject>();
        for (Individual individual : bestIndividuals)
        {
            bestProjects.add((IndividualProject) individual);
        }
        Collections.sort(bestProjects, new IndividualProjectComparator());

        System.out.println("\n========= BEST PROJECTS=========");
        printBestProjects(bestProjects);

        //LinkedList<IndividualProject> uniques = getUniqueProjects(bestProjects);

        System.out.println("\nUNIQUE PROJECTS:");
        //printBestProjects(uniques);

        switch (disMode)
        {
            case DISPLAY:
            {
                JFrame costEIFrame = NSGA2.DisplayCostVsEI(bestIndividuals, 12);
                JFrame timeEIFrame = NSGA2.DisplayTimeVsEI(bestIndividuals, 12);
                JFrame timeCostFrame = NSGA2.DisplayTimeVsCost(bestIndividuals, 12);
                JFrame frame3D = NSGA2.plot3DScatter(bestIndividuals, 12);

                while (costEIFrame.isVisible() || timeEIFrame.isVisible() || timeCostFrame.isVisible() || frame3D.isVisible())
                    continue;
            }
            case NO_DISPLAY:
                break;
            default:
                break;
        }


        int count = 1;
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
        String fileName = "C:\\Documents and Settings\\fdot\\Desktop\\Results\\results" + dateFormat.format(cal.getTime()) + "_Run_" + count++;
        String txtFile = fileName + ".txt";
        fileName = fileName + ".xml";
        File temp = new File(fileName);
        File txtTemp = new File(txtFile);
        while (temp.exists())
        {
            fileName = "C:\\Documents and Settings\\fdot\\Desktop\\Results\\results" + dateFormat.format(cal.getTime()) + "_Run_" + count++;
            txtFile = fileName + ".txt";
            fileName = fileName + ".xml";
            temp = new File(fileName);
        }
        GAResultWriter.WriteOutResultsXML(bestProjects, fileName);
        GAResultWriter.WriteOutResultsText(bestProjects, txtFile);

//        GAResultWriter.WriteOutResultsXML(uniques, fileName);
//        GAResultWriter.WriteOutResultsText(uniques, txtFile);

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
    private static void printBestProjects(LinkedList<IndividualProject> bestProjects)
    {
        //DecimalFormat fmt = new DecimalFormat("0000000.0000");
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        NumberFormat precision = new DecimalFormat("#.00");
        if (bestProjects == null)
        {
            throw new IllegalArgumentException("'bestSchedules' must not be null.");
        }

        // sort best schedules
        IndividualProject[] array =
                bestProjects.toArray(new IndividualProject[bestProjects.size()]);
        Arrays.sort(array, new IndividualProjectComparator());

        System.out.println();
        System.out.println("Number of offered solutions: " + bestProjects.size());

        for (int i = 0; i < array.length; i++)
        {
            System.out.print(" Cost: " + fmt.format(array[i].getFitnessValue(0)));
            System.out.print(" / Environmental Impact: " + precision.format(array[i].getFitnessValue(1)));
            System.out.println(" / Estimated Duration: " + array[i].getFitnessValue(2));
        }
    }

    private static LinkedList<IndividualProject> getUniqueProjects(List<IndividualProject> projs)
    {
        IndividualProjectComparator comparator = new IndividualProjectComparator();
        LinkedList<IndividualProject> uniqueProjs = new LinkedList<IndividualProject>();

        for (IndividualProject proj : projs)
        {
            boolean found = false;
            for (int i = 0; i < uniqueProjs.size(); i++)
            {
                if (comparator.compare(proj, uniqueProjs.get(i)) == 0)
                {
                    found = true;
                    break;
                }
            }
            if (!found)
                uniqueProjs.add(proj);
        }
        return uniqueProjs;
    }

    /**
     * This inner class implements a comparator for two schedule individuals.
     */
    private static class IndividualProjectComparator implements Comparator<IndividualProject>
    {

        /**
         * Compares the two specified schedule individuals.
         *
         * @param individual1 first individual
         * @param individual2 second individual
         * @return -1, 0 or 1 as the first individual is less than, equal to, or
         * greater than the second one
         */
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
