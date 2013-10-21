package ProjectOptimization;

import IDF.POption;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import NSGAII.NSGA2;
import java.io.File;
import java.util.*;

/**
 *
 * <p>Represents an individual Project. Each project has a list of assemblies.
 * The Cost and Environmental Impact (EI) of a project is determined by its
 * assemblies.</p>
 */
public class IndividualProject extends Individual
{

    private double[] fitnessValues;
    private AssemblySet assemSet; //Set of components and their available options
    private Map<String, Assembly> currentAssemblies; //Component ==> Currently selected Assembly
    private Map<String, List<POption>> parametrics;
    private ComponentGraph currentOrder; //Graph of current Assemblies to calculate IndividualProject duration
    private ArrayList<Precedence> precedence;
    private File energyDirectory;

    public Map<String, Assembly> getCurrentAssemblies()
    {
        return currentAssemblies;
    }

    public void setCurrentAssemblies(HashMap<String, Assembly> currentAssemblies)
    {
        this.currentAssemblies = currentAssemblies;
    }

    public IndividualProject(NSGA2 nsga)
    {
        super(nsga);
    }

    public IndividualProject(NSGA2 nsga, AssemblySet aSet, ArrayList<Precedence> order)
    {
        super(nsga);
        assemSet = aSet;
        precedence = order;
        currentAssemblies = generateRandomProject();
        currentOrder = generateComponentGraph(currentAssemblies, precedence);
        fitnessValues = new double[nsga2.getNSGA2Configuration().getNumberOfObjectives()];
        for (int i = 0; i < fitnessValues.length; i++)
        {
            fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
        }

    }

    public IndividualProject(NSGA2 nsga, AssemblySet aSet, ArrayList<Precedence> order, Map<String, List<POption>> paras, File eDir)
    {
        super(nsga);
        assemSet = aSet;
        precedence = order;
        currentAssemblies = generateRandomProject();
        currentOrder = generateComponentGraph(currentAssemblies, precedence);
        parametrics = paras;
        energyDirectory = eDir;
        fitnessValues = new double[nsga2.getNSGA2Configuration().getNumberOfObjectives()];
        for (int i = 0; i < fitnessValues.length; i++)
        {
            fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
        }

    }

    public IndividualProject(NSGA2 nsga, AssemblySet aSet, Map<String, Assembly> currAssemblies)
    {
        super(nsga);
        assemSet = aSet;
        currentAssemblies = currAssemblies;
        currentOrder = generateComponentGraph(currAssemblies, precedence);
        fitnessValues = new double[nsga2.getNSGA2Configuration().getNumberOfObjectives()];
        for (int i = 0; i < fitnessValues.length; i++)
        {
            fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
        }
    }

    public Map<String, List<POption>> getParametrics()
    {
        return parametrics;
    }

    public void setParametrics(Map<String, List<POption>> paras)
    {
        parametrics = paras;
    }

    public File getEnergyDirectory()
    {
        return energyDirectory;
    }

    public void setEnergyDirectory(File ed)
    {
        energyDirectory = ed;
    }

    private Map<String, Assembly> generateRandomProject()
    {
        HashMap<String, Assembly> map = new HashMap<String, Assembly>();
        Random rand = new Random();
        HashMap<String, ArrayList<Assembly>> options = assemSet.getOptionSet();
        for (String str : options.keySet())
        {
            ArrayList<Assembly> assems = options.get(str);
            map.put(str, assems.get(rand.nextInt(assems.size())));
        }
        return map;
    }

    private ComponentGraph generateComponentGraph(Map<String, Assembly> assems, ArrayList<Precedence> precs)
    {
        ComponentGraph g = new ComponentGraph();
        for (Precedence p : precs)
        {
            if (p.getPredecessor().trim().equalsIgnoreCase(Assembly.START.getName()))
            {
                //edge between start and first component does not affect time
                Assembly prev = assems.get(p.getAssemName());
                Assembly to = assems.get(p.getSuccessor());

                g.addEdge(Assembly.START, prev, 0);
                if (!g.containsEdge(prev, to))
                    g.addEdge(prev, to, prev.getDuration());
            }
            else if (p.getSuccessor().trim().equalsIgnoreCase(Assembly.END.getName()))
            {
                Assembly curr = assems.get(p.getAssemName());
                Assembly from = assems.get(p.getPredecessor());
                //g.addEdge(prev, curr, prev.getDuration());
                g.addEdge(curr, Assembly.END, curr.getDuration());
                if (!g.containsEdge(from, curr))
                    g.addEdge(from, curr, from.getDuration());
            }
            else
            {
                Assembly curr = assems.get(p.getAssemName());
                Assembly from = assems.get(p.getPredecessor());
                Assembly to = assems.get(p.getSuccessor());

                if (!g.containsEdge(from, curr))
                    g.addEdge(from, curr, from.getDuration());
                if (!g.containsEdge(curr, to))
                    g.addEdge(curr, to, curr.getDuration());
            }
        }

        return g;
    }

    @Override
    protected Individual createClonedIndividual()
    {
        Individual clone = new IndividualProject(nsga2, assemSet, precedence,
                parametrics, energyDirectory);
        return clone;
    }

    @Override
    /**
     *
     * <p>Mutation function randomly changes single genes in the chromosome.
     * Each gene is an option for a given activity. The probably of a new option
     * being selected for any gene is given by the defined MutationProbablity in
     * the NSGAConfiguration object.</p>
     */
    protected void mutate()
    {
        boolean mutated = false;
        Random rand = new Random();

        for (Map.Entry<String, Assembly> entry : currentAssemblies.entrySet())
        {
            if (rand.nextDouble() <= nsga2.getNSGA2Configuration().getMutationProbability())
            {
                Assembly toReplace = entry.getValue();
                ArrayList<Assembly> options = assemSet.getOptionSet().get(entry.getKey());

                if (options.size() <= 1)
                    break;
                Assembly replacement = options.get(rand.nextInt(options.size()));
                while (toReplace.equals(replacement))
                    replacement = options.get(rand.nextInt(options.size()));
                entry.setValue(replacement);

                //System.out.printf("Name: %s - Category: %s\n", replacement.getName(), replacement.getCategory());

                mutated = true;
            }
        }
        if (mutated)
        {
            // update fitness values
            for (int i = 0; i < fitnessValues.length; i++)
            {
                fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
            }
        }
    }

    @Override
    protected void crossover(Individual otherIndividual)
    {


        if (otherIndividual == null)
        {
            throw new IllegalArgumentException("'otherIndividual' must not be null.");
        }
        if (!(otherIndividual instanceof IndividualProject))
        {
            throw new IllegalArgumentException("Must be IndividualProject.");
        }

        IndividualProject otherProj = (IndividualProject) otherIndividual;

        if (nsga2 != otherProj.nsga2)
        {
            throw new IllegalArgumentException("Both individuals must belong to the same NSGA-II instance.");
        }

        Random rand = new Random();
        if (rand.nextDouble() < nsga2.getNSGA2Configuration().getCrossoverProbability())
        {
            // crossover in front of 'randomIndex'
            int randomIndex = rand.nextInt(currentAssemblies.size());
            int currIndex = 0;
            for (Map.Entry<String, Assembly> entry : currentAssemblies.entrySet())
            {
                if (currIndex >= randomIndex)
                    break;

                //Swap
                Assembly toSwap = entry.getValue();
                entry.setValue(otherProj.getCurrentAssemblies().get(entry.getKey()));

                otherProj.getCurrentAssemblies().put(entry.getKey(), toSwap);
                currIndex++;
            }


            //update fitness values
            for (int i = 0; i < fitnessValues.length; i++)
            {
                FitnessFunction fitnessFunction = nsga2.getNSGA2Configuration().getFitnessFunction(i);
                fitnessValues[i] = fitnessFunction.evaluate(this);
                otherProj.fitnessValues[i] = fitnessFunction.evaluate(otherProj);
            }
        }
    }

    public ComponentGraph getCurrentOrder()
    {
        return currentOrder;
    }

    private void printIndividual()
    {
        System.out.printf("Number of Components: %s\n\n", currentAssemblies.size());
        for (Map.Entry<String, Assembly> entry : currentAssemblies.entrySet())
            System.out.println(entry.getKey() + " ==> " + entry.getValue().toString());

        System.out.println("\nEstimated Project Cost: " + this.fitnessValues[0]);
        System.out.println("Estimated Project EI: " + this.fitnessValues[1]);
        System.out.println("Estimated Project Duration: " + this.fitnessValues[2]);

        System.out.println();
        this.getCurrentOrder().printPath("END");
    }
    private static final double MUTATION_PROBABILITY = 0.05;  // A much higher mutation rate seems to have a negative effect!
    private static final double CROSSOVER_PROBABILITY = 0.9;
    private static final int POPULATION_SIZE = 40;
    private static final int NUMBER_OF_GENERATIONS = 100;
    private static final double DIFFERENCE_THRESHOLD = .10;

    public static HashMap<String, Assembly> testAssemblies()
    {
        HashMap<String, Assembly> map = new HashMap<String, Assembly>();

        map.put("Footing", new Assembly("Footing 2", "", 11010.45, 10764.6348, 2));
        map.put("Stem Wall", new Assembly("Stem Wall Construction 3", "", 9713.78, 8616.6189, 1));
        map.put("Subgrade Insulation", new Assembly("Subgrade Insulation 1", "", 337.49, 112.4346, 1));
        map.put("Slab-on-Grade Construction", new Assembly("Slab-on-Grade Construction 2", "", 7758.80, 8696.2009, 1));
        map.put("Exterior Wall Construction", new Assembly("Exterior Wall Construction 2", "", 400.88, 6.1134, 1));
        map.put("Roof Truss Construction", new Assembly("Roof Truss Construction 1", "", 0, 0, 0));
        map.put("Interior Wall Framing", new Assembly("Interior Wall Framing 1", "", 205.44, 6.0251, 1));
        map.put("Interior Sheathing", new Assembly("Interior Sheathing 2", "", 1566.76, 863.0177, 2));
        map.put("Exterior wall Insulation ", new Assembly("Exterior wall Insulation 1", "", 5913.56, 1949.8478, 13));
        map.put("Exterior wall Sheathing", new Assembly("Exterior wall Sheathing 2", "", 3057.01, 1678.5044, 3));
        map.put("Roof Insulation", new Assembly("Roof Insulation 1", "", 3797.78, 1255.1357, 9));
        map.put("Roofing", new Assembly("Roofing 3", "", 6150.11, 8265.8170, 13));
        map.put("Flooring", new Assembly("Flooring 2", "", 29783.12, 53.7175, 9));
        map.put("Exterior Siding", new Assembly("Exterior Siding 2", "", 8953.44, 3056.9397, 4));


        return map;
    }

    @Override
    public double getFitnessValue(int index) throws IndexOutOfBoundsException
    {
        return fitnessValues[index];
    }
}
