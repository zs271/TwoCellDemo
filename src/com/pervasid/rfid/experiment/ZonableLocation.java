package com.pervasid.rfid.experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javax.swing.JFrame;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.pervasid.rfid.experiment.TdmaTest.SettingsException;

public class ZonableLocation {

	private PervasidServerSettings settings;
	private long[] reader_id_all;
	private String[] tag_id_all;
	private String[] tag_type_all;
	private double[] tag_rssi;
	private int[] cellNum;
	private String settings_path;
	private DatabaseWorker dw; 
	private Connection con; 
	private int num_of_reads=0;
	private int read_rate=0;
	private long tag_exist_time=100;
	private long together_limit;
	private long depart_limit;
	
	private display ds;
	private JFrame jf=new JFrame();
	private int width=1300,height=550;
	private HashMap<Long,Integer> reader_hash=new HashMap<Long,Integer>();
	
	private EmailAlert emailAlert;
	private SMSAlert smsAlert;
	private String username = "pervasid99";
	private String password = "pervasid";
	private String smtpServer="smtp.gmail.com";
	private String sender="pervasid99@gmail.com";
	private String recepient="zsc33zsc@gmail.com";
	private String phoneNumber;
	private DatabaseAlert dbAlert;
	
	
	
	public ZonableLocation(String setting_path) {
	
		this.settings_path=setting_path;
		this.settings = new PervasidServerSettings();
		this.smsAlert=new SMSAlert();
		
		}
	
	public double getRSSI(long reader_id,String tag_id,int num_of_reads){
		
		PreparedStatement pstmt = null;
		int i=1;
		double tag_rssi=-100;
		try{
		pstmt = con.prepareStatement("SELECT AVG(rssi/10) as tag_rssi FROM (SELECT rssi FROM tag_reads_simple WHERE reader_id=? AND tag_id=? ORDER BY tag_read_id DESC LIMIT ?) as per_tag");
		//pstmt.setString(i++, table_name);
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
	
	public int getCellNum_readrate(String tag_id,int num_of_reads){
		
		PreparedStatement pstmt = null;
		int i=1;
		long located_reader_id = reader_id_all[0];
		long time_diff=1000;
		
		try{
		pstmt = con.prepareStatement("SELECT reader_id,read_rate FROM (SELECT reader_id,COUNT(*) AS read_rate FROM (SELECT  reader_id,tag_id,rssi FROM `tag_reads_simple` WHERE tag_id = ? ORDER BY tag_read_id DESC LIMIT ?) AS last_reads GROUP BY reader_id) AS read_rate_count ORDER BY read_rate DESC LIMIT 1");
		//pstmt.setString(i++, table_name);
		pstmt.setString(i++, tag_id);
		pstmt.setInt(i++, num_of_reads);
		
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		
		located_reader_id= rs.getLong("reader_id");
		
		read_rate=rs.getInt("read_rate");
		
		pstmt.close();
		i=1;
		pstmt = con.prepareStatement("SELECT (NOW()-server_time) AS time_diff FROM `tag_reads` WHERE tag_id = ? ORDER BY tag_read_id DESC LIMIT 1");
		pstmt.setString(i++, tag_id);
		rs = pstmt.executeQuery();
		rs.next();
		time_diff=rs.getLong("time_diff");
		pstmt.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(reader_hash.containsKey(located_reader_id)){
			if(time_diff<tag_exist_time){
				return reader_hash.get(located_reader_id);}}
		
		return -1;
		
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
		if (MaxRSSI<=-100){
			return -1;
		}
		
		return MaxRSSI_pos+1;
		
		
	}
	
	
	public void run(){
		
		ds=new display(width,height,tag_id_all.length);
		//initialise graphics
		jf.setTitle("ZonableLocation");
		jf.setSize(width+50, height+50);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(ds);	
		int curTagPos[]=new int[2];
		int curTagPos_x, curTagPos_y,newTagPos_x,newTagPos_y;
		int cell_i=1;
		long[] tlastTogether,tlastDepart,tTogetherEnd,tDepartEnd,togetherSent,departSent;
		
		for (Long reader_id:reader_id_all){
			Integer cell_index=reader_hash.get(reader_id);
			reader_hash.put(reader_id, cell_i++);
			//System.out.println(reader_id+" "+reader_hash.get(reader_id));
		}
		
		int numOfPairs=tag_id_all.length/2;
		
		tlastTogether=new long[numOfPairs];
		tlastDepart=new long[numOfPairs];
		togetherSent=new long[numOfPairs];
		departSent=new long[numOfPairs];
		
		for (int i=0;i<numOfPairs;i++){
			tlastTogether[i]=System.currentTimeMillis();
			tlastDepart[i]=System.currentTimeMillis();
			togetherSent[i]=0;
			departSent[i]=0;
		}
			

		while(true){
			
		//repeatedly find the cell for each tag, and update their positions 	
		for(int tag_index=0;tag_index<tag_id_all.length;tag_index++){
			
			
			cellNum[tag_index]=getCellNum_readrate(tag_id_all[tag_index],num_of_reads);
			//System.out.println("Tag"+(tag_index+1)+":"+tag_id_all[tag_index]+" Cell "+cellNum[tag_index]);
			//System.out.println("read rate = "+read_rate);
			//newTagPos_x=(ds.getWidth()/16+(cellNum[tag_index]-1)*ds.getWidth()/2)%ds.getWidth()+(tag_index*70);
			
				newTagPos_x=ds.getWidth()/20*2+(cellNum[tag_index]-1)*ds.getWidth()/20*10;
				newTagPos_y=(0+(tag_index)*20+50);
				
			if(cellNum[tag_index]==-1){
				newTagPos_x=-50;
				newTagPos_y=-50;
			}
				
			//newTagPos_y=(cellNum[tag_index]<=2?ds.getHeight()*1/16:ds.getHeight()*5/8);
			
			curTagPos=ds.getTagPos(tag_index);
			curTagPos_x=curTagPos[0];
			curTagPos_y=curTagPos[1];
			
			if(curTagPos_x!=newTagPos_x || curTagPos_y!=newTagPos_y){
				ds.drawTag(newTagPos_x,newTagPos_y,tag_index,tag_type_all[tag_index]);
			}
			
		      

		}
		
		//if the odd tag is not in the same zone as the even tag, start counter
		for (int i=0;i<numOfPairs;i++){
			if(cellNum[2*i]==cellNum[2*i+1]){
			tlastTogether[i]=System.currentTimeMillis();
			
			}else{
				tlastDepart[i]=System.currentTimeMillis();
			}
		}
		
		
		long tnow=System.currentTimeMillis();
		//Tags have been departed for a certain time
		
		for (int i=0;i<numOfPairs;i++){
			if((tnow-tlastTogether[i])/1000>depart_limit){
				if(departSent[i]==0){
					String alertMessage="Alert!Warning: "+tag_type_all[2*i]+" and "+ tag_type_all[2*i+1]+" are split!";
					emailAlert.sendEmail("Sicheng", "Alert!", "Warning: "+tag_type_all[2*i]+" and "+ tag_type_all[2*i+1]+" are split!");
					dbAlert.sendDBAlert(tag_id_all[2*i], cellNum[2*i],reader_id_all[cellNum[2*i]-1], "Split", alertMessage);
					dbAlert.sendDBAlert(tag_id_all[2*i+1], cellNum[2*i+1],reader_id_all[cellNum[2*i+1]-1], "Split", alertMessage);

					//smsAlert.sendSMS(phoneNumber, alertMessage);
					departSent[i]=1;
					togetherSent[i]=0;
				}
			}
		
		//tags have been together for a certain time
			if((tnow-tlastDepart[i])/1000>together_limit){
				if(togetherSent[i]==0){
					String alertMessage="Alert!Warning: "+tag_type_all[2*i]+" and "+ tag_type_all[2*i+1]+" are together for too long!";
					emailAlert.sendEmail("Sicheng", "Alert!", "Warning: "+tag_type_all[2*i]+" and "+ tag_type_all[2*i+1]+" are together for too long!");	
					dbAlert.sendDBAlert(tag_id_all[2*i], cellNum[2*i], reader_id_all[cellNum[2*i]-1], "Together", alertMessage);
					dbAlert.sendDBAlert(tag_id_all[2*i+1], cellNum[2*i+1], reader_id_all[cellNum[2*i+1]-1], "Together", alertMessage);

					//smsAlert.sendSMS(phoneNumber, alertMessage);
					togetherSent[i]=1;
					departSent[i]=0;
					}
				
			}
		}
		
		
		
		//System.out.println("");
		
		//display tag positions
		
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
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
			
		
				Ini.Section location_settings = ini.get("location_settings");
				tag_id_all=location_settings.getAll("tag_id",String[].class);
				tag_type_all=location_settings.getAll("tag_name",String[].class);
				
				reader_id_all=location_settings.getAll("reader_id",long[].class);
				num_of_reads=location_settings.get("num_of_reads",int.class);
				tag_exist_time=location_settings.get("tag_exist_time",int.class);
				
				Ini.Section alert_settings=ini.get("alert_settings");
				together_limit=alert_settings.get("together_limit",long.class);
				depart_limit=alert_settings.get("depart_limit",long.class);
				username=alert_settings.get("username",String.class);
				password=alert_settings.get("password",String.class);
				smtpServer=alert_settings.get("smtpServer",String.class);
				sender=alert_settings.get("sender",String.class);
				recepient=alert_settings.get("recepient",String.class);
				phoneNumber=alert_settings.get("phoneNumber",String.class);
				
				this.emailAlert=new EmailAlert(username,password,smtpServer,sender,recepient);
				
				cellNum=new int[tag_id_all.length];
				

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
			this.dbAlert=new DatabaseAlert(con);
			
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
		
		
		String conf_location = "test-settings.ini";
				

		if (args.length > 0) {
			//System.out.printf("From commandline args.\n");
			conf_location = args[0];
		}
		
	
		
		ZonableLocation zl=new ZonableLocation(conf_location);
		
		
		//load settings and connections
		try {
			zl.loadSettings();
			zl.loadConnection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//run zonable location
		zl.run();
		
		
		


		
		
	}

}
