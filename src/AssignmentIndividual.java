/* ===========================================================
 * JNSGA2: a free NSGA-II library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2006-2007, Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 *
 * Project Info:  http://sourceforge.net/projects/jnsga2/
 *
 * This library is free software; you can redistribute it and/or modify it  under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */



import NSGAII.FitnessFunction;
import NSGAII.Individual;
import NSGAII.NSGA2;
import java.util.HashSet;

/**
 * This class implements an individual representing a possible event assignment for use in the
 * multi-objective genetic algorithm NSGA-II.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class AssignmentIndividual extends Individual {

   private Registration[] registrations;
   private Event[] assignments;
   
   private HashSet<Event> eventsSet;
   private Event[] eventsArray;
   
   private double[] fitnessValues;
   
   /**
    * Constructor.
    * 
    * @param nsga2 NSGA-II instance
    * @param registrations registrations
    * @param assignments event assignments (for all registrants) in the same order as 'registrations'
    * @param eventsSet set of offered events
    */
   public AssignmentIndividual(NSGA2 nsga2, Registration[] registrations, Event[] assignments,
                               HashSet<Event> eventsSet) {
      super(nsga2);
      
      if (registrations == null) {
         throw new IllegalArgumentException("'registrations' must not be null.");
      }

      if (assignments == null) {
         throw new IllegalArgumentException("'assignments' must not be null.");
      }
      if (eventsSet == null) {
         throw new IllegalArgumentException("'eventsSet' must not be null.");
      }
      
      this.registrations = registrations;
      this.assignments = assignments;
      this.eventsSet = eventsSet;
      this.eventsArray = eventsSet.toArray(new Event[eventsSet.size()]);
      
      // set fitness values
      fitnessValues = new double[nsga2.getNSGA2Configuration().getNumberOfObjectives()];
      for (int i = 0; i < fitnessValues.length; i++) {
         fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
      }
   }
   
   /**
    * Gets the registrations.
    * 
    * @return registrations
    */
   public Registration[] getRegistrations() {
      return registrations.clone();
   }
   
   /**
    * Gets the event assignments (for all registrants) in the same order as in the returned array
    * of method {@link #getRegistrations()}.
    * 
    * @return event assignments
    */
   public Event[] getAssignments() {
      return assignments.clone();
   }
   
   /**
    * Gets this individual's fitness value for the index-th objective.
    * 
    * @param index index
    * @return fitness value for the index-th objective
    * @throws IndexOutOfBoundsException if the index is out of bounds
    */
   public double getFitnessValue(int index) throws IndexOutOfBoundsException {
      return fitnessValues[index];
   }
   
   /**
    * Mutates this individual.
    * 
    * After mutation, the fitness values are updated.
    */
   protected void mutate() {
      boolean mutated = false;
      
      for (int i = 0; i < assignments.length; i++) {
         // mutate this assignment (for one registration)?
         if (Math.random() < nsga2.getNSGA2Configuration().getMutationProbability()) {
            int randomIndex = (int)(Math.random() * eventsArray.length);
            
            assignments[i] = eventsArray[randomIndex];
            
            mutated = true;
         }
      }
      
      if (mutated) {
         // update fitness values
         for (int i = 0; i < fitnessValues.length; i++) {
            fitnessValues[i] = nsga2.getNSGA2Configuration().getFitnessFunction(i).evaluate(this);
         }
      }
   }
   
   /**
    * Does a crossover between the two individuals. Afterwards, both individuals are altered. If
    * the origial individuals are still needed, use the {@link #clone()} method to get clones and
    * use them instead. 
    * 
    * After crossover, the fitness values of both individuals are updated.
    * 
    * @param otherIndividual other individual
    */
   protected void crossover(Individual otherIndividual) {
      if (otherIndividual == null) {
         throw new IllegalArgumentException("'otherIndividual' must not be null.");
      }
      
      AssignmentIndividual otherAssignmentIndividual = (AssignmentIndividual)otherIndividual;
      
      if (nsga2 != otherAssignmentIndividual.nsga2) {
         throw new IllegalArgumentException("Both individuals must belong to the same NSGA-II instance.");
      }
      
      // execute crossover?
      if (Math.random() < nsga2.getNSGA2Configuration().getCrossoverProbability()) {
         // crossover in front of 'randomIndex'
         int randomIndex = (int)(Math.random() * (assignments.length + 1));
         
         for (int i = 0; i < randomIndex; i++) {
            Event dummy = assignments[i];
            assignments[i] = otherAssignmentIndividual.assignments[i];
            otherAssignmentIndividual.assignments[i] = dummy;
         }
         
         // update fitness values
         for (int i = 0; i < fitnessValues.length; i++) {
            FitnessFunction fitnessFunction = nsga2.getNSGA2Configuration().getFitnessFunction(i);
            fitnessValues[i] = fitnessFunction.evaluate(this);
            otherAssignmentIndividual.fitnessValues[i] = fitnessFunction.evaluate(otherAssignmentIndividual);
         }
      }
   }
   
   /**
    * Creates a clone of this individual, so that changes on the clone do not change the intern data
    * of the original. The rank and crowding distance are not copied by this method. The NSGA-II
    * instance is only copied.
    * 
    * @return cloned individual (without correct rank and crowding distance)
    */
   public Individual createClonedIndividual() {
      AssignmentIndividual clone = new AssignmentIndividual(nsga2, registrations,
                                                            assignments.clone(), eventsSet);
      
      return clone;
   }
}