package ProjectOptimization;

import IDF.POption;
import IDF.EnergySimParametricOptionReader;
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
 * @author Joseph Rivera
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
    private static int NUMBER_OF_GENERATIONS = 200;
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
        File eResults = null;
        File resultsDir = null;
        //===============DEBUGGING MODES===========//
        DebugMode dbgMode = DebugMode.COMMAND;
        DisplayMode disMode = DisplayMode.NO_DISPLAY;
        //=========================================//

        switch (dbgMode)
        {
            case SIMPLE:
            {
                //testing with UNT building and. Not meant to be accurate.
                compFile = "C:\\Documents and Settings\\fdot\\Desktop\\Parametric\\simple_test_comps.xml";
                orderFile = "C:\\Documents and Settings\\fdot\\Desktop\\Parametric\\simple_test_order.xml";
                poFile = "C:\\Documents and Settings\\fdot\\Desktop\\Parametric\\simple_test_options.xml";
                eResults = new File("C:\\Documents and Settings\\fdot\\Desktop\\Parametric\\Output\\output.txt");
                resultsDir = new File("C:\\Documents and Settings\\fdot\\Desktop\\Results");
                break;
            }
            case FULL:
                compFile = "C:\\Documents and Settings\\fdot\\My Documents\\NetBeansProjects\\NSGAII\\SmartHouseComponents3_5_2013.xml";
                orderFile = "C:\\Documents and Settings\\fdot\\My Documents\\NetBeansProjects\\NSGAII\\SmartHouseOrder3_5_2013.xml";
                break;
            case COMMAND:
            {
                if (args.length < 3)
                {
                    System.err.println("Missing input. Try: "
                            + "<program> <components file> <precedence file> "
                            + "<output directory>");
                    System.exit(-1);
                }
                compFile = args[0];
                orderFile = args[1];
                resultsDir = new File(args[2]);
                if (args.length < 5)
                    System.out.println("Using default population = 200, # of generations = 500");
                else
                {
                    POPULATION_SIZE = Integer.parseInt(args[3]);
                    NUMBER_OF_GENERATIONS = Integer.parseInt(args[4]);

                    if (POPULATION_SIZE % 4 != 0)
                    {
                        System.err.println("Population size must be divisible by 4.");
                        System.exit(-1);
                    }
                }
                if (args.length < 7)
                    System.err.println("No Parametric Options file and "
                            + "Energy directory found. Energy simulation data "
                            + "will not be considered!");
                else
                {
                    poFile = args[5];
                    eResults = new File(args[6]);
                }
                break;
            }
            default:
                return;
        }

        AssemblySet assemSet = new AssemblySet(compFile);
        FitnessFunction fitnessFunction1;
        FitnessFunction fitnessFunction2;
        FitnessFunction fitnessFunction3;

        fitnessFunction1 = eResults == null ? new CostFitnessFunction() : new CostESFitnessFunction();
        fitnessFunction2 = eResults == null ? new EnvironmentalImpactFitnessFunction() : new EnvironmentalImpactSMFitnessFunction();
        fitnessFunction3 = new ProjectTimeFitnessFunction();

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
        Map<String, List<POption>> parametrics = EnergySimParametricOptionReader.readParametricOptions(poFile);
        for (int i = 0; i < POPULATION_SIZE; i++)
        {
            IndividualProject indv;
            if (parametrics != null || eResults != null)
                indv = new IndividualProject(nsga2, assemSet, order, parametrics, eResults);
            else
                indv = new IndividualProject(nsga2, assemSet, order);

            startPopulation.add(indv);
        }


        System.out.println("Simulation Info");
        System.out.println("===============");
        System.out.printf("Population Siz :%d\n"
                + "# of Generations: %d\n"
                + "Mutation probability: %.2f\n"
                + "Crossover probability: %.2f\n", POPULATION_SIZE, NUMBER_OF_GENERATIONS,
                MUTATION_PROBABILITY, CROSSOVER_PROBABILITY);
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

        LinkedList<IndividualProject> uniques = getUniqueProjects(bestProjects);
        System.out.println("\nUNIQUE PROJECTS:");
        printBestProjects(uniques);

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
        String fileName = resultsDir.getPath() + "\\results" + dateFormat.format(cal.getTime()) + "_Run_" + count++;
        String txtFile = fileName + ".txt";
        fileName = fileName + ".xml";
        File temp = new File(fileName);
        File txtTemp = new File(txtFile);
        while (temp.exists())
        {
            fileName = resultsDir.getPath() + "\\results" + dateFormat.format(cal.getTime()) + "_Run_" + count++;
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
