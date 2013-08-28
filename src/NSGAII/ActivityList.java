package NSGAII;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author Joseph Rivera
 * 
 * Represents a list of all alternatives grouped by ID.
 * 
 * e.g. ID=1 may have 3 alternative activities, each with different 
 * time, cost, and EI values.
 */
public class ActivityList
{
    private TreeMap<String, ArrayList<Activity>> altsByName;

    public ActivityList()
    {
        altsByName = new TreeMap();
    }

    public TreeMap<String, ArrayList<Activity>> getAltsByName()
    {
        return altsByName;
    }

    public ArrayList<Activity> getAlternates(String id)
    {
        return altsByName.get(id);
    }

    public Activity getActivity(String id, String name)
    {
        ArrayList<Activity> alts = altsByName.get(id);
        Activity result = null;
        for (Activity act : alts)
        {
            if (act.getName().equalsIgnoreCase(name))
            {
                result = act;
                break;
            }
        }
        return result;
    }

    /*
     * Retreive a randomly chosen activity alternative based on the id.
     *
     * e.g. If id 'Act-1' is given, a random alternative associated with
     * 'Act-1' will be returned, assuming one exists.
     */
    public Activity getRandomActivity(String id)
    {
        Random rand = new Random();
        ArrayList<Activity> list = altsByName.get(id);
        return list.get(rand.nextInt(list.size()));
    }

    /*
     * Read a file to populate the list of alternatives for every Activity in
     * the schedule.
     */
    public boolean loadActivities(String file)
    {
        boolean loaded = false;

        try
        {
            Scanner scan = new Scanner(new File(file));
            
            while (scan.hasNextLine())
            {
                Scanner reader = new Scanner(scan.nextLine());
                reader.useDelimiter(", *");


                String id = reader.next().trim();
                String name = reader.next().trim();
                double time = reader.nextDouble();
                double cost = reader.nextDouble();
                double ei = reader.nextDouble();

                ArrayList<Activity> acts = altsByName.get(id);

                if (acts == null)
                {
                    acts = new ArrayList<Activity>();
                }
                acts.add(new Activity(id, name, time, cost, ei));
                altsByName.put(id, acts);
            }
            loaded = true;
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Invalid File given.");
            e.printStackTrace();
        }

        return loaded;
    }

  
}
