package IDF;

import java.util.ArrayList;
import java.util.List;

public class Zone
{

    private List<Surface> surfs;
    private String name;

    public Zone(String name)
    {
        this();
        this.name = name;
    }
    
    public Zone()
    {
        this.surfs = new ArrayList<Surface>();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Surface> getSurfaces()
    {
        return surfs;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Zone))
            throw new ClassCastException();

        Zone other = (Zone) obj;
        return this.name.equals(other.name);
    }
}
