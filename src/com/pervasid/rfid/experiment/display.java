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
	int num_of_tags;
	private int width=1000,height=600;
	
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
		g.drawRect(0, 0, width-10, height-10);
		g.drawLine(0, height/2, width-10, height/2);
		g.drawLine(width/2, 0, width/2, height-10);
		
		
		Font font = new Font("Courier", Font.BOLD,18);
		g.setFont(font);
		g.drawString("Cell 1", width/20, height/20);
		g.drawString("Cell 2", width/20+width/2, height/20);
		g.drawString("Cell 3", width/20, height/20+height/2);
		g.drawString("Cell 4", width/20+width/2, height/20+height/2);

	
		for(int tag_index=0;tag_index<num_of_tags;tag_index++){
			
		g.setColor(Color.BLUE);
		if(tag_index>=9){
			g.drawRect(x[tag_index],y[tag_index] , 30, 20);
		}else{
			g.drawRect(x[tag_index],y[tag_index] , 20, 20);
		}
		g.drawString(Integer.toString(tag_index+1), x[tag_index]+5, y[tag_index]+15);
		}

		
	}
	
	
	public void drawTag(int x,int y,int tag_index){

		this.x[tag_index]=x;
		this.y[tag_index]=y;
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
		display ds=new display(600,400,4);
		JFrame jf=new JFrame();
		jf.setTitle("ZonableLocation");
		jf.setSize(600, 400);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(ds);
		
		
		
	    //ds.drawTag(100,200);
		//ds.run();
		
	}



	
	
	
}
