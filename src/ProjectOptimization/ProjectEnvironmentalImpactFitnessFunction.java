/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjectOptimization;

import IDF.POption;
import NSGAII.FitnessFunction;
import NSGAII.Individual;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author fdot
 */
public class ProjectEnvironmentalImpactFitnessFunction implements FitnessFunction
{

    @Override
    public double evaluate(Individual indv)
    {
        if (indv == null)
        {
            throw new IllegalArgumentException("Individual must not be null.");
        }
        if (!(indv instanceof IndividualProject))
        {
            throw new IllegalArgumentException("Individual must be of type IndividualSchedule.");
        }

        IndividualProject projIndv = (IndividualProject) indv;

        double EI = 0.0;
        StringBuilder geneSequence = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Assembly> entry : projIndv.getCurrentAssemblies().entrySet())
        {
            EI += entry.getValue().getCo2();
            if (projIndv.getParametrics() != null)
            {
                String assemName = "";
                List<POption> opts = projIndv.getParametrics().get(entry.getKey());
                for (POption opt : opts)
                {
                    assemName = entry.getValue().getName();
                    if (opt.getName().equalsIgnoreCase(assemName))
                    {
                        geneSequence.append(opt.getValue());
                        if (count++ != projIndv.getCurrentAssemblies().size() - 1)
                            geneSequence.append("-");
                    }
                }
            }
        }
        double elecJ = 0;
        double gasJ = 0;
        geneSequence.append(".csv");
        String gs = geneSequence.toString();
        //update EI based on simulation results
        File eDir = projIndv.getEnergyDirectory();
        if (!eDir.isDirectory())
            System.out.printf("%s is not a directory! Excluding Energy Simulation data!!!\n", eDir.getName());
        else
        {
            File[] files = eDir.listFiles();
            for (File f : files)
            {
                if (gs.equalsIgnoreCase(f.getName()))
                {
                    try
                    {
                        Scanner lineScan = new Scanner(f);
                        //Assumptions: columns 2 and 17 contain the
                        //desired values: electricity:facility(RunPeriod)
                        //and Gas:Facility(RunPeriod).
                        //Both are in Joules.
                        //Only the last row (December) contains a useful value
                        for (int i = 0; i < 12; i++)
                            lineScan.nextLine(); //disgard
                        String[] data = lineScan.nextLine().split(",");
                        elecJ = Double.parseDouble(data[1]);
                        gasJ = Double.parseDouble(data[16]);
                    }
                    catch (Exception e)
                    {
                        return EI;
                    }
                    break;
                }
            }
            //convert data to CO2 equivalent in lbs.
            //conversion factor based off of:
            //http://www.epa.gov/cleanenergy/documents/egridzips/eGRID2012V1_0_year09_GHGOutputrates.pdf
            //We use the FRCC Annual total output emission rate
            //1 J = 2.77777778e-10 MWH
            double conversion = Double.parseDouble("2.77777778e-10");
            double elecMWH = conversion * elecJ;
            double gasMWH = conversion * gasJ;

            double elecKgYear = (elecMWH * 1176.61) * 0.453592;//MHW * CO2lbsCnvt * kgCvnt
            double gasKgYear = (gasMWH * 1176.61) * 0.453592;

            EI += elecKgYear;
        }
        return EI;
    }
}
