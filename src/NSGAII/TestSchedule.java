package NSGAII;


import NSGAII.ActivityList;
import NSGAII.Schedule;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jrive034
 */
public class TestSchedule
{

    public static void main(String[] args)
    {
        
        System.out.println(args.length);
//        ActivityList list = new ActivityList();
//        list.loadActivities("src\\activities.txt");
//        
//        Schedule s = new Schedule("src\\associations.txt", list);
//        
//        System.out.println("Time: " + s.TimeObjective());
//        System.out.println("Cost: " + s.CostObjective());
//        System.out.println("EI: " + s.EIObjective());
        
        
        
//        Activity start = new Activity("<START>", 0, 0, 0);
//        Activity a1 = new Activity("a1", 10, 100, 100);
//        Activity a2A = new Activity("a2A", 10, 100, 500);
//        Activity a2B = new Activity("a2B", 5, 100, 100);
//        Activity a3 = new Activity("a3", 5, 50, 100);
//        Activity a1Alt = new Activity("a1", 15, 100, 100);
//        Activity finish = new Activity("<END>", 0, 0, 0);
//
//        ActivityGraph g = new ActivityGraph();
//
//        g.addEdge(start, a1, 0);
//        g.addEdge(a1, a2A, a1.getTime());
//        g.addEdge(a1, a2B, a1.getTime());
//        g.addEdge(a2A, a3, a2A.getTime());
//        g.addEdge(a2B, a3, a2B.getTime());
//        g.addEdge(a3, finish, a3.getTime());
//
//
//
//        Activity b1 = new Activity("a1", 10, 100, 100);
//        Activity b2A = new Activity("a2A", 5, 100, 500);
//        Activity b2B = new Activity("a2B", 5, 100, 100);
//        Activity b3 = new Activity("a3", 5, 50, 100);
//        Activity b1Alt = new Activity("a1", 15, 100, 100);
//
//
//        ActivityGraph g2 = new ActivityGraph();
//
//        g2.addEdge(start, b1, 0);
//        g2.addEdge(b1, b2A, b1.getTime());
//        g2.addEdge(b2B, b3, b2B.getTime());
//        g2.addEdge(b2A, b3, b2A.getTime());
//        g2.addEdge(b1, b2B, b1.getTime());
//        g2.addEdge(b3, finish, b3.getTime());
//
//        Schedule s = new Schedule(g);
//        Schedule s2 = new Schedule(g2);
//
//        for (Activity a : s.getActivities())
//        {
//            System.out.println(a.getID() + ": " + a.getTime());
//        }
//        System.out.println("");
//        for (Activity b : s2.getActivities())
//        {
//            System.out.println(b.getID() + ": " + b.getTime());
//        }
//
//        System.out.println("");
//
//
//        System.out.println("");
//        System.out.println("Time: " + s.TimeObjective());
//        System.out.println("Cost: " + s.CostObjective());
//        System.out.println("EI: " + s.EIObjective());
    }
}
