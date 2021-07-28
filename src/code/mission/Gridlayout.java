package code.mission;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//
//import javax.imageio.ImageIO;
import javax.swing.*;  
  
public class Gridlayout{
public static boolean isEqual(String postionx,String postiony , int x, int y) {
	return (x == Integer.parseInt(postionx) && y == Integer.parseInt(postiony));
}	
JFrame frame;  
JPanel game_panel;  
JPanel instruc_panel; 
JLabel instruc_text;
JLabel instruc_text1;
String [] actions_list;
static int max_capacity;
int current_action = 0;
Gridlayout(String grid, String actions_list,int max_capacity){ 
	Gridlayout.max_capacity = max_capacity;
	this.actions_list = actions_list.split(",");
	frame = new JFrame();  
	   game_panel=new JPanel(); 
	    instruc_panel = new JPanel(); 
	    instruc_text = new JLabel(); 
	    instruc_text1 = new JLabel(); 
	String[] grid_data = grid.split(";");

    try {
        // Open an audio input stream.
        URL url = this.getClass().getClassLoader().getResource("game1.wav");
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        // Get a sound clip resource.
        Clip clip = AudioSystem.getClip();
        // Open audio clip and load samples from the audio input stream.
        clip.open(audioIn);
        clip.start();
     } catch (UnsupportedAudioFileException e) {
        e.printStackTrace();
     } catch (IOException e) {
        e.printStackTrace();
     } catch (LineUnavailableException e) {
        e.printStackTrace();
     }
	
//	//TODO NEW_STATE
	grid =grid_data[0]+";"+grid_data[1]+";"+grid_data[2]+";"+updateIMF(grid_data[3])+";"+grid_data[4]+";"+grid_data[5];
	
	display(grid);
    frame.setTitle("Game View");	
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
}  

public void display(String grid) {
	frame.getContentPane().removeAll();
	frame.repaint();
	game_panel=new JPanel(); 
	instruc_panel = new JPanel(); 
	instruc_text = new JLabel();
	instruc_text1 = new JLabel();
 
    
    String[] grid_lists = grid.split(";");  
    String[] grid_size = grid_lists[0].split(",");
    String[] ethan_postion = grid_lists[1].split(",");
    String[] submarian_postion = grid_lists[2].split(",");
    String[] members_postions = grid_lists[3].split(",");
    String[] members_health = grid_lists[4].split(",");
    System.out.println("NEEEDED:"+grid_lists[3]);
    for(int i = 0 ; i < Integer.parseInt(grid_size[0]); i++) {
        for(int j = 0 ; j < Integer.parseInt(grid_size[1]); j++) {
        	String name_path =null;
        	int member_index = 0;
        	JButton button=new JButton();
        	for (int k = 0; k < (members_health).length && member_index<(members_postions.length-1); k++) {
        		if(isEqual(members_postions[member_index],members_postions[member_index+1],j,i)) {
        			if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<15) {
        				name_path = "Assesments/injured_soldier_5.png";
        			}else {
        				if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<30) {
            				name_path = "Assesments/injured_soldier_6.png";
            			}else {
            				if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<50) {
                				name_path = "Assesments/injured_soldier_9.png";
                			}else {
                				if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<80) {
                    				name_path = "Assesments/injured_soldier_10.png";
                    			}else {
                    				if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<100) {
                        				name_path = "Assesments/injured_soldier_7.png";
                        			}else {
                        				if(Integer.parseInt(members_health[Integer.parseInt(members_postions[member_index+2])])<101) {
                            				name_path = "Assesments/injured_soldier_4.png";
                            			}
                        			}
                    			}
                			}
            			}
        			}
        			button.setToolTipText(members_health[Integer.parseInt(members_postions[member_index+2])]);
        		}
        		member_index +=3;
        	}
        	if(isEqual(ethan_postion[0],ethan_postion[1],j,i)) {
                int current_capacity = Integer.parseInt(grid_lists[5]);
                if(current_capacity != max_capacity)
                	name_path = "Assesments/army_truck_0.png";
                else
                	name_path = "Assesments/army_truck_3.png";
    		}
        	
        	if(isEqual(submarian_postion[0],submarian_postion[1],j,i)) {
        		name_path = "Assesments/submarine_3.jpg";
    		}
        	
        	if (name_path != null) {
            Image img = new ImageIcon(name_path).getImage();
            Image newimg = img.getScaledInstance(90, 90,  java.awt.Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(newimg));
        	}
        	game_panel.add(button);
        }	
    }

    Button b=new Button("NEXT ACTION");  
    b.setBounds(50,100,95,30);  
    if(grid_lists[3].contains("-1")) {
    	if(grid_lists[5].equals(""+max_capacity))
    		instruc_text.setText("Mission is Completed");
    	else
    		instruc_text.setText("All Soliders are collected");
    	instruc_panel.add(instruc_text);	
    }
    else {
    	instruc_text.setText("Remaining Soldiers:"+grid_lists[3]); 
    	instruc_text1.setText("Healths of All soldiers"+grid_lists[4]); 
        instruc_panel.add(instruc_text); 
        instruc_panel.add(instruc_text1); 
    	}
    instruc_panel.add(b);
    b.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent e){  
            String[] grid_lists = grid.split(";");  
            String[] ethan_postion = grid_lists[1].split(",");

            String[] members_health = grid_lists[4].split(",");
            int capacity = Integer.parseInt(grid_lists[5]);
            int ethan_x = Integer.parseInt(ethan_postion[0]);
            int ethan_y = Integer.parseInt(ethan_postion[1]);
            String new_grid =grid;
            
            String new_ethan_postion ="";
            int ethan_y_ = ethan_y;
            int ethan_x_ = ethan_x;
            if(current_action<actions_list.length) {
            	new_grid ="";
            String soldiers_info = updateHealths(members_health,actions_list[current_action],grid_lists[3],ethan_x,ethan_y,capacity);
            
            switch(actions_list[current_action]) {  
            case"DOWN": ethan_x_ = ethan_x + 1; break;
            case"UP":ethan_x_ = ethan_x  - 1; break;
            case"LEFT": ethan_y_ = ethan_y - 1; break;
            case"RIGHT": ethan_y_ = ethan_y + 1; break;
            case"DROP": capacity = max_capacity; break;
            case"CARRY": capacity -= 1; break;
            
            }
            new_ethan_postion += ethan_x_ + "," + ethan_y_;
            new_grid += grid_lists[0]+";";
            new_grid += new_ethan_postion+";";
            new_grid += grid_lists[2]+";";
            new_grid += soldiers_info;
            
            current_action++;
            System.out.println("New Grid Ino :"+new_grid);
            
            }
            display(new_grid);
        }  
        });  
    game_panel.setLayout(new GridLayout(Integer.parseInt(grid_size[0]),Integer.parseInt(grid_size[1])));  
    instruc_panel.setLayout(new GridLayout(3,1));  
    frame.setLayout(new BorderLayout());  
    
    instruc_panel.setPreferredSize(new Dimension(Integer.parseInt(grid_size[0])*100, 100));
    game_panel.setSize(Integer.parseInt(grid_size[0])*100,Integer.parseInt(grid_size[1])*100);  
    frame.setSize((Integer.parseInt(grid_size[0])+2)*100,(Integer.parseInt(grid_size[1])+2)*100);  
    
    frame.add(instruc_panel,BorderLayout.SOUTH);
    frame.add(game_panel,BorderLayout.CENTER); 
    frame.revalidate();
    frame.repaint();

}
public static String updateIMF(String IMF_postions) {
	String updated_postions = "";
	String[] IMF_postions_list = IMF_postions.split(",");
	for(int i = 0; i < IMF_postions_list.length / 2; i++) {
		updated_postions += IMF_postions_list[i*2] + "," + IMF_postions_list[i*2+1] + "," + i;
		if(IMF_postions_list.length / 2 - i != 1) updated_postions += ",";		
	}
	
	return updated_postions;
}
public static String removeIMF(String IMF_postions, int index) {
	String updated_postions = "";
	String[] IMF_postions_list = IMF_postions.split(",");

	for(int i = 0; i < IMF_postions_list.length / 3; i++) {
		if(Integer.parseInt(IMF_postions_list[i*3+2]) != index) {
			updated_postions += IMF_postions_list[i*3] + "," + IMF_postions_list[i*3+1] + "," + IMF_postions_list[i*3+2];
			if(IMF_postions_list.length / 3 - i != 1) updated_postions += ",";
		}
	}
	// TODO May be delete this part in not needed
	if(updated_postions.length() != 0 &&updated_postions.charAt(updated_postions.length()-1)==',')
		updated_postions = updated_postions.substring(0, updated_postions.length()-1);
	else if(updated_postions.length() == 0)
		updated_postions = "-1";
	return updated_postions;
}
public static String updateHealths(String[] IMF_healths_x, String operator, 
		String soldier_positions, int ethan_x, int ethan_y, int  remaining_capacity) {
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
				&& soldiers_index == -1 && remaining_capacity != 0) {
			
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
		else 
			current_health = IMF_healths_numbers.get(current_soldier_index);
		
		if (current_health >= 100) { 
			current_health = 100;
			
		}
		IMF_healths_numbers.remove(current_soldier_index);
		IMF_healths_numbers.add(current_soldier_index, current_health);

		
	}
	for(int i = 0;i<IMF_healths_numbers.size();i++) {
		healths_string += IMF_healths_numbers.get(i);
		if(IMF_healths_numbers.size() - i != 1) 
		{
			healths_string += ",";
		}
	}
	if (operator.equals("DROP"))
		return IMF_positions_+";"+healths_string+";"+max_capacity;
	return IMF_positions_+";"+healths_string+";"+remaining_capacity;
}

 
}  