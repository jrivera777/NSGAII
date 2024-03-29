package IDF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
public class IDFTranscriber
{

    private String zoneFile;
    //private Map<String, List<Surface>> zonesToSurfaces;
    private List<Zone> zones;

    public IDFTranscriber(String fName)
    {
        zoneFile = fName;
        //zonesToSurfaces = new HashMap<String, List<Surface>>();
        zones = new ArrayList<Zone>();
        loadZones();
    }

    private Boolean loadZones()
    {
        boolean isLoaded = true;
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try
            {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(zoneFile);

                //root node
                Element docEle = dom.getDocumentElement();
                docEle.normalize();

                NodeList list = docEle.getElementsByTagName("Zone");
                if (list != null && list.getLength() > 0)
                {
                    for (int i = 0; i < list.getLength(); i++)
                    {
                        Node zone = list.item(i);
                        String zoneName = zone.getAttributes().item(0).getNodeValue().trim();
                        Zone nZone = new Zone(zoneName);
                        if (zone.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element elem = (Element) zone;

                            //get surface objects in zone
                            NodeList surfaces = elem.getElementsByTagName("Surface");
                            if (surfaces != null && surfaces.getLength() > 0)
                            {
                                for (int j = 0; j < surfaces.getLength(); j++)
                                {
                                    Node surface = surfaces.item(j);
                                    if (surface.getNodeType() == Node.ELEMENT_NODE)
                                    {
                                        Element surf = (Element) surface;
                                        String surfaceName = surf.getAttribute("id");
                                        String surfaceType = surf.getAttribute("type");

                                        Surface nSurf = new Surface(surfaceName);
                                        nSurf.setType(surfaceType);

                                        //get construction objects in surface
                                        NodeList constructs = surf.getElementsByTagName("Construction");
                                        if (constructs != null && constructs.getLength() > 0)
                                        {
                                            for (int k = 0; k < constructs.getLength(); k++)
                                            {
                                                Node construction = constructs.item(k);
                                                String constrName = construction.getChildNodes().item(0).getNodeValue().trim();
                                                nSurf.setconstruction(constrName);

                                                if (zones.contains(nZone))
                                                {
                                                    zones.get(zones.indexOf(nZone)).getSurfaces().add(nSurf);
                                                }
                                                else
                                                {
                                                    nZone.getSurfaces().add(nSurf);
                                                    zones.add(nZone);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            catch (ParserConfigurationException pce)
            {
                pce.printStackTrace();
            }
            catch (SAXException se)
            {
                se.printStackTrace();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        catch (Exception ex)
        {
            isLoaded = false;
        }
        return isLoaded;
    }

    public boolean replaceZoneInfo(String baseFile) throws IOException
    {
        boolean isReplaced = true;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try
        {
            reader = new BufferedReader(new FileReader(baseFile));
            String updatedFile = baseFile.substring(0, baseFile.length() - 4) + "_updated.idf";
            writer = new BufferedWriter(new FileWriter(updatedFile));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                if (line.toLowerCase().contains("buildingsurface:detailed,")) //we've found Surface information
                {
                    writer.write(line);
                    writer.newLine();

                    // next line should be the surface name
                    line = reader.readLine();
                    writer.write(line);
                    writer.newLine();

                    String surfaceName = line;

                    //Name excludes the comment AND the comma .
                    surfaceName = surfaceName.substring(0, surfaceName.lastIndexOf(",")).trim();
                    Zone newZone = getZone(surfaceName);

                    line = reader.readLine(); //ignore surface type for now
                    writer.write(line);
                    writer.newLine();
                    line = reader.readLine(); //get old construction name
                    String newConstruction = getConstruction(newZone, surfaceName);
                    if (!(newConstruction.equals("")))//found new contruction; update line
                    {
                        writer.write("\t" + newConstruction + ",\t!- Construction Name");
                        writer.newLine();
                    }
                    else
                    {
                        writer.write(line);
                        writer.newLine();
                    }
                    line = reader.readLine(); //get old zone

                    if (newZone != null) //found zone; update line
                    {
                        //write new zone instead.
                        writer.write("\t" + newZone.getName() + ",\t!- Zone Name");
                        writer.newLine();
                    }
                    else
                    {
                        writer.write(line); //write old zone
                        writer.newLine();
                    }
                }
                else
                {
                    writer.write(line);
                    writer.newLine();
                }
            }

            reader.close();
            writer.close();
            File old = new File(baseFile);
            File file = new File(updatedFile);

            //overwrite old file.  This way we only have one file which is used
            //in different energy simulations.
            if (file.exists() && old.exists())
            {
                old.delete();
                file.renameTo(old);
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
        }

        return isReplaced;
    }

    private Zone getZone(String surfName)
    {
        Surface surf = new Surface(surfName);
        for (int i = 0; i < zones.size(); i++)
            if (zones.get(i).getSurfaces().contains(surf))
                return zones.get(i);

        return null;
    }

    private String getConstruction(Zone zone, String surfName)
    {
        Surface surface = new Surface(surfName);
        List<Surface> surfs = zone.getSurfaces();
        if (surfs.contains(surface))
        {
            Surface hasConstr = surfs.get(surfs.indexOf(surface));
            return hasConstr.getConstruction();
        }

        return "";
    }
}
