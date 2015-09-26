package com.pervasid.rfid.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.pervasid.rfid.experiment.PervasidServerSettings;
import com.pervasid.rfid.experiment.DatabaseSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.*;
import java.sql.Connection;

//import com.pervasid.rfid.experiment.Experiment.SettingsException;



public class TdmaTest {

	private PervasidServerSettings settings;
	private ExperimentSettings reader_settings;
	private String settings_path;
	private long reader_id;
	private int pause_seconds;
	private int reset_pause_second;
	private String[] reset_time;
	private long[] reader_list;
	private int num_of_group;
	private int difference_check;
	private DatabaseWorker dw; 
	private Connection con;  
	private int max_retry_count;
	private final static Logger logger = Logger.getLogger("TDMALog");;
	
	
	public void loadConnection(){
		try{
			dw= new DatabaseWorker(settings);
			con= dw.getConnection();	
		}catch(Exception e){
			logger.info("Error getting SQL connection");
		}
	}
	
	
	public class SettingsException extends Exception {
		public SettingsException(String msg) {
			super(msg);
		}
	}
	
	public TdmaTest(String settings_path) {
		this.settings = new PervasidServerSettings();
		this.reader_settings = new ExperimentSettings();
		this.settings_path = settings_path;
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
		
		
		pause_seconds = ini.get("test", "pause_seconds", int.class);
		reset_pause_second = ini.get("test", "reset_pause_second", int.class);
		
		difference_check= ini.get("test", "difference_check", int.class);
		
		Ini.Section tdma_settings = ini.get("tdma_settings");
		Ini.Section test = ini.get("test");
		reader_list=tdma_settings.getAll("reader_list",long[].class);
		num_of_group=tdma_settings.get("num_of_group",int.class);
		max_retry_count= ini.get("test", "max_retry_count", int.class);
		reset_time =  test.getAll( "reset_time",String[].class);
		
		
		
		

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
		logger.info("Error creating SQL data source");
		throw new SettingsException("Error creating SQL data source\n");
	}

	}
	
	public void loadTestSettings() 
			throws SettingsException {

			try {
				Ini ini = new Ini(new java.io.FileInputStream(
									  new java.io.File(settings_path)));

				
				reader_settings.inventory_period 	= ini.get("test_settings","inventory_period",long.class);
				reader_settings.start_q				= ini.get("test_settings","start_q",long.class);
				reader_settings.min_q				= ini.get("test_settings","min_q",long.class);
				reader_settings.max_q				= ini.get("test_settings","max_q",long.class);
				reader_settings.retry_count			= ini.get("test_settings","retry_count",long.class); 
				reader_settings.threshold_multiplier= ini.get("test_settings","threshold_multiplier",long.class);
				reader_settings.power_level			= ini.get("test_settings","power_level",long.class); 
				reader_settings.session				= ini.get("test_settings","session",int.class);
				reader_settings.target				= ini.get("test_settings","target",int.class); 
				reader_settings.profile				= ini.get("test_settings","profile",long.class); 
				reader_settings.switch_states		= ini.get("test_settings","switch_states",long.class); 
				reader_settings.freq_dwell_time		= ini.get("test_settings","freq_dwell_time",long.class);
				reader_settings.mode				= ini.get("test_settings","mode",int.class); 
				reader_settings.previous_states		= ini.get("test_settings","previous_states",int.class); 
				reader_settings.trans_packets		= ini.get("test_settings","trans_packets",int.class); 
				reader_settings.hop_rate			= ini.get("test_settings","hop_rate",int.class);

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
				logger.info("Error creating SQL data source\n");
				throw new SettingsException("Error creating SQL data source\n");
			}

		}
	
	public long getDifference(String time1, String time2) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = format.parse(time1);
		Date date2 = format.parse(time2);
		long difference = (date2.getTime() - date1.getTime())/1000; 
		
		return difference;
	}
	
	
	public String getMachineTime(SimpleDateFormat sdf){
		Calendar cal = Calendar.getInstance();
        String machine_time=sdf.format(cal.getTime());
        return machine_time;
	}
	
	public String run(List<Integer> group_list) {
		
		String current_time=null;
		MacSetting mac_setting=new MacSetting();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat daily_time = new SimpleDateFormat("HH:mm:ss");
		Boolean reset_time_met=false;
		
		long difference=0;
		
		try {
			
			
			String reader_time=null;
			String machine_time=null;

			//System.out.print("\r");
				
			reader_settings.reader_id = reader_id;
			//initial attempt to start all readers
				{
				for(int i=0;i<group_list.size();i++){
						int reader_index=group_list.get(i)-1;
						reader_id=reader_list[reader_index];
						

						dw.SetStartInventory(reader_id,con);

						
						//System.out.println("Starting reader "+group_list.get(i));
					}	
					
				Thread.sleep(1000);
				}
				
				
				//System.out.print("\r");
				//check the ones that have not been started 
				
				for(int i=0;i<group_list.size();i++){
					int reader_index=group_list.get(i)-1;
					reader_id=reader_list[reader_index];
					try{
						
						reader_time=dw.GetReaderTime(reader_id,con);
				        machine_time=getMachineTime(sdf);
						difference=getDifference(reader_time,machine_time);
						
						int retry_count=0;
						while(dw.GetReaderStatus(reader_id,con)!=1){
							reader_time=dw.GetReaderTime(reader_id,con);
							machine_time=getMachineTime(sdf);
							difference=getDifference(reader_time,machine_time);
						//System.out.println(difference);
						if (difference<=difference_check){
							dw.SetStartInventory(reader_id,con);
							//Thread.sleep(1000);
						}else{
							break;
							
						}
						
					     
					     current_time=getMachineTime(daily_time);
					     
						for (int k=0;k<reset_time.length;k++){
						if(current_time.startsWith(reset_time[k]))
						{
							reset_time_met=true;
							break;
							
						}
						}
						
						if(reset_time_met){
							reset_time_met=false;
							break;
						}
						
						Thread.sleep(1000);
						retry_count++;
						if(retry_count==max_retry_count){
							break;
						}
						
						}
					
						current_time=getMachineTime(daily_time);
						
						
						for (int k=0;k<reset_time.length;k++){
						if(current_time.startsWith(reset_time[k]))
						{
							reset_time_met=true;
							break;
						}
						}
						
						if(reset_time_met){
							reset_time_met=false;
							break;
						}
						
					 if(difference>difference_check){
						logger.info("reader "+Integer.toString(group_list.get(i))+" is offline!");
						// System.out.println("offline time: "+Long.toString(difference)+" s");
						 
						 
					 }
					 else if (dw.GetReaderStatus(reader_id,con)==1)
					 {
						// System.out.println("reader "+Integer.toString(group_list.get(i))+" is on!");
					 }else
					 {
						// System.out.println("reader "+Integer.toString(group_list.get(i))+" failed to start after max retry!");
					 }
					 
					 
	
					} catch(Exception e) {
						logger.info("reader "+Integer.toString(group_list.get(i))+" failed to start due to errors!");
						logger.info(e.getMessage());
					}
				}
			
				
				//System.out.print("Inventory Started! \n");
				current_time=getMachineTime(daily_time);

				
				for(int n=1; n<settings.dur_seconds; n++)
				{
			        
			        
					for (int k=0;k<reset_time.length;k++){
					if(current_time.startsWith(reset_time[k]))
					{
						reset_time_met=true;
						break;
					}
					}
					
					if(reset_time_met){
						reset_time_met=false;
						break;
					}
					
					Thread.sleep(1000);
				}
				
				

				
				//initial attempt to stop all readers
				for(int i=0;i<group_list.size();i++){
					int reader_index=group_list.get(i)-1;
					reader_id=reader_list[reader_index];	
					
					dw.SetStopInventory(reader_id,con);
					
					//System.out.println("Stopping reader "+group_list.get(i));
				}
				
	
					Thread.sleep(1000);
					//System.out.print("\r");
					//Make sure all readers are stopped
					for(int i=0;i<group_list.size();i++){
						int reader_index=group_list.get(i)-1;
						reader_id=reader_list[reader_index];
						try{
							
							reader_time=dw.GetReaderTime(reader_id,con);
							machine_time=getMachineTime(sdf);
							difference=getDifference(reader_time,machine_time);
							
							int retry_count=0;
							while(dw.GetReaderStatus(reader_id,con)!=0){
								reader_time=dw.GetReaderTime(reader_id,con);
								machine_time=getMachineTime(sdf);
								difference=getDifference(reader_time,machine_time);
							if (difference<=difference_check){
								
								dw.SetStopInventory(reader_id,con);
								Thread.sleep(1000);		
							}else{
								break;
							}
							retry_count++;
							if(retry_count==max_retry_count){
								break;
							}
							}
						
						if(difference>difference_check   ){
							logger.info("reader "+Integer.toString(group_list.get(i))+" is offline!");
							//System.out.println("offline time:"+Long.toString(difference)+"s");
							
						
						}else if(dw.GetReaderStatus(reader_id,con)==0){
							//System.out.println("reader "+Integer.toString(group_list.get(i))+" is off!");
						}else {
							//System.out.println("reader "+Integer.toString(group_list.get(i))+" cannot be stopped after max retry!");
						}
						
						
							
							
						} catch(Exception e) {
							logger.info("reader "+Integer.toString(group_list.get(i))+" failed to stop!");
							logger.info(e.getMessage());
						}
					}	

			
			
				
			}	catch (Exception e) {
				logger.info("Error while processing:\n");
				logger.info(e.getMessage());
				
			} 
			
				
		return current_time;	
				
	}
	
	public long getTagCount() throws SQLException{
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement("SELECT MAX(tag_read_id)FROM tag_reads");
	
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		long tag_reads = rs.getLong("MAX(tag_read_id)");
		pstmt.close();
		return tag_reads;
	}
	
	
	public List<Long> getOffReaders() throws SQLException{
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement("SELECT reader_id FROM off_readers");
		
		List<Long> offReaderList=new ArrayList();
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			long reader_id=rs.getLong("reader_id");
			
			offReaderList.add(reader_id);
		}
		
		pstmt.close();
		return offReaderList;
	}
	
	
	public static int randInt(int min, int max, double p[]) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();
	    int return_num=-1;
	    
	    double accum_p[]=new double[p.length+1];
	    double sum_p=0;
	    accum_p[0]=0;
	    for (int i=0;i<p.length;i++){
	    	sum_p=sum_p+p[i];
	    	accum_p[i+1]=sum_p;
	    	
	    	
	    }

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt(100);
	    
	    for(int p_index=0;p_index<p.length;p_index++){
	    	if(randomNum>=accum_p[p_index]*100 & randomNum<(accum_p[p_index+1]*100)){
	    		return_num=min+p_index;
	    	}
	    }

	    return return_num;
	}
	
	
	public static void main(String[] args) throws InterruptedException, InvalidFileFormatException, FileNotFoundException, IOException, SQLException, com.pervasid.rfid.experiment.TdmaTest.SettingsException {

		String conf_location = "test-settings.ini";
		try {  
		    
			 Date date = new Date() ;
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
			 
			     FileHandler fh;
		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler(dateFormat.format(date)+"_log.txt");  
		        logger.addHandler(fh);
		        logger.setUseParentHandlers(false);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  

		        // the following statement is used to log any messages  
		        logger.info("Log created!");  

		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }
				
		
		
		String current_time=null;
		long tag_reads=0;

		if (args.length > 0) {
			//System.out.printf("From commandline args.\n");
			conf_location = args[0];
		}
		
		TdmaTest test = new TdmaTest(conf_location);	
		
		test.loadSettings();
		test.loadTestSettings();
		test.loadConnection();
		
		
		Ini ini = new Ini(new java.io.FileInputStream(new java.io.File(conf_location)));
		
		Ini.Section tdma_settings = ini.get("tdma_settings");
		
		 
		

		while(true)	{
			
			 int[] group=new int[test.num_of_group];
			 
			  for(int grp_index=1;grp_index<=test.num_of_group;grp_index++){
				  int[] random_group=tdma_settings.getAll("random_grp"+Integer.toString(grp_index),int[].class);
				  double[] pro=tdma_settings.getAll("random_grp"+Integer.toString(grp_index)+"_pro",double[].class);
				  
				  
				  group[grp_index-1]=random_group[TdmaTest.randInt(0,random_group.length-1,pro)];
			  }
			  // System.out.println("starting reader_index_grp"+Integer.toString(group_index));
				
					
				List<Integer> group_list=new ArrayList<Integer>();
				for(int value:group){
					group_list.add(value);
				}
				
			
				List<Long> off_readers_list=test.getOffReaders();
				//System.out.println("\rOff readers:"+off_readers_list);

				
				for (int i=0;i<group.length;i++){
					if(off_readers_list.contains(test.reader_list[group[i]-1])){
					group_list.remove(Integer.valueOf(group[i]));
				}
				}
				
				
				try{
					current_time=test.run(group_list);
				}catch(Exception e){
					test.loadConnection();
					current_time=test.run(group_list);
				}
				
				
				for (int k=0;k<test.reset_time.length;k++){
					if(current_time.startsWith(test.reset_time[k])){
					//System.out.println("All reader reset");
					//test.dw.ResetAllReaders(test.con);
					//System.out.println("Reset time "+current_time);
					//System.out.println("wait for "+test.reset_pause_second+'s');
					Thread.sleep(test.reset_pause_second*1000);
					tag_reads=test.getTagCount();
					
					//System.out.println("Last read before reset: "+tag_reads);
					
					}
				}
				
				if (test.pause_seconds>0){
				Thread.sleep(test.pause_seconds*1000);
				}

			}
		
			//tag_reads=test.getTagCount();
			//System.out.println("Total unique tags: "+tag_reads);
		}
		
		

	
	
}
				
		

	
	

