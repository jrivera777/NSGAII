/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IDF;

import NSGAII.Individual;

/**
 *
 * @author fdot
 */
public interface SimulationReader
{
    public double getSimulationElectricity(Individual indv, String geneSequence);
}
