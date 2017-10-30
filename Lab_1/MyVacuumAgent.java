/*package tddc17;
 * 


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.Random;

class MyAgentState
{
	public static int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" c ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 15;
	public MyAgentState state = new MyAgentState();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
			return turnLeft();
			
		} else if (action==1) {
			return turnRight();
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	// Turns right
	private Action turnRight() {

			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT; 
	}
	// Turns left
	private Action turnLeft() {
		
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	}
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
	    
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {
	    	if (bump)
	    	{	
		    	switch (state.agent_direction) { 
				case MyAgentState.NORTH:
					return turnRight();
				case MyAgentState.EAST:
					return turnRight();						
				case MyAgentState.SOUTH:
					return turnRight();			
				case MyAgentState.WEST:
					return turnLeft();
		    	} 		 
	    	}
	    	else
	    	{
				state.agent_last_action=state.ACTION_MOVE_FORWARD;
	    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;		
	    	}
	    }
	}
}


public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
*/
package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * Class to represent each tile in the grid
 */
class Node {
	
	private final int X;
	private final int Y;
	private Node parent;
	private int heuristic; // not used atm
	
	public Node(int x, int y) {
		this.X = x;
		this.Y = y;
		this.parent = null;
		this.heuristic = 0;
	}
	
	public Node(int x, int y, Node parent) {
		this.X = x;
		this.Y = y;
		this.parent = parent;
		this.heuristic = 0;
	}
	
	public int getDirectionTo(Node neighbor){
		if (neighbor.Y < this.Y) return MyAgentState.NORTH;
		else if (neighbor.Y > this.Y) return MyAgentState.SOUTH;
		else if (neighbor.X < this.X) return MyAgentState.WEST;
		else if (neighbor.X > this.X) return MyAgentState.EAST;
		return -1;
	}

	public int getY() {
		return Y;
	}

	public int getX() {
		return X;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}
	public String toString() {
		return "(" + this.X + "," + this.Y + ") ";
	}
	
	// hashcode generator for hashtables
	@Override
	public int hashCode() {
		return 37*this.X + this.Y;
	}
	@Override
	public boolean equals(Object obj) {
		return this.X == ((Node)obj).X && this.Y == ((Node)obj).Y;
	}
}

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	public boolean isGoingHome; // true if searching for home node
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" c ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initialRandomActions = 10;
	private Random random_generator = new Random();
	
	public MyAgentState state = new MyAgentState();
	
	// Here you can define your variables!
	public int iterationCounter = (int) (Math.pow(state.world.length, 2) * 2);
	
	/**
	 *  variables for first version (conditionals) 
	 */
	public int first = 1; 
	public int mid = 0;
	public int last = 0;
	
	public int RightUturn = 0;
	public int LeftUturn = 0;
	public int LeftUturn2 = 0;
	
	public int noStepsDown = 0;
	public boolean lastLeft = false;
	public boolean lastRight = false;
	public boolean DoneUpSteps = false;
	
	public int RightUturnLast = 0;
	public int LeftUturnLast = 0;
	public int LeftUturnLast2 = 0;
	
	/**
	 * Search version
	 */
	
	public LinkedList<Node> route = new LinkedList<Node>();
	
    public Queue<Action> actionQueue = new LinkedList<Action>();	// default queue 
    
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
			return turnLeft();
			
		} else if (action==1) {
			return turnRight();
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	/**
	 * TASK 1 execute(), using conditional logic only 
	 * 
	 */
	/*
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			return suckDirt();
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	  //  state.printWorldDebug();
 
	    // Next action selection based on the percept value
	    
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	return suckDirt();
	    } 
	    else if(first == 1) // PHASE 1: Clearing the first row
	    {

	    	if(state.agent_direction != MyAgentState.EAST && LeftUturn == 0)
	    	{
	    		if(state.agent_direction == MyAgentState.NORTH || state.agent_direction == MyAgentState.WEST)
	    		{
	    			return turnRight();
	    		}
	    		else if(state.agent_direction == MyAgentState.SOUTH)
	    		{
	    			return turnLeft();	
	    		}
	    	}
	    	
	    	if (bump && (LeftUturn == 0))
	    	{
	    		System.out.println("first left!!");
	    		
	    		LeftUturn = 1;
	    		return turnLeft();		
	    	}
	    	
	    	if(LeftUturn == 1)
	    	{
	    		System.out.println("second left!!");

	    		LeftUturn = 2;
	    		return turnLeft();	
	    	}
	    	
	    	if(bump && (LeftUturn == 2)) // Initiating Phase 2
	    	{
	    	    first = 0;
	    		mid = 1;

	    		LeftUturn = 0;
	    		LeftUturn2 = 1;
	    		
	    		noStepsDown++;
	    		return turnLeft();
	    	}
	    	
    		return moveForward();
	    	
	    }
	    else if(mid == 1) // PHASE 2: Stepping down
	    {
	    	if (bump)
	    	{
	    		if(state.agent_direction == MyAgentState.SOUTH && LeftUturn == 0) // Initiating Phase 3
	    		{
	    			mid = 0;
	    			last = 1;
	    			return turnLeft();

	    		}
	    		else if(state.agent_direction == MyAgentState.SOUTH && LeftUturn != 0) // Initiating Phase 3
	    		{
	    			mid = 0;
	    			last = 1;
	    			return turnLeft();
	    		}
	    		else if(LeftUturn == 0)
	    		{
	    			RightUturn = 1;
	    			noStepsDown++;
	    			return turnRight();
	    		}
	    		else
	    		{
		    		LeftUturn = 0;
		    		LeftUturn2 = 1;
		    		noStepsDown++;
		    		return turnLeft();
	    		}

	    	}	
	    	else
	    	{
	    		if ((state.agent_last_action == state.ACTION_MOVE_FORWARD || state.agent_last_action == state.ACTION_SUCK) && RightUturn == state.WALL)
	    		{
		    		RightUturn = 0;
		    		LeftUturn = 1;
		    		return turnRight();	
	    		}
	    		
	    		else if((state.agent_last_action == state.ACTION_MOVE_FORWARD  || state.agent_last_action == state.ACTION_SUCK) && LeftUturn2 == state.WALL)
	    		{
		    		LeftUturn2 = 0;
		    		return turnLeft();
	    		}
	    		else
	    		{
	    			return moveForward();
	    		}

	    	}
	    	
	    } 
	    else if(last == 1) // PHASE 3: Stepping up toward home
	    {
	    	
	    	if(DoneUpSteps == false) // Stepping up to first potential non-visited block
	    	{
	    		if(state.agent_direction == MyAgentState.EAST)
	    		{
	    			return turnLeft();
	    		}

	    		else if(noStepsDown == 0 && LeftUturn2 == 1) // We have passed all previously visited blocks
	    		{
	    			DoneUpSteps = true;
	    			return turnRight();
	    		}
	    		else if(noStepsDown == 0 && RightUturn == 1)
	    		{
	    			DoneUpSteps = true;
	    			return turnLeft();
	    		}
	    		else
	    		{
	    			noStepsDown--;
	    			return moveForward();
	    		}
	    	}
	    	else // Reached first potential non-visited block
	    	{
		    		if(bump)
		    		{
			    		if(state.agent_direction == MyAgentState.NORTH && lastRight == true) // BUMP IN THE ROOF !
			    		{
			    			lastRight = true;
			    			return turnLeft();

			    		}
			    		else if(state.agent_direction == MyAgentState.NORTH && lastLeft == true) // BUMP IN THE ROOF ! (we have reached home)
			    		{	
			    			return NoOpAction.NO_OP;
			    		}
			    		else if(state.agent_direction == MyAgentState.EAST)
			    		{
			    			lastRight = true;
			    			return turnLeft();
			    		}
			    		else
			    		{
			    			lastLeft = true;
				    		return turnRight();
			    		}

		    		}
		    		else if((state.agent_last_action == state.ACTION_MOVE_FORWARD  || state.agent_last_action == state.ACTION_SUCK) && lastLeft == true)
		    		{
		    			lastLeft = false;
		    			return turnRight();
		    		}
		    		else if((state.agent_last_action == state.ACTION_MOVE_FORWARD  || state.agent_last_action == state.ACTION_SUCK) && lastRight == true)
		    		{
		    			lastRight = false;
		    			return turnLeft();
		    		}
		    		else if(home)
		    		{
		    			return NoOpAction.NO_OP;
		    		}
		    		else
		    			return moveForward();			
	    	}

	    }
	    else
	    	return NoOpAction.NO_OP; 
	    	  
	}
	
	*/

	/**
	 * TASK 2 execute(), using BFS
	 */
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			return suckDirt();
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0) {
	    	System.out.println("Iteration counter = 0");
	    	return NoOpAction.NO_OP;
	    }
	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
 
	    // Next action selection based on the percept value
	    
	    if(dirt) 
	    	return suckDirt();

	    if ( actionQueue.isEmpty() && route.isEmpty() ) {
	    	//System.out.println("Action queue and route is empty");
			route = Search(state.isGoingHome);
			if ( route == null ) { // means there are no more unknown nodes to find
				if (home) { // special case when we finish on the "home" position
					//System.out.println("I'M HOME!");
					return doNothing();
				} else {
					//System.out.println("GOING HOME");
					goHome();
				}
			}
		}
	    if ( actionQueue.isEmpty() ) {
	    	if (home && state.isGoingHome) { // check if done and at home position, else it tries to remove
				//System.out.println("I'M HOME!");
				return doNothing();
	    	}
	    	
	    	//System.out.println("Action queue is empty");
			// Pop next point in current route
	    	Node nextPoint = route.remove();

			//System.out.println("Next point in route: (" + nextPoint.getX() + "," + nextPoint.getY() + ")"); 
			Node currentPoint = new Node( state.agent_x_position, state.agent_y_position );

			int direction = currentPoint.getDirectionTo(nextPoint);
			switch (direction){
				case MyAgentState.EAST:
					//System.out.println("Queue EAST");
					queueMoveEast();
					break;
				case MyAgentState.WEST:
					//System.out.println("Queue WEST");
					queueMoveWest();
					break;
				case MyAgentState.NORTH:
					//System.out.println("Queue NORTH");
					queueMoveNorth();
					break;
				case MyAgentState.SOUTH:
					//System.out.println("Queue SOUTH");
					queueMoveSouth();
					break;
				default:
					break;
			}
		}
	    // Perform action at front of queue, if any 
	    if(!actionQueue.isEmpty()) {
	    	//System.out.println("Action queue not empty");
	    	return doAction(actionQueue.remove());
	    }
	   
	    return doNothing();   
		
	}
	
	private void goHome() {
		//System.out.println("Going home");
		state.isGoingHome = true;
		route = Search(state.isGoingHome);
	}
	
	private LinkedList<Node> Search( boolean isGoingHome ) {
		
		//System.out.println("Searching path...");
	//    PriorityQueue<Node> queue = initQueue(); // queue with heuristics comparator
		Queue<Node> queue = new LinkedList<Node>();
	    Map<Node, Node> parentLinks = new HashMap<Node, Node>();
	    
		// add current node to the open list
		Node startnode = new Node( state.agent_x_position, state.agent_y_position );

		queue.add(startnode);
		parentLinks.put(startnode, null);
		
		while( !queue.isEmpty() ) {
			
			//System.out.println("Choose closest neighbor");
			//System.out.println(queue);
			Node currentnode = queue.remove();
			
			//System.out.println("Current Node: (" + currentnode.getX() + "," + currentnode.getY() + ")");
			//queue.clear();
			
			// build route to this node if unknown 
			if ( !isGoingHome && state.world[currentnode.getX()][currentnode.getY()] == state.UNKNOWN ) {
				return buildRoute( currentnode );
			}
			// build route to HOME
			if ( isGoingHome && currentnode.getX() == 1 && currentnode.getY() == 1 ) {
				return buildRoute( currentnode );
			}
			
			
		// look at adjacent nodes and add the to the open list
		// only check vertically and horizontally
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					
					//System.out.println("Adding neighbors...");
					Node neighbor = new Node(currentnode.getX() + dx, currentnode.getY() + dy);
					if (Math.abs(dx) != Math.abs(dy) 
							&& !parentLinks.containsKey(neighbor) 
					//		&& neighbor.getParent() != currentnode
							&& (neighbor.getX() >= 0)
							&& (neighbor.getY() >= 0)
							&& state.world[neighbor.getX()][neighbor.getY()] != state.WALL)
							{
						// add current node as parent of adjacent nodes
						//System.out.println("Adding neighbor (" + neighbor.getX() + "," + neighbor.getY() + ")");
						
						neighbor.setParent(currentnode);
						parentLinks.put(neighbor, currentnode);

						// add to queue
						queue.add(neighbor);
					}
				}
			}
		}
		return null;
	}
	
	// Build a route to a specific node, backtracking from that node via its ancestor
	private LinkedList<Node> buildRoute( Node goalNode ) {
		
		LinkedList<Node> route = new LinkedList<Node>();
		route.addFirst(goalNode);
		
		//System.out.println("Added (" + goalNode.getX() + "," + goalNode.getY() + ") to route"); 
		
		while ( goalNode.getParent() != null ) {
			
			goalNode = goalNode.getParent();
			route.addFirst(goalNode);
			
			//System.out.println("Added parentNode (" + goalNode.getX() + "," + goalNode.getY() + ") to route"); 
		}
		// Remove start node since we don't want to go to start from start
		route.remove();
		return route;
	}
	
    // Perform action in queue
	private Action doAction( Action action ) {
		
		if (action == LIUVacuumEnvironment.ACTION_TURN_LEFT) {
			return turnLeft();
		}
		else if (action == LIUVacuumEnvironment.ACTION_TURN_RIGHT) {
			return turnRight();
		}	
		else 
			return moveForward();
	}

	// Turns right
	private Action turnRight() {
	
		state.agent_direction = ((state.agent_direction+1) % 4);
	    state.agent_last_action = state.ACTION_TURN_RIGHT;
	    return LIUVacuumEnvironment.ACTION_TURN_RIGHT; 
	}
	// Turns left
	private Action turnLeft() {
		
	    state.agent_direction = ((state.agent_direction-1) % 4);
	    if (state.agent_direction<0) 
	    	state.agent_direction +=4;
	    state.agent_last_action = state.ACTION_TURN_LEFT;
		return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	}
	// moves forward
	private Action moveForward() {
		
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	// sucks dirt
	private Action suckDirt() {
		
		state.agent_last_action=state.ACTION_SUCK;
    	return LIUVacuumEnvironment.ACTION_SUCK;
	}
	// do nothing, ends program 
	private Action doNothing() {
		state.agent_last_action=state.ACTION_NONE;
		return NoOpAction.NO_OP; 
	}
	
	/**
	 * Queues movement actions
	 */
	private void queueMoveEast() {
		switch (state.agent_direction) {
			case MyAgentState.SOUTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.WEST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.NORTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_RIGHT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			default:
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
		}
	}

	private void queueMoveWest() {
		switch (state.agent_direction) {
			case MyAgentState.NORTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.EAST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.SOUTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_RIGHT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			default:
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
		}
	}

	private void queueMoveNorth() {
		switch (state.agent_direction) {
			case MyAgentState.EAST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.SOUTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.WEST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_RIGHT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			default:
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
		}
	}

	private void queueMoveSouth() {
		switch (state.agent_direction) {
			case MyAgentState.WEST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.NORTH:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_LEFT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			case MyAgentState.EAST:
				actionQueue.add(LIUVacuumEnvironment.ACTION_TURN_RIGHT);
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
			default:
				actionQueue.add(LIUVacuumEnvironment.ACTION_MOVE_FORWARD);
				break;
		}
	}
	
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}



