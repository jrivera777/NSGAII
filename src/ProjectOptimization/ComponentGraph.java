package ProjectOptimization;

import NSGAII.PairingHeap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 
 * @author Joseph Rivera
 * 
 * Extended from Weiss.util.Graph, created by Dr. Mark Allen Weiss
 * 
 * Meant to hold associations between activities in a schedule.
 * Edge costs are the time of each Assembly until its successor.
 * This allows for a simple longest-path search to find the duration 
 * of a schedule. 
 */

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{

    public GraphException(String name)
    {
        super(name);
    }
}

// Represents an edge in the graph.
class Edge implements Serializable
{

    public Vertex dest;   // Second vertex in Edge
    public double cost;   // Edge cost

    public Edge(Vertex d, double c)
    {
        dest = d;
        cost = c;
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.
class Path implements Comparable<Path>, Serializable
{

    public Vertex dest;   // w
    public double cost;   // d(w)

    public Path(Vertex d, double c)
    {
        dest = d;
        cost = c;
    }

    @Override
    public int compareTo(Path rhs)
    {
        double otherCost = rhs.cost;

        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }
}

// Represents a vertex in the graph.
// Can hold an Assembly as well.
class Vertex implements Serializable
{

    public String name;   // Vertex name
    public List<Edge> adj;    // Adjacent vertices
    public double dist;   // Cost
    public Vertex prev;   // Previous vertex on shortest path
    public int scratch;// Extra variable used in algorithm
    public Assembly act;

    public Vertex(String nm)
    {
        name = nm;
        adj = new LinkedList<Edge>();
        reset();
    }

    public Vertex(String nm, Assembly act)
    {
        name = nm;
        adj = new LinkedList<Edge>();
        this.act = act;
        reset();
    }

    public void reset()
    {
        dist = ComponentGraph.INFINITY;
        prev = null;
        pos = null;
        scratch = 0;
    }
    public PairingHeap.Position<Path> pos;  // Used for dijkstra2 (Chapter 23)
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.
public class ComponentGraph implements Serializable
{
    private boolean isNegated = false;;
    public static final double INFINITY = Double.MAX_VALUE;
    private static final long serialVersionUID = 8470871277235368942L;
    private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();

    /**
     * Add a new edge to the graph.
     */
    public void addEdge(String sourceName, String destName, double cost)
    {
        Vertex v = getVertex(sourceName);
        Vertex w = getVertex(destName);
        v.adj.add(new Edge(w, cost));
    }

    /**
     * Add a new edge to the graph using an Assembly object.
     */
    public void addEdge(Assembly sourceName, Assembly destName, double cost)
    {
        Vertex v = getVertex(sourceName);
        Vertex w = getVertex(destName);
        v.adj.add(new Edge(w, cost));
    }
    
    public boolean containsEdge(Assembly source, Assembly dest)
    {
        Vertex v = getVertex(source.getName());
        Vertex w = getVertex(dest.getName());
        if(v == null || w == null)
            return false;
        for(Edge e : v.adj)
            if(e.dest.name.equals(dest.getName()) && e.cost == source.getDuration())
                return true;
        
        return false;
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */
    public void printPath(String destName)
    {
        Vertex w = vertexMap.get(destName);
        if (w == null)
        {
            throw new NoSuchElementException("Destination vertex not found");
        }
        else if (w.dist == INFINITY)
        {
            System.out.println(destName + " is unreachable");
        }
        else
        {
            System.out.print("(Cost is: " + w.dist + ") ");
            printPath(w);
            System.out.println();
        }
    }
    
    /*
     * Returns the absolute value of the total cost after shortest path algorithm is run.
     */
    public double getTotalCostAbs(String destName)
    {
        Vertex w = vertexMap.get(destName);
        double cost = 0;
        if (w == null)
        {
            throw new NoSuchElementException("Destination vertex not found");
        }
        else if (w.dist == INFINITY)
        {
            System.out.println(destName + " is unreachable");
        }
        else
        {
            cost = Math.abs(w.dist);
        }
        return cost;
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private Vertex getVertex(String vertexName)
    {
        Vertex v = vertexMap.get(vertexName);
        if (v == null)
        {
            v = new Vertex(vertexName);
            vertexMap.put(vertexName, v);
        }
        return v;
    }

    /**
     * If vertexName is not present, add it to vertexMap. Uses activities.
     * In either case, return the Vertex.
     */
    private Vertex getVertex(Assembly a)
    {
        Vertex v = vertexMap.get(a.getName());
        if (v == null)
        {
            v = new Vertex(a.getName(), a);
            vertexMap.put(a.getName(), v);
        }
        return v;
    }
    
    private Vertex getNullableVertex(Assembly a)
    {
        return vertexMap.get(a.getName());
    }

    //Attempt to replace old Assembly with alternate Assembly
    //Names should match, but data values will likely be different.
    public void replaceAssembly(Assembly orig, Assembly alt)
    {
        Vertex v = vertexMap.get(orig.getName());
        if (v == null)
        {
            throw new NoSuchElementException("Assembly not found.");
        }
        //Replace vertex and update adjacent edge costs
        v.act = alt;
        int count = 0;
        for (Edge e : v.adj)
        {
            count++;
            e.cost = Math.abs(v.act.getDuration()) * -1;
        }
        vertexMap.put(v.name, v);
    }

    /*
     * Retreive an Assembly by its id. Returns null when no matching
     * id is found in the graph.
     */
    public Assembly getAssembly(String id)
    {
        Vertex v = vertexMap.get(id);
        if (v == null)
            return null;
        
        return v.act;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
     */
    private void printPath(Vertex dest)
    {
        if (dest.prev != null)
        {
            printPath(dest.prev);
            System.out.print(" to ");
        }
        System.out.print(dest.name);
    }

    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll()
    {
        for (Vertex v : vertexMap.values())
        {
            v.reset();
        }
    }

    /**
     * Single-source unweighted shortest-path algorithm.
     */
    public void unweighted(String startName)
    {
        clearAll();

        Vertex start = vertexMap.get(startName);
        if (start == null)
        {
            throw new NoSuchElementException("Start vertex not found");
        }

        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(start);
        start.dist = 0;

        while (!q.isEmpty())
        {
            Vertex v = q.remove();

            for (Edge e : v.adj)
            {
                Vertex w = e.dest;
                if (w.dist == INFINITY)
                {
                    w.dist = v.dist + 1;
                    w.prev = v;
                    q.add(w);
                }
            }
        }
    }

    /**
     * Single-source weighted shortest-path algorithm.
     */
    public void dijkstra(String startName)
    {
        PriorityQueue<Path> pq = new PriorityQueue<Path>();

        Vertex start = vertexMap.get(startName);
        if (start == null)
        {
            throw new NoSuchElementException("Start vertex not found");
        }

        clearAll();
        pq.add(new Path(start, 0));
        start.dist = 0;

        int nodesSeen = 0;
        while (!pq.isEmpty() && nodesSeen < vertexMap.size())
        {
            Path vrec = pq.remove();
            Vertex v = vrec.dest;
            if (v.scratch != 0)  // already processed v
            {
                continue;
            }

            v.scratch = 1;
            nodesSeen++;

            for (Edge e : v.adj)
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if (cvw < 0)
                {
                    throw new GraphException("Graph has negative edges");
                }

                if (w.dist > v.dist + cvw)
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    pq.add(new Path(w, w.dist));
                }
            }
        }
    }

    /**
     * Single-source weighted shortest-path algorithm using pairing heaps.
     */
    public void dijkstra2(String startName)
    {
        PairingHeap<Path> pq = new PairingHeap<Path>();

        Vertex start = vertexMap.get(startName);
        if (start == null)
        {
            throw new NoSuchElementException("Start vertex not found");
        }

        clearAll();
        start.pos = pq.insert(new Path(start, 0));
        start.dist = 0;

        while (!pq.isEmpty())
        {
            Path vrec = pq.deleteMin();
            Vertex v = vrec.dest;

            for (Edge e : v.adj)
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if (cvw < 0)
                {
                    throw new GraphException("Graph has negative edges");
                }

                if (w.dist > v.dist + cvw)
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;

                    Path newVal = new Path(w, w.dist);
                    if (w.pos == null)
                    {
                        w.pos = pq.insert(newVal);
                    }
                    else
                    {
                        pq.decreaseKey(w.pos, newVal);
                    }
                }
            }
        }
    }

    /**
     * Single-source negative-weighted shortest-path algorithm.
     */
    public void negative(String startName)
    {
        clearAll();

        Vertex start = vertexMap.get(startName);
        if (start == null)
        {
            throw new NoSuchElementException("Start vertex not found");
        }

        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(start);
        start.dist = 0;
        start.scratch++;

        while (!q.isEmpty())
        {
            Vertex v = q.remove();
            if (v.scratch++ > 2 * vertexMap.size())
            {
                throw new GraphException("Negative cycle detected");
            }

            for (Edge e : v.adj)
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if (w.dist > v.dist + cvw)
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    // Enqueue only if not already on the queue
                    if (w.scratch++ % 2 == 0)
                    {
                        q.add(w);
                    }
                    else
                    {
                        w.scratch--;  // undo the enqueue increment    
                    }
                }
            }
        }
    }

    /**
     * Single-source negative-weighted acyclic-graph shortest-path algorithm.
     */
    public void acyclic(String startName)
    {
        Vertex start = vertexMap.get(startName);
        if (start == null)
        {
            throw new NoSuchElementException("Start vertex not found");
        }

        clearAll();
        Queue<Vertex> q = new LinkedList<Vertex>();
        start.dist = 0;

        // Compute the indegrees
        Collection<Vertex> vertexSet = vertexMap.values();
        for (Vertex v : vertexSet)
        {
            for (Edge e : v.adj)
            {
                e.dest.scratch++;
            }
        }

        // Enqueue vertices of indegree zero
        for (Vertex v : vertexSet)
        {
            if (v.scratch == 0)
            {
                q.add(v);
            }
        }

        int iterations;
        for (iterations = 0; !q.isEmpty(); iterations++)
        {
            Vertex v = q.remove();

            for (Edge e : v.adj)
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if (--w.scratch == 0)
                {
                    q.add(w);
                }

                if (v.dist == INFINITY)
                {
                    continue;
                }

                if (w.dist > v.dist + cvw)
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                }
            }
        }

        if (iterations != vertexMap.size())
        {
            throw new GraphException("Graph has a cycle!");
        }
    }

    /**
     * Negates all edges in graph.  Used for before running acyclic or negative
     * for longest path finding.
     */
    public void negateEdges()
    {
        if(!isNegated)
        {
            for (Vertex v : vertexMap.values())
            {
                for (Edge e : v.adj)
                {
                    e.cost = e.cost * -1;
                }
            }
            isNegated = true;
        }
    }

    //Returns a list of all activites present in the graph.
    public java.util.List<Assembly> getActivities()
    {
        java.util.List<Assembly> list = new java.util.ArrayList<Assembly>();
        for (Vertex v : vertexMap.values())
        {
            list.add(v.act);
        }
        return list;
    }
    
    public static void main(String[] args)
    {
        Assembly start = Assembly.START;
        Assembly end = Assembly.END;
        Assembly middle = new Assembly("test", "test", 1, 1, 1);
        
        ComponentGraph graph = new ComponentGraph();
        
        graph.addEdge(start, middle, 0);
        graph.addEdge(middle, end, middle.getDuration());
        System.out.println(graph.containsEdge(end, end));
        graph.dijkstra("START");
        graph.printPath("END");
    }
}

