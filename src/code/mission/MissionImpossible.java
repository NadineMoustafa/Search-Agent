package code.mission;

import code.generic.SearchProblem;
import code.generic.SearchTree;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;

public class MissionImpossible extends SearchProblem {
    private static final long MEGABYTE = 1024L * 1024L;

	
	int max_row;
	int max_col;
	int submarine_x;
	int submarine_y;
	int max_capacity;
	int deaths_factor;
	int number_of_expanded;
	String init_state;
	ArrayList<String> operators ;
	Hashtable<String,Integer> state_table;
	String grid;
	public MissionImpossible(String grid) {
		// initialize the MissionImpossible Problem and set the operators of the problem and initial state
		this.grid = grid;
		String[] grid_data = grid.split(";");
		String[] grid_size = grid_data[0].split(",");
		String[] submarine_position = grid_data[2].split(",");
		
		max_row = Integer.parseInt(grid_size[0]);
		max_col = Integer.parseInt(grid_size[1]);
		
		submarine_x  = Integer.parseInt(submarine_position[0]);
		submarine_y  = Integer.parseInt(submarine_position[1]);
		
		max_capacity = Integer.parseInt(grid_data[5]);
		this.deaths_factor = 700;

		init_state = grid_data[1]+";"+updateIMF(grid_data[3])+";"+grid_data[4]+";"+grid_data[5];
		
		state_table = new Hashtable<String,Integer>();
		operators = new ArrayList<String>();
		operators.add("DOWN");		
		operators.add("LEFT");
		operators.add("UP");	
		operators.add("RIGHT");
		operators.add("DROP");
		operators.add("CARRY");
		
	}
	public static String indexConvertor(String positions,int num_cols) 
	{
		// It takes the positions as 1D String then convert it to 2D position as X and Y it reshape
		// 1D Position to 2D Position by dividing the 1D  by number of columns to Get the X position
		// then Get Y position by get the reminder of 1D Position of number of  columns
		String[] postions_list = positions.split(",");
		String converted_postions = "";
		
		for (int i = 0; i < postions_list.length; i++) {
			int x = Integer.parseInt(postions_list[i]) / num_cols;
			int y = Integer.parseInt(postions_list[i]) % num_cols;
			converted_postions += x + "," + y;
			if(postions_list.length - i != 1)
				converted_postions += ",";	
		}
		return converted_postions;
	}

	public static String updateIMF(String IMF_postions) 
	{
		// it takes the positions of the IMF members as pair (x,y) and append to each pair its corresponding order (index)
		// to be (x,y,i)
		String updated_postions = "";
		String[] IMF_postions_list = IMF_postions.split(",");
		for(int i = 0; i < IMF_postions_list.length / 2; i++) {
			updated_postions += IMF_postions_list[i*2] + "," + IMF_postions_list[i*2+1] + "," + i;
			if(IMF_postions_list.length / 2 - i != 1) updated_postions += ",";		
		}	
		return updated_postions;
	}
	public static String removeIMF(String IMF_postions, int index) 
	{
		// it takes the positions of the IMF members as (x,y,i) and the index of the IMF member to be removed from this String
		// then return the string without this member
		String updated_postions = "";
		String[] IMF_postions_list = IMF_postions.split(",");

		for(int i = 0; i < IMF_postions_list.length / 3; i++) {
			if(Integer.parseInt(IMF_postions_list[i*3+2]) != index) {
				updated_postions += IMF_postions_list[i*3] + "," + IMF_postions_list[i*3+1] + "," + IMF_postions_list[i*3+2];
				if(IMF_postions_list.length / 3 - i != 1) updated_postions += ",";
			}
		}
		if(updated_postions.length() != 0 &&updated_postions.charAt(updated_postions.length()-1)==',')
			updated_postions = updated_postions.substring(0, updated_postions.length()-1);
		else if(updated_postions.length() == 0)
			updated_postions = "-1";
		return updated_postions;
	}
	public static String updateHealths(String[] IMF_healths_x, String operator, 
			String soldier_positions, int ethan_x, int ethan_y, int  remaining_capacity) 
	{
		// it update the Health of IMF members by two in each step and does not change the health of picked members 
		//and update the remaining capacity depending on the operator  
		ArrayList<Integer> IMF_healths_numbers = new ArrayList<Integer>();	
		for(int i=0; i<IMF_healths_x.length; i++)
			IMF_healths_numbers.add(Integer.parseInt(IMF_healths_x[i]));	
		String healths_string = "";		
		String[] soldier_positions_ = soldier_positions.split(",");
		int soldiers_index = -1;
		String IMF_positions_ = soldier_positions;
		
		for(int i = 0; i<(soldier_positions_.length / 3); i++)
		{
			if(operator.equals("CARRY") && i < (soldier_positions_.length / 3)
					&& soldiers_index == -1 && remaining_capacity != 0) 
			{		
				if ((ethan_x == Integer.parseInt(soldier_positions_[i*3])) 
						&& (ethan_y == Integer.parseInt(soldier_positions_[i*3+1])) )
				{
					soldiers_index = Integer.parseInt(soldier_positions_[i*3+2]);
					IMF_positions_ = removeIMF(IMF_positions_, soldiers_index);
					remaining_capacity -= 1;				
				}
			}
			
			int current_health;
			int current_soldier_index = Integer.parseInt(soldier_positions_[i*3+2]);
			if(i != soldiers_index && IMF_healths_numbers.get(current_soldier_index) < 100) 
				current_health = IMF_healths_numbers.get(current_soldier_index) + 2;
			else current_health = IMF_healths_numbers.get(current_soldier_index);
			
			if (current_health >= 100) { 
				current_health = 100;
				
			}
			IMF_healths_numbers.remove(current_soldier_index);
			IMF_healths_numbers.add(current_soldier_index, current_health);
		}
		for(int i = 0;i<IMF_healths_numbers.size();i++) 
		{
			healths_string += IMF_healths_numbers.get(i);
			if(IMF_healths_numbers.size() - i != 1) healths_string += ",";
			
		}
		if (operator.equals("DROP")) return IMF_positions_+";"+healths_string;
		return IMF_positions_+";"+healths_string+";"+remaining_capacity;

	}	
	@Override
	public void clearStateTable() 
	{
		// it clears the Hash table to save the new states it used mainly in ID search
		//where the agent start from the root node repeatedly
		this.state_table.clear();
	}
	public void setExpandedNodes(int number_Nodes) 
	{
		// set the number of expanded nodes 
		this.number_of_expanded = number_Nodes;
	}
	public static int getDeads(String healths) 
	{
		// it takes the health of IMF members after collecting the all members 
		// and return the number of members whose health is 100
		int number_of_deads = 0;
		String[] healths_list = healths.split(",");
		for(String health :healths_list) 
		{
			if(health.equals("100"))
				number_of_deads += 1;
		}
		return number_of_deads;
	}
	public static String getPosition(String positions, int index) 
	{
		// it takes the positions of the IMF members as (x,y,i) and the index of the IMF member to be returned from this String
		// then return the x and y position of this IMF member index
		String position = "";
		String[] positions_list =positions.split(",");
		for (int i = 0; i < positions_list.length; i+=3) {
			if(positions_list[i+2].equals(""+index))
				position = positions_list[i]+","+ positions_list[i+1];
		}
		return position;
		
	}
	@Override
	public String getInitState() 
	{
		// return the initial state
		return init_state;
	}
	
	public ArrayList<SearchTree> transitionFunction(SearchTree current_node) {
		// it takes the current node and get possible next nodes with available operators 
		// depending on the current position of ethan, the position of IMF members and the submarine position 
		ArrayList<SearchTree> nodes = new ArrayList<SearchTree>();
		String[] current_state = current_node.state.split(";");
		String[] ethan_position = current_state[0].split(",");
		
		int ethan_x  = Integer.parseInt(ethan_position[0]);
		int ethan_y  = Integer.parseInt(ethan_position[1]);
		
		String[] IMF_positions = current_state[1].split(",");
		String IMF_locations = "";
		
		String[] IMF_healths = current_state[2].split(",");
		String updated_data = "";
		ArrayList<Integer> IMF_healths_numbers = new ArrayList<Integer>();
		
		int remaining_capacity = Integer.parseInt(current_state[3]);
		
		for(int i=0; i<IMF_positions.length; i++)
		{
			IMF_locations += IMF_positions[i];
			if(IMF_positions.length - i != 1) 
			{
				IMF_locations += ",";
			}		
		}
		
		for(int i=0; i<IMF_healths.length; i++)
			IMF_healths_numbers.add(Integer.parseInt(IMF_healths[i]));		
		
		for(String operator : operators) {
			SearchTree next_node = null;			
			if(operator.equals("LEFT"))
			{
				if(ethan_y !=0)
				{
					int ethan_y_ = ethan_y - 1;
					int next_node_depth= current_node.depth + 1;
					
					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x, ethan_y_, remaining_capacity);
					String next_state=ethan_x+ ","+ ethan_y_ + ";" +updated_data;
					next_node= new SearchTree(current_node,next_state,operator,next_node_depth,pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);
				}		
			}
			if(operator.equals("RIGHT"))
			{				
				if(ethan_y != max_row -1)
				{
					int ethan_y_= ethan_y + 1;
					int next_node_depth= current_node.depth + 1;

					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x, ethan_y_, remaining_capacity);
					String next_state=ethan_x+ ","+ ethan_y_ + ";" +updated_data;
					next_node= new SearchTree(current_node,next_state,operator,next_node_depth,pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);
				}
			}
			if(operator.equals("UP"))
			{
				if(ethan_x != 0)
				{
					int ethan_x_ = ethan_x - 1;
					int next_node_depth= current_node.depth + 1;

					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x_, ethan_y, remaining_capacity);
					String next_state=ethan_x_+ ","+ ethan_y + ";" +updated_data;
					next_node= new SearchTree(current_node,next_state,operator,next_node_depth,pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);
				}
				
			}
			if(operator.equals("DOWN")){
				if(ethan_x != max_col -1)
				{
					int ethan_x_ = ethan_x + 1;
					int next_node_depth= current_node.depth + 1;

					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x_, ethan_y, remaining_capacity);
					String next_state=ethan_x_ + ","+ ethan_y + ";" +updated_data;
					next_node = new SearchTree(current_node,next_state,operator,next_node_depth,pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);
				}
				
			}
			if(operator.equals("DROP")){				
				if (remaining_capacity != max_capacity && (ethan_x==submarine_x) && (ethan_y==submarine_y))
				{
					int next_node_depth= current_node.depth + 1;

					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x, ethan_y, remaining_capacity);
					String next_state=ethan_x+ ","+ ethan_y + ";"+ updated_data+ ";" + max_capacity;
					next_node = new SearchTree(current_node,next_state,operator,next_node_depth,pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);
				}
			}
			if(operator.equals("CARRY")){
				if(remaining_capacity != 0) 
				{
					//TODO here should be checked if member is carried or not 
					updated_data = updateHealths(IMF_healths, operator, IMF_locations, ethan_x, ethan_y, remaining_capacity);
					if(updated_data != null) 
					{
						String next_state=ethan_x+ ","+ ethan_y + ";" +updated_data;
						next_node= new SearchTree(current_node, next_state, operator, current_node.depth+1, pathCost(current_state[2],updated_data.split(";")[1])+current_node.path_cost);			
					}
				}
			}
			if(state_table.get(current_state[0]+";"+current_state[1]+";"+current_state[3]) == null)
				state_table.put(current_state[0]+";"+current_state[1]+";"+current_state[3], state_table.size());
			if(next_node != null) 
			{
				String[] next_state_ = next_node.state.split(";");
				if(state_table.get(next_state_[0]+";"+next_state_[1]+";"+next_state_[3]) ==null) 
				{ 
					nodes.add(next_node);
					state_table.put(next_state_[0]+";"+next_state_[1]+";"+next_state_[3], state_table.size());
				}
			}
		}
		return nodes;
	}
	@Override
	public boolean goalTest(String state) {
		// it takes the state and check if there is a remaining soldiers and the truck is empty or not 
		String[] state_list = state.split(";");
		return state.contains("-1")&& Integer.parseInt(state_list[3])==this.max_capacity;

	}
	public long pathCost(String old_health, String new_health){
		// it takes the old health of the IMF members and the new health and calculate the cost of these state 
		// path cost equals total alive health plus the death factor (that give a priority to deaths over the normal increase in health)
		// multiplied by the increases in health and the number of death in this state  
		int sum_of_differences=0;
		int total_alive_healths=0;
		int no_of_deaths=0;
		String[] old_health_list=old_health.split(",");
		String[] new_health_list=new_health.split(",");
		for(int i=0; i<old_health_list.length; i++)
		{
			sum_of_differences+=Integer.parseInt(new_health_list[i])-Integer.parseInt(old_health_list[i]);
			if(Integer.parseInt(new_health_list[i])==100 && Integer.parseInt(old_health_list[i]) !=100)
				no_of_deaths+=1;
			total_alive_healths += Integer.parseInt(new_health_list[i]);
		}
		return total_alive_healths + deaths_factor * (sum_of_differences * no_of_deaths);
	}
	

	public int heuristicCost(SearchTree node, String strategy) 
	{
		// it takes node and the heuristic strategy and calculate its heuristicCost depending on the current node state and using 
		//IMF members positions, health and in one strategy the remaining capacity 
		int cost = 0;
		String[] data_list = node.state.split(";");
		String[] ethan_postion = data_list[0].split(",");
		
		String IMF_postions_String = data_list[1];
		String capacity = data_list[3];

		switch(strategy) 
		{
			case"H1":
			{
				for (int j=0; j<Integer.parseInt(capacity); j++) 
				{
					ArrayList<Integer> manhattan_distances = new ArrayList<Integer>();
					String[] IMF_postions = IMF_postions_String.split(",");
					int min_distance = 0;
					int index = -1;
					for(int i=0; i < IMF_postions.length/3; i++) 
					{
						int IMF_distance = Math.abs(Integer.parseInt(ethan_postion[0])-Integer.parseInt(IMF_postions[i*3]))+
								Math.abs(Integer.parseInt(ethan_postion[1])-Integer.parseInt(IMF_postions[i*3+1]));
						manhattan_distances.add(IMF_distance);
						if(i == 0|| IMF_distance < min_distance) 
						{
							min_distance = IMF_distance;
							index = Integer.parseInt(IMF_postions[i*3+2]);
						}			
					}
					cost += 2 * (IMF_postions.length/3) * min_distance;
					if(index !=-1) 
					{
						String[] positon = getPosition(IMF_postions_String,index).split(",");
						ethan_postion[0] = positon[0];
						ethan_postion[1] = positon[1];
						IMF_postions_String = removeIMF(IMF_postions_String,index);
					}

				}
					String[] IMF_postions = IMF_postions_String.split(",");		
					cost += 2 * (IMF_postions.length/3) *Math.abs(Integer.parseInt(ethan_postion[0])-this.submarine_x)+
							Math.abs(Integer.parseInt(ethan_postion[1])-this.submarine_y); 
			} break;
			case"H2":
			{
				while(!(IMF_postions_String.contains("-1"))) 
				{
					ArrayList<Integer> manhattan_distances = new ArrayList<Integer>();
					String[] IMF_postions = IMF_postions_String.split(",");
					int min_distance = 0;
					int index = -1;
					for(int i=0; i < IMF_postions.length/3; i++) 
					{
						int IMF_distance = Math.abs(Integer.parseInt(ethan_postion[0])-Integer.parseInt(IMF_postions[i*3]))+
								Math.abs(Integer.parseInt(ethan_postion[1])-Integer.parseInt(IMF_postions[i*3+1]));
						manhattan_distances.add(IMF_distance);
						if(i == 0|| IMF_distance < min_distance) 
						{
							min_distance = IMF_distance;
							index = Integer.parseInt(IMF_postions[i*3+2]);
						}				
					}
					cost += 2 * (IMF_postions.length/3) * min_distance;
					if(index !=-1) 
					{
						String[] positon = getPosition(IMF_postions_String,index).split(",");
						ethan_postion[0] = positon[0];
						ethan_postion[1] = positon[1];
						IMF_postions_String = removeIMF(IMF_postions_String,index);
					}
				}
				String[] IMF_postions = IMF_postions_String.split(",");
				
				cost += 2 * (IMF_postions.length/3) *Math.abs(Integer.parseInt(ethan_postion[0])-this.submarine_x)+
							Math.abs(Integer.parseInt(ethan_postion[1])-this.submarine_y); 
			} break;
		}
		return cost;
	}
	
	public static String genGrid() {
		// The function generate the Grid Size, the number of IMF members, their positions, their health, the position of Ethan,
		// the position of Submarine and the truck capacity random each cell in the grid is either free, has IMF member, has Ethan
		// or has Submarine
		// The range of Grid Size is 5 x 5 till 15 x 15
		// The range of Number of IMF members is 1 till 10
		// The range of Positions of IMF members is within Grid Size
		// The range of Health of IMF members is 1 till 99
		// The range of Position of Ethan is within Grid Size
		// The range of Position of Submarine is within Grid Size
		// The range of truck capacity is 1 till 10
		
		String grid = ""; 
		
		int max_size_grid = 15;
		int min_size_grid = 5;
		
		int max_num_IMF = 10;
		int min_num_IMF = 5;
		
		int max_health = 99;
		int min_health = 1;
		
		int max_capacity = 10;
		int min_capacity = 1;
		
		int num_rows = (int)(Math.random()*((max_size_grid - min_size_grid)+1))+min_size_grid;
		int num_cols = (int)(Math.random()*((max_size_grid - min_size_grid)+1))+min_size_grid;

		int num_IMF = (int)(Math.random()*((max_num_IMF - min_num_IMF)+1))+min_num_IMF;
		
		int capacity = (int)(Math.random()*((max_capacity - min_capacity)+1))+min_capacity;

		int max_index = num_rows * num_cols - 1;		
		int min_index = 0;
		
		int ethan_postion = (int)(Math.random()*((max_index - min_index)+1))+min_index;
			
		int submarine_postion = (int)(Math.random()*((max_index - min_index)+1))+min_index;
		
		while (submarine_postion == ethan_postion) submarine_postion = (int)(Math.random()*((max_index - min_index)+1))+min_index;
		
		String IMF_postions = "";
		String IMF_healths = "";
		for (int i =0; i < num_IMF; i++) 
		{
			int IMF_member = (int)(Math.random()*((max_index - min_index)+1))+min_index;
			while(IMF_member == ethan_postion || IMF_member == submarine_postion || IMF_postions.contains(IMF_member+"")) {
				IMF_member = (int)(Math.random()*((max_index - min_index)+1))+min_index;
			}
			IMF_postions += IMF_member;
			IMF_healths += (int)(Math.random()*((max_health - min_health)+1))+min_health;
			if(num_IMF - i != 1) 
			{
				IMF_postions += ",";
				IMF_healths += ",";
			}		
		}
		
		grid += num_rows + "," + num_cols +";";	
		grid += indexConvertor(ethan_postion+"", num_cols) + ";";
		grid += indexConvertor(submarine_postion+"", num_cols) + ";";
		grid += indexConvertor(IMF_postions+"", num_cols) + ";";
		grid += IMF_healths + ";";
		grid += capacity;
	
		return grid;
	}
	
	public static String solve(String grid, String strategy, boolean visualize) {
		// it takes the grid , strategy and return a solution if there is one and NO solution else and 
		//has boolean visualize it show the sequence of actions and its effect on the grid if it is true
		MissionImpossible problem = new MissionImpossible(grid);
		SearchTree node = problem.general_search(problem, strategy);
		if(node == null) return "No Soluation";
		String[] data_list = node.state.split(";");
		String healths = data_list[2];
		int number_of_deads = getDeads(healths);
		String actions_secquence =problem.getSecquence(node);

		if (visualize) {
		    new Gridlayout(grid,actions_secquence.substring(0, actions_secquence.length()-1),problem.max_capacity);  
		}	
		return actions_secquence.substring(0, actions_secquence.length()-1).toLowerCase()+";"+number_of_deads+";"+healths+";"+problem.number_of_expanded;
	}

	public static void main(String[] args) {

//		String grid = "7,7;1,6;5,4;2,2,1,4,0,3,2,3,0,1,4,5;6,44,82,49,24,54;4";
//		 ArrayList<String> list=new ArrayList<String>();  
//		 list.add("5,5;2,1;1,0;1,3,4,2,4,1,3,1;54,31,39,98;1");
//		 list.add("5,5;2,1;1,0;1,3,4,2,4,1,3,1;54,31,39,98;2");
//		 list.add("5,5;2,1;1,0;1,3,4,2,4,1,3,1;54,31,39,98;10");
//		 list.add("6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;1");
//		 list.add("6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;3");
//		 list.add("6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;10");
//		 list.add("7,7;1,6;5,4;2,2,1,4,0,3,2,3,0,1,4,5;6,44,82,49,24,54;1");  
//		 list.add("7,7;1,6;5,4;2,2,1,4,0,3,2,3,0,1,4,5;6,44,82,49,24,54;3"); 
//		 list.add("7,7;1,6;5,4;2,2,1,4,0,3,2,3,0,1,4,5;6,44,82,49,24,54;10"); 
//		 list.add("8,8;4,2;7,4;5,1,7,7,4,0,6,7;93,85,72,78;1");
//		 list.add("8,8;4,2;7,4;5,1,7,7,4,0,6,7;93,85,72,78;2");
//		 list.add("8,8;4,2;7,4;5,1,7,7,4,0,6,7;93,85,72,78;10");
//		 list.add("9,9;8,7;5,0;0,8,2,6,5,6,1,7,5,5,8,3,2,2,2,5,0,7;11,13,75,50,56,44,26,77,18;1");
//		 list.add("9,9;8,7;5,0;0,8,2,6,5,6,1,7,5,5,8,3,2,2,2,5,0,7;11,13,75,50,56,44,26,77,18;5");
//		 list.add("9,9;8,7;5,0;0,8,2,6,5,6,1,7,5,5,8,3,2,2,2,5,0,7;11,13,75,50,56,44,26,77,18;10");
//		 list.add("10,10;6,3;4,8;9,1,2,4,4,0,3,9,6,4,3,4,0,5,1,6,1,9;97,49,25,17,94,3,96,35,98;1");
//		 list.add("10,10;6,3;4,8;9,1,2,4,4,0,3,9,6,4,3,4,0,5,1,6,1,9;97,49,25,17,94,3,96,35,98;5");
//		 list.add("10,10;6,3;4,8;9,1,2,4,4,0,3,9,6,4,3,4,0,5,1,6,1,9;97,49,25,17,94,3,96,35,98;10");
//		 list.add("11,11;7,7;8,8;9,7,7,4,7,6,9,6,9,5,9,1,4,5,3,10,5,10;14,3,96,89,61,22,17,70,83;1"); 
//		 list.add("11,11;7,7;8,8;9,7,7,4,7,6,9,6,9,5,9,1,4,5,3,10,5,10;14,3,96,89,61,22,17,70,83;5"); 
//		 list.add("11,11;7,7;8,8;9,7,7,4,7,6,9,6,9,5,9,1,4,5,3,10,5,10;14,3,96,89,61,22,17,70,83;10"); 
//		 list.add("12,12;7,7;10,6;0,4,2,2,1,3,8,2,4,2,9,3;95,4,68,2,94,91;1");
//		 list.add("12,12;7,7;10,6;0,4,2,2,1,3,8,2,4,2,9,3;95,4,68,2,94,91;3");
//		 list.add("12,12;7,7;10,6;0,4,2,2,1,3,8,2,4,2,9,3;95,4,68,2,94,91;10");
//		 list.add("13,13;7,4;4,0;9,3,3,9,12,7,7,9,3,12,11,8,4,2,12,6;22,62,74,56,43,70,17,14;1"); 
//		 list.add("13,13;7,4;4,0;9,3,3,9,12,7,7,9,3,12,11,8,4,2,12,6;22,62,74,56,43,70,17,14;4"); 
//		 list.add("13,13;7,4;4,0;9,3,3,9,12,7,7,9,3,12,11,8,4,2,12,6;22,62,74,56,43,70,17,14;10"); 
//		 list.add("14,14;13,9;1,13;5,3,9,7,11,10,8,3,10,7,13,6,11,1,5,2;76,30,2,49,63,43,72,1;1");
//		 list.add("14,14;13,9;1,13;5,3,9,7,11,10,8,3,10,7,13,6,11,1,5,2;76,30,2,49,63,43,72,1;4");
//		 list.add("14,14;13,9;1,13;5,3,9,7,11,10,8,3,10,7,13,6,11,1,5,2;76,30,2,49,63,43,72,1;10");
//		 list.add("15,15;5,10;14,14;0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8;81,13,40,38,52,63,66,36,13;1");
//		 list.add("15,15;5,10;14,14;0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8;81,13,40,38,52,63,66,36,13;5");
//		 list.add("15,15;5,10;14,14;0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8;81,13,40,38,52,63,66,36,13;10");
//		 int grid_size = 5;
//		 int version = 0;
//		 for(String grid:list)  {
//
//			 int deaths_factor = 50;
//			 int min_number_of_Death = 10;
//			 int min_death_factor = -1;
//			 int start_number_of_Death = -1;
//			 
//			 String[] searchtype_list = {"UC","AS1","AS2"};
//			 textLogger("./tunningTheDeath"+grid_size+"by"+grid_size+"V."+(version%3));
//			 System.out.println(grid);
//			 boolean is_found = false;
//			 l:for(int i=0; i < 15; i++) {
//				 for(String searchtype : searchtype_list) {
//					 String solution = solve(grid,searchtype,false,deaths_factor);
//					 if(solution.contains(";0;")) is_found =true;	
//					 if(!is_found && i ==14) i=1;	
//					 System.out.println("searchtype: "+searchtype+" :deaths_factor: "+deaths_factor);
//					 System.out.println(solution);
//					 int number_of_deaths = Integer.parseInt(solution.split(";")[1]);
//					 if (i==0) {
//						 start_number_of_Death = number_of_deaths;
//					 }
//					 if(number_of_deaths<min_number_of_Death) {
//						 min_number_of_Death = number_of_deaths;
//						 min_death_factor = deaths_factor;
//						 }
//				 }
//				 deaths_factor += 50;
//				 if(deaths_factor>8000 || is_found) {
//					 System.out.println("Start   Number of Death is: " + start_number_of_Death);
//					 System.out.println("Minimum Number of Death is: " + min_number_of_Death);
//					 System.out.println("Minimum Deaths Factor   is: " + min_death_factor);
//					 break l;
//					 }
//			 }
//			 version++;
//			 if(version%3 == 0)
//				 grid_size++;
//		 }
//      long startTime = System.currentTimeMillis();
//		performaceCalcaulation(startTime);



	}

	
	//Help Functions
    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
	public static void performaceCalcaulation(long startTime) {
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long stopTime = System.currentTimeMillis();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + bytesToMegabytes(memory));
        
        long elapsedTime = stopTime - startTime;
        System.out.println("Running Time is : ("+(1.0*elapsedTime/1000)+")S");
	}
	public  static void textLogger(String file_name) 
	{
		PrintStream printStream = null;
		try 
		{
			printStream = new PrintStream(new FileOutputStream(file_name+".txt"));
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		System.setOut(printStream);
	}
}
