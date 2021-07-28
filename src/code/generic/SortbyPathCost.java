package code.generic;

import java.util.Comparator;

public class SortbyPathCost implements Comparator<SearchTree> 
{ 
	@Override
	public int compare(SearchTree o1, SearchTree o2) {
		return (int) (o1.path_cost - o2.path_cost);
	} 
} 