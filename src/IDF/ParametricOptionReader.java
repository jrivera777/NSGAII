package IDF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParametricOptionReader
{

    public static Map<String, List<POption>> readParametricOptions(String paraOptions)
    {
        if(paraOptions == null || paraOptions.equals(""))
            return null;
        Map<String, List<POption>> params = new TreeMap<String, List<POption>>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(paraOptions);

            Element docEle = dom.getDocumentElement();
            docEle.normalize();

            NodeList list = docEle.getElementsByTagName("ParametricOption");
            if (list != null && list.getLength() > 0)
            {
                for (int i = 0; i < list.getLength(); i++)
                {
                    Node pOpt = list.item(i);
                    String paramName = pOpt.getAttributes().getNamedItem("comp").getNodeValue().trim(); //component name

                    if (pOpt.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element elem = (Element) pOpt;
                        NodeList options = elem.getElementsByTagName("Option");
                        if (options != null && options.getLength() > 0)
                        {
                            for (int j = 0; j < options.getLength(); j++)
                            {
                                Node opt = options.item(j);
                                String optionName = opt.getChildNodes().item(0).getNodeValue().trim();
                                String value = opt.getAttributes().item(0).getNodeValue().trim();

                                List<POption> opts = params.get(paramName);
                                if (opts == null)
                                {
                                    opts = new ArrayList<POption>();
                                    opts.add(new POption(optionName, value));
                                    params.put(paramName, opts);
                                }
                                else
                                {
                                    opts.add(new POption(optionName, value));
                                    params.put(paramName, opts);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (ParserConfigurationException pce)
        {
            params = null;
            pce.printStackTrace();
        }
        catch (SAXException se)
        {
            params = null;
            se.printStackTrace();
        }
        catch (IOException ioe)
        {
            params = null;
            ioe.printStackTrace();
        }
        return params;
    }
}
