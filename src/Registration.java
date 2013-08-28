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



import java.util.HashMap;

/**
 * This class implements a registration.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class Registration {
   
   private String firstName;
   private String lastName;
   HashMap<Event, Priority> eventPriorities;
   
   /**
    * Constructor. There must be given at least six events rated with a priority.
    * 
    * @param firstName registrant's first name
    * @param lastName registrant's last name
    * @param eventPriorities mapping between event and priority
    */
   public Registration(String firstName, String lastName, HashMap<Event, Priority> eventPriorities) {
      if (firstName == null) {
         throw new IllegalArgumentException("'firstName' must not be null.");
      }
      if (lastName == null) {
         throw new IllegalArgumentException("'lastName' must not be null.");
      }
      if (eventPriorities == null) {
         throw new IllegalArgumentException("'eventPriorities' must not be null.");
      }
      
      if (eventPriorities.size() < 6) {
         throw new IllegalArgumentException("Priorities for at least six events necessary!");
      }
      
      this.firstName = firstName;
      this.lastName = lastName;
      this.eventPriorities = eventPriorities;
   }
   
   /**
    * Gets the registrant's first name.
    * 
    * @return registrant's first name
    */
   public String getFirstName() {
      return firstName;
   }
   
   /**
    * Gets the registrant's last name.
    * 
    * @return last name
    */
   public String getLastName() {
      return lastName;
   }
   
   /**
    * Gets the registrant's event ratings.
    * 
    * @return registrant's event ratings
    */
   public HashMap<Event, Priority> getEventPriorities() {
      return (HashMap<Event, Priority>)eventPriorities.clone();
   }
   
   /**
    * Gets the registrant's rating for the specified event.
    * 
    * @param event event
    * @return registrant's rating for this event
    */
   public Priority getPriorityForEvent(Event event) {
      if (event == null) {
         throw new IllegalArgumentException("'event' must not be null.");
      }
      
      return eventPriorities.get(event);
   }
}