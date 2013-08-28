/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ComponentOrderReader
{

    public static ArrayList<Precedence> ReadXml(String fileName)
    {
        ArrayList<Precedence> order = null;
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
            if (list != null && list.getLength() > 0)
            {
                order = new ArrayList<Precedence>();
                for (int i = 0; i < list.getLength(); i++)
                {
                    Node wall = list.item(i);
                    if (wall.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element elem = (Element) wall;
                        NodeList componentName = elem.getElementsByTagName("Name");
                        Element componentNameElement = (Element) componentName.item(0);

                        NodeList componentCategory = elem.getElementsByTagName("Category");
                        Element componentCategoryElement = (Element) componentCategory.item(0);

                        NodeList predName = elem.getElementsByTagName("PredecessorName");
                        Element predNameElement = (Element) predName.item(0);

                        NodeList succName = elem.getElementsByTagName("SuccessorName");
                        Element succNameElement = (Element) succName.item(0);
                        
                        //Retrieve current component name.
                        NodeList textCompList = componentNameElement.getChildNodes();
                        String compName = ((Node) textCompList.item(0)).getNodeValue().trim();

                        //Retrieve current component Category
                        NodeList catList = componentCategoryElement.getChildNodes();
                        String compCategory = ((Node) catList.item(0)).getNodeValue().trim();
                        
                        //Retrieve current component predecessor.
                        NodeList predCompList = predNameElement.getChildNodes();
                        String pred = ((Node) predCompList.item(0)).getNodeValue().trim();

                        //Retrieve current component successor
                        NodeList succCompList = succNameElement.getChildNodes();
                        String succ = ((Node) succCompList.item(0)).getNodeValue().trim();

                        Precedence prec = new Precedence(compName, pred, succ);
                        order.add(prec);
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
        return order;
    }
    
    public static void main(String[] args)
    {
        ArrayList<Precedence> order = ComponentOrderReader.ReadXml("order.xml");
        
        for(Precedence p : order)
            System.out.println(p);
    }
}
