package ProjectOptimization;

import java.util.ArrayList;
import java.util.HashMap;

public class AssemblySet 
{
    private HashMap<String, ArrayList<Assembly>> optionSet;

    public HashMap<String, ArrayList<Assembly>> getOptionSet()
    {
        return optionSet;
    }
    
    public AssemblySet(String fileName)
    {
        optionSet = AssemblyDataReader.ReadXml(fileName);
    }
    
    public int size()
    {
        return optionSet.size();
    }
}
