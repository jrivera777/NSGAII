
import NSGAII.FitnessFunction;
import NSGAII.Individual;

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




/**
 * This class implements a fitness function rating the sum of priorities of the events assigned to
 * the different registrants. A smaller value is better.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class PriorityFitnessFunction implements FitnessFunction {
   
   private static final double NON_EXISTING_PRIORITY = 10.0;

   /**
    * Evaluates the fitness value (sum of priorities of the events assigned to the different
    * registrans) of the specified individual.
    * 
    * @param individual individual
    * @return fitness value
    */
   public double evaluate(Individual individual) {
      if (individual == null) {
         throw new IllegalArgumentException("'individual' must not be null.");
      }
      if (!(individual instanceof AssignmentIndividual)) {
         throw new IllegalArgumentException("'individual' must be of type 'AssignmentIndividual'.");
      }
      
      AssignmentIndividual aIndividual = (AssignmentIndividual)individual;
      
      Registration[] registrations = aIndividual.getRegistrations();
      Event[] assignments = aIndividual.getAssignments();
      
      double sumPriorities = 0.0;      
      for (int i = 0; i < registrations.length; i++) {
         Priority priority = registrations[i].getPriorityForEvent(assignments[i]);
         if (priority == null) {
            sumPriorities += NON_EXISTING_PRIORITY;
         } else {
            sumPriorities += priority.ordinal() + 1;  // because ordinal() starts with 0 instead of 1!
         }
      }
      
      return sumPriorities;
   }
}