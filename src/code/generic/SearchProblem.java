package code.generic;


import java.util.ArrayList;
import java.util.Collections;
import code.mission.*;
public abstract class SearchProblem {
public  SearchTree general_search(SearchProblem problem, String qing_func) {
	ArrayList<SearchTree> nodes = new ArrayList<SearchTree>();
	ArrayList<SearchTree> possible_nodes = new ArrayList<SearchTree>();
	int number_of_nodes = 0;
	if(qing_func.equals("GR1")) 
	{
		SearchTree node_ =  new SearchTree(null,problem.getInitState(),null,0,0);
		node_.setHCost(((MissionImpossible)problem).heuristicCost(node_,"H1"));
		nodes.add(node_);
	}
	else 
	{		
		if(qing_func.equals("GR2")) 
		{
			SearchTree node_ =  new SearchTree(null,problem.getInitState(),null,0,0);
			node_.setHCost(((MissionImpossible)problem).heuristicCost(node_,"H2"));
			nodes.add(node_);
		}
		else 
		{
			if(qing_func.equals("AS1")) 
			{
				SearchTree node_ =  new SearchTree(null,problem.getInitState(),null,0,0);
				node_.setHCost(((MissionImpossible)problem).heuristicCost(node_,"H1"));
				nodes.add(node_);
			}
			else 
			{
				if(qing_func.equals("AS2")) 
				{	
					SearchTree node_ =  new SearchTree(null,problem.getInitState(),null,0,0);
					node_.setHCost(((MissionImpossible)problem).heuristicCost(node_,"H2"));
					nodes.add(node_);
				}
				else 
				{
					nodes.add(new SearchTree(null,problem.getInitState(),null,0,0));
				}
			}
		}		
	}
	int cutoff = 0;
	int max_cutoff = 0;
	
	while(true) 
	{
		if(nodes.isEmpty()) 
		{
			if(max_cutoff > cutoff) 
			{
				nodes = new ArrayList<SearchTree>();
				possible_nodes= new ArrayList<SearchTree>();
				nodes.add(new SearchTree(null,problem.getInitState(),null,0,0));
				cutoff += 1;
				problem.clearStateTable();
			}
			else return null;
		}
		SearchTree node = nodes.remove(0);
		number_of_nodes += 1;
		if(problem.goalTest(node.state)) {
			((MissionImpossible) problem).setExpandedNodes(number_of_nodes);
			return node;
		}
		switch(qing_func) {
		case "BF" :
			
			if (problem instanceof MissionImpossible)
			{
				possible_nodes=problem.transitionFunction(node);
			}
			for(int i=0; i<possible_nodes.size();i++)
			{
				nodes.add(possible_nodes.get(i));

			}
		break;
		case "DF" : 
			if (problem instanceof MissionImpossible)
			{
				possible_nodes=problem.transitionFunction(node);
			}
			for(int i=0; i<possible_nodes.size();i++)
			{
				nodes.add(0, possible_nodes.get(i));
			}
		break;
		case "ID" : {
			if (problem instanceof MissionImpossible )
				 possible_nodes = problem.transitionFunction(node);
			if (possible_nodes != null) 
			{
				for(SearchTree possible_node :possible_nodes) 
				{
					if(possible_node.depth <= cutoff) 
					{
						nodes.add(0, possible_node);
					}
					else 
					{
						max_cutoff = possible_node.depth;
					}
				}
			}
		}

		break;
		case "UC" :		

		if (problem instanceof MissionImpossible)
		{
			possible_nodes=problem.transitionFunction(node);
		}
		for(int i=0; i<possible_nodes.size();i++)
		{
			nodes.add(possible_nodes.get(i));
		}
		Collections.sort(nodes, new SortbyPathCost()); 
		break;
		case "GR1":		
		if (problem instanceof MissionImpossible)
		{
			possible_nodes=problem.transitionFunction(node);
		}
		for(int i=0; i<possible_nodes.size();i++)
		{
			SearchTree possible_node = possible_nodes.get(i);
			possible_node.setHCost(((MissionImpossible)problem).heuristicCost(possible_node,"H1"));
			nodes.add(possible_node);
		}
		Collections.sort(nodes, new SortbyHeuristicCost()); 
		break;
		case "GR2":		
		if (problem instanceof MissionImpossible)
		{
			possible_nodes=problem.transitionFunction(node);
		}
		for(int i=0; i<possible_nodes.size();i++)
		{
			SearchTree possible_node = possible_nodes.get(i);
			possible_node.setHCost(((MissionImpossible)problem).heuristicCost(possible_node,"H2"));
			nodes.add(possible_node);
		}
		Collections.sort(nodes, new SortbyHeuristicCost()); 
		break;
		case "AS1":		
		if (problem instanceof MissionImpossible)
		{
			possible_nodes=problem.transitionFunction(node);
		}
		for(int i=0; i<possible_nodes.size();i++)
		{
			SearchTree possible_node = possible_nodes.get(i);
			possible_node.setHCost(((MissionImpossible)problem).heuristicCost(possible_node,"H1"));
			nodes.add(possible_node);
		}
		Collections.sort(nodes, new SortbyHeuristicCost2()); 
		break;
		case "AS2":		
		if (problem instanceof MissionImpossible)
		{
			possible_nodes=problem.transitionFunction(node);
		}
		for(int i=0; i<possible_nodes.size();i++)
		{
			SearchTree possible_node = possible_nodes.get(i);
			possible_node.setHCost(((MissionImpossible)problem).heuristicCost(possible_node,"H2"));	
			nodes.add(possible_node);
		}
		Collections.sort(nodes, new SortbyHeuristicCost2()); 
		break;
		
		}	
	}
}

protected abstract void clearStateTable();

public String getSecquence(SearchTree node) {
	String secquence = "";
	while (node != null && node.parent !=null) 
	{ 
		secquence = node.operator +"," +secquence;
		node =node.parent;
	}
	
	return secquence;
}
public abstract ArrayList<SearchTree> transitionFunction(SearchTree node);
public abstract boolean goalTest(String state);
public abstract String getInitState();
}
