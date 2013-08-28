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
import NSGAII.NSGA2Configuration;
import java.awt.Color;
import java.util.*;

/**
 * This class implements a test for the event assignment using the NSGA-II
 * multi-objective genetic algorithm.
 *
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class AssignmentTest
{

    private static final double MUTATION_PROBABILITY = 0.05;  // A much higher mutation rate seems to have a negative effect!
    private static final double CROSSOVER_PROBABILITY = 0.9;
    private static final int POPULATION_SIZE = 100;
    private static final int NUMBER_OF_GENERATIONS = 500;
    private static final int NUMBER_EVENTS = 10;
    private static final int NUMBER_REGISTRATIONS = 100;

    /**
     * Main method
     *
     * @param args arguments (not used)
     */
    public static void main(String[] args)
    {
        // create events and registration test data
        HashSet<Event> events = createEvents();
        Registration[] registrations = createRegistrations(events);

        // create NSGA-II instance
        NoWishEventFitnessFunction fitnessFunction0 = new NoWishEventFitnessFunction();
        PriorityFitnessFunction fitnessFunction1 = new PriorityFitnessFunction();
        EventSizesVarianceFitnessFunction fitnessFunction2 =
                new EventSizesVarianceFitnessFunction(events);
        FitnessFunction[] fitnessFunctions = new FitnessFunction[3];
        fitnessFunctions[0] = fitnessFunction0;
        fitnessFunctions[1] = fitnessFunction1;
        fitnessFunctions[2] = fitnessFunction2;
        NSGA2Configuration conf = new NSGA2Configuration(fitnessFunctions,
                MUTATION_PROBABILITY,
                CROSSOVER_PROBABILITY,
                0.0,
                POPULATION_SIZE,
                NUMBER_OF_GENERATIONS);
        NSGA2 nsga2 = new NSGA2(conf);
        nsga2.addNSGA2Listener(new AssignmentNSGA2Listener());

        // create start population
        LinkedList<Individual> startPopulation = new LinkedList<Individual>();

        // ... one half wish assignment
        int i = 0;
        for (; i < POPULATION_SIZE / 2; i++)
        {
            AssignmentIndividual individual =
                    new AssignmentIndividual(nsga2, registrations,
                    createWishAssignment(registrations), events);

            startPopulation.add(individual);
        }

        // ... one half random assignment
        for (; i < POPULATION_SIZE; i++)
        {
            AssignmentIndividual individual =
                    new AssignmentIndividual(nsga2, registrations,
                    createRandomAssignment(NUMBER_REGISTRATIONS, events), events);

            startPopulation.add(individual);
        }

        // start evolution
        LinkedList<Individual> bestIndividuals = nsga2.evolve(startPopulation);

        LinkedList<AssignmentIndividual> bestAssignments = new LinkedList<AssignmentIndividual>();
        for (Individual individual : bestIndividuals)
        {
            bestAssignments.add((AssignmentIndividual) individual);
        }

        printBestAssignments(bestAssignments);
        
        NSGA2.DisplayData(bestIndividuals, "Test", "Variance", "Priority", 2, 1, Color.RED, 12);
    }

    /**
     * Creates test data for offered events.
     *
     * @return set of offered events
     */
    private static HashSet<Event> createEvents()
    {
        HashSet<Event> events = new HashSet<Event>();

        for (int i = 0; i < NUMBER_EVENTS; i++)
        {
            Event event = new Event("EVENT_" + i);
            events.add(event);
        }

        return events;
    }

    /**
     * Creates test data for registrations.
     *
     * @param events offered events
     * @return registrations
     */
    private static Registration[] createRegistrations(HashSet<Event> events)
    {
        if (events == null)
        {
            throw new IllegalArgumentException("'events' must not be null.");
        }

        Event[] eventsArray = events.toArray(new Event[events.size()]);
        Priority[] priorities = Priority.values();

        Registration[] registrations = new Registration[NUMBER_REGISTRATIONS];

        for (int i = 0; i < registrations.length; i++)
        {
            String preName = "PRE_NAME_" + i;
            String lastName = "LAST_NAME" + i;

            HashMap<Event, Priority> event2priority = new HashMap<Event, Priority>();

            for (int j = 0; j < 6; j++)
            {
                // search random event
                Event randomEvent = null;
                do
                {
                    int randomEventIndex = (int) (Math.random() * eventsArray.length);
                    randomEvent = eventsArray[randomEventIndex];
                }
                while (event2priority.containsKey(randomEvent));

                int randomPriorityIndex = (int) (Math.random() * priorities.length);
                event2priority.put(randomEvent, priorities[randomPriorityIndex]);
            }

            Registration registration = new Registration(preName, lastName, event2priority);
            registrations[i] = registration;
        }

        return registrations;
    }

    /**
     * Creates a random event assignment for all the registrants
     *
     * @param size number of registrants
     * @param events offered events
     * @return random event assignment
     */
    private static Event[] createRandomAssignment(int size, HashSet<Event> events)
    {
        if (events == null)
        {
            throw new IllegalArgumentException("'events' must not be null.");
        }

        Event[] assignment = new Event[size];

        Event[] eventsArray = events.toArray(new Event[events.size()]);

        for (int i = 0; i < assignment.length; i++)
        {
            // search random event
            int randomEventIndex = (int) (Math.random() * eventsArray.length);
            assignment[i] = eventsArray[randomEventIndex];
        }

        return assignment;
    }

    /**
     * Creates an event assignment which assigns all registrants the event with
     * the highest priority.
     *
     * @param registrations registrations
     * @return wish assignment
     */
    private static Event[] createWishAssignment(Registration[] registrations)
    {
        if (registrations == null)
        {
            throw new IllegalArgumentException("'registrations' must not be null.");
        }

        Event[] assignment = new Event[registrations.length];

        for (int i = 0; i < registrations.length; i++)
        {
            // search wish event
            HashMap<Event, Priority> event2priority = registrations[i].getEventPriorities();
            Iterator<Event> it = event2priority.keySet().iterator();
            Event wishEvent = it.next();
            while (it.hasNext())
            {
                Event nextEvent = it.next();
                if (event2priority.get(nextEvent).ordinal() < event2priority.get(wishEvent).ordinal())
                {
                    wishEvent = nextEvent;
                }
            }
            assignment[i] = wishEvent;
        }

        return assignment;
    }

    /**
     * Prints the specified assignment individuals.
     *
     * @param bestAssignments assignment individuals
     */
    private static void printBestAssignments(LinkedList<AssignmentIndividual> bestAssignments)
    {
        if (bestAssignments == null)
        {
            throw new IllegalArgumentException("'bestAssignments' must not be null.");
        }

        // sort best assignments
        AssignmentIndividual[] array =
                bestAssignments.toArray(new AssignmentIndividual[bestAssignments.size()]);
        Arrays.sort(array, new AssignmentIndividualComparator());

        System.out.println();
        System.out.println("Number of offered solutions: " + bestAssignments.size());

        for (int i = 0; i < array.length; i++)
        {
            System.out.print(" Number no wish event: " + array[i].getFitnessValue(0));
            System.out.print(" / Sum priorities: " + array[i].getFitnessValue(1));
            System.out.println(" / Sizes variance: " + array[i].getFitnessValue(2));
        }
    }

    /**
     * This inner class implemens a comparator for two assignment individuals.
     */
    private static class AssignmentIndividualComparator implements Comparator<AssignmentIndividual>
    {

        /**
         * Compares the two specified assignment individuals. First criterion is
         * small number of "no wish events", second one is a small sum of event
         * priorities and the third one a small variance of the event sizes.
         *
         * @param individual1 first individual
         * @param individual2 second individual
         * @return -1, 0 or 1 as the first individual is less than, equal to, or
         * greater than the second one
         */
        public int compare(AssignmentIndividual individual1, AssignmentIndividual individual2)
        {
            if (individual1 == null)
            {
                throw new IllegalArgumentException("'individual1' must not be null.");
            }
            if (individual2 == null)
            {
                throw new IllegalArgumentException("'individual2' must not be null.");
            }

            // (1) number of "no wish events" (objective 0)
            if (individual1.getFitnessValue(0) < individual2.getFitnessValue(0))
            {
                return -1;
            }

            if (individual1.getFitnessValue(0) > individual2.getFitnessValue(0))
            {
                return 1;
            }

            // (2) sum priorities (objective 1)
            if (individual1.getFitnessValue(1) < individual2.getFitnessValue(1))
            {
                return -1;
            }

            if (individual1.getFitnessValue(1) > individual2.getFitnessValue(1))
            {
                return 1;
            }

            // (3) event sizes variance (objective 2)
            if (individual1.getFitnessValue(2) < individual2.getFitnessValue(2))
            {
                return -1;
            }

            if (individual1.getFitnessValue(2) > individual2.getFitnessValue(2))
            {
                return 1;
            }

            // both individuals are equal
            return 0;
        }
    }
}