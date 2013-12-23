package IDF;

import NSGAII.Individual;
import ProjectOptimization.IndividualProject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EnergySimParametricOptionReader extends ParametricOptionReader implements SimulationReader
{

    @Override
    public double getSimulationElectricity(Individual indv, String geneSequence)
    {
        double res = -1;
        boolean found = false;
        if (!(indv instanceof IndividualProject))
            throw new IllegalArgumentException("Must be an IndividualProject.");
        IndividualProject proj = (IndividualProject) indv;
        File eRes = proj.getEnergyResults();
        Scanner scan = null;
        try
        {
            scan = new Scanner(eRes);
            while (scan.hasNextLine())
            {
                // Entry format <gene sequence> : <electricity value>
                String[] line = scan.nextLine().split(":");
                if (line[0].trim().equals(geneSequence))
                {
                    res = Double.parseDouble(line[1].trim());
                    found = true;
                    break;
                }
            }
        }
        catch (IOException e)
        {
            return -1;
        }
        finally
        {
            if (scan != null)
                scan.close();
        }
        if (!found)
            throw new NoEnergyResultFoundException(String.format("Failed to find %s results", geneSequence));
        
        return res;
    }
}
