package IDF;

import java.util.ArrayList;
import java.util.List;

public class Surface
{
    private String name;
    private String type;
    private String construction;
    private List<String> constructionOptions;

    public Surface(String name)
    {
        this();
        this.name = name;
    }
    
    public Surface()
    {
        this.constructionOptions = new ArrayList<String>();
    }

    public String getConstruction()
    {
        return construction;
    }

    public void setconstruction(String constr)
    {
        this.construction = constr;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Surface))
            throw new ClassCastException();

        Surface other = (Surface) obj;
        return this.name.equals(other.name);
    }
}
