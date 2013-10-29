package ProjectOptimization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Joseph Rivera
 */
public class GAResultWriter {

    public static void WriteOutResultsText(LinkedList<IndividualProject> results, String fileName)
    {
        try
        {
            PrintWriter out = new PrintWriter(new File(fileName));
            for(IndividualProject indv : results)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(indv.getFitnessValue(0));
                sb.append(",");
                sb.append(indv.getFitnessValue(1));
                sb.append(",");
                sb.append(indv.getFitnessValue(2));
                out.println(sb.toString());
            }
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
    public static void WriteOutResultsXML(LinkedList<IndividualProject> results, String fileName)
    {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Results");
            doc.appendChild(rootElement);
            int count = 0;
            for(IndividualProject indv : results)
            {
                Element proj = doc.createElement("Project");
                rootElement.appendChild(proj);
                
                Attr att = doc.createAttribute("id");
                att.setValue("Project " + ++count);
                proj.setAttributeNode(att);
                
                Element projCost = doc.createElement("ProjectCost");
                projCost.appendChild(doc.createTextNode(String.valueOf(indv.getFitnessValue(0))));
                proj.appendChild(projCost);
                
                Element projC02 = doc.createElement("ProjectC02");
                projC02.appendChild(doc.createTextNode(String.valueOf(indv.getFitnessValue(1))));
                proj.appendChild(projC02);
                
                Element projDur = doc.createElement("ProjectDuration");
                projDur.appendChild(doc.createTextNode(String.valueOf(indv.getFitnessValue(2))));
                proj.appendChild(projDur);
                
                for(Map.Entry<String, Assembly> entry : indv.getCurrentAssemblies().entrySet())
                {
                    String componentName = entry.getKey();
                    Assembly assem = entry.getValue();
                    
                    Element currAssem = doc.createElement("Component");
                    proj.appendChild(currAssem);
                    
                    Attr compAtt = doc.createAttribute("id");
                    compAtt.setValue(componentName);
                    currAssem.setAttributeNode(compAtt);
                    
                    Element name = doc.createElement("AssemblyName");
                    name.appendChild(doc.createTextNode(assem.getName()));
                    currAssem.appendChild(name);
                    
                    Element cost = doc.createElement("AssemblyCost");
                    cost.appendChild(doc.createTextNode(String.valueOf(assem.getCost())));
                    currAssem.appendChild(cost);
                    
                    Element co2 = doc.createElement("AssemblyCO2");
                    co2.appendChild(doc.createTextNode(String.valueOf(assem.getCo2())));
                    currAssem.appendChild(co2);
                    
                    Element dur = doc.createElement("AssemblyDuration");
                    dur.appendChild(doc.createTextNode(String.valueOf(assem.getDuration())));
                    currAssem.appendChild(dur);
                }
            }
            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            
            transformer.transform(source, result);
        }
        catch (ParserConfigurationException pce) 
        {
		pce.printStackTrace();
        } 
        catch (TransformerException tfe) 
        {
		tfe.printStackTrace();
        }
    }
}
