package IDF;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author fdot
 */
public class Test_IDFTranscriber
{

    public static void main(String[] args)
    {
        try
        {
            IDFTranscriber scribe = new IDFTranscriber("H:\\surface2zone.xml");
            scribe.replaceZoneInfo("H:\\NetZerolab.idf");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
