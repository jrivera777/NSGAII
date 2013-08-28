**NOTE: Several of the classes here were extended from the JNSGA2: a free NSGA-II library. Details can be found in NSGA2.java.
		

Activity.java: Represents an activity in a project Schedule.  Each activity has a Cost, Duration, and Environmental Impact.

ActivityGraph.java: Represents a graph (assumed to be Directed and Acyclic) of Activities for some project schedule.  It's primary purpose is to calculate 
					the duration of an entire project schedule.  Each activity is a vertex in the graph, while the duration of each activity is 
					a weighted edge to its successor.  The Duration (time) for the schedule is calculated by finding the longest path in the graph.
					To do this, all edge weights are negated and the shortest path from the start activity to end activity is found.
					
ActivityList.java: Used to populate a Map containing Activity IDs and all alternatives related to them, from a given file.

CostFitnessFunction.java: Represents the Cost Objective for a schedule (Implements FitnessFunction interface).  Cost is calculated by summing the cost
						  of each activity in the schedule.

EnvironmentalImpactFitnessFunction.java: Represents the EI Objective for a schedule (Implements FitnessFunction interface). Calculated by summing the
										 EI value of each activity in the schedule.

IndividualSchedulejava:	Represents a single Schedule in the NSGA-II algorithm.  Extends the Individual class and implements several abstract functions, 
						specifically Mutate and Crossover.

Schedule.java:	Represents a single project schedule.  Needs a list of all alternatives and file with the association structure for all 
				activities (to build the ActivityGraph).  TestSchedule.java tests functions in this class.
						
TimeFitnessFunction.java:	Represents the Time (duration) Objective for a schedule (Implements FitnessFunction interface).  Time is calculated using an
							ActivityGraph within a Schedule object.  The longest path in the graph from start activity to end activity is the time for the
							schedule.
