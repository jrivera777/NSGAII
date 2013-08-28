package NSGAII;


/**
 *
 * @author Joseph Rivera
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Schedule implements Serializable
{
    private static final long serialVersionUID = 2078339510272473758L;
    private static long ID = 0;
    private long scheduleID;

    public long getScheduleID()
    {
        return scheduleID;
    }

    public void setScheduleID(long scheduleID)
    {
        this.scheduleID = scheduleID;
    }

    public String getStartActivity()
    {
        return startActivity;
    }

    public void setStartActivity(String startActivity)
    {
        this.startActivity = startActivity;
    }
    private ActivityGraph associations;
    private List<Activity> activities;
    private String startActivity;

    //Take in built ActivityGraph
    public Schedule(ActivityGraph assoc)
    {
        if (assoc == null)
        {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        associations = assoc;
        activities = assoc.getActivities();
        scheduleID = ID++;
    }

    /**
     * Build  random ActivityGraph with given associations and activity list
     */
    public Schedule(String file, ActivityList acts)
    {
        ActivityGraph g = buildRandomActivityGraph(file, acts);
        if (g == null)
        {
            throw new NullPointerException("Failed to build ActivityGraph.");
        }
        associations = g;
        activities = associations.getActivities();
        scheduleID = ID++;
    }

    /*
     * Generates an ActivityGraph based on the given association file.
     * Random alternatives are chosen for each Activity.
     */
    private ActivityGraph buildRandomActivityGraph(String file, ActivityList acts)
    {
        ActivityGraph g = new ActivityGraph();

        try
        {
            Scanner scan = new Scanner(new File(file));

            while (scan.hasNextLine())
            {
                String line = scan.nextLine();
                Scanner reader = new Scanner(line);
                reader.useDelimiter(", *");

                String id = reader.next().trim();
                String prevName = reader.next().trim();
                String nextId = reader.next().trim();


                Activity from = null;
                Activity to = null;
                Activity prev = null;
                if (prevName.equalsIgnoreCase(Activity.START.getName()))
                {
                    startActivity = id;

                   //from = to;
                   from = acts.getRandomActivity(id);
                   prev = g.getActivity(nextId);
                   
                   if(prev == null)
                       to = acts.getRandomActivity(nextId);
                   else
                       to = prev;
                }
                else if (nextId.equalsIgnoreCase(Activity.END.getName()))
                {
                    prev = g.getActivity(id);
                    if (prev == null)
                        from = acts.getRandomActivity(id);
                    else
                        from = prev;
                    to = Activity.END;
                }
                else
                {
                    prev = g.getActivity(id);
                    if(prev == null)
                       from = acts.getRandomActivity(id);
                    else
                        from = prev;
                    
                    prev = g.getActivity(nextId);
                    if(prev == null)
                        to = acts.getRandomActivity(nextId);
                    else
                        to = prev;
                }
                g.addEdge(from, to, from.getTime()* -1);
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("Invalid File given.");
            e.printStackTrace();
        }

        return g;
    }

    public List<Activity> getActivities()
    {
        return activities;
    }

    public ActivityGraph getAssociations()
    {
        return associations;
    }

    public void ReplaceActivity(Activity orig, Activity alt)
    {
        if (alt == null || orig == null)
        {
            throw new IllegalArgumentException("Activities cannot be null.");
        }
        if (!activities.contains(orig))
        {
            throw new NoSuchElementException("Activity not found.");
        }

        associations.replaceActivity(orig, alt);
        //activities.set(activities.indexOf(orig), alt);
        activities = associations.getActivities();

    }

    public Schedule()
    {
        associations = new ActivityGraph();
    }
    
    /**The START and END Nodes have no values, but mark the beginning 
     * and end of the schedule.*/
    public double TimeObjective()
    {
        //Negate for longest path algorithm
        //associations.negateEdges();
        //associations.acyclic("<START>");
        associations.negative(startActivity);
        double time = associations.getTotalCostAbs(Activity.END.getName());
        
        //put edges back to normal
        //associations.negateEdges();
        return time;
    }

    public double CostObjective()
    {
        double total = 0;
        for (Activity a : activities)
        {
            total += a.getCost();
        }
        return total;
    }

    public double EIObjective()
    {
        double total = 0;
        for (Activity a : activities)
        {
            total += a.getEnvImpact();
        }
        return total;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.scheduleID);
        sb.append(", ");
        sb.append(this.TimeObjective());
        sb.append(", ");
        sb.append(this.CostObjective());
        sb.append(", ");
        sb.append(this.EIObjective());
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Schedule))
            throw new IllegalArgumentException("Object must be matching type.");
        
        Schedule rhs = (Schedule) other;
        
        boolean same = this.scheduleID == rhs.scheduleID 
                && this.TimeObjective() == rhs.TimeObjective()
                && this.CostObjective() == rhs.CostObjective()
                && this.EIObjective() == rhs.EIObjective();
        
        return same;
    }

    @Override
    //Auto-generated by NetBeans
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (int) (this.scheduleID ^ (this.scheduleID >>> 32));
        hash = 23 * hash + (this.startActivity != null ? this.startActivity.hashCode() : 0);
        return hash;
    }
    
}
