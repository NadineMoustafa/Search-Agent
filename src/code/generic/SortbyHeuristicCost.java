package code.generic;

import java.util.Comparator;

public class SortbyHeuristicCost implements Comparator<SearchTree> 
{ 
	@Override
	public int compare(SearchTree o1, SearchTree o2) {
		return (int) (o1.heuristic_cost - o2.heuristic_cost);
	} 
} 