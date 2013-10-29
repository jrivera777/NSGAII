package ProjectOptimization;
/**
 * 
 * @author Joseph Rivera
 */
public class Assembly
{

    //Mark start and end of an ActivityGraph.
    public static final Assembly START = new Assembly("START", "", 0, 0, 0);
    public static final Assembly END = new Assembly("END", "", 0, 0, 0);
    
    private String name;
    private String Category;
    private String code;
    private double cost;
    private double co2;
    private double duration;

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double duration)
    {
        this.duration = duration;
    }
    
    public double getCo2()
    {
        return co2;
    }

    public void setCo2(double co2)
    {
        this.co2 = co2;
    }

    public String getCategory()
    {
        return Category;
    }

    public void setCategory(String Category)
    {
        this.Category = Category;
    }
    
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
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

    public Assembly()
    {
    }

    public Assembly(String nm, String cd, double cost, double co2, double duration)
    {
        name = nm;
        code = cd;
        this.cost = cost;
        this.co2 = co2;
        this.duration = duration;
    }
    
    public String toString()
    {
       StringBuilder sb = new StringBuilder(this.name + " - ");
       //sb.append(this.code + ": ");
       sb.append(this.cost + ", ");
       sb.append(this.co2 + ", ");
       sb.append(this.duration);
       return sb.toString();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other == null || !(other instanceof Assembly))
            return false;
        
        Assembly that = (Assembly)other;
        return this.name.equals(that.name); //&& this.code.equals(that.code);
    }
}
