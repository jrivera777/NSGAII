package ProjectOptimization;

/**
 *
 * @author fdot
 */
public class Precedence 
{
    private String assemName;
    private String predecessor;
    private String successor;
    
    public Precedence(String name, String pred, String succ)
    {
        assemName = name;
        predecessor = pred;
        successor = succ;
    }
    
    public String getAssemName()
    {
        return assemName;
    }

    public void setAssemName(String assemName)
    {
        this.assemName = assemName;
    }

    public String getPredecessor()
    {
        return predecessor;
    }

    public void setPredecessor(String predecessor)
    {
        this.predecessor = predecessor;
    }

    public String getSuccessor()
    {
        return successor;
    }

    public void setSuccessor(String successor)
    {
        this.successor = successor;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s - Pred: %s, Succ: %s\n", this.assemName, this.predecessor, this.successor);
    }
}
