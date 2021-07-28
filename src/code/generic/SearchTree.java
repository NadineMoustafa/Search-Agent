package code.generic;

public class SearchTree {
	
	public SearchTree parent;
	public String state;
	public String operator;
	public int depth;
	public long path_cost;
	public long heuristic_cost;
	
	
	public SearchTree(SearchTree parent, String state, String operator, int depth, long path_cost) 
	{
		this.parent = parent;
		this.state = state;
		this.operator = operator;
		this.depth = depth;
		this.path_cost = path_cost;
		this.heuristic_cost = 0;
	}
	public void setHCost(long heuristic_cost) 
	{
		this.heuristic_cost = heuristic_cost;
	}
}
