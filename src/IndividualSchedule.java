
import NSGAII.ActivityList;
import NSGAII.Activity;
import NSGAII.Individual;
import NSGAII.NSGA2;
import NSGAII.Schedule;
import NSGAII.FitnessFunction;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * <p>Represents an individual Schedule.  Each schedule has a list
 * of activities. The Time, Cost, and Environmental Impact (EI) of a schedule 
 * is determined by its activities.</p>
 */
public class IndividualSchedule extends Individual implements Cloneable
{

    private Schedule sched;
    private double[] fitnessValues;
    private ActivityList activities;

    public IndividualSchedule(NSGA2 nsga, Schedule sch, ActivityList actList)
    {
        super(nsga);
        sched = sch;
        activities = actList;

        fitnessValues = new double[nsga2.getNSGA2Configuration().getNumberOfObjectives()];
        for (int i = 0; i < fitnessValues.length; i++)
        {
            fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
        }

    }

    public Schedule getSchedule()
    {
        return sched;
    }

    @Override
    protected Individual createClonedIndividual()
    {
        Individual clone = new IndividualSchedule(nsga2, sched, activities);
        return clone;
    }

    /*
     * Randomly swaps out activities with possible alternatives.
     * ASSUMPTION: Switching activies does NOT invalidate the integrity of the
     * schedule, i.e., the schedule structure does not change.
     */
    @Override
    protected void mutate()
    {
        boolean mutated = false;
        Random rand = new Random();

        for (int i = 0; i < sched.getActivities().size(); i++)
        {
            if (rand.nextDouble() < nsga2.getNSGA2Configuration().getMutationProbability())
            {
                Activity toReplace = sched.getActivities().get(i);
                if (toReplace.getID().equalsIgnoreCase("<START>") || toReplace.getID().equalsIgnoreCase("<END>"))
                {
                    continue;
                }
                //get alternates for current activity.
                ArrayList<Activity> listAlts = activities.getAlternates(toReplace.getID());
                int newActIndex = rand.nextInt(listAlts.size());

                //swap in alternate Activity
                Activity alt = listAlts.get(newActIndex);
                sched.ReplaceActivity(toReplace, alt);

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

    /*
     * Switches an activity from this schedule with an activity from the given
     * schedule.  They must be at the same "position" in the schedule structure
     * for this to work.
     * 
     * ASSUMPTION: Switching activies does NOT invalidate the integrity of the
     * schedules, i.e., the schedules' structures do not change. AND they must
     * be at the same "position". e.g. swap the first activity of schedule-1
     * with the first activity of schedule-2, assuming both have the same
     * structure.
     */
    @Override
    protected void crossover(Individual otherIndividual)
    {
        if (otherIndividual == null)
        {
            throw new IllegalArgumentException("'otherIndividual' must not be null.");
        }
        if (!(otherIndividual instanceof IndividualSchedule))
        {
            throw new IllegalArgumentException("Must be IndividualSchedule.");
        }

        IndividualSchedule otherSched = (IndividualSchedule) otherIndividual;

        if (nsga2 != otherSched.nsga2)
        {
            throw new IllegalArgumentException("Both individuals must belong to the same NSGA-II instance.");
        }

        Random rand = new Random();
        if (rand.nextDouble() < nsga2.getNSGA2Configuration().getCrossoverProbability())
        {
            // crossover in front of 'randomIndex'
            int randomIndex = rand.nextInt(sched.getActivities().size() + 1);

            for (int i = 0; i < randomIndex; i++)
            {
                Activity schedTemp = sched.getActivities().get(i);
                if (schedTemp.getID().equalsIgnoreCase("<START>") || schedTemp.getID().equalsIgnoreCase("<END>"))
                {
                    continue;
                }
                Activity otherTemp = otherSched.getSchedule().getActivities().get(i);

                if (!schedTemp.getID().equalsIgnoreCase(otherTemp.getID()))
                {
                    throw new IllegalArgumentException("Activity IDs must Match.");
                }

                //sched.getActivities().set(i, otherTemp);
                sched.ReplaceActivity(schedTemp, otherTemp);

                //otherSched.getSchedule().getActivities().set(i, schedTemp);
                otherSched.getSchedule().ReplaceActivity(otherTemp, schedTemp);

            }

            // update fitness values
            for (int i = 0; i < fitnessValues.length; i++)
            {
                FitnessFunction fitnessFunction = nsga2.getNSGA2Configuration().getFitnessFunction(i);
                fitnessValues[i] = fitnessFunction.evaluate(this);
                otherSched.fitnessValues[i] = fitnessFunction.evaluate(otherSched);
            }
        }
    }

    @Override
    public double getFitnessValue(int index) throws IndexOutOfBoundsException
    {
        return fitnessValues[index];
    }
}
