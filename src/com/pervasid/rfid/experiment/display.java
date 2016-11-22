package com.pervasid.rfid.experiment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class display extends JPanel    {
	
	
	
	private int[] x,y;
	private String[] tag_type=new String[20];
	int num_of_tags;
	private int width=1200,height=900;
	
	public display(int width,int height, int num_of_tags){
		this.width=width;
		this.height=height;
		this.num_of_tags=num_of_tags;
		this.x=new int[num_of_tags];
		this.y=new int[num_of_tags];
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.RED);
		//g.drawRect(0, 0, width-10, height-10);
		g.drawRect(0, 10, width/20*10,height-30 );
		g.drawRect(width/20*10, 10, width/20*10,height-30 );
		//g.drawRect(width/30*20, 10, width/30*10,height-30 );
		
		
		Font font = new Font("Courier", Font.BOLD,18);
		g.setFont(font);
		g.drawString("Cell 1", 0+20, 30);
		g.drawString("Cell 2", width/20*10+20, 30);
		//g.drawString("Cell 3", width/30*20+20, 30);

	
		for(int tag_index=0;tag_index<num_of_tags;tag_index++){
		
			
		g.setColor(Color.BLUE);
		
		//g.drawRect(x[tag_index],y[tag_index] , 30, 20);
		
		
		g.drawString(tag_type[tag_index], x[tag_index]+5, y[tag_index]+15);
		}

		
	}
	
	
	public void drawTag(int x,int y,int tag_index,String tag_type){
		this.x[tag_index]=x;
		this.y[tag_index]=y;
		this.tag_type[tag_index]=tag_type;
		repaint();
		
	}
	
	
	public int getWidth(){

		return width;
		
	}

	public int getHeight(){

		return height;
		
	}
	
	
	public int[] getTagPos(int tag_index){

	   int[] tagPos=new int[] {x[tag_index],y[tag_index]};
		
	   return tagPos;
	}

	
	
	public static void main(String args[]){
		display ds=new display(1050,450,4);
		JFrame jf=new JFrame();
		jf.setTitle("ZonableLocation");
		jf.setSize(1050, 450);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(ds);
		
		
		
	    //ds.drawTag(100,200);
		//ds.run();
		
	}



	
	
	
}
