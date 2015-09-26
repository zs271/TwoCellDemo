package com.pervasid.rfid.experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.pervasid.rfid.experiment.TdmaTest.SettingsException;

public class ZonableLocation {

	private PervasidServerSettings settings;
	private long[] reader_id_all;
	private String[] tag_id_all;
	private double[] tag_rssi;
	private int[] cellNum;
	private String settings_path;
	private DatabaseWorker dw; 
	private Connection con; 
	private int num_of_reads=0;
	
	public ZonableLocation(String setting_path) {
	
		this.settings_path=setting_path;
		this.settings = new PervasidServerSettings();
		}
	
	public double getRSSI(long reader_id,String tag_id,int num_of_reads){
		
		PreparedStatement pstmt = null;
		int i=1;
		double tag_rssi=-100;
		try{
		pstmt = con.prepareStatement("SELECT AVG(rssi/10) as tag_rssi FROM tag_reads_simple_new WHERE reader_id=? AND tag_id=? ORDER BY tag_read_id DESC LIMIT ?");
		pstmt.setLong(i++, reader_id);
		pstmt.setString(i++, tag_id);
		pstmt.setInt(i++, num_of_reads);
		
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		
		tag_rssi = rs.getDouble("tag_rssi");
		pstmt.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(tag_rssi>=0){
			tag_rssi=-100;
		}
		
		return tag_rssi;
		
	}
	
	//starting from cell 1
	public int getCellNum(int tag_index){
		
		
		int MaxRSSI_pos=0;
		double MaxRSSI=getRSSI(this.reader_id_all[0],tag_id_all[tag_index],num_of_reads);
		double rssi_temp;
		for (int cell_index=1;cell_index<reader_id_all.length;cell_index++){
			rssi_temp=getRSSI(reader_id_all[cell_index],tag_id_all[tag_index],num_of_reads);
			if (rssi_temp>MaxRSSI) {
				MaxRSSI=rssi_temp;
				MaxRSSI_pos=cell_index;
			}
			
		}
		
		return MaxRSSI_pos+1;
		
		
	}
	
	
	public void loadSettings() 
			throws SettingsException {

			try {
				Ini ini = new Ini(new java.io.FileInputStream(
									  new java.io.File(settings_path)));

				settings.db_location = ini.get("database", "location");
				settings.db_password = ini.get("database", "password");
				settings.db_username = ini.get("database", "user");
				settings.db_name     = ini.get("database", "name");
				settings.db_port     = ini.get("database", "port", int.class);
				
				settings.dur_seconds = ini.get("test", "duration", int.class);
				
				
		
				Ini.Section tdma_settings = ini.get("tdma_settings");
				Ini.Section test = ini.get("test");
				
				
				

			} catch (java.io.FileNotFoundException e) {
				throw new SettingsException("Cannot open config file. (FileNotFoundException)");
			} catch (java.io.IOException e) {
				throw new SettingsException("Error reading config file. (IOException)");
			} catch (java.lang.IllegalArgumentException e) {
				throw new SettingsException("Error converting value while parseing config file. (IllegalArgument)");
			}

			try {
				DatabaseSource ds = DatabaseSource.getInstance(settings);
			} catch (java.sql.SQLException e) {
				
				throw new SettingsException("Error creating SQL data source\n");
			}

			}
	
	public void loadConnection(){
		try{
			dw= new DatabaseWorker(settings);
			con= dw.getConnection();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public class SettingsException extends Exception {
		public SettingsException(String msg) {
			super(msg);
		}
	}
	
	public static void main(String[] args){
		
		
		//long[] reader_id_all={12345678,87654321};
		//String[] tag_id_all={"000000001"};
		
		String conf_location = "test-settings.ini";
				
		String current_time=null;
		long tag_reads=0;

		if (args.length > 0) {
			//System.out.printf("From commandline args.\n");
			conf_location = args[0];
		}
		
	
		ZonableLocation zl=new ZonableLocation(conf_location);
		
		
		try {
			zl.loadSettings();
		} catch (SettingsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		zl.loadConnection();
		
		
		
		Ini ini;
		Ini.Section location_settings;
		try {
			ini = new Ini(new java.io.FileInputStream(new java.io.File(conf_location)));
			location_settings = ini.get("location_settings");
			zl.tag_id_all=location_settings.getAll("tag_id",String[].class);
			zl.reader_id_all=location_settings.getAll("reader_id",long[].class);
			zl.num_of_reads=location_settings.get("num_of_reads",int.class);
		} catch (InvalidFileFormatException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		int[] cellNum=new int[zl.tag_id_all.length];
		 
			
		
		while(true){
		for(int tag_index=0;tag_index<zl.tag_id_all.length;tag_index++){
			cellNum[tag_index]=zl.getCellNum(tag_index);
			System.out.println("Tag"+(tag_index+1)+":"+zl.tag_id_all[tag_index]+" Cell "+cellNum[tag_index]);
		}
		System.out.println("");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		
		
	}

}
