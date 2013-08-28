/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author fdot
 */
public class AssemblyDataReader 
{
    public static HashMap<String, ArrayList<Assembly>> ReadXml(String fileName)
    {
        HashMap<String, ArrayList<Assembly>> options = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(fileName);
            
            //root node
            Element docEle = dom.getDocumentElement();
            docEle.normalize();
            
//            System.out.println ("Root element of the doc is " + 
//                 docEle.getNodeName());
            NodeList list = docEle.getElementsByTagName("Component");
            if(list != null && list.getLength() > 0)
            {
                options = new HashMap<String, ArrayList<Assembly>>();
                for(int i = 0; i < list.getLength(); i++)
                {
                    Node wall = list.item(i);
                    if(wall.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element elem = (Element)wall;
                        NodeList componentName = elem.getElementsByTagName("Name");
                        Element componentNameElement = (Element)componentName.item(0);
                        
                        NodeList componentCategory = elem.getElementsByTagName("Category");
                        Element componentCategoryElement = (Element)componentCategory.item(0);

                        //Retrieve current component name.
                        NodeList textCompList = componentNameElement.getChildNodes();
                        String compName = ((Node)textCompList.item(0)).getNodeValue().trim();
                        
                        //Retrieve current component Category
                        NodeList catList = componentCategoryElement.getChildNodes();
                        String compCategory = ((Node)catList.item(0)).getNodeValue().trim();
                        
                        //Find all existing alternatives
                        NodeList alternatives = elem.getElementsByTagName("Alternative");
                        if(alternatives != null)
                        {
                            //build up all alternative assemblies for current component
                            for(int j = 0; j < alternatives.getLength(); j++)
                            {
                                Assembly assem = createAssembly((Element)alternatives.item(j));
                                assem.setCategory(compCategory);
                                
                                ArrayList<Assembly> alts = options.get(compName);
                                if(alts == null)
                                    alts = new ArrayList<Assembly>();
                                
                                alts.add(assem);
                                options.put(compName, alts);
                            }
                        }
                    }
                }
            }
        }
        catch(ParserConfigurationException pce) 
        {
			pce.printStackTrace();
        }
        catch(SAXException se) {
			se.printStackTrace();
	}
        catch(IOException ioe) 
        {
			ioe.printStackTrace();
        }
        return options;
    }
    
    private static Assembly createAssembly(Element elem)
    {
        Assembly assem = new Assembly();
        
        String name = ((Element)elem.getElementsByTagName("Name").item(0)).getChildNodes().item(0).getNodeValue().trim();
        if(name != null)
            assem.setName(name);
        
//        Element possibleCode = (Element)elem.getElementsByTagName("Code").item(0);
//        if(possibleCode != null)
//        {
//            String code = possibleCode.getChildNodes().item(0).getNodeValue().trim();
//            if(code != null)
//                assem.setCode(code);
//        }
        String cost = ((Element)elem.getElementsByTagName("TotalAssemblyCost").item(0)).getChildNodes().item(0).getNodeValue().trim();
        if(cost != null)
            assem.setCost(Double.parseDouble(cost));

        String co2 = ((Element)elem.getElementsByTagName("TotalAssemblyCO2").item(0)).getChildNodes().item(0).getNodeValue().trim();
        if(co2 != null)
            assem.setCo2(Double.parseDouble(co2));
        String duration = ((Element)elem.getElementsByTagName("EstimatedDuration").item(0)).getChildNodes().item(0).getNodeValue().trim();
        if(duration != null)
            assem.setDuration(Double.parseDouble(duration));
        return assem;
    }
    
    public static void main(String [] args)
    {
       HashMap<String, ArrayList<Assembly>> options = ReadXml("TestComponents.xml");
       
       for(Map.Entry<String, ArrayList<Assembly>> entry : options.entrySet())
       {
           System.out.println(entry.getKey() + ":");
           ArrayList<Assembly> opts = entry.getValue();
           printAssemblies(opts);
       }
    }
    
    public static void printAssemblies(List<Assembly> opts)
    {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        DecimalFormat decFmt = new DecimalFormat("0.000");
        for(Assembly assem : opts)
        {
            String output = String.format("Alternative Name: %s - Code: %s, "
                                        + "Total Cost: %s, Total CO2: %s, "
                                        + "Estimated Duration: %s",
                                            assem.getName(), assem.getCode(), 
                                            fmt.format(assem.getCost()), 
                                            decFmt.format(assem.getCo2()), assem.getDuration());
            System.out.println(output);
        }
    }
}
