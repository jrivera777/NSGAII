package NSGAII;

import java.io.Serializable;

public class Activity implements Serializable
{
    private static final long serialVersionUID = -8812875185888493699L;
    private String id;
    private String name;
    private double time;
    private double cost;
    private double EnvImpact;
    
    //Mark start and end of an ActivityGraph.
    public static final Activity START = new Activity("<START>", "<START>", 0, 0, 0);
    public static final Activity END = new Activity("<END>", "<END>", 0, 0, 0);

    public Activity(String id, String n, double t, double c, double EI)
    {
        this.id = id;
        name = n;
        time = t;
        cost = c;
        EnvImpact = EI;
    }
    
    public double getEnvImpact()
    {
        return EnvImpact;
    }

    public String getID()
    {
        return id;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public void setEnvImpact(double EnvImpact)
    {
        this.EnvImpact = EnvImpact;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getTime()
    {
        return time;
    }

    public void setTime(double time)
    {
        this.time = time;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Activity ");
        sb.append(id );
        sb.append(" -");
        sb.append(" Name=");
        sb.append(name);
        sb.append(" Time=");
        sb.append(time);
        sb.append(" Cost=");
        sb.append(cost);
        sb.append(" EI=");
        sb.append(EnvImpact);
        
        return sb.toString();
    }

    @Override
    //Automatically generated by Netbeans.
    public int hashCode()
    {
        int hash = 5;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.cost) ^ (Double.doubleToLongBits(this.cost) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.EnvImpact) ^ (Double.doubleToLongBits(this.EnvImpact) >>> 32));
        return hash;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Activity))
            throw new IllegalArgumentException("Object must be matching type.");
        
        Activity rhs = (Activity) other;
        
        boolean same = (this.cost == rhs.cost) 
                && (this.EnvImpact == rhs.EnvImpact) 
                && (this.time == rhs.time)
                && (this.id.equals(rhs.id));
        
        return same;
    }
}
