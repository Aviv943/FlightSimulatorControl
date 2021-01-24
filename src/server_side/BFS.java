package server_side;

import java.util.ArrayList;
import java.util.HashSet;

public class BFS<Solution> extends CommonSearcher<Solution> {

	@Override
	public Solution search(Searchable s) 
	{
		openList.add(s.getInitialState());
		HashSet<State> closedSet=new HashSet<State>();
		while(openList.size()>0)
		{
			State n=popOpenList();// dequeue
			closedSet.add(n);
			ArrayList<State> successors=s.getAllPossibleStates(n); //however it is implemented
			if(n.equals(s.getGoalState()))
				return backTrace(n, s.getInitialState());
				// private method, back traces through the parents
			for(State successor : successors){
				if(!closedSet.contains(successor) && ! openList.contains(successor)){
					successor.setCameFrom(n);
					openList.add(successor);
				}
				else if(n.getCost()+(successor.getCost()-successor.getCameFrom().getCost())<successor.getCost())
					 	if(openList.contains(successor))
					 		successor.setCameFrom(n);
						else  {
							successor.setCameFrom(n);
							closedSet.remove(successor);
							openList.add(successor);
					}
			}
		}
		return backTrace(s.getGoalState(), s.getInitialState());
	}



	@Override
	public int getNumberOfNodesEvaluated() {
		return evluateNodes;
	}
	
}
